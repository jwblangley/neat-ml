package jwblangley.neat.genotype;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import jwblangley.neat.evolution.InnovationGenerator;
import jwblangley.neat.proto.Genotypes;
import jwblangley.neat.util.DisjointExcess;
import jwblangley.neat.util.ImmutableHomogeneousPair;

/**
 * Genotype representing a neural network
 */
public class NetworkGenotype {

  /**
   * Constant for compatibility distance calculation weighting the relative importance of excess
   * genes
   */
  public static final double DIST_C1 = 1d;
  /**
   * Constant for compatibility distance calculation weighting the relative importance of disjoint
   * genes
   */
  public static final double DIST_C2 = 1d;
  /**
   * Constant for compatibility distance calculation weighting the relative importance of the
   * average difference in weights of matching genes
   */
  public static final double DIST_C3 = 0.4d;

  /**
   * Probability of perturbing a weight rather than assigning a new random weight during weight
   * mutation
   */
  private static final double W_PROB_PERTURB = 0.9;

  private final List<NeuronGenotype> neurons;
  private final List<ConnectionGenotype> connections;

  /**
   * Construct a new (blank) NetworkGenotype
   */
  public NetworkGenotype() {
    neurons = new ArrayList<>();
    connections = new ArrayList<>();
  }

  /**
   * Copy constructor: creates a new, deeply copied, NetworkGenotype object equal to toCopy
   *
   * @param toCopy NetworkGenotype to be copied
   */
  public NetworkGenotype(NetworkGenotype toCopy) {
    neurons = toCopy.neurons.stream()
        .map(NeuronGenotype::new)
        .collect(Collectors.toList());
    connections = toCopy.connections.stream()
        .map(ConnectionGenotype::new)
        .collect(Collectors.toList());
  }

  /**
   * Create a new NeuronGenotype from a protobuf object
   *
   * @param protoNetwork the protobuf object
   */
  public NetworkGenotype(Genotypes.NetworkGenotype protoNetwork) {
    neurons = protoNetwork.getNeuronsList().stream()
        .map(NeuronGenotype::new)
        .collect(Collectors.toList());
    connections = protoNetwork.getConnectionsList().stream()
        .map(ConnectionGenotype::new)
        .collect(Collectors.toList());
  }

  /**
   * Create a protobuf object of this Network
   *
   * @return the protobuf object
   */
  public Genotypes.NetworkGenotype toProto() {
    List<Genotypes.NeuronGenotype> protoNeurons = neurons.stream()
        .map(NeuronGenotype::toProto)
        .collect(Collectors.toList());

    List<Genotypes.ConnectionGenotype> protoConnections = connections.stream()
        .map(ConnectionGenotype::toProto)
        .collect(Collectors.toList());

    return Genotypes.NetworkGenotype.newBuilder()
        .addAllNeurons(protoNeurons)
        .addAllConnections(protoConnections)
        .build();
  }

  public List<NeuronGenotype> getNeurons() {
    return neurons;
  }

  public List<ConnectionGenotype> getConnections() {
    return connections;
  }

  public void addNeuron(NeuronGenotype neuron) {
    neurons.add(neuron);
  }

  public void addConnection(ConnectionGenotype connection) {
    connections.add(connection);
  }

  /**
   * Gets the connection in this network that has the provided innovation marker if it exists
   *
   * @param innovationMarker given innovation marker
   * @return Optional-wrapped ConnectionGenotype with the given innovation marker if it exists,
   * Optional.empty() otherwise
   */
  public Optional<ConnectionGenotype> getConnectionByInnovationMarker(int innovationMarker) {
    assert connections.stream()
        .filter(con -> con.getInnovationMarker() == innovationMarker).count() <= 1
        : "There should be a maximum of 1 connection per innovation marker";

    return connections.stream()
        .filter(con -> con.getInnovationMarker() == innovationMarker)
        .findAny();
  }

  /**
   * @param random seeded Random object
   * @return a random NeuronGenotype from those in this network
   */
  private NeuronGenotype getRandomNeuron(Random random) {
    return neurons.get(random.nextInt(neurons.size()));
  }

  /**
   * @param random seeded Random object
   * @return a random ConnectionGenotype from those in this network
   */
  private ConnectionGenotype getRandomConnection(Random random) {
    return connections.get(random.nextInt(connections.size()));
  }

  /**
   * @param random seeded Random object
   * @return random double between -1 and 1
   */
  private static double generateRandomWeight(Random random) {
    return (random.nextDouble() * 2d) - 1d;
  }

