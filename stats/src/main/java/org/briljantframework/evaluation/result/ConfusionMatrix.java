package org.briljantframework.evaluation.result;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.*;
import java.util.*;
import java.util.List;

import org.briljantframework.chart.Chartable;
import org.briljantframework.classification.Label;
import org.briljantframework.vector.Vector;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.GrayPaintScale;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

import com.google.common.base.Strings;

/**
 * In the field of machine learning, a confusion matrix, also known as a contingency table or an
 * error matrix, is a specific table layout that allows visualization of the performance of an
 * algorithm, typically a supervised learning one. Each column of the matrix represents the
 * instances in a predicted class, while each row represents the instances in an actual class. The
 * name stems from the fact that it makes it easy to see if the system is confusing two classes
 * (i.e. commonly mislabeling one as another).
 * <p>
 * Example:
 * 
 * <pre>
 *           acc       unacc       vgood       good
 *   acc     344.0     28.0        3.0         15.0
 *   unacc   29.0      1179.0      0.0         0.0
 *   vgood   5.0       0.0         61.0        9.0
 *   good    6.0       3.0         1.0         45.0
 * </pre>
 * 
 * Created by isak on 02/10/14.
 */
public class ConfusionMatrix implements Chartable {

  private static final InvertedGrayPaintScale GRAY_PAINT_SCALE = new InvertedGrayPaintScale(0, 1,
      200);

  private final Map<String, Map<String, Double>> matrix;
  private final Set<String> labels;
  private final double sum;

  /**
   * Instantiates a new Confusion matrix.
   *
   * @param matrix the matrix
   * @param labels the labels
   * @param sum the sum
   */
  public ConfusionMatrix(Map<String, Map<String, Double>> matrix, Set<String> labels, double sum) {
    this.matrix = checkNotNull(matrix, "Matrix cannot be null");
    this.labels = Collections.unmodifiableSet(checkNotNull(labels, "Labels cannot be null"));

    this.sum = sum;
  }

  /**
   * Create confusion matrix.
   *
   * @param predictions the predictions
   * @param truth the target
   * @return the confusion matrix
   */
  public static ConfusionMatrix compute(List<Label> predictions, Vector truth) {
    checkArgument(predictions.size() == truth.size(), "The vector sizes don't match %s != %s.",
        predictions.size(), truth.size());

    Map<String, Map<String, Double>> matrix = new HashMap<>();
    Set<String> labels = new HashSet<>();
    double sum = 0;
    for (int i = 0; i < predictions.size(); i++) {
      String predicted = predictions.get(i).getPredictedValue();
      String actual = truth.getAsString(i);

      Map<String, Double> actuals = matrix.get(predicted);
      if (actuals == null) {
        actuals = new HashMap<>();
        matrix.put(predicted, actuals);
      }
      actuals.compute(actual, (key, value) -> value == null ? 1 : value + 1);

      labels.add(predicted);
      labels.add(actual);
      sum++;
    }
    return new ConfusionMatrix(matrix, labels, sum);
  }

  /**
   * Gets average recall.
   *
   * @return the average recall
   */
  public double getAverageRecall() {
    return labels.stream().mapToDouble(this::getRecall).summaryStatistics().getAverage();
  }

  /**
   * Gets average precision.
   *
   * @return the average precision
   */
  public double getAveragePrecision() {
    return labels.stream().mapToDouble(this::getPrecision).summaryStatistics().getAverage();
  }

  /**
   * Gets average f measure.
   *
   * @param beta the beta
   * @return the average f measure
   */
  public double getAverageFMeasure(double beta) {
    return labels.stream().mapToDouble(value -> getFMeasure(value, beta)).summaryStatistics()
        .getAverage();
  }

  /**
   * Gets f measure.
   *
   * @param target the target
   * @param beta the beta
   * @return the f measure
   */
  public double getFMeasure(String target, double beta) {
    double precision = getPrecision(target);
    double recall = getRecall(target);
    double beta2 = beta * beta;
    if (precision > 0 && recall > 0) {
      return (1 + beta2) * ((precision * recall) / ((beta2 * precision) + recall));
    } else {
      return 0;
    }
  }

