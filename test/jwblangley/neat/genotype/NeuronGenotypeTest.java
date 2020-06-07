package jwblangley.neat.genotype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

public class NeuronGenotypeTest {

  @Test
  public void testDifferentObjectsAreNotEqual() {
    NeuronGenotype firstNeuron = new NeuronGenotype(NeuronLayer.HIDDEN, 1);
    NeuronGenotype secondNeuron = new NeuronGenotype(NeuronLayer.HIDDEN, 2);

    assertNotEquals(firstNeuron, secondNeuron);
  }

  @Test
  public void testDifferentIdenticalObjectsAreEqual() {
    NeuronGenotype firstNeuron = new NeuronGenotype(NeuronLayer.HIDDEN, 1);
    NeuronGenotype secondNeuron = new NeuronGenotype(NeuronLayer.HIDDEN, 1);

    assertNotSame(firstNeuron, secondNeuron);
    assertEquals(firstNeuron, secondNeuron);
  }

  @Test
  public void testCopiedObjectsEqual() {
    NeuronGenotype firstNeuron = new NeuronGenotype(NeuronLayer.HIDDEN, 1);
    NeuronGenotype secondNeuron = new NeuronGenotype(firstNeuron);

    assertNotSame(firstNeuron, secondNeuron);
    assertEquals(firstNeuron, secondNeuron);
  }

}
