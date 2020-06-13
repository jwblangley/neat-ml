package jwblangley.neat.proto;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import jwblangley.neat.evolution.FullEvolutionPhenotypeTest;
import jwblangley.neat.genotype.NetworkGenotype;
import jwblangley.neat.phenotype.Network;
import org.junit.Before;
import org.junit.Test;

public class SaveAndReadXorNetworkTest {

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
  public void savedAndLoadedGenotypeKnowsXor() {
    NetworkGenotype knowsXor = FullEvolutionPhenotypeTest.networkLearnsXor();

    System.out.println("Saving to proto...");
    Genotypes.NetworkGenotype protoNetwork = knowsXor.toProto();
    System.out.println("Reading from proto...");
    NetworkGenotype fromProto = new NetworkGenotype(protoNetwork);

    System.out.println("Testing loaded network");
    Network bestNetwork = Network
        .createSigmoidOutputNetworkFromGenotype(fromProto);
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
  public void savedAndLoadedDiskGenotypeKnowsXor() throws IOException {
    final File outputFile = new File(outputDir, "xor.genotype");

    NetworkGenotype knowsXor = FullEvolutionPhenotypeTest.networkLearnsXor();

    System.out.println("Saving to disk...");
    ProtoIO.toFile(knowsXor, outputFile);

    System.out.println("Reading from proto...");
    NetworkGenotype fromProto = ProtoIO.fromFile(outputFile);

    System.out.println("Testing loaded network");
    Network bestNetwork = Network
        .createSigmoidOutputNetworkFromGenotype(fromProto);
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
