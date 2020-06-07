package jwblangley.neat.genotype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

public class ConnectionGenotypeTest {

  @Test
  public void testCopiedObjectsAreEqual() {
    // Setup
    ConnectionGenotype connection = new ConnectionGenotype(
        1,
        2,
        1,
        0,
        true
    );

    // Perform copy
    ConnectionGenotype copiedConnection = new ConnectionGenotype(connection);

    assertNotSame(copiedConnection, connection);
    assertEquals(copiedConnection, connection);
  }

}
