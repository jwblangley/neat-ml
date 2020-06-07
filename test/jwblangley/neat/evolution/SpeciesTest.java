package jwblangley.neat.evolution;

import static org.junit.Assert.assertEquals;

import java.util.Random;
import jwblangley.neat.genotype.NetworkGenotype;
import org.junit.Test;

public class SpeciesTest {

  @Test
  public void resettingSpeciesClearsMembersButKeepsMascotDefinition() {
    NetworkGenotype mascot = new NetworkGenotype();

    Species species = new Species(mascot);
    assertEquals(1, species.size());

    species.reset(new Random());

    assertEquals(0, species.size());
    assertEquals(mascot, species.getMascot());
  }

}
