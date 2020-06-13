package jwblangley.neat.proto;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import jwblangley.neat.evolution.FullEvolutionPhenotypeTest;
import jwblangley.neat.genotype.NetworkGenotype;
import jwblangley.neat.phenotype.Network;
import org.junit.Test;

public class SaveAndReadXorNetworkTest {

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

}
