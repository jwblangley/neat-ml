package jwblangley.neat.evolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import jwblangley.neat.genotype.NetworkGenotype;
import jwblangley.neat.proto.EvolutionOuterClass;
import jwblangley.neat.proto.Genotypes;
import jwblangley.neat.proto.ProtoEquivalent;

/**
 * Class to control the evolution and growth of neural network at the core of the NEAT algorithm
 */
public class Evolution implements ProtoEquivalent {

  private static final double INITIAL_COMPATIBILITY_DISTANCE_THRESHOLD = 10d;
  private static final double COMPATIBILITY_MODIFIER = 1.7d;
  public static final double WEIGHT_MUTATION_RATE = 0.5f;
  public static final double ADD_CONNECTION_MUTATION_RATE = 0.1f;
  public static final double ADD_NEURON_MUTATION_RATE = 0.1f;
  public static final int ADD_CONNECTION_ATTEMPTS = 10;

  private final SingleEvaluator singleEvaluator;
  private final BulkEvaluator bulkEvaluator;

  private final int numThreads;
  private final int populationSize;
  private final int targetNumSpecies;
  private final InnovationGenerator innovationGenerator;

  private final List<Species> allSpecies;
  private List<NetworkGenotype> currentGeneration;

  private double compatibilityDistanceThreshold;

  private double highestFitness;
  private NetworkGenotype fittestGenotype;
  private boolean verbose = false;
  private int generationNumber;

  private final Map<NetworkGenotype, Species> genotypeSpeciesMap;
  /**
   * Map from network genotypes to their adjusted fitness
   */
  private final Map<NetworkGenotype, Double> genotypeFitnessMap;

  /**
   * Construct a new Evolution object with single evaluator
   *
   * @param populationSize      size of the population for each generation
   * @param targetNumSpecies    number of targeted species in the population
   * @param startingGenotype    genotype for the initial population to be filled with
   * @param innovationGenerator Generator for innovation markers
   * @param singleEvaluator     Function to simulate and evaluate a single genotype
   * @param numThreads          Number of concurrent threads to evaluate the population with
   */
  public Evolution(int populationSize, int targetNumSpecies, NetworkGenotype startingGenotype,
      InnovationGenerator innovationGenerator, int numThreads, SingleEvaluator singleEvaluator) {
    this(populationSize, targetNumSpecies, startingGenotype, innovationGenerator, numThreads,
        singleEvaluator, null);
  }

  /**
   * Construct a new Evolution object with bulk evaluator
   *
   * @param populationSize      size of the population for each generation
   * @param targetNumSpecies    number of targeted species in the population
   * @param startingGenotype    genotype for the initial population to be filled with
   * @param innovationGenerator Generator for innovation markers
   * @param bulkEvaluator       Function to simulate and evaluate a list of genotypes
   */
  public Evolution(int populationSize, int targetNumSpecies, NetworkGenotype startingGenotype,
      InnovationGenerator innovationGenerator, BulkEvaluator bulkEvaluator) {
    this(populationSize, targetNumSpecies, startingGenotype, innovationGenerator, -1,
        null, bulkEvaluator);
  }

  private Evolution(int populationSize, int targetNumSpecies, NetworkGenotype startingGenotype,
      InnovationGenerator innovationGenerator, int numThreads,
      SingleEvaluator singleEvaluator, BulkEvaluator bulkEvaluator) {

    assert populationSize > 1;
    assert targetNumSpecies > 1;
    assert targetNumSpecies < populationSize;

    this.populationSize = populationSize;
    this.generationNumber = 0;
    this.targetNumSpecies = targetNumSpecies;
    this.innovationGenerator = innovationGenerator;
    this.singleEvaluator = singleEvaluator;
    this.bulkEvaluator = bulkEvaluator;
    this.numThreads = numThreads;
    this.compatibilityDistanceThreshold = INITIAL_COMPATIBILITY_DISTANCE_THRESHOLD;

    // Initialise population
    currentGeneration = new ArrayList<>(populationSize);
    for (int i = 0; i < populationSize; i++) {
      currentGeneration.add(new NetworkGenotype(startingGenotype));
    }

    // Initialise empty stats
    genotypeSpeciesMap = new HashMap<>();
    genotypeFitnessMap = new HashMap<>();
    allSpecies = new ArrayList<>();
  }