  /**
   * Gets precision.
   *
   * @param target the target
   * @return the precision
   */
  public double getPrecision(String target) {
    double tp = get(target, target);
    if (tp == 0) {
      return 0;
    } else {
      double conditional = 0.0;
      for (String actual : labels) {
        conditional += get(target, actual);
      }
      return conditional > 0 ? tp / conditional : 0;
    }
  }

  /**
   * Gets recall.
   *
   * @param target the target
   * @return the recall
   */
  public double getRecall(String target) {
    double tp = get(target, target);

    if (tp == 0) {
      return 0;
    } else {
      double conditional = 0.0;
      for (String actual : getLabels()) {
        conditional += get(actual, target);
      }
      return conditional > 0 ? tp / conditional : 0;
    }
  }

  /**
   * Get double.
   *
   * @param predicted the predicted
   * @param actual the actual
   * @return the double
   */
  public double get(String predicted, String actual) {
    Map<String, Double> values = matrix.get(predicted);
    if (values == null) {
      return 0;
    } else {
      return values.getOrDefault(actual, 0.0);
    }
  }

  /**
   * Gets labels.
   *
   * @return the labels
   */
  public Set<String> getLabels() {
    return labels;
  }

  /**
   * Gets accuracy.
   *
   * @return the accuracy
   */
  public double getAccuracy() {
    double diagonal = 0.0;
    for (String value : labels) {
      diagonal += get(value, value);
    }
    return diagonal / sum;
  }

  /**
   * Gets error.
   *
   * @return the error
   */
  public double getError() {
    return 1 - getAccuracy();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    int longest = labels.stream().mapToInt(String::length).summaryStatistics().getMax();
    if (longest < 3) {
      longest = 3;
    }

    int longestValue = 0;
    for (String p : labels) {
      for (String n : labels) {
        int len = Double.toString(get(p, n)).length();
        if (len > longestValue) {
          longestValue = len;
        }
      }
    }

    builder.append(Strings.repeat(" ", longest + 3));
    for (String value : labels) {
      builder.append(value);
      builder.append(Strings.repeat(" ", longestValue + 1));
    }

    builder.append("\n");
    for (String predicted : labels) {
      builder.append(Strings.padEnd(predicted, longest + 3, ' '));

      for (String actual : labels) {
        String valueStr = Double.toString(get(predicted, actual));
        builder.append(valueStr);
        builder.append(Strings.repeat(" ", actual.length() + 1 + longestValue - valueStr.length()));
      }
      builder.append("\n");
    }

    builder.append("Accuracy: ");
    builder.append(String.format("%.2f", getAccuracy()));
    builder.append(" (");
    builder.append(String.format("%.2f", getError()));
    builder.append(")");

    return builder.toString();
  }

  /**
   * Gets precision recall chart.
   *
   * @return the precision recall chart
   */
  public JFreeChart getPrecisionRecallChart() {
    return Chartable.create("Average precision and recall", getPlot());

  }

  @Override
  public JFreeChart getChart() {
    List<String> labels = new ArrayList<>(getLabels());
    DefaultXYZDataset dataset = new DefaultXYZDataset();

    double[] x = new double[labels.size() * labels.size()];
    double[] y = new double[labels.size() * labels.size()];
    double[] z = new double[labels.size() * labels.size()];

    for (int i = 0; i < labels.size(); i++) {
      for (int j = 0; j < labels.size(); j++) {
        double zz = get(labels.get(i), labels.get(j)) / getActual(labels.get(j));
        x[j * labels.size() + i] = i;
        y[j * labels.size() + i] = j;
        z[j * labels.size() + i] = zz;
      }
    }
    dataset.addSeries("Class Labels", new double[][] {x, y, z});
    XYBlockRenderer block = new XYBlockRenderer();
    block.setPaintScale(GRAY_PAINT_SCALE);
    String[] labelArray = labels.stream().toArray(String[]::new);

    SymbolAxis predicted = new SymbolAxis("Predicted class label", labelArray);
    predicted.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    predicted.setLowerMargin(0.0D);
    predicted.setUpperMargin(0.0D);
    predicted.setAxisLinePaint(Color.white);
    predicted.setTickMarkPaint(Color.white);


    SymbolAxis actual = new SymbolAxis("Actual class label", labelArray);
    actual.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    actual.setLowerMargin(0.0D);
    actual.setUpperMargin(0.0D);
    actual.setAxisLinePaint(Color.white);
    actual.setTickMarkPaint(Color.white);
    actual.setInverted(true);

    NumberAxis scale = new NumberAxis("Fraction");
    scale.setAxisLinePaint(Color.white);
    scale.setTickMarkPaint(Color.white);
    scale.setTickLabelFont(new Font(Font.SANS_SERIF, 0, 7));


    XYPlot plot = new XYPlot(dataset, predicted, actual, block);
    plot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));

    JFreeChart chart = Chartable.create("Confusion Matrix", plot);
    chart.removeLegend();

    PaintScaleLegend paintscalelegend = new PaintScaleLegend(GRAY_PAINT_SCALE, scale);
    paintscalelegend.setStripOutlineVisible(false);
    paintscalelegend.setSubdivisionCount(20);
    paintscalelegend.setAxisLocation(AxisLocation.TOP_OR_RIGHT);
    paintscalelegend.setAxisOffset(5D);
    paintscalelegend.setMargin(new RectangleInsets(2D, 2D, 2D, 2D));
    paintscalelegend.setPadding(new RectangleInsets(5D, 5D, 5D, 5D));
    paintscalelegend.setStripWidth(10D);
    paintscalelegend.setPosition(RectangleEdge.RIGHT);
    chart.addSubtitle(paintscalelegend);
    return Chartable.applyTheme(chart);
  }

  @Override
  public Plot getPlot() {
    CombinedDomainCategoryPlot c = new CombinedDomainCategoryPlot();

    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (String label : getLabels()) {
      dataset.addValue(getPrecision(label), "Precision", label);
    }
    c.add(new CategoryPlot(dataset, new CategoryAxis("Label"), new NumberAxis("Precision"),
        new BarRenderer()));

    dataset = new DefaultCategoryDataset();
    for (String label : getLabels()) {
      dataset.addValue(getRecall(label), "Recall", label);
    }
    c.add(new CategoryPlot(dataset, new CategoryAxis("Label"), new NumberAxis("Recall"),
        new BarRenderer()));

    dataset = new DefaultCategoryDataset();
    for (String label : getLabels()) {
      dataset.addValue(getFMeasure(label, 2), "F-measure", label);
    }
    c.add(new CategoryPlot(dataset, new CategoryAxis("Label"), new NumberAxis("F-measure"),
        new BarRenderer()));
    c.setOrientation(PlotOrientation.HORIZONTAL);

    return c;
  }

  /**
   * Gets actual.
   *
   * @param actual the actual
   * @return the actual
   */
  public double getActual(String actual) {
    double sum = 0;
    for (String predicted : getLabels()) {
      sum += get(predicted, actual);
    }
    return sum;
  }

  /**
   * Inverts the GrayScalePaint, i.e., the largest getPosteriorProbabilities become black, and the
   * smallest becomes white
   */
  private static class InvertedGrayPaintScale extends GrayPaintScale {

    private InvertedGrayPaintScale(double lowerBound, double upperBound, int alpha) {
      super(lowerBound, upperBound, alpha);
    }

    @Override
    public Paint getPaint(double value) {
      Paint s = super.getPaint(value);
      if (s instanceof Color) {
        return invert((Color) s);
      }
      return s;
    }

    private Paint invert(Color s) {
      return new Color(255 - s.getRed(), 255 - s.getGreen(), 255 - s.getBlue(), s.getAlpha());
    }
  }
}
