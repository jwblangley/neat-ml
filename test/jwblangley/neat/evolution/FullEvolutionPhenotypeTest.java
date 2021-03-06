package jwblangley.neat.evolution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;
import jwblangley.neat.genotype.NetworkGenotype;
import jwblangley.neat.phenotype.Network;
import jwblangley.neat.visualiser.Visualiser;
import org.junit.Test;

public class FullEvolutionPhenotypeTest {

  public static final int XOR_TESTS_IN_EVALUATE = 20;
  public static final int XOR_NUM_GENERATIONS = 100;
  public static final int XOR_POPULATION_SIZE = 100;
  public static final int XOR_TARGET_NUM_SPECIES = 5;
  public static final int XOR_NUM_THREADS = 8;
  private static Random innerRandom = new Random();
  public static final SingleEvaluator XOR_EVALUATOR = networkGenotype -> {
    Network network = Network.createSigmoidOutputNetworkFromGenotype(networkGenotype);

    int numCorrect = 0;
    for (int i = 0; i < XOR_TESTS_IN_EVALUATE; i++) {
      final double a = Math.round(innerRandom.nextDouble());
      final double b = Math.round(innerRandom.nextDouble());

      final double output = network.calculateOutputs(a, b).get(0);

      final boolean expected = (a > 0.5) ^ (b > 0.5);
      final boolean actual = output > 0.5;

      if (expected == actual) {
        numCorrect++;
      }
    }

    return numCorrect * 100d / XOR_TESTS_IN_EVALUATE;
  };

  @Test
  public void neatCanLearnXor() {
    final boolean visualise = true;
    Evolution evolution = networkLearnsXor();

    if (visualise) {
      BufferedImage image = Visualiser.visualiseNetwork(evolution.getFittestGenotype());
      Visualiser.saveImageToFile(image, new File("xor.png"), true);
    }
  }

  public static Evolution networkLearnsXor() {
    Random random = new Random(100);
    // Inner random so NEAT is deterministic with the previous random
    Random innerRandom = new Random(100);

    Evolution evolution = EvolutionFactory
        .createOptimisation(2, 1, XOR_POPULATION_SIZE, XOR_TARGET_NUM_SPECIES, XOR_NUM_THREADS,
            XOR_EVALUATOR);

    // Evolve
    evolution.setVerbose(true);
    for (int i = 1; i <= XOR_NUM_GENERATIONS; i++) {
      evolution.evolve(random);

      System.out.println();
    }

    // Test cases
    NetworkGenotype fittestGenotype = evolution.getFittestGenotype();
    Network bestNetwork = Network
        .createSigmoidOutputNetworkFromGenotype(fittestGenotype);
    final boolean ff = bestNetwork.calculateOutputs(0d, 0d).get(0) > 0.5;
    final boolean ft = bestNetwork.calculateOutputs(0d, 1d).get(0) > 0.5;
    final boolean tf = bestNetwork.calculateOutputs(1d, 0d).get(0) > 0.5;
    final boolean tt = bestNetwork.calculateOutputs(1d, 1d).get(0) > 0.5;

    assertFalse(ff);
    assertTrue(ft);
    assertTrue(tf);
    assertFalse(tt);

    return evolution;
  }

  @Test
  public void neatCanLearnAddition() {
    Random random = new Random(100);
    // Inner random so NEAT is deterministic with the previous random
    Random innerRandom = new Random(100);

    final double tolerance = 0.1d;

    final int testsInEvaluate = 5;
    final int numGenerations = 200;
    final int populationSize = 300;
    final int targetNumSpecies = 30;
    final int numThreads = 8;

    final double additionUpTo = 20d;

    Evolution evolution = EvolutionFactory
        .createOptimisation(2, 1, populationSize, targetNumSpecies, numThreads, networkGenotype -> {
          Network network = Network.createLinearOutputNetworkFromGenotype(networkGenotype);

          double totalDiff = 0;
          for (int i = 0; i < testsInEvaluate; i++) {
            final double a = innerRandom.nextDouble() * additionUpTo;
            final double b = innerRandom.nextDouble() * additionUpTo;

            final double output = network.calculateOutputs(a, b).get(0);
            final double expected = a + b;

            totalDiff += Math.abs(expected - output);

          }

          return 1000d / totalDiff;
        });

    // Evolve
    evolution.setVerbose(true);
    for (int i = 1; i <= numGenerations; i++) {
      evolution.evolve(random);

      System.out.println();
    }

    // Some randomly picked test cases
    Network bestNetwork = Network
        .createLinearOutputNetworkFromGenotype(evolution.getFittestGenotype());

    assertEquals(10d, bestNetwork.calculateOutputs(5d, 5d).get(0), tolerance);
    assertEquals(10d, bestNetwork.calculateOutputs(4d, 6d).get(0), tolerance);
    assertEquals(10d, bestNetwork.calculateOutputs(1d, 9d).get(0), tolerance);
    assertEquals(18d, bestNetwork.calculateOutputs(14d, 4d).get(0), tolerance);
    assertEquals(12d, bestNetwork.calculateOutputs(7d, 5d).get(0), tolerance);
    assertEquals(5d, bestNetwork.calculateOutputs(2d, 3d).get(0), tolerance);
  }

  @Test
  public void neatCanLearnSubtraction() {
    Random random = new Random(100);
    // Inner random so NEAT is deterministic with the previous random
    Random innerRandom = new Random(100);

    final double tolerance = 0.2d;

    final int testsInEvaluate = 5;
    final int numGenerations = 200;
    final int populationSize = 300;
    final int targetNumSpecies = 30;
    final int numThreads = 8;

    final double subtractionUpTo = 20d;

    Evolution evolution = EvolutionFactory
        .createOptimisation(2, 1, populationSize, targetNumSpecies, numThreads, networkGenotype -> {
          Network network = Network.createLinearOutputNetworkFromGenotype(networkGenotype);

          double totalDiff = 0;
          for (int i = 0; i < testsInEvaluate; i++) {
            final double a = innerRandom.nextDouble() * subtractionUpTo;
            final double b = innerRandom.nextDouble() * subtractionUpTo;

            final double output = network.calculateOutputs(a, b).get(0);
            final double expected = a - b;

            totalDiff += Math.abs(expected - output);

          }

          return 1000d / totalDiff;
        });

    // Evolve
    evolution.setVerbose(true);
    for (int i = 1; i <= numGenerations; i++) {
      evolution.evolve(random);

      System.out.println();
    }

    // Some randomly picked test cases
    Network bestNetwork = Network
        .createLinearOutputNetworkFromGenotype(evolution.getFittestGenotype());

    assertEquals(10d, bestNetwork.calculateOutputs(15d, 5d).get(0), tolerance);
    assertEquals(10d, bestNetwork.calculateOutputs(17d, 7d).get(0), tolerance);
    assertEquals(10d, bestNetwork.calculateOutputs(11d, 1d).get(0), tolerance);
    assertEquals(18d, bestNetwork.calculateOutputs(20d, 2d).get(0), tolerance);
    assertEquals(12d, bestNetwork.calculateOutputs(16d, 4d).get(0), tolerance);
    assertEquals(5d, bestNetwork.calculateOutputs(18d, 3d).get(0), tolerance);
  }

}