  /**
   * Create a new Evolution object from a protobuf object At least one call to evolve on the new
   * object must happen before statistics are available
   *
   * @param protoEvolution   protobuf object to create from
   * @param targetNumSpecies number of targeted species in the population
   * @param numThreads       Number of concurrent threads to evaluate the population with
   * @param singleEvaluator  Function to simulate and evaluate a single genotype
   */
  public Evolution(EvolutionOuterClass.Evolution protoEvolution, int targetNumSpecies,
      int numThreads, SingleEvaluator singleEvaluator) {
    this(protoEvolution, targetNumSpecies, numThreads, singleEvaluator, null);
  }

  /**
   * Create a new Evolution object from a protobuf object At least one call to evolve on the new
   * object must happen before statistics are available
   *
   * @param protoEvolution   protobuf object to create from
   * @param targetNumSpecies number of targeted species in the population
   * @param bulkEvaluator    Function to simulate and evaluate a list of genotypes
   */
  public Evolution(EvolutionOuterClass.Evolution protoEvolution, int targetNumSpecies,
      BulkEvaluator bulkEvaluator) {
    this(protoEvolution, targetNumSpecies, -1, null, bulkEvaluator);
  }

  private Evolution(EvolutionOuterClass.Evolution protoEvolution, int targetNumSpecies,
      int numThreads, SingleEvaluator singleEvaluator, BulkEvaluator bulkEvaluator) {

    this.populationSize = protoEvolution.getCurrentGenerationList().size();
    this.generationNumber = protoEvolution.getGenerationNumber();
    this.targetNumSpecies = targetNumSpecies;
    this.innovationGenerator = new InnovationGenerator(protoEvolution.getCurrentInnovationMarker());
    this.singleEvaluator = singleEvaluator;
    this.bulkEvaluator = bulkEvaluator;
    this.numThreads = numThreads;
    this.compatibilityDistanceThreshold = protoEvolution.getCompatibilityDistanceThreshold();

    // Initialise population
    currentGeneration = protoEvolution.getCurrentGenerationList().stream()
        .map(NetworkGenotype::new)
        .collect(Collectors.toList());

    // Initialise empty stats
    genotypeSpeciesMap = new HashMap<>();
    genotypeFitnessMap = new HashMap<>();
    allSpecies = new ArrayList<>();
  }

  /**
   * Create a protobuf object of this Evolution object
   *
   * @return the protobuf object
   */
  @Override
  public EvolutionOuterClass.Evolution toProto() {
    List<Genotypes.NetworkGenotype> protoCurrentGen = currentGeneration.stream()
        .map(NetworkGenotype::toProto)
        .collect(Collectors.toList());
    /*
     N.B: we increment the innovation generator here, but this has no adverse side affects.
     It just resembles an innovation where nothing happened
     */

    return EvolutionOuterClass.Evolution.newBuilder()
        .addAllCurrentGeneration(protoCurrentGen)
        .setGenerationNumber(generationNumber)
        .setCurrentInnovationMarker(innovationGenerator.next())
        .setCompatibilityDistanceThreshold(compatibilityDistanceThreshold)
        .build();
  }

  /**
   * Set verbose mode. In verbose mode, generation number, highest fitness and number of species are
   * reported to stdout after each generation is evaluated. Verbose mode is initially disabled
   *
   * @param verbose whether verbose mode should be enabled
   */
  public void setVerbose(boolean verbose) {
    this.verbose = verbose;
  }

  /**
   * @return The current number of species in the population
   */
  public int getNumberOfSpecies() {
    return allSpecies.size();
  }

  /**
   * @return The current highest fitness achieved by a member of the population
   */
  public double getHighestFitness() {
    return highestFitness;
  }

  /**
   * @return The genotype responsible for achieving the current highest fitness in the population
   */
  public NetworkGenotype getFittestGenotype() {
    return fittestGenotype;
  }

  /**
   * @return the current generation number
   */
  public int getGenerationNumber() {
    return generationNumber;
  }

