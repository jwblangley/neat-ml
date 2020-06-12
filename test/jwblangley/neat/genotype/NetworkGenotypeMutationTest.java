package jwblangley.neat.genotype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import java.util.List;
import java.util.Random;
import jwblangley.neat.evolution.InnovationGenerator;
import org.junit.Test;

public class NetworkGenotypeMutationTest {

  private final static double DELTA = 0.00001;

  @Test
  public void addConnectionMutationAddsSingleConnection() {
    InnovationGenerator innovation = new InnovationGenerator();
    Random random = new Random();

    NetworkGenotype network = new NetworkGenotype();

    NeuronGenotype input = new NeuronGenotype(NeuronLayer.INPUT, 0);
    NeuronGenotype output = new NeuronGenotype(NeuronLayer.OUTPUT, 1);

    network.addNeuron(input);
    network.addNeuron(output);

    while (!network.addConnectionMutation(random, innovation, 10)) {
      System.out.println("Connection attempt unsuccessful");
    }

    System.out.println("Connection succeeded");
    List<ConnectionGenotype> connections = network.getConnections();

    assertEquals(1, connections.size());
  }

  @Test
  public void addConnectionMutationAddsConnectionBetweenCorrect() {
    InnovationGenerator innovation = new InnovationGenerator();
    Random random = new Random();

    NetworkGenotype network = new NetworkGenotype();

    NeuronGenotype input = new NeuronGenotype(NeuronLayer.INPUT, 0);
    NeuronGenotype output = new NeuronGenotype(NeuronLayer.OUTPUT, 1);

    network.addNeuron(input);
    network.addNeuron(output);

    while (!network.addConnectionMutation(random, innovation, 10)) {
      System.out.println("Connection attempt unsuccessful");
    }

    System.out.println("Connection succeeded");
    List<ConnectionGenotype> connections = network.getConnections();

    assertEquals(1, connections.size());
    ConnectionGenotype connection = connections.get(0);

    assertEquals(0, connection.getNeuronFrom());
    assertEquals(1, connection.getNeuronTo());
  }

  @Test
  public void addConnectionMutationDoesNotOverride() {
    InnovationGenerator innovation = new InnovationGenerator();
    Random random = new Random();

    NetworkGenotype network = new NetworkGenotype();

    NeuronGenotype input = new NeuronGenotype(NeuronLayer.INPUT, 0);
    NeuronGenotype output = new NeuronGenotype(NeuronLayer.OUTPUT, 1);

    network.addNeuron(input);
    network.addNeuron(output);

    while (!network.addConnectionMutation(random, innovation, 10)) {
      System.out.println("Connection attempt unsuccessful");
    }

    assertFalse(network.addConnectionMutation(random, innovation, 1000));
  }

  @Test
  public void addConnectionMutationDoesNotCreateCycles() {
    InnovationGenerator innovation = new InnovationGenerator();
    Random random = new Random();

    NetworkGenotype network = new NetworkGenotype();

    NeuronGenotype input = new NeuronGenotype(NeuronLayer.INPUT, 0);
    network.addNeuron(input);
    NeuronGenotype hidden1 = new NeuronGenotype(NeuronLayer.HIDDEN, 1);
    network.addNeuron(hidden1);
    NeuronGenotype hidden2 = new NeuronGenotype(NeuronLayer.HIDDEN, 2);
    network.addNeuron(hidden2);
    NeuronGenotype output = new NeuronGenotype(NeuronLayer.OUTPUT, 3);
    network.addNeuron(output);

    // Create all connections except hidden2 -> hidden1, which adding would make a cycle
    network.addConnection(
        new ConnectionGenotype(input.getUid(), hidden1.getUid(), innovation.next(), 0, true)
    );
    network.addConnection(
        new ConnectionGenotype(input.getUid(), hidden2.getUid(), innovation.next(), 0, true)
    );
    network.addConnection(
        new ConnectionGenotype(input.getUid(), output.getUid(), innovation.next(), 0, true)
    );
    network.addConnection(
        new ConnectionGenotype(hidden1.getUid(), hidden2.getUid(), innovation.next(), 0, true)
    );
    network.addConnection(
        new ConnectionGenotype(hidden1.getUid(), output.getUid(), innovation.next(), 0, true)
    );
    network.addConnection(
        new ConnectionGenotype(hidden2.getUid(), output.getUid(), innovation.next(), 0, true)
    );

    assertFalse(network.addConnectionMutation(random, innovation, 1000));
  }

  @Test
  public void addNeuronMutationAddsANeuron() {
    InnovationGenerator innovation = new InnovationGenerator();
    Random random = new Random();

    NetworkGenotype network = new NetworkGenotype();

    NeuronGenotype input = new NeuronGenotype(NeuronLayer.INPUT, 0);
    NeuronGenotype output = new NeuronGenotype(NeuronLayer.OUTPUT, 1);

    network.addNeuron(input);
    network.addNeuron(output);

    ConnectionGenotype connection = new ConnectionGenotype(0, 1, innovation.next(), 0.5f, true);
    network.addConnection(connection);

    network.addNeuronMutation(random, innovation);

    assertEquals(3, network.getNeurons().size());
  }

  @Test
  public void addNeuronMutationAddsConnections() {
    InnovationGenerator innovation = new InnovationGenerator();
    Random random = new Random();

    NetworkGenotype network = new NetworkGenotype();

    NeuronGenotype input = new NeuronGenotype(NeuronLayer.INPUT, 0);
    NeuronGenotype output = new NeuronGenotype(NeuronLayer.OUTPUT, 1);

    network.addNeuron(input);
    network.addNeuron(output);

    ConnectionGenotype connection = new ConnectionGenotype(0, 1, innovation.next(), 0.5f, true);
    network.addConnection(connection);

    network.addNeuronMutation(random, innovation);

    assertEquals(2,
        network.getConnections().stream().filter(ConnectionGenotype::isEnabled).count());
  }

