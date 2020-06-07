package jwblangley.neat.genotype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.Random;
import jwblangley.neat.evolution.InnovationGenerator;
import org.junit.Test;

public class NetworkGenotypeMutationTest {

  final static double DELTA = 0.00001;

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


}