  /**
   * Sort a generation into species, evaluate each member of the generation, kill off weaker
   * members, generate child members from surviving members repopulate generation
   *
   * @param random seeded Random object. If you care about seeded behaviour, do not use the same
   *               random object here as in you evaluator. Due to thread pooling that will yield
   *               inconsistent results.
   */
  public void evolve(Random random) {
    generationNumber++;
    if (verbose) {
      System.out.println("Generation " + generationNumber);
    }

    // Reset all stats before next generation evaluation
    reset(random);

    // Place genotypes into species
    for (NetworkGenotype genotype : currentGeneration) {
      boolean foundSpecies = false;

      for (Species species : allSpecies) {
        if (NetworkGenotype.compatibilityDistance(genotype, species.getMascot())
            < compatibilityDistanceThreshold) {
          species.addMember(genotype);
          genotypeSpeciesMap.put(genotype, species);
          foundSpecies = true;
          break;
        }
      }
      if (!foundSpecies) {
        // No new species found - create a new species
        Species newSpecies = new Species(genotype);
        allSpecies.add(newSpecies);
        genotypeSpeciesMap.put(genotype, newSpecies);
      }
    }

    // Remove any dormant species
    allSpecies.removeIf(s -> s.getMembers().isEmpty());

    // Adjust compatibility distance threshold to achieve number of species
    // Unlike most implementations, I use an exponential growth / shrink to ensure this can keep up
    if (allSpecies.size() < targetNumSpecies) {
      compatibilityDistanceThreshold /= COMPATIBILITY_MODIFIER;
    } else if (allSpecies.size() > targetNumSpecies) {
      compatibilityDistanceThreshold *= COMPATIBILITY_MODIFIER;
    }
    // Ensure minimum
    compatibilityDistanceThreshold = Math
        .max(COMPATIBILITY_MODIFIER, compatibilityDistanceThreshold);

    // Evaluate each genotype and assign its fitness
    if (singleEvaluator != null) {
      final ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
      final Lock criticalLock = new ReentrantLock();
      for (NetworkGenotype genotype : currentGeneration) {
        threadPool.execute(() -> {

          // Simulate the genotype and evaluate fitness
          final double fitness = singleEvaluator.evaluate(genotype);

          // Read-only
          Species genotypesSpecies = genotypeSpeciesMap.get(genotype);

          // Adjust fitness by species size to prevent elitism. genotypeSpecies.size() is read-only
          final double adjustedFitness = fitness / ((double) genotypesSpecies.size());

          // Critical section
          criticalLock.lock();
          genotypeFitnessMap.put(genotype, adjustedFitness);

          // Store highest fitness
          if (fitness > highestFitness) {
            highestFitness = fitness;
            fittestGenotype = genotype;
          }
          criticalLock.unlock();
        });
      }

      // Accept no more tasks
      threadPool.shutdown();
      // Wait until thread pool execution is finished
      try {
        threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
      } catch (InterruptedException e) {
        System.err.println("Waiting for thread pool execution to finish, outlasted the universe");
      }
    } else if (bulkEvaluator != null) {
      List<Double> fitnesses = bulkEvaluator.evaluate(currentGeneration);
      // N.B: assuming fitnesses in same order as currentGeneration
      for (int i = 0; i < currentGeneration.size(); i++) {
        NetworkGenotype genotype = currentGeneration.get(i);
        double fitness = fitnesses.get(i);

        Species genotypesSpecies = genotypeSpeciesMap.get(genotype);

        // Adjust fitness by species size to prevent elitism.
        final double adjustedFitness = fitness / ((double) genotypesSpecies.size());

        genotypeFitnessMap.put(genotype, adjustedFitness);

        // Store highest fitness
        if (fitness > highestFitness) {
          highestFitness = fitness;
          fittestGenotype = genotype;
        }
      }
    } else {
      throw new RuntimeException("Both single and bulk evaluators are undefined");
    }

    // Report generation statistics
    if (verbose) {
      System.out.println("Highest fitness: " + getHighestFitness());
      System.out.println("Number of species: " + getNumberOfSpecies());
    }

    // Sort all species
    // Sort all members within each species

    // List of all species ordered by their total (size adjusted (averaged)) fitness (descending)
    final List<Species> sortedSpecies = new ArrayList<>(allSpecies);
    // Map from each species to a sorted List of members, sorted by their individual fitness (descending)
    final Map<Species, List<NetworkGenotype>> sortedSpeciesMembers = new HashMap<>();

    for (Species species : allSpecies) {
      List<NetworkGenotype> sortedMembers = new ArrayList<>(species.getMembers());
      sortedMembers.sort((o1, o2) -> -1 * Double.compare(genotypeFitnessMap.get(o1),
          genotypeFitnessMap.get(o2)));
      sortedSpeciesMembers.put(species, sortedMembers);
    }
    sortedSpecies.sort((species1, species2) -> {
      double totalSpecies1Fitness = species1.getMembers().stream()
          .mapToDouble(genotypeFitnessMap::get)
          .sum();

      double totalSpecies2Fitness = species2.getMembers().stream()
          .mapToDouble(genotypeFitnessMap::get)
          .sum();

      return -1 * Double.compare(totalSpecies1Fitness, totalSpecies2Fitness);
    });

    // Create next generation
    List<NetworkGenotype> nextGeneration = new ArrayList<>(populationSize);

    /*
      Add fittest in each species to next generation
      This ensures that the next generation is at least as good as the current
     */
    for (Species species : allSpecies) {
      nextGeneration.add(sortedSpeciesMembers.get(species).get(0));
    }

    // Breed genotypes to fill population size
    while (nextGeneration.size() < populationSize) {
      // Pick a species at weighted random
      Species chosenSpecies = getRandomSpeciesBiasedByFitness(random, sortedSpecies);

      // Pick two members from the species to be parents
      NetworkGenotype parent1 = getRandomMemberBiasedByFitness(random,
          sortedSpeciesMembers.get(chosenSpecies));
      NetworkGenotype parent2 = getRandomMemberBiasedByFitness(random,
          sortedSpeciesMembers.get(chosenSpecies));

      boolean parent1Fittest = genotypeFitnessMap.get(parent1) > genotypeFitnessMap.get(parent2);

      // Generate child as crossover of parents
      NetworkGenotype child = NetworkGenotype.crossover(
          parent1Fittest ? parent1 : parent2,
          parent1Fittest ? parent2 : parent1,
          random
      );

      // Mutate child
      if (random.nextDouble() < WEIGHT_MUTATION_RATE) {
        child.weightMutation(random);
      }
      if (random.nextDouble() < ADD_CONNECTION_MUTATION_RATE) {
        child.addConnectionMutation(random, innovationGenerator, ADD_CONNECTION_ATTEMPTS);
      }
      if (random.nextDouble() < ADD_NEURON_MUTATION_RATE) {
        child.addNeuronMutation(random, innovationGenerator);
      }

      nextGeneration.add(child);
    }

    // Switch to next generation
    currentGeneration = nextGeneration;
  }

