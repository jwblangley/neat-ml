package jwblangley.neat.genotype;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

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
