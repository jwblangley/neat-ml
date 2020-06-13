package jwblangley.neat.proto;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import jwblangley.neat.evolution.Evolution;
import jwblangley.neat.evolution.FullEvolutionPhenotypeTest;
import jwblangley.neat.phenotype.Network;
import org.junit.Before;
import org.junit.Test;

public class SaveAndReadXorEvolutionTest {

  final File outputDir = new File("testOutput/").getAbsoluteFile();

  @Before
  public void clearOutputDirectory() {
    System.out.println("Hello");
    if (outputDir.exists()) {
      outputDir.delete();
    }

    outputDir.mkdir();
  }

  @Test
  public void savedAndLoadedEvolutionKnowsXor() {
    Evolution evolvesXor = FullEvolutionPhenotypeTest.networkLearnsXor();

    System.out.println("Saving to proto...");
    EvolutionOuterClass.Evolution protoEvolution = evolvesXor.toProto();
    System.out.println("Reading from proto...");
    Evolution fromProto = new Evolution(
        protoEvolution,
        FullEvolutionPhenotypeTest.XOR_TARGET_NUM_SPECIES,
        FullEvolutionPhenotypeTest.XOR_NUM_THREADS,
        FullEvolutionPhenotypeTest.XOR_EVALUATOR
    );

    System.out.println("Evolving loaded Evolution once");
    fromProto.setVerbose(true);
    fromProto.evolve(new Random(100));

    System.out.println("\nTesting loaded network");
    Network bestNetwork = Network
        .createSigmoidOutputNetworkFromGenotype(fromProto.getFittestGenotype());
    final boolean ff = bestNetwork.calculateOutputs(0d, 0d).get(0) > 0.5;
    final boolean ft = bestNetwork.calculateOutputs(0d, 1d).get(0) > 0.5;
    final boolean tf = bestNetwork.calculateOutputs(1d, 0d).get(0) > 0.5;
    final boolean tt = bestNetwork.calculateOutputs(1d, 1d).get(0) > 0.5;

    assertFalse(ff);
    assertTrue(ft);
    assertTrue(tf);
    assertFalse(tt);
  }

  @Test
  public void savedAndLoadedDiskEvolutionKnowsXor() throws IOException {
    final File outputFile = new File(outputDir, "xor.evolution");

    Evolution evolvesXor = FullEvolutionPhenotypeTest.networkLearnsXor();

    System.out.println("Saving to disk...");
    ProtoIO.toFile(evolvesXor, outputFile);

    System.out.println("Reading from proto...");
    Evolution fromProto = ProtoIO.evolutionFromFile(
        outputFile,
        FullEvolutionPhenotypeTest.XOR_TARGET_NUM_SPECIES,
        FullEvolutionPhenotypeTest.XOR_NUM_THREADS,
        FullEvolutionPhenotypeTest.XOR_EVALUATOR
    );

    System.out.println("Evolving loaded Evolution once");
    fromProto.setVerbose(true);
    fromProto.evolve(new Random(100));

    System.out.println("\nTesting loaded network");
    Network bestNetwork = Network
        .createSigmoidOutputNetworkFromGenotype(fromProto.getFittestGenotype());
    final boolean ff = bestNetwork.calculateOutputs(0d, 0d).get(0) > 0.5;
    final boolean ft = bestNetwork.calculateOutputs(0d, 1d).get(0) > 0.5;
    final boolean tf = bestNetwork.calculateOutputs(1d, 0d).get(0) > 0.5;
    final boolean tt = bestNetwork.calculateOutputs(1d, 1d).get(0) > 0.5;

    assertFalse(ff);
    assertTrue(ft);
    assertTrue(tf);
    assertFalse(tt);
  }

}
