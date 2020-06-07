package jwblangley.neat.genotype;

import java.util.ArrayList;
import java.util.List;
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
}
