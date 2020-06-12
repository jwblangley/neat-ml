package jwblangley.neat.phenotype;

import static org.junit.Assert.assertEquals;

import java.util.InputMismatchException;
import java.util.List;
import jwblangley.neat.evolution.InnovationGenerator;
import jwblangley.neat.genotype.ConnectionGenotype;
import jwblangley.neat.genotype.NetworkGenotype;
import jwblangley.neat.genotype.NeuronGenotype;
import jwblangley.neat.genotype.NeuronLayer;
import org.junit.Test;

public class NetworkTest {

  final static double TOLERANCE = 0.0000001;

  @Test(expected = InputMismatchException.class)
  public void cannotProvideTooManyInputs() {
    // Setup genotype
    NetworkGenotype networkGenotype = new NetworkGenotype();

    NeuronGenotype inputGenotype = new NeuronGenotype(NeuronLayer.INPUT);
    NeuronGenotype outputGenotype = new NeuronGenotype(NeuronLayer.OUTPUT);
    networkGenotype.addNeuron(inputGenotype);
    networkGenotype.addNeuron(outputGenotype);

    InnovationGenerator innovationGenerator = new InnovationGenerator();
    ConnectionGenotype connectionGenotype = new ConnectionGenotype(inputGenotype.getUid(),
        outputGenotype.getUid(), innovationGenerator.next(), 2d, true);
    networkGenotype.addConnection(connectionGenotype);

    // Create phenotype
    Network network = Network.createRegressionNetworkFromGenotype(networkGenotype);
    List<Double> result = network.calculateOutputs(2.5d, 2.5d);
  }

  @Test(expected = InputMismatchException.class)
  public void cannotProvideTooFewInputs() {
    // Setup genotype
    NetworkGenotype networkGenotype = new NetworkGenotype();

    NeuronGenotype input1Genotype = new NeuronGenotype(NeuronLayer.INPUT);
    NeuronGenotype input2Genotype = new NeuronGenotype(NeuronLayer.INPUT);
    NeuronGenotype outputGenotype = new NeuronGenotype(NeuronLayer.OUTPUT);
    networkGenotype.addNeuron(input1Genotype);
    networkGenotype.addNeuron(input2Genotype);
    networkGenotype.addNeuron(outputGenotype);

    InnovationGenerator innovationGenerator = new InnovationGenerator();
    ConnectionGenotype connection1Genotype = new ConnectionGenotype(input1Genotype.getUid(),
        outputGenotype.getUid(), innovationGenerator.next(), 1d, true);
    ConnectionGenotype connection2Genotype = new ConnectionGenotype(input2Genotype.getUid(),
        outputGenotype.getUid(), innovationGenerator.next(), 2d, true);
    networkGenotype.addConnection(connection1Genotype);
    networkGenotype.addConnection(connection2Genotype);

    // Create phenotype
    Network network = Network.createRegressionNetworkFromGenotype(networkGenotype);
    List<Double> result = network.calculateOutputs(2.5d);
  }

  @Test(timeout = 10000)
  public void simpleRegressionNetworkCalculationIsCorrect() {
    // Setup genotype
    NetworkGenotype networkGenotype = new NetworkGenotype();

    NeuronGenotype inputGenotype = new NeuronGenotype(NeuronLayer.INPUT);
    NeuronGenotype outputGenotype = new NeuronGenotype(NeuronLayer.OUTPUT);
    networkGenotype.addNeuron(inputGenotype);
    networkGenotype.addNeuron(outputGenotype);

    InnovationGenerator innovationGenerator = new InnovationGenerator();
    ConnectionGenotype connectionGenotype = new ConnectionGenotype(inputGenotype.getUid(),
        outputGenotype.getUid(), innovationGenerator.next(), 2d, true);
    networkGenotype.addConnection(connectionGenotype);

    // Create phenotype
    Network network = Network.createRegressionNetworkFromGenotype(networkGenotype);
    List<Double> result = network.calculateOutputs(5d);

    assertEquals(1, result.size());
    assertEquals(10d, result.get(0), TOLERANCE);
  }

  @Test(timeout = 10000)
  public void simpleSigmoidNetworkCalculationIsCorrect() {
    // Setup genotype
    NetworkGenotype networkGenotype = new NetworkGenotype();

    NeuronGenotype inputGenotype = new NeuronGenotype(NeuronLayer.INPUT);
    NeuronGenotype outputGenotype = new NeuronGenotype(NeuronLayer.OUTPUT);
    networkGenotype.addNeuron(inputGenotype);
    networkGenotype.addNeuron(outputGenotype);

    InnovationGenerator innovationGenerator = new InnovationGenerator();
    ConnectionGenotype connectionGenotype = new ConnectionGenotype(inputGenotype.getUid(),
        outputGenotype.getUid(), innovationGenerator.next(), 2d, true);
    networkGenotype.addConnection(connectionGenotype);

    // Create phenotype
    Network network = Network.createSigmoidOutputNetworkFromGenotype(networkGenotype);
    List<Double> result = network.calculateOutputs(0d);

    assertEquals(1, result.size());
    assertEquals(0.5, result.get(0), TOLERANCE);
  }

  @Test(timeout = 10000)
  public void regressionNetworkWithDisabledCalculationIsCorrect() {
    // Setup genotype
    NetworkGenotype networkGenotype = new NetworkGenotype();

    NeuronGenotype inputGenotype = new NeuronGenotype(NeuronLayer.INPUT);
    NeuronGenotype hiddenGenotype = new NeuronGenotype(NeuronLayer.HIDDEN);
    NeuronGenotype outputGenotype = new NeuronGenotype(NeuronLayer.OUTPUT);
    networkGenotype.addNeuron(inputGenotype);
    networkGenotype.addNeuron(hiddenGenotype);
    networkGenotype.addNeuron(outputGenotype);

    InnovationGenerator innovationGenerator = new InnovationGenerator();
    ConnectionGenotype con1 = new ConnectionGenotype(inputGenotype.getUid(),
        outputGenotype.getUid(), innovationGenerator.next(), 1d, false);

    ConnectionGenotype con2 = new ConnectionGenotype(inputGenotype.getUid(),
        hiddenGenotype.getUid(), innovationGenerator.next(), 3d, true);
    ConnectionGenotype con3 = new ConnectionGenotype(hiddenGenotype.getUid(),
        outputGenotype.getUid(), innovationGenerator.next(), 5d, true);

    networkGenotype.addConnection(con1);
    networkGenotype.addConnection(con2);
    networkGenotype.addConnection(con3);

    // Create phenotype
    Network network = Network.createRegressionNetworkFromGenotype(networkGenotype);
    List<Double> result = network.calculateOutputs(5d);

    assertEquals(1, result.size());
    assertEquals(75d, result.get(0), TOLERANCE);
  }

  @Test(timeout = 10000)
  public void complexNetworkCalculationIsCorrect() {
    // Setup genotype
    NetworkGenotype networkGenotype = new NetworkGenotype();

    // Neurons
    NeuronGenotype input1 = new NeuronGenotype(NeuronLayer.INPUT);
    NeuronGenotype input2 = new NeuronGenotype(NeuronLayer.INPUT);

    NeuronGenotype hidden1 = new NeuronGenotype(NeuronLayer.HIDDEN);
    NeuronGenotype hidden2 = new NeuronGenotype(NeuronLayer.HIDDEN);

    NeuronGenotype output1 = new NeuronGenotype(NeuronLayer.OUTPUT);
    NeuronGenotype output2 = new NeuronGenotype(NeuronLayer.OUTPUT);

    networkGenotype.addNeuron(input1);
    networkGenotype.addNeuron(input2);
    networkGenotype.addNeuron(hidden1);
    networkGenotype.addNeuron(hidden2);
    networkGenotype.addNeuron(output1);
    networkGenotype.addNeuron(output2);

    // Connections
    InnovationGenerator ig = new InnovationGenerator();

    ConnectionGenotype con1 = new ConnectionGenotype(input1.getUid(), hidden1.getUid(), ig.next(),
        3d, true);
    ConnectionGenotype con2 = new ConnectionGenotype(input1.getUid(), hidden2.getUid(), ig.next(),
        1d, true);
    ConnectionGenotype con3 = new ConnectionGenotype(input1.getUid(), output2.getUid(), ig.next(),
        -5d, true);

    ConnectionGenotype con4 = new ConnectionGenotype(input2.getUid(), hidden1.getUid(), ig.next(),
        2d, true);
    // N.B: not enabled!
    ConnectionGenotype con5 = new ConnectionGenotype(input2.getUid(), hidden2.getUid(), ig.next(),
        1d, false);

    ConnectionGenotype con6 = new ConnectionGenotype(hidden1.getUid(), output1.getUid(), ig.next(),
        2d, true);
    ConnectionGenotype con7 = new ConnectionGenotype(hidden1.getUid(), output2.getUid(), ig.next(),
        0.5d, true);

    ConnectionGenotype con8 = new ConnectionGenotype(hidden2.getUid(), output2.getUid(), ig.next(),
        1.5d, true);

    networkGenotype.addConnection(con1);
    networkGenotype.addConnection(con2);
    networkGenotype.addConnection(con3);
    networkGenotype.addConnection(con4);
    networkGenotype.addConnection(con5);
    networkGenotype.addConnection(con6);
    networkGenotype.addConnection(con7);
    networkGenotype.addConnection(con8);

    // Create phenotype
    Network network = Network.createRegressionNetworkFromGenotype(networkGenotype);
    List<Double> result = network.calculateOutputs(3d, 2d);

    assertEquals(2, result.size());
    assertEquals(26, result.get(0), TOLERANCE);
    assertEquals(-4, result.get(1), TOLERANCE);
  }

}