  /**
   * Create a new connection between two randomly chosen existing neurons without creating cycles
   *
   * @param random      seeded Random object
   * @param innovation  innovation marker generator
   * @param maxAttempts maximum number of attempts at selecting two random compatible neurons to
   *                    connect
   * @return whether the connection was successful
   */
  public boolean addConnectionMutation(
      Random random, InnovationGenerator innovation, int maxAttempts) {

    int attempts = 0;
    attempt:
    while (attempts < maxAttempts) {
      attempts++;

      // Pick two random neurons
      final NeuronGenotype firstNeuron = getRandomNeuron(random);
      final NeuronGenotype secondNeuron = getRandomNeuron(random);

      // Prevent invalid connections
      if ((firstNeuron.getLayer() == NeuronLayer.INPUT
          && secondNeuron.getLayer() == NeuronLayer.INPUT)
          || (firstNeuron.getLayer() == NeuronLayer.OUTPUT
          && secondNeuron.getLayer() == NeuronLayer.OUTPUT)
      ) {
        continue;
      }

      // Prevent circular connections
      if (firstNeuron.equals(secondNeuron)
          || circularIfConnected(firstNeuron, secondNeuron)) {
        continue;
      }

      final boolean reversed = secondNeuron.getLayer().compareTo(firstNeuron.getLayer()) < 0;

      // Create new connection
      ConnectionGenotype connection = new ConnectionGenotype(
          reversed ? secondNeuron.getUid() : firstNeuron.getUid(),
          reversed ? firstNeuron.getUid() : secondNeuron.getUid(),
          innovation.next(),
          generateRandomWeight(random),
          true
      );

      // If connection already exists, re-enable if disabled, prevent overriding otherwise
      for (ConnectionGenotype existingConnection : connections) {
        if (existingConnection.equals(connection)) {
          if (!existingConnection.isEnabled()) {
            existingConnection.enable();
            return true;
          } else {
            continue attempt;
          }
        }
      }

      return connections.add(connection);
    }

    return false;
  }

  /**
   * Splits a randomly chosen connection into two new connections, with a new neuron created between
   * the two. The old connection is disabled. The first new connection has a weight of 1 and the
   * second inherits the weight of the old connection
   *
   * @param random     seeded Random object
   * @param innovation innovation marker generator
   */
  public void addNeuronMutation(Random random, InnovationGenerator innovation) {
    ConnectionGenotype originalConnection = getRandomConnection(random);
    originalConnection.disable();

    // Create a new neuron with the next available ID
    NeuronGenotype newNeuron = new NeuronGenotype(NeuronLayer.HIDDEN);
    neurons.add(newNeuron);

    // Create two new connections in place of the original connection
    ConnectionGenotype fromToNew = new ConnectionGenotype(
        originalConnection.getNeuronFrom(),
        newNeuron.getUid(),
        innovation.next(),
        1d,
        true
    );
    ConnectionGenotype newToTo = new ConnectionGenotype(
        newNeuron.getUid(),
        originalConnection.getNeuronTo(),
        innovation.next(),
        originalConnection.getWeight(),
        true
    );

    connections.add(fromToNew);
    connections.add(newToTo);
  }

  /**
   * Mutates the weights of every connection by perturbation or by assigning a new random weight
   *
   * @param random seeded Random object
   */
  public void weightMutation(Random random) {
    for (ConnectionGenotype connection : connections) {
      if (random.nextDouble() < W_PROB_PERTURB) {
        connection.setWeight(connection.getWeight() * 2 * generateRandomWeight(random));
      } else {
        connection.setWeight(generateRandomWeight(random) * 2);
      }
    }
  }

  /**
   * Genes between the two parents are either matching (same innovation number), disjoint (doesn't
   * have innovation number) or excess (outside the range of the the other parent's genes). When
   * creating the child, matching genes (connections) are chosen at random from either parent. All
   * excess or disjoint genes are always taken from the more fit parent only.
   *
   * @param fittestParent the 'fitter' parent
   * @param secondParent  the second parent
   * @param random        seeded Random object
   * @return resulting genotype
   */
  public static NetworkGenotype crossover(NetworkGenotype fittestParent,
      NetworkGenotype secondParent, Random random) {

    NetworkGenotype childNetwork = new NetworkGenotype();

    /*
    Since we take matching from either and excess or disjoint always from fittest,
    we can take a copy of all the fittest parent's neurons to stay connected
     */

    // Add all neurons from fittest parent to child
    for (NeuronGenotype parentNeuron : fittestParent.getNeurons()) {
      childNetwork.addNeuron(new NeuronGenotype(parentNeuron));
    }

    // Add genes (connections) from parents
    for (ConnectionGenotype fittestParentsGene : fittestParent.getConnections()) {
      // Get matching gene (if it exists) from second parent
      Optional<ConnectionGenotype> secondParentsGene
          = secondParent.getConnectionByInnovationMarker(fittestParentsGene.getInnovationMarker());

      ConnectionGenotype toInherit;
      if (secondParentsGene.isPresent()) {
        // Matching genes
        // Inherit randomly from either parent
        toInherit = random.nextBoolean()
            ? fittestParentsGene : secondParentsGene.get();
      } else {
        // Disjoint or excess gene
        // Always copy from fitter parent
        toInherit = fittestParentsGene;
      }
      ConnectionGenotype childGene = new ConnectionGenotype(toInherit);
      childNetwork.addConnection(childGene);
    }

    return childNetwork;
  }

