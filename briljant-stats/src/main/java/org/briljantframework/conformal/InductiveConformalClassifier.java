package org.briljantframework.conformal;

import java.util.Objects;

import org.briljantframework.Bj;
import org.briljantframework.Check;
import org.briljantframework.array.BooleanArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.classification.AbstractPredictor;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.Vectors;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class InductiveConformalClassifier implements ConformalClassifier {

  private final NonconformityLearner nonconformityLearner;

  public InductiveConformalClassifier(NonconformityLearner nonconformityLearner) {
    this.nonconformityLearner = Objects.requireNonNull(nonconformityLearner);
  }

  @Override
  public ConformalPredictor fit(DataFrame x, Vector y) {
    Objects.requireNonNull(x, "Input data is required.");
    Objects.requireNonNull(y, "Input target is required.");
    Check.argument(x.rows() == y.size(), "The size of input data and input target don't match.");
    return new Predictor(nonconformityLearner.fit(x, y), Vectors.unique(y));
  }

  /**
   * @author Isak Karlsson <isak-kar@dsv.su.se>
   */
  public static class Predictor extends AbstractPredictor implements ConformalPredictor {

    /**
     * The nonconformity scores
     */
    private final NonconformityScorer nonconformityScorer;

    /**
     * [no-calibration, 1] double array of nonconformity scores for the calibration set used to
     * estimate the p-values when performing a conformal prediction
     */
    private DoubleArray calibration;

    protected Predictor(NonconformityScorer nonconformityScorer, Vector classes) {
      super(classes);
      this.nonconformityScorer =
          Objects.requireNonNull(nonconformityScorer, "Requires nonconformity scorer");
    }

    @Override
    public void calibrate(DataFrame x, Vector y) {
      calibration = nonconformityScorer.nonconformity(x, y);
    }

    @Override
    public DoubleArray estimate(Vector example) {
      Check.state(calibration != null, "the conformal predictor must be calibrated");
      DoubleArray significance = Bj.doubleArray(getClasses().size());
      for (int i = 0; i < significance.size(); i++) {
        Object trueClass = getClasses().loc().get(Object.class, i);
        double testNc = nonconformityScorer.nonconformity(example, trueClass);
        double nCal = calibration.size();
        double nGt = calibration.filter(score -> score > testNc).size();
        double nEq = calibration.filter(score -> score == testNc).size() + 1;
        significance.set(i, nGt / (nCal + 1) + nEq / (nCal + 1));
      }

      return significance;
    }

    @Override
    public BooleanArray conformalPredict(Vector example, double significance) {
      return estimate(example).satisfies(v -> v > significance);
    }
  }
}
