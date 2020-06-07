package jwblangley.neat.evolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import jwblangley.neat.genotype.NetworkGenotype;

/**
 * Each Species has a mascot genotype and all of it's members are defined as being genotypes that
 * have a compatibility distance within a threshold of the mascot's fitness
 */
public class Species {

  private final List<NetworkGenotype> members;

  private NetworkGenotype mascot;

  /**
   * Construct a new Species
   *
   * @param mascot the mascot of the new species
   */
  public Species(NetworkGenotype mascot) {
    this.mascot = mascot;
    this.members = new ArrayList<>();
    this.members.add(mascot);
  }

  public NetworkGenotype getMascot() {
    return mascot;
  }

  public List<NetworkGenotype> getMembers() {
    return members;
  }

  public void addMember(NetworkGenotype member) {
    members.add(member);
  }

  /**
   * @return the number of members in the species
   */
  public int size() {
    return members.size();
  }

  /**
   * Clear the members from this species and set a random member as the new mascot. N.B: the new
   * mascot is NOT a member of the reset species
   *
   * @param random seeded Random object
   */
  public void reset(Random random) {
    mascot = members.get(random.nextInt(members.size()));
    members.clear();
  }
}
