package jwblangley.neat.genotype;

import java.util.Objects;
import jwblangley.neat.proto.Genotypes;
import jwblangley.neat.proto.ProtoEquivalent;

/**
 * Genotype representing a connection between two neurons
 */
public class ConnectionGenotype implements ProtoEquivalent {

  private final int neuronFrom;
  private final int neuronTo;
  private final int innovationMarker;

  private double weight;
  private boolean enabled;

  /**
   * Construct a new ConnectionGenotype
   *
   * @param neuronFrom       uid of the neuron the connection is from
   * @param neuronTo         uid of the neuron the connection is to
   * @param innovationMarker identifier for the creation of this connection as a result of an
   *                         innovation
   * @param weight           initial weight for this connection
   * @param enabled          whether the new connection is initially enabled
   */
  public ConnectionGenotype(
      int neuronFrom,
      int neuronTo,
      int innovationMarker,
      double weight,
      boolean enabled) {

    this.neuronFrom = neuronFrom;
    this.neuronTo = neuronTo;
    this.innovationMarker = innovationMarker;
    this.weight = weight;
    this.enabled = enabled;
  }

  /**
   * Copy constructor: creates a new ConnectionGenotype object equal to toCopy
   *
   * @param toCopy ConnectionGenotype to be copied
   */
  public ConnectionGenotype(ConnectionGenotype toCopy) {
    this(
        toCopy.neuronFrom,
        toCopy.neuronTo,
        toCopy.innovationMarker,
        toCopy.weight,
        toCopy.enabled
    );
  }

  /**
   * Construct a new ConnectionGenotype from a protobuf object
   *
   * @param protoConnection the protobuf object
   */
  public ConnectionGenotype(Genotypes.ConnectionGenotype protoConnection) {
    this(
        protoConnection.getNeuronUidFrom(),
        protoConnection.getNeuronUidTo(),
        protoConnection.getInnovationMarker(),
        protoConnection.getWeight(),
        protoConnection.getEnabled()
    );
  }

  /**
   * Create a protobuf object of this Connection
   *
   * @return the protobuf object
   */
  @Override
  public Genotypes.ConnectionGenotype toProto() {
    return Genotypes.ConnectionGenotype.newBuilder()
        .setNeuronUidFrom(neuronFrom)
        .setNeuronUidTo(neuronTo)
        .setInnovationMarker(innovationMarker)
        .setWeight(weight)
        .setEnabled(enabled)
        .build();
  }

  /**
   * @return uid of the neuron the connection is from
   */
  public int getNeuronFrom() {
    return neuronFrom;
  }

  /**
   * @return uid of the neuron the connection is to
   */
  public int getNeuronTo() {
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

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    ConnectionGenotype that = (ConnectionGenotype) other;

    boolean equal = this.neuronFrom == that.neuronFrom &&
        this.neuronTo == that.neuronTo;

    return equal;
  }

  @Override
  public int hashCode() {
    return Objects.hash(neuronFrom, neuronTo);
  }
}
