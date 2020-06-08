package jwblangley.neat.visualiser;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import jwblangley.neat.genotype.NetworkGenotype;
import jwblangley.neat.genotype.NeuronGenotype;
import jwblangley.neat.genotype.NeuronLayer;

public class Visualiser {

  private static final int INITIAL_IMAGE_SIZE = 1024;
  private static final int NEURON_SIZE = 50;
  private static final int MAX_POSITION_ATTEMPTS = 10;

  private static final Color INPUT_NEURON_COLOUR = Color.BLACK;
  private static final Color HIDDEN_NEURON_COLOUR = Color.BLACK;
  private static final Color OUTPUT_NEURON_COLOUR = Color.BLACK;
  private static final Color NEGATIVE_CONNECTION_COLOUR = Color.BLACK;
  private static final Color POSITIVE_CONNECTION_COLOUR = Color.BLACK;

  public static BufferedImage visualiseNetwork(NetworkGenotype network) {
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

    // Try to fit all outputs, double image size until fit
    int exponent = -1;
    BufferedImage image;
    allFitAttempt: while (true) {
      exponent++;
      final int imageSize = (int) Math.pow(2, exponent) * INITIAL_IMAGE_SIZE;

      image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);
      Graphics graphics = image.getGraphics();

      final Map<Integer, Point> neuronPositions = new HashMap<>();

      // If inputs or outputs do not fit (with some spacing), increase size
      if (inputNeurons.size() * (NEURON_SIZE * 1.25) > imageSize
          || outputNeurons.size() * (NEURON_SIZE * 1.25) > imageSize) {
        continue;
      }

      graphics.setColor(INPUT_NEURON_COLOUR);
      // Draw input neurons
      for (int i = 0; i < inputNeurons.size(); i++) {
        NeuronGenotype neuron = inputNeurons.get(i);
        int x = 2 * NEURON_SIZE;

        int y = inputNeurons.size() == 1 ? imageSize / 2 : 2 * NEURON_SIZE + (imageSize - 4 * NEURON_SIZE) / (inputNeurons.size() - 1) * i;
        neuronPositions.put(neuron.getUid(), new Point(x, y));
        graphics.fillOval(x - NEURON_SIZE / 2, y - NEURON_SIZE / 2, NEURON_SIZE, NEURON_SIZE);
      }

      graphics.setColor(OUTPUT_NEURON_COLOUR);
      // Draw output neurons
      for (int i = 0; i < outputNeurons.size(); i++) {
        NeuronGenotype neuron = outputNeurons.get(i);
        int x = imageSize - 2 * NEURON_SIZE;
        int y = outputNeurons.size() == 1 ? imageSize / 2 : 2 * NEURON_SIZE + (imageSize - 4 * NEURON_SIZE) / (outputNeurons.size() - 1) * i;
        neuronPositions.put(neuron.getUid(), new Point(x, y));
        graphics.fillOval(x - NEURON_SIZE / 2, y - NEURON_SIZE / 2, NEURON_SIZE, NEURON_SIZE);
      }

      // Draw hidden neurons by placing them randomly until they don't overlap (with padding)
      for (NeuronGenotype neuron : hiddenNeurons) {
        boolean fitSucessful = false;
        neuronFitAttempt: for (int i = 0; i < MAX_POSITION_ATTEMPTS; i++) {

          final int x = 8 * NEURON_SIZE + seededRandom.nextInt(imageSize - 16 * NEURON_SIZE);
          final int y = 4 * NEURON_SIZE + seededRandom.nextInt(imageSize - 8 * NEURON_SIZE);

          for (Point existingNeuron : neuronPositions.values()) {
            if (Point.distance(x, y, existingNeuron.x, existingNeuron.y) < 2 * 2 * NEURON_SIZE) {
              continue neuronFitAttempt;
            }
          }

          // No clash with any other existing neuron
          fitSucessful = true;
          neuronPositions.put(neuron.getUid(), new Point(x, y));
          graphics.fillOval(x - NEURON_SIZE / 2, y - NEURON_SIZE / 2, NEURON_SIZE, NEURON_SIZE);
          break;
        }
        if (!fitSucessful) {
          System.out.println("Did not all fit");
          continue allFitAttempt;
        }
      }

      // All neurons fit and placed
      return image;
    }

  }

  public static void saveImageToFile(BufferedImage image, File file) {
    try {
      ImageIO.write(image, "PNG", file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}