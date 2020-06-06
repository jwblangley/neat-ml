package jwblangley.neat.genotype;

/**
 * Genotype representing a connection between two neurons
 */
public class ConnectionGenotype {

  private final NeuronGenotype neuronFrom;
  private final NeuronGenotype neuronTo;
  private final int innovationMarker;

  private double weight;
  private boolean enabled;

  /**
   * Construct a new ConnectionGenotype
   *
   * @param neuronFrom       neuron the connection is from
   * @param neuronTo         neuron the connection is to
   * @param innovationMarker identifier for the creation of this
   *                         connection as a result of an innovation
   * @param weight           initial weight for this connection
   * @param enabled          whether the new connection is initially enabled
   */
  public ConnectionGenotype(
      NeuronGenotype neuronFrom,
      NeuronGenotype neuronTo,
      int innovationMarker,
      double weight,
      boolean enabled) {

    this.neuronFrom = neuronFrom;
    this.neuronTo = neuronTo;
    this.innovationMarker = innovationMarker;
    this.weight = weight;
    this.enabled = enabled;
  }

  public ConnectionGenotype(ConnectionGenotype toCopy) {
    this(
        new NeuronGenotype(toCopy.neuronFrom),
        new NeuronGenotype(toCopy.neuronTo),
        toCopy.innovationMarker,
        toCopy.weight,
        toCopy.enabled
    );
  }

  public NeuronGenotype getNeuronFrom() {
    return neuronFrom;
  }

  public NeuronGenotype getNeuronTo() {
    return neuronTo;
  }

  public int getInnovationMarker() {
    return innovationMarker;
  }

  public double getWeight() {
    return weight;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setWeight(double weight) {
    this.weight = weight;
  }

  public void enable() {
    this.enabled = true;
  }

  public void disable() {
    this.enabled = false;
  }


}
