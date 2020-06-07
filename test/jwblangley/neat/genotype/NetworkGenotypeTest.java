package jwblangley.neat.genotype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Optional;
import org.junit.Test;

public class NetworkGenotypeTest {

  private final static double DELTA = 0.00001;

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
    assertEquals(target, result.get());
  }

  @Test
  public void straightNetworkIsNotCircular() {
    NetworkGenotype network = new NetworkGenotype();
    NeuronGenotype input = new NeuronGenotype(NeuronLayer.INPUT, 0);
    network.addNeuron(input);
    NeuronGenotype hidden = new NeuronGenotype(NeuronLayer.HIDDEN, 1);
    network.addNeuron(hidden);
    NeuronGenotype output = new NeuronGenotype(NeuronLayer.OUTPUT, 2);
    network.addNeuron(output);

    network.addConnection(
        new ConnectionGenotype(input.getUid(), hidden.getUid(), 0, 0, true));

    assertFalse(network.circularIfConnected(hidden, output));
  }

  @Test
  public void forkedNetworkIsNotCircular() {
    NetworkGenotype network = new NetworkGenotype();
    NeuronGenotype input = new NeuronGenotype(NeuronLayer.INPUT, 0);
    network.addNeuron(input);
    NeuronGenotype hidden1 = new NeuronGenotype(NeuronLayer.HIDDEN, 1);
    network.addNeuron(hidden1);
    NeuronGenotype hidden2 = new NeuronGenotype(NeuronLayer.HIDDEN, 2);
    network.addNeuron(hidden2);
    NeuronGenotype hidden3 = new NeuronGenotype(NeuronLayer.HIDDEN, 3);
    network.addNeuron(hidden3);
    NeuronGenotype output = new NeuronGenotype(NeuronLayer.OUTPUT, 4);
    network.addNeuron(output);

    network.addConnection(
        new ConnectionGenotype(input.getUid(), hidden1.getUid(), 0, 0, true));
    network.addConnection(
        new ConnectionGenotype(input.getUid(), hidden2.getUid(), 1, 0, true));
    network.addConnection(
        new ConnectionGenotype(hidden1.getUid(), hidden3.getUid(), 2, 0, true));
    network.addConnection(
        new ConnectionGenotype(hidden2.getUid(), hidden3.getUid(), 3, 0, true));

    assertFalse(network.circularIfConnected(hidden3, output));
  }

  @Test
  public void simpleCircularNetworkIsCircular() {
    NetworkGenotype network = new NetworkGenotype();
    NeuronGenotype input = new NeuronGenotype(NeuronLayer.INPUT, 0);
    network.addNeuron(input);
    NeuronGenotype hidden1 = new NeuronGenotype(NeuronLayer.HIDDEN, 1);
    network.addNeuron(hidden1);
    NeuronGenotype hidden2 = new NeuronGenotype(NeuronLayer.HIDDEN, 2);
    network.addNeuron(hidden2);
    NeuronGenotype output = new NeuronGenotype(NeuronLayer.OUTPUT, 3);
    network.addNeuron(output);

    network.addConnection(
        new ConnectionGenotype(input.getUid(), hidden1.getUid(), 0, 0, true));
    network.addConnection(
        new ConnectionGenotype(hidden1.getUid(), hidden2.getUid(), 1, 0, true));
    network.addConnection(
        new ConnectionGenotype(hidden2.getUid(), output.getUid(), 2, 0, true));

    assertTrue(network.circularIfConnected(hidden2, hidden1));
  }

  @Test
  public void largerCircularNetworkIsCircular() {
    NetworkGenotype network = new NetworkGenotype();
    NeuronGenotype input = new NeuronGenotype(NeuronLayer.INPUT, 0);
    network.addNeuron(input);
    NeuronGenotype hidden1 = new NeuronGenotype(NeuronLayer.HIDDEN, 1);
    network.addNeuron(hidden1);
    NeuronGenotype hidden2 = new NeuronGenotype(NeuronLayer.HIDDEN, 2);
    network.addNeuron(hidden2);
    NeuronGenotype hidden3 = new NeuronGenotype(NeuronLayer.HIDDEN, 3);
    network.addNeuron(hidden3);
    NeuronGenotype hidden4 = new NeuronGenotype(NeuronLayer.HIDDEN, 4);
    network.addNeuron(hidden4);
    NeuronGenotype hidden5 = new NeuronGenotype(NeuronLayer.HIDDEN, 5);
    network.addNeuron(hidden5);
    NeuronGenotype output = new NeuronGenotype(NeuronLayer.OUTPUT, 6);
    network.addNeuron(output);

    network.addConnection(
        new ConnectionGenotype(input.getUid(), hidden1.getUid(), 0, 0, true));
    network.addConnection(
        new ConnectionGenotype(hidden1.getUid(), hidden2.getUid(), 0, 0, true));
    network.addConnection(
        new ConnectionGenotype(hidden2.getUid(), hidden3.getUid(), 0, 0, true));
    network.addConnection(
        new ConnectionGenotype(hidden3.getUid(), hidden4.getUid(), 0, 0, true));
    network.addConnection(
        new ConnectionGenotype(hidden4.getUid(), hidden5.getUid(), 0, 0, true));
    network.addConnection(
        new ConnectionGenotype(hidden5.getUid(), hidden1.getUid(), 0, 0, true));

    assertTrue(network.circularIfConnected(hidden3, output));
  }

  @Test
  public void multipleInputStraightNetworkIsNotCircular() {
    NetworkGenotype network = new NetworkGenotype();
    NeuronGenotype input1 = new NeuronGenotype(NeuronLayer.INPUT, 0);
    network.addNeuron(input1);
    NeuronGenotype input2 = new NeuronGenotype(NeuronLayer.HIDDEN, 1);
    network.addNeuron(input2);
    NeuronGenotype hidden1 = new NeuronGenotype(NeuronLayer.HIDDEN, 2);
    network.addNeuron(hidden1);
    NeuronGenotype hidden2 = new NeuronGenotype(NeuronLayer.HIDDEN, 3);
    network.addNeuron(hidden2);
    NeuronGenotype hidden3 = new NeuronGenotype(NeuronLayer.HIDDEN, 4);
    network.addNeuron(hidden3);
    NeuronGenotype hidden4 = new NeuronGenotype(NeuronLayer.HIDDEN, 5);
    network.addNeuron(hidden4);
    NeuronGenotype output = new NeuronGenotype(NeuronLayer.OUTPUT, 6);
    network.addNeuron(output);

    network.addConnection(
        new ConnectionGenotype(input1.getUid(), hidden1.getUid(), 0, 0, true));
    network.addConnection(
        new ConnectionGenotype(hidden1.getUid(), hidden2.getUid(), 0, 0, true));
    network.addConnection(
        new ConnectionGenotype(hidden2.getUid(), hidden3.getUid(), 0, 0, true));
    network.addConnection(
        new ConnectionGenotype(hidden3.getUid(), output.getUid(), 0, 0, true));
    network.addConnection(
        new ConnectionGenotype(input2.getUid(), hidden4.getUid(), 0, 0, true));

    assertFalse(network.circularIfConnected(hidden4, output));
  }

  @Test
  public void multipleInputCircularNetworkIsCircular() {
    NetworkGenotype network = new NetworkGenotype();
    NeuronGenotype input1 = new NeuronGenotype(NeuronLayer.INPUT, 0);
    network.addNeuron(input1);
    NeuronGenotype input2 = new NeuronGenotype(NeuronLayer.HIDDEN, 1);
    network.addNeuron(input2);
    NeuronGenotype hidden1 = new NeuronGenotype(NeuronLayer.HIDDEN, 2);
    network.addNeuron(hidden1);
    NeuronGenotype hidden2 = new NeuronGenotype(NeuronLayer.HIDDEN, 3);
    network.addNeuron(hidden2);
    NeuronGenotype hidden3 = new NeuronGenotype(NeuronLayer.HIDDEN, 4);
    network.addNeuron(hidden3);
    NeuronGenotype hidden4 = new NeuronGenotype(NeuronLayer.HIDDEN, 5);
    network.addNeuron(hidden4);
    NeuronGenotype output = new NeuronGenotype(NeuronLayer.OUTPUT, 6);
    network.addNeuron(output);

    network.addConnection(
        new ConnectionGenotype(input1.getUid(), hidden1.getUid(), 0, 0, true));
    network.addConnection(
        new ConnectionGenotype(hidden1.getUid(), hidden2.getUid(), 0, 0, true));
    network.addConnection(
        new ConnectionGenotype(hidden2.getUid(), hidden3.getUid(), 0, 0, true));
    network.addConnection(
        new ConnectionGenotype(hidden3.getUid(), hidden1.getUid(), 0, 0, true));
    network.addConnection(
        new ConnectionGenotype(hidden3.getUid(), output.getUid(), 0, 0, true));
    network.addConnection(
        new ConnectionGenotype(input2.getUid(), hidden4.getUid(), 0, 0, true));

    assertTrue(network.circularIfConnected(hidden4, output));
  }

  @Test
  public void compatibilityDistanceBetweenIdenticalIsZero() {
    NetworkGenotype network = new NetworkGenotype();
    NeuronGenotype input = new NeuronGenotype(NeuronLayer.INPUT, 0);
    network.addNeuron(input);
    NeuronGenotype hidden1 = new NeuronGenotype(NeuronLayer.HIDDEN, 1);
    network.addNeuron(hidden1);
    NeuronGenotype hidden2 = new NeuronGenotype(NeuronLayer.HIDDEN, 2);
    network.addNeuron(hidden2);
    NeuronGenotype output = new NeuronGenotype(NeuronLayer.OUTPUT, 3);
    network.addNeuron(output);

    network.addConnection(
        new ConnectionGenotype(input.getUid(), hidden1.getUid(), 0, 0, true));
    network.addConnection(
        new ConnectionGenotype(hidden1.getUid(), hidden2.getUid(), 1, 0, true));
    network.addConnection(
        new ConnectionGenotype(hidden2.getUid(), output.getUid(), 2, 0, true));

    NetworkGenotype copiedNetwork = new NetworkGenotype(network);

    assertEquals(0, NetworkGenotype.compatibilityDistance(network, copiedNetwork), DELTA);
  }

  @Test
  public void compatibilityDistanceIsCorrect() {
    NetworkGenotype network = new NetworkGenotype();
    NeuronGenotype input = new NeuronGenotype(NeuronLayer.INPUT, 0);
    network.addNeuron(input);
    NeuronGenotype hidden1 = new NeuronGenotype(NeuronLayer.HIDDEN, 1);
    network.addNeuron(hidden1);
    NeuronGenotype output = new NeuronGenotype(NeuronLayer.OUTPUT, 3);
    network.addNeuron(output);

    network.addConnection(
        new ConnectionGenotype(input.getUid(), hidden1.getUid(), 0, 0, true));
    network.addConnection(
        new ConnectionGenotype(hidden1.getUid(), output.getUid(), 1, 0, true));

    NetworkGenotype newNetwork = new NetworkGenotype(network);
    newNetwork.addConnection(
        new ConnectionGenotype(input.getUid(), output.getUid(), 2, 0, true));

    assertEquals(NetworkGenotype.DIST_C1 / 3, NetworkGenotype.compatibilityDistance(network, newNetwork), DELTA);
  }

}