  /**
   * Randomly select a species biased on the species total (size adjusted) fitness
   *
   * @param random        seeded Random object
   * @param sortedSpecies List of all species ordered by their total (size adjusted (averaged))
   *                      fitness (descending)
   * @return selected Species
   */
  private Species getRandomSpeciesBiasedByFitness(Random random,
      List<Species> sortedSpecies) {
    final double target = random.nextDouble();

    double totalOfAllSums = 0;

    final Map<Species, Double> speciesSums = new HashMap<>();
    for (Species species : allSpecies) {
      final double speciesSum = species.getMembers().stream()
          .mapToDouble(genotypeFitnessMap::get)
          .sum();

      speciesSums.put(species, speciesSum);
      totalOfAllSums += speciesSum;
    }

    double acc = 0;
    for (Species species : sortedSpecies) {
      acc += speciesSums.get(species) / totalOfAllSums;
      if (acc > target) {
        return species;
      }
    }

    // Should never fall through to here, but if so return the last element
    return sortedSpecies.get(sortedSpecies.size() - 1);
  }

  /**
   * Randomly select a member biased on the member's fitness
   *
   * @param random        seeded Random object
   * @param sortedMembers List of all members ordered by their individual fitness (descending)
   * @return selected member
   */
  private NetworkGenotype getRandomMemberBiasedByFitness(Random random,
      List<NetworkGenotype> sortedMembers) {
    final double target = random.nextDouble();

    final double total = sortedMembers.stream()
        .mapToDouble(genotypeFitnessMap::get)
        .sum();

    double acc = 0;
    for (NetworkGenotype member : sortedMembers) {
      acc += genotypeFitnessMap.get(member) / total;
      if (acc > target) {
        return member;
      }
    }

    // Should never fall through to here, but if so return the last element
    return sortedMembers.get(sortedMembers.size() - 1);
  }

  /**
   * Resets each species, genotype-fitness and genotype-species maps, highest fitness and fittest
   * genotype. Does NOT empty the current population/generation
   *
   * @param random seeded Random object
   */
  private void reset(Random random) {
    for (Species s : allSpecies) {
      s.reset(random);
    }
    genotypeSpeciesMap.clear();
    genotypeFitnessMap.clear();
    highestFitness = Double.MIN_VALUE;
    fittestGenotype = null;
  }
}