  @Test
  public void addNeuronMutationDisablesOriginalConnection() {
    InnovationGenerator innovation = new InnovationGenerator();
    Random random = new Random();

    NetworkGenotype network = new NetworkGenotype();

    NeuronGenotype input = new NeuronGenotype(NeuronLayer.INPUT, 0);
    NeuronGenotype output = new NeuronGenotype(NeuronLayer.OUTPUT, 1);

    network.addNeuron(input);
    network.addNeuron(output);

    ConnectionGenotype connection = new ConnectionGenotype(0, 1, innovation.next(), 0.5f, true);
    network.addConnection(connection);

    network.addNeuronMutation(random, innovation);

    assertFalse(connection.isEnabled());
  }

  @Test
  public void addNeuronMutationSplitsConnection() {
    InnovationGenerator innovation = new InnovationGenerator();
    Random random = new Random();

    NetworkGenotype network = new NetworkGenotype();

    NeuronGenotype input = new NeuronGenotype(NeuronLayer.INPUT, 0);
    NeuronGenotype output = new NeuronGenotype(NeuronLayer.OUTPUT, 1);

    network.addNeuron(input);
    network.addNeuron(output);

    ConnectionGenotype connection = new ConnectionGenotype(0, 1, innovation.next(), 0.5f, true);
    network.addConnection(connection);

    network.addNeuronMutation(random, innovation);

    ConnectionGenotype firstConnection = network.getConnections().get(1);
    ConnectionGenotype secondConnection;
    if (firstConnection.getNeuronFrom() == 0) {
      secondConnection = network.getConnections().get(2);
    } else {
      firstConnection = network.getConnections().get(2);
      secondConnection = network.getConnections().get(1);
    }

    for (ConnectionGenotype con : network.getConnections()) {
      System.out.println("con.getNeuronFrom() = " + con.getNeuronFrom());
      System.out.println("con.getNeuronTo() = " + con.getNeuronTo());
    }

    assertEquals(0, firstConnection.getNeuronFrom());

    assertEquals(1, secondConnection.getNeuronTo());

    assertFalse(connection.isEnabled());
  }

  @Test
  public void addNeuronMutationPreservesWeightAcrossConnection() {
    InnovationGenerator innovation = new InnovationGenerator();
    Random random = new Random();

    NetworkGenotype network = new NetworkGenotype();

    NeuronGenotype input = new NeuronGenotype(NeuronLayer.INPUT, 0);
    NeuronGenotype output = new NeuronGenotype(NeuronLayer.OUTPUT, 1);

    network.addNeuron(input);
    network.addNeuron(output);

    final double targetWeight = 0.5;

    ConnectionGenotype connection
        = new ConnectionGenotype(0, 1, innovation.next(), targetWeight, true);
    network.addConnection(connection);

    network.addNeuronMutation(random, innovation);

    ConnectionGenotype firstConnection = network.getConnections().get(1);
    ConnectionGenotype secondConnection = network.getConnections().get(2);

    assertEquals(targetWeight, firstConnection.getWeight() * secondConnection.getWeight(), DELTA);
  }

  @Test
  public void weightMutationChangesWeight() {
    InnovationGenerator innovation = new InnovationGenerator();
    Random random = new Random(100);

    NetworkGenotype network = new NetworkGenotype();

    NeuronGenotype input = new NeuronGenotype(NeuronLayer.INPUT, 0);
    NeuronGenotype output = new NeuronGenotype(NeuronLayer.OUTPUT, 1);

    network.addNeuron(input);
    network.addNeuron(output);

    final double origWeight = 0.5;

    ConnectionGenotype connection = new ConnectionGenotype(0, 1, innovation.next(), origWeight,
        true);

    network.addConnection(connection);

    network.weightMutation(random);

    assertNotEquals(origWeight, connection.getWeight(), DELTA);
  }

  @Test
  public void weightMutationChangesAllWeights() {
    InnovationGenerator innovation = new InnovationGenerator();
    Random random = new Random(100);

    NetworkGenotype network = new NetworkGenotype();

    NeuronGenotype input = new NeuronGenotype(NeuronLayer.INPUT, 0);
    NeuronGenotype intermediary = new NeuronGenotype(NeuronLayer.INPUT, 1);
    NeuronGenotype output = new NeuronGenotype(NeuronLayer.OUTPUT, 2);

    network.addNeuron(input);
    network.addNeuron(intermediary);
    network.addNeuron(output);

    final double origWeight = 0.5;

    ConnectionGenotype connectionA
        = new ConnectionGenotype(0, 1, innovation.next(), origWeight, true);
    ConnectionGenotype connectionB
        = new ConnectionGenotype(1, 2, innovation.next(), origWeight, true);
    ConnectionGenotype connectionC
        = new ConnectionGenotype(0, 2, innovation.next(), origWeight, true);

    network.addConnection(connectionA);
    network.addConnection(connectionB);
    network.addConnection(connectionC);

    network.weightMutation(random);

    assertNotEquals(origWeight, connectionA.getWeight(), DELTA);
    assertNotEquals(origWeight, connectionB.getWeight(), DELTA);
    assertNotEquals(origWeight, connectionC.getWeight(), DELTA);
  }

}
