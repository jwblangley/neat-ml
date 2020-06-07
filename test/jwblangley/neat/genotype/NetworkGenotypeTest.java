package jwblangley.neat.genotype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

public class NetworkGenotypeTest {

  @Test
  public void copiedObjectsAreDeeplyCopied() {
    // Setup
    NeuronGenotype fromNeuron = new NeuronGenotype(NeuronLayer.HIDDEN, 1);
    NeuronGenotype toNeuron = new NeuronGenotype(NeuronLayer.HIDDEN, 2);

    ConnectionGenotype connection = new ConnectionGenotype(
        fromNeuron.getUid(),
        toNeuron.getUid(),
        1,
        0,
        true
    );

    NetworkGenotype network = new NetworkGenotype();
    network.addNeuron(fromNeuron);
    network.addNeuron(toNeuron);
    network.addConnection(connection);

    // Perform copy
    NetworkGenotype copiedNetwork = new NetworkGenotype(network);

    // Get copied objects
    ConnectionGenotype copiedConnection = copiedNetwork.getConnections().get(0);
    NeuronGenotype copiedFromNeuron = copiedNetwork.getNeurons().get(0);
    NeuronGenotype copiedToNeuron;
    if (copiedFromNeuron.getUid() == fromNeuron.getUid()) {
      copiedToNeuron = copiedNetwork.getNeurons().get(1);
    } else {
      copiedFromNeuron = copiedNetwork.getNeurons().get(1);
      copiedToNeuron = copiedNetwork.getNeurons().get(0);
    }

    // Perform checks
    assertNotSame(copiedConnection, connection);
    assertEquals(copiedConnection, connection);

    assertNotSame(copiedFromNeuron, fromNeuron);
    assertEquals(copiedFromNeuron, fromNeuron);

    assertNotSame(copiedToNeuron, toNeuron);
    assertEquals(copiedToNeuron, toNeuron);
  }

}
