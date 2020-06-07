package jwblangley.neat.genotype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Optional;
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

  @Test
  public void getConnectionReturnsEmptyWithNoConnections() {
    NetworkGenotype network = new NetworkGenotype();

    assertEquals(Optional.empty(), network.getConnectionByInnovationMarker(1));
  }

  @Test
  public void getConnectionReturnsEmptyWithWrongConnections() {
    NetworkGenotype network = new NetworkGenotype();
    network.addConnection(new ConnectionGenotype(1, 2, 1, 0, true));
    network.addConnection(new ConnectionGenotype(1, 2, 2, 0, true));

    assertEquals(Optional.empty(), network.getConnectionByInnovationMarker(3));
  }

  @Test
  public void getConnectionReturnsWithCorrectConnections() {
    NetworkGenotype network = new NetworkGenotype();

    ConnectionGenotype target = new ConnectionGenotype(1, 2, 1, 0, true);
    network.addConnection(target);
    ConnectionGenotype falseTarget = new ConnectionGenotype(1, 2, 2, 0, true);
    network.addConnection(falseTarget);

    Optional<ConnectionGenotype> result
        = network.getConnectionByInnovationMarker(target.getInnovationMarker());

    assertTrue(result.isPresent());
    assertSame(target, result.get());
  }

}
