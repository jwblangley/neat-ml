package jwblangley.neat.genotype;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

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
   *
   * @param random seeded Random object
   * @return a random NeuronGenotype from those in this network
   */
  private NeuronGenotype getRandomNeuron(Random random) {
    return neurons.get(random.nextInt(neurons.size()));
  }

  /**
   *
   * @param random seeded Random object
   * @return a random ConnectionGenotype from those in this network
   */
  private ConnectionGenotype getRandomConnection(Random random) {
    return connections.get(random.nextInt(connections.size()));
  }

  /**
   *
   * @param random seeded Random object
   * @return random double between -1 and 1
   */
  private static double generateRandomWeight(Random random) {
    return (random.nextDouble() * 2d) - 1d;
  }
}
