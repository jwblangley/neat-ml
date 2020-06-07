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

/**
 * Genotype representing a neural network
 */
public class NetworkGenotype {

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
    // N.B: NetworkGenotype and ConnectionGenotype copy constructors are deep copies
    neurons = toCopy.neurons.stream()
        .map(NeuronGenotype::new)
        .collect(Collectors.toList());
    connections = toCopy.connections.stream()
        .map(ConnectionGenotype::new)
        .collect(Collectors.toList());
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
   * Create a new connection between two randomly chosen existing neurons
   * without creating cycles
   * @param random seeded Random object
   * @param innovation innovation marker generator
   * @param maxAttempts maximum number of attempts at selecting two random compatible
   *                    neurons to connect
   * @return whether the connection was successful
   */
  public boolean addConnectionMutation(
      Random random, InnovationGenerator innovation, int maxAttempts) {

    int attempts = 0;
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

      // Prevent overriding connections
      if (connections.contains(connection)) {
        continue;
      }

      return connections.add(connection);
    }

    return false;
  }


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
