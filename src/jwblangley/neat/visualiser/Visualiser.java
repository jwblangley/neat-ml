package jwblangley.neat.visualiser;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import jwblangley.neat.genotype.NetworkGenotype;
import jwblangley.neat.genotype.NeuronGenotype;
import jwblangley.neat.genotype.NeuronLayer;

public class Visualiser {

  private static final int SIZE = 1024;
  private static Color NEURON_COLOUR = Color.BLACK;

  public static BufferedImage visualiseNetwork(NetworkGenotype network) {
    final BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
    final Graphics graphics = image.getGraphics();

    final Random seededRandom = new Random(100);
    /*
     By sorting by uid and drawing in that order with a seeded random, two identical networks
     should be visualised identically also
     */


    List<NeuronGenotype> inputNeurons = network.getNeurons().stream()
        .filter(neuron -> neuron.getLayer() == NeuronLayer.INPUT)
        .sorted(Comparator.comparingDouble(NeuronGenotype::getUid))
        .collect(Collectors.toList());

    List<NeuronGenotype> hiddenNeurons = network.getNeurons().stream()
        .filter(neuron -> neuron.getLayer() == NeuronLayer.HIDDEN)
        .sorted(Comparator.comparingDouble(NeuronGenotype::getUid))
        .collect(Collectors.toList());

    List<NeuronGenotype> outputNeurons = network.getNeurons().stream()
        .filter(neuron -> neuron.getLayer() == NeuronLayer.OUTPUT)
        .sorted(Comparator.comparingDouble(NeuronGenotype::getUid))
        .collect(Collectors.toList());

    return image;
  }

}