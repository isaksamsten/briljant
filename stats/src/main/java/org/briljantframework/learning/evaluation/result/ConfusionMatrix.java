package org.briljantframework.learning.evaluation.result;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.briljantframework.chart.Chartable;
import org.briljantframework.data.column.Column;
import org.briljantframework.data.values.Value;
import org.briljantframework.learning.Predictions;
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

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * In the field of machine learning, a confusion matrix, also known as a contingency table or an error matrix [1] , is a
 * specific table layout that allows visualization of the performance of an algorithm, typically a supervised learning
 * one (in unsupervised learning it is usually called a matching matrix). Each column of the matrix represents the
 * instances in a predicted class, while each row represents the instances in an actual class. The name stems from the
 * fact that it makes it easy to see if the system is confusing two classes (i.e. commonly mislabeling one as another).
 * <p>
 * Example:
 * <pre>
 *           acc       unacc       vgood       good
 *   acc     344.0     28.0        3.0         15.0
 *   unacc   29.0      1179.0      0.0         0.0
 *   vgood   5.0       0.0         61.0        9.0
 *   good    6.0       3.0         1.0         45.0
 * </pre>
 * <p>
 * The sum of diagonal entries divided by the sum of all entries are called the accuracy. The error is 1 - accuracy.
 * <p>
 * Created by isak on 02/10/14.
 */
public class ConfusionMatrix implements Chartable {

    private static final InvertedGrayPaintScale GRAY_PAINT_SCALE = new InvertedGrayPaintScale(0, 1, 200);

    private final Map<Value, Map<Value, Double>> matrix;
    private final Set<Value> labels;
    private final double sum;

    /**
     * Instantiates a new Confusion matrix.
     *
     * @param matrix the matrix
     * @param labels the labels
     * @param sum    the sum
     */
    public ConfusionMatrix(Map<Value, Map<Value, Double>> matrix, Set<Value> labels, double sum) {
        this.matrix = Preconditions.checkNotNull(matrix, "Matrix cannot be null");
        this.labels = Collections.unmodifiableSet(Preconditions.checkNotNull(labels, "Labels cannot be null"));

        this.sum = sum;
    }

    /**
     * Create confusion matrix.
     *
     * @param predictions the predictions
     * @param column      the target
     * @return the confusion matrix
     */
    public static ConfusionMatrix create(Predictions predictions, Column column) {
        Preconditions.checkArgument(predictions.size() == column.size());

        Map<Value, Map<Value, Double>> matrix = new HashMap<>();
        Set<Value> labels = new HashSet<>();
        double sum = 0;
        for (int i = 0; i < predictions.size(); i++) {
            Value predicted = predictions.get(i).getValue();
            Value actual = column.getValue(i);

            Map<Value, Double> actuals = matrix.get(predicted);
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
        return labels.stream().mapToDouble(value -> getFMeasure(value, beta)).summaryStatistics().getAverage();
    }

    /**
     * Gets f measure.
     *
     * @param target the target
     * @param beta   the beta
     * @return the f measure
     */
    public double getFMeasure(Value target, double beta) {
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
    public double getPrecision(Value target) {
        double tp = get(target, target);
        if (tp == 0) {
            return 0;
        } else {
            double conditional = 0.0;
            for (Value actual : labels) {
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
    public double getRecall(Value target) {
        double tp = get(target, target);

        if (tp == 0) {
            return 0;
        } else {
            double conditional = 0.0;
            for (Value actual : getLabels()) {
                conditional += get(actual, target);
            }
            return conditional > 0 ? tp / conditional : 0;
        }
    }

    /**
     * Get double.
     *
     * @param predicted the predicted
     * @param actual    the actual
     * @return the double
     */
    public double get(Value predicted, Value actual) {
        Map<Value, Double> values = matrix.get(predicted);
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
    public Set<Value> getLabels() {
        return labels;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        int longest = labels.stream().mapToInt(x -> x.toString().length()).summaryStatistics().getMax();
        if (longest < 3) {
            longest = 3;
        }

        int longestValue = 0;
        for (Value p : labels) {
            for (Value n : labels) {
                int len = Double.toString(get(p, n)).length();
                if (len > longestValue) {
                    longestValue = len;
                }
            }
        }

        builder.append(Strings.repeat(" ", longest + 3));
        for (Value value : labels) {
            builder.append(value);
            builder.append(Strings.repeat(" ", longestValue + 1));
        }

        builder.append("\n");
        for (Value predicted : labels) {
            builder.append(Strings.padEnd(predicted.toString(), longest + 3, ' '));

            for (Value actual : labels) {
                String valueStr = Double.toString(get(predicted, actual));
                builder.append(valueStr);
                builder.append(Strings.repeat(" ", actual.toString().length() + 1 + longestValue - valueStr.length()));
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
     * Gets accuracy.
     *
     * @return the accuracy
     */
    public double getAccuracy() {
        double diagonal = 0.0;
        for (Value value : labels) {
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
        List<Value> labels = new ArrayList<>(getLabels());
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
        dataset.addSeries("Class Labels", new double[][]{x, y, z});
        XYBlockRenderer block = new XYBlockRenderer();
        block.setPaintScale(GRAY_PAINT_SCALE);
        String[] labelArray = labels.stream().map(Value::toString).toArray(String[]::new);

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

    /**
     * Gets actual.
     *
     * @param actual the actual
     * @return the actual
     */
    public double getActual(Value actual) {
        double sum = 0;
        for (Value predicted : getLabels()) {
            sum += get(predicted, actual);
        }
        return sum;
    }

    @Override
    public Plot getPlot() {
        CombinedDomainCategoryPlot c = new CombinedDomainCategoryPlot();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Value label : getLabels()) {
            dataset.addValue(getPrecision(label), "Precision", label);
        }
        c.add(new CategoryPlot(dataset, new CategoryAxis("Label"), new NumberAxis("Precision"), new BarRenderer()));

        dataset = new DefaultCategoryDataset();
        for (Value label : getLabels()) {
            dataset.addValue(getRecall(label), "Recall", label);
        }
        c.add(new CategoryPlot(dataset, new CategoryAxis("Label"), new NumberAxis("Recall"), new BarRenderer()));

        dataset = new DefaultCategoryDataset();
        for (Value label : getLabels()) {
            dataset.addValue(getFMeasure(label, 2), "F-measure", label);
        }
        c.add(new CategoryPlot(dataset, new CategoryAxis("Label"), new NumberAxis("F-measure"), new BarRenderer()));
        c.setOrientation(PlotOrientation.HORIZONTAL);

        return c;
    }

    /**
     * Inverts the GrayScalePaint, i.e., the largest values become black, and the smallest becomes white
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
