package jwblangley.neat.genotype;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import jwblangley.neat.proto.Genotypes;

/**
 * Genotype representing a neuron
 */
public class NeuronGenotype {

  private final static AtomicInteger uidGenerator = new AtomicInteger();

  private final NeuronLayer layer;
  private final int uid;

  /**
   * Constructs a new NeuronGenotype
   *
   * @param layer the layer in which this neuron resides
   * @param uid   unique identifier for this neuron
   */
  public NeuronGenotype(NeuronLayer layer, int uid) {
    this.layer = layer;
    this.uid = uid;
  }

  public NeuronGenotype(NeuronLayer layer) {
    this(layer, uidGenerator.getAndIncrement());
  }


  /**
   * Copy constructor: creates a new NeuronGenotype object equal to toCopy
   *
   * @param toCopy NeuronGenotype to be copied
   */
  public NeuronGenotype(NeuronGenotype toCopy) {
    this(toCopy.layer, toCopy.uid);
  }

  /**
   * Construct a new NeuronGenotype from a protobuf object
   *
   * @param protoNeuron the protobuf object
   */
  public NeuronGenotype(Genotypes.NeuronGenotype protoNeuron) {
    this(protoToLayer(protoNeuron.getLayer()), protoNeuron.getUid());
  }

  /**
   * Create a protobuf object of this Neuron
   *
   * @return the protobuf object
   */
  public Genotypes.NeuronGenotype toProto() {
    return Genotypes.NeuronGenotype.newBuilder()
        .setUid(uid)
        .setLayer(layerToProto(layer))
        .build();
  }

  // These could be derived from enum values, but writing explicitly for implementation decoupling
  private static Genotypes.NeuronGenotype.NeuronLayer layerToProto(NeuronLayer layer) {
    if (layer == NeuronLayer.INPUT) {
      return Genotypes.NeuronGenotype.NeuronLayer.INPUT;
    }
    if (layer == NeuronLayer.HIDDEN) {
      return Genotypes.NeuronGenotype.NeuronLayer.HIDDEN;
    }
    if (layer == NeuronLayer.OUTPUT) {
      return Genotypes.NeuronGenotype.NeuronLayer.OUTPUT;
    }
    throw new IllegalArgumentException("Invalid neuron layer");
  }

  private static NeuronLayer protoToLayer(Genotypes.NeuronGenotype.NeuronLayer layer) {
    if (layer == Genotypes.NeuronGenotype.NeuronLayer.INPUT) {
      return NeuronLayer.INPUT;
    }
    if (layer == Genotypes.NeuronGenotype.NeuronLayer.HIDDEN) {
      return NeuronLayer.HIDDEN;
    }
    if (layer == Genotypes.NeuronGenotype.NeuronLayer.OUTPUT) {
      return NeuronLayer.OUTPUT;
    }
    throw new IllegalArgumentException("Invalid neuron layer");
  }

  public NeuronLayer getLayer() {
    return layer;
  }

  public int getUid() {
    return uid;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    NeuronGenotype that = (NeuronGenotype) other;

    assert this.uid != that.uid || this.layer == that.layer
        : "equal neurons should have the same type";

    return this.uid == that.uid;
  }

  @Override
  public int hashCode() {
    return Objects.hash(layer, uid);
  }
}