  /**
   * Calculate how 'similar' two network genooypes are with the calculation: delta = (c_1 * E / N) +
   * (c_2 * D / N) + c_3 * W_bar
   *
   * @param first
   * @param second
   * @return compatibility distance between the two - greater -> more different
   */
  public static double compatibilityDistance(NetworkGenotype first, NetworkGenotype second) {
    List<Integer> firstParentInnovations = first.getConnections().stream()
        .map(ConnectionGenotype::getInnovationMarker)
        .collect(Collectors.toList());
    List<Integer> secondParentInnovations = second.getConnections().stream()
        .map(ConnectionGenotype::getInnovationMarker)
        .collect(Collectors.toList());

    ImmutableHomogeneousPair<Integer> disjointExcesses
        = DisjointExcess.calculate(firstParentInnovations, secondParentInnovations);
    final int disjoints = disjointExcesses.getFirst();
    final int excesses = disjointExcesses.getSecond();

    final double avgWeightDiffOfMatching = averageWeightDifferenceOfMatchingGenes(first, second);

    int numGenes = Math.max(first.getConnections().size(), second.getConnections().size());

    return DIST_C1 * excesses / ((double) numGenes)
        + DIST_C2 * disjoints / ((double) numGenes)
        + DIST_C3 * avgWeightDiffOfMatching;
  }

  private static double averageWeightDifferenceOfMatchingGenes(NetworkGenotype first,
      NetworkGenotype second) {

    double totalDifference = 0;
    int numMatch = 0;

    for (ConnectionGenotype parent1ConnectionGenotype : first.getConnections()) {
      Optional<ConnectionGenotype> parent2Connection
          = second.getConnectionByInnovationMarker(parent1ConnectionGenotype.getInnovationMarker());

      // Check if second also contains the same gene
      if (parent2Connection.isPresent()) {
        // Matching gene
        numMatch++;
        totalDifference += Math.abs(
            parent1ConnectionGenotype.getWeight() - parent2Connection.get().getWeight());
      }
    }
    return totalDifference / ((double) numMatch);
  }


  /**
   * Checks whether adding a connection will cause cycles
   *
   * @param additionalFrom the uid of the neuron which the additional connection would be from
   * @param additionalTo   the uid of the neuron which the additional connection would be to
   * @return whether the additional connection would create cycles
   */
  public boolean circularIfConnected(NeuronGenotype additionalFrom, NeuronGenotype additionalTo) {
    List<NeuronGenotype> inputs = neurons.stream()
        .filter(n -> n.getLayer() == NeuronLayer.INPUT)
        .collect(Collectors.toList());

    for (NeuronGenotype input : inputs) {
      if (dfsCheck(input.getUid(), new Stack<>(), new HashSet<>(), additionalFrom.getUid(),
          additionalTo.getUid())) {
        return true;
      }
    }

    return false;
  }

  private boolean dfsCheck(int currentUid, Stack<Integer> path,
      Set<Integer> visited, int additionalFrom, int additionalTo) {
    // Add current to path and visited
    path.push(currentUid);
    visited.add(currentUid);

    // Check additional connection
    if (additionalFrom == currentUid) {
      // Check cycle
      if (path.contains(additionalTo)) {
        return true;
      }
      // Visit if not visited
      if (!visited.contains(additionalTo)) {
        if (dfsCheck(additionalTo, path, visited, additionalFrom, additionalTo)) {
          return true;
        }
      }
    }

    // Check existing connections
    for (ConnectionGenotype connection : connections) {
      if (connection.getNeuronFrom() == currentUid) {
        // Check cycle
        if (path.contains(connection.getNeuronTo())) {
          return true;
        }
        // Visit if not visited
        if (!visited.contains(connection.getNeuronTo())) {
          if (dfsCheck(connection.getNeuronTo(), path, visited, additionalFrom, additionalTo)) {
            return true;
          }
        }
      }
    }

    // Exploration of branch finished: remove from path
    path.pop();
    return false;
  }
}
