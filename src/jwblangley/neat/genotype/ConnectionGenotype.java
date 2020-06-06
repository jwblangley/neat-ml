package jwblangley.neat.genotype;

public class ConnectionGenotype {

  private final NeuronGenotype neuronFrom;
  private final NeuronGenotype neuronTo;
  private final int innovationMarker;

  private double weight;
  private boolean enabled;

  public ConnectionGenotype(
      NeuronGenotype neuronFrom,
      NeuronGenotype neuronTo,
      int innovationMarker,
      double weight) {

    this.neuronFrom = neuronFrom;
    this.neuronTo = neuronTo;
    this.innovationMarker = innovationMarker;
    this.weight = weight;
    this.enabled = true;
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
