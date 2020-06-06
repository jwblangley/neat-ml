package jwblangley.neat.genotype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

public class ConnectionGenotypeTest {

  @Test
  public void testCopiedObjectsDeeplyCopied() {
    NeuronGenotype fromNeuron = new NeuronGenotype(NeuronLayer.HIDDEN, 1);
    NeuronGenotype toNeuron = new NeuronGenotype(NeuronLayer.HIDDEN, 2);

    ConnectionGenotype connection = new ConnectionGenotype(
        fromNeuron,
        toNeuron,
        1,
        0,
        true
    );

    ConnectionGenotype copiedConnection = new ConnectionGenotype(connection);

    assertNotSame(copiedConnection, connection);

    assertNotSame(fromNeuron, copiedConnection.getNeuronFrom());
    assertEquals(fromNeuron, copiedConnection.getNeuronFrom());

    assertNotSame(toNeuron, copiedConnection.getNeuronTo());
    assertEquals(toNeuron, copiedConnection.getNeuronTo());
  }

}
