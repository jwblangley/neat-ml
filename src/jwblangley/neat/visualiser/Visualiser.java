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
import jwblangley.neat.genotype.ConnectionGenotype;
import jwblangley.neat.genotype.NetworkGenotype;
import jwblangley.neat.genotype.NeuronGenotype;
import jwblangley.neat.genotype.NeuronLayer;

/**
 * Class holding static implementation methods for visualising neural networks (genotypes)
 */
public class Visualiser {

  private static final int INITIAL_IMAGE_SIZE = 1024;
  private static final int NEURON_SIZE = 50;
  private static final int MAX_CONNECTION_SIZE = 30;
  private static final int MIN_CONNECTION_SIZE = 15;
  private static final int TAPERING_RATIO = 5;
  private static final int MAX_POSITION_ATTEMPTS = 10;

  private static final Color INPUT_NEURON_COLOUR = Color.BLACK;
  private static final Color HIDDEN_NEURON_COLOUR = Color.GRAY;
  private static final Color OUTPUT_NEURON_COLOUR = Color.BLACK;
  private static final Color NEGATIVE_CONNECTION_COLOUR = new Color(106, 27, 154);
  private static final Color POSITIVE_CONNECTION_COLOUR = new Color(239, 108, 0);

  /**
   * Create an image that visualises a network genotype
   *
   * @param network network to be visualised
   * @return resulting visual
   */
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
    allFitAttempt:
    while (true) {
      exponent++;
      final int imageSize = (int) Math.pow(2, exponent) * INITIAL_IMAGE_SIZE;

      BufferedImage image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);
      Graphics graphics = image.getGraphics();

      BufferedImage neuronLayer = new BufferedImage(imageSize, imageSize,
          BufferedImage.TYPE_INT_ARGB);
      Graphics neuronGraphics = neuronLayer.getGraphics();

      final Map<Integer, Point> neuronPositions = new HashMap<>();

      // If inputs or outputs do not fit (with some spacing), increase size
      if (inputNeurons.size() * (NEURON_SIZE * 1.25) > imageSize
          || outputNeurons.size() * (NEURON_SIZE * 1.25) > imageSize) {
        continue;
      }

      neuronGraphics.setColor(INPUT_NEURON_COLOUR);
      // Draw input neurons
      for (int i = 0; i < inputNeurons.size(); i++) {
        NeuronGenotype neuron = inputNeurons.get(i);
        int x = 2 * NEURON_SIZE;

        int y = inputNeurons.size() == 1 ? imageSize / 2
            : 2 * NEURON_SIZE + (imageSize - 4 * NEURON_SIZE) / (inputNeurons.size() - 1) * i;
        neuronPositions.put(neuron.getUid(), new Point(x, y));
        neuronGraphics.fillOval(x - NEURON_SIZE / 2, y - NEURON_SIZE / 2, NEURON_SIZE, NEURON_SIZE);
      }

      neuronGraphics.setColor(OUTPUT_NEURON_COLOUR);
      // Draw output neurons
      for (int i = 0; i < outputNeurons.size(); i++) {
        NeuronGenotype neuron = outputNeurons.get(i);
        int x = imageSize - 2 * NEURON_SIZE;
        int y = outputNeurons.size() == 1 ? imageSize / 2
            : 2 * NEURON_SIZE + (imageSize - 4 * NEURON_SIZE) / (outputNeurons.size() - 1) * i;
        neuronPositions.put(neuron.getUid(), new Point(x, y));
        neuronGraphics.fillOval(x - NEURON_SIZE / 2, y - NEURON_SIZE / 2, NEURON_SIZE, NEURON_SIZE);
      }

