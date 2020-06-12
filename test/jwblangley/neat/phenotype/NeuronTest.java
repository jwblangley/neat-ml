package jwblangley.neat.phenotype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.function.Function;
import org.junit.Test;

public class NeuronTest {

  final static double TOLERANCE = 0.0000001;

  @Test(expected = RuntimeException.class)
  public void cannotGetOutputOfNewNeuron() {
    Neuron neuron = new Neuron(Activation.LINEAR);
    double x = neuron.getOutput();
    System.out.println(x);
  }

  @Test(expected = RuntimeException.class)
  public void cannotGetOutputOfNewInputNeuron() {
    InputNeuron neuron = new InputNeuron(Activation.LINEAR);
    double x = neuron.getOutput();
    System.out.println(x);
  }

  @Test
  public void calculatedInputNeuronIsOutputting() {
    InputNeuron neuron = new InputNeuron(Activation.LINEAR);
    neuron.setInput(5);
    neuron.tryCalculate();

    assertTrue(neuron.isOutputting());
  }

  @Test
  public void canGetOutputOfInputNeuron() {
    InputNeuron neuron = new InputNeuron(Activation.LINEAR);
    neuron.setInput(5);
    neuron.tryCalculate();

    assertEquals(5d, neuron.getOutput(), TOLERANCE);
  }

  @Test
  public void clearedSetInputNeuronIsNotOutputting() {
    InputNeuron neuron = new InputNeuron(Activation.LINEAR);
    neuron.setInput(5);
    neuron.tryCalculate();
    neuron.clear();

    assertFalse(neuron.isOutputting());
  }

  @Test(expected = RuntimeException.class)
  public void cannotGetOutputOfClearedCalculatedInputNeuron() {
    InputNeuron neuron = new InputNeuron(Activation.LINEAR);
    neuron.setInput(5);
    neuron.tryCalculate();
    neuron.clear();

    double x = neuron.getOutput();
    System.out.println(x);
  }

  @Test
  public void neuronOutputUsesActivationFunction() {
    Function<Double, Double> mockActivation = mock(Function.class);
    doReturn(0d).when(mockActivation).apply(0d);

    InputNeuron neuron = new InputNeuron(mockActivation);
    neuron.setInput(0);
    neuron.tryCalculate();

    verify(mockActivation).apply(0d);
  }

  @Test
  public void neuronOutputActivationFunctionIsOnSum() {
    Function<Double, Double> mockActivation = mock(Function.class);
    doReturn(5d).when(mockActivation).apply(1.5d);

    InputNeuron input1 = new InputNeuron(Activation.LINEAR);
    input1.setInput(1d);

    InputNeuron input2 = new InputNeuron(Activation.LINEAR);
    input2.setInput(0.5d);

    Neuron neuron = new Neuron(mockActivation);
    neuron.addInput(input1, 1);
    neuron.addInput(input2, 1);

    input1.tryCalculate();
    input2.tryCalculate();
    neuron.tryCalculate();

    verify(mockActivation).apply(1.5d);
    assertEquals(5d, neuron.getOutput(), TOLERANCE);
  }

  @Test
  public void neuronOutputActivationFunctionIsOnWeightedSum() {
    Function<Double, Double> mockActivation = mock(Function.class);
    doReturn(5d).when(mockActivation).apply(3.5d);

    InputNeuron input1 = new InputNeuron(Activation.LINEAR);
    input1.setInput(1d);

    InputNeuron input2 = new InputNeuron(Activation.LINEAR);
    input2.setInput(0.5d);

    Neuron neuron = new Neuron(mockActivation);
    neuron.addInput(input1, 2);
    neuron.addInput(input2, 3);

    input1.tryCalculate();
    input2.tryCalculate();
    neuron.tryCalculate();

    verify(mockActivation).apply(3.5d);
    assertEquals(5d, neuron.getOutput(), TOLERANCE);
  }


}
