package jwblangley.neat.evolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import jwblangley.neat.genotype.NetworkGenotype;

public class Evolution {

  private static final double COMPATIBILITY_DISTANCE_THRESHOLD = 10d;
  public static final double WEIGHT_MUTATION_RATE = 0.5f;
  public static final double ADD_CONNECTION_MUTATION_RATE = 0.1f;
  public static final double ADD_NEURON_MUTATION_RATE = 0.1f;
  public static final int ADD_CONNECTION_ATTEMPTS = 10;

  private final Evaluator evaluator;
  private final int populationSize;
  private final InnovationGenerator innovationGenerator;

  private List<Species> allSpecies;
  private List<NetworkGenotype> currentGeneration;

  private double highestFitness;
  private NetworkGenotype fittestGenotype;

  private Map<NetworkGenotype, Species> genotypeSpeciesMap;
  /**
   * Map from network genotypes to their adjusted fitness
   */
  private Map<NetworkGenotype, Double> genotypeFitnessMap;


  public Evolution(int populationSize, NetworkGenotype startingGenotype,
      InnovationGenerator innovationGenerator, Evaluator evaluator) {

    this.populationSize = populationSize;
    this.innovationGenerator = innovationGenerator;
    this.evaluator = evaluator;

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
   * Sort a generation into species,
   * evaluate each member of the generation,
   * kill off weaker members,
   * generate child members from surviving members
   * repopulate generation
   *
   * @param random seeded Random object
   */
  public void evolve(Random random) {
    // Reset all stats before next generation evaluation
    reset(random);

    // Place genotypes into species
    for (NetworkGenotype genotype : currentGeneration) {
      boolean foundSpecies = false;

      for (Species species : allSpecies) {
        if (NetworkGenotype.compatibilityDistance(genotype, species.getMascot())
            < COMPATIBILITY_DISTANCE_THRESHOLD) {
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

    // Evaluate each genotype and assign its fitness
    for (NetworkGenotype genotype : currentGeneration) {
      Species genotypesSpecies = genotypeSpeciesMap.get(genotype);

      // Simulate the genotype and evaluate fitness
      final double fitness = evaluator.evaluate(genotype);

      // Adjust fitness by species size to prevent elitism
      final double adjustedFitness = fitness / ((double) genotypesSpecies.size());

      genotypeFitnessMap.put(genotype, adjustedFitness);

      // Store highest fitness
      if (fitness > highestFitness) {
        highestFitness = fitness;
        fittestGenotype = genotype;
      }
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