      // Draw hidden neurons by placing them randomly until they don't overlap (with padding)
      neuronGraphics.setColor(HIDDEN_NEURON_COLOUR);
      for (NeuronGenotype neuron : hiddenNeurons) {
        boolean fitSucessful = false;
        neuronFitAttempt:
        for (int i = 0; i < MAX_POSITION_ATTEMPTS; i++) {

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
          neuronGraphics
              .fillOval(x - NEURON_SIZE / 2, y - NEURON_SIZE / 2, NEURON_SIZE, NEURON_SIZE);
          break;
        }
        if (!fitSucessful) {
          continue allFitAttempt;
        }
      }

      // All neurons fit and placed

      // Draw connections
      // Get connection min and max weight
      double minWeight = Double.POSITIVE_INFINITY;
      double maxWeight = Double.NEGATIVE_INFINITY;
      for (ConnectionGenotype connection : network.getConnections()) {
        if (!connection.isEnabled()) {
          continue;
        }

        final double weight = connection.getWeight();
        if (weight < minWeight) {
          minWeight = weight;
        }
        if (weight > maxWeight) {
          maxWeight = weight;
        }
      }

      // Draw connections with linearly interpolated widths
      for (ConnectionGenotype connection : network.getConnections()) {
        if (!connection.isEnabled()) {
          continue;
        }

        final double weight = connection.getWeight();
        final double lineWidth = MIN_CONNECTION_SIZE
            + (weight - minWeight) / (maxWeight - minWeight)
            * (MAX_CONNECTION_SIZE - MIN_CONNECTION_SIZE);

        graphics.setColor(weight < 0 ? NEGATIVE_CONNECTION_COLOUR : POSITIVE_CONNECTION_COLOUR);

        int fromUid = connection.getNeuronFrom();
        int toUid = connection.getNeuronTo();
        Point from = neuronPositions.get(fromUid);
        Point to = neuronPositions.get(toUid);

        final double dx = to.x - from.x;
        final double dy = to.y - from.y;
        final double dxdy = dx / dy;
        final double gradPerp = -1 / dxdy;

        // For every 1 unit in x, we go gradPerp in y. Therefore direction vector is (1, gradPerp)
        // Normalise direction vector;
        final double normalisingCont = Math.sqrt(1 + Math.pow(gradPerp, 2));
        final double vecX = 1 / normalisingCont;
        final double vecY = gradPerp / normalisingCont;

        Point fromTaperPos = new Point((int) (from.x + vecX * lineWidth / 2),
            (int) (from.y + vecY * lineWidth / 2));
        Point fromTaperNeg = new Point((int) (from.x - vecX * lineWidth / 2),
            (int) (from.y - vecY * lineWidth / 2));

        Point toTaperPos = new Point((int) (to.x + vecX * lineWidth / (2 * TAPERING_RATIO)),
            (int) (to.y + vecY * lineWidth / (2 * TAPERING_RATIO)));
        Point toTaperNeg = new Point((int) (to.x - vecX * lineWidth / (2 * TAPERING_RATIO)),
            (int) (to.y - vecY * lineWidth / (2 * TAPERING_RATIO)));

        graphics.fillPolygon(
            new int[]{fromTaperPos.x, fromTaperNeg.x, toTaperNeg.x, toTaperPos.x},
            new int[]{fromTaperPos.y, fromTaperNeg.y, toTaperNeg.y, toTaperPos.y},
            4);
      }

      // Draw neuron layer on after connections so they appear on top
      graphics.drawImage(neuronLayer, 0, 0, null);

      return image;
    }

  }

  /**
   * Save an image to disk
   *
   * @param image      image to save
   * @param file       location on disk to save to (no overwrite checks)
   * @param background whether the background should be opaque (white), transparent otherwise
   */
  public static void saveImageToFile(BufferedImage image, File file, boolean background) {
    BufferedImage toSave = image;
    if (background) {
      BufferedImage bg = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
      bg.getGraphics().setColor(Color.WHITE);
      bg.getGraphics().fillRect(0, 0, image.getWidth(), image.getHeight());
      bg.getGraphics().drawImage(image, 0, 0, null);
      toSave = bg;
    }

    try {
      ImageIO.write(toSave, "PNG", file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}