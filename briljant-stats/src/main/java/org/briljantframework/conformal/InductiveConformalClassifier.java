package org.briljantframework.conformal;

import java.util.Objects;

import org.briljantframework.Check;
import org.briljantframework.array.Arrays;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.classification.AbstractClassifier;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.Vectors;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class InductiveConformalClassifier extends AbstractClassifier implements ConformalClassifier {

  private final Nonconformity nonconformity;

  /**
   * [no-calibration, 1] double array of nonconformity scores for the calibration set used to
   * estimate the p-values when performing a conformal prediction
   */
  private DoubleArray calibration;

  protected InductiveConformalClassifier(Nonconformity nonconformity, Vector classes) {
    super(classes);
    this.nonconformity = Objects.requireNonNull(nonconformity, "Requires nonconformity scorer");
  }

  @Override
  public void calibrate(DataFrame x, Vector y) {
    calibration = nonconformity.estimate(x, y);
  }

  @Override
  public DoubleArray estimate(Vector example) {
    Check.state(calibration != null, "the conformal predictor must be calibrated");
    double nCal = calibration.size();
    DoubleArray significance = Arrays.doubleArray(getClasses().size());
    for (int i = 0; i < significance.size(); i++) {
      Object trueClass = getClasses().loc().get(Object.class, i);
      double testNc = nonconformity.estimate(example, trueClass);
      double nGt = calibration.filter(score -> score > testNc).size();
      double nEq = calibration.filter(score -> score == testNc).size() + 1;
      significance.set(i, nGt / (nCal + 1) + nEq / (nCal + 1));
    }

    return significance;
  }

  /**
   * @author Isak Karlsson <isak-kar@dsv.su.se>
   */
  public static class Learner implements ConformalClassifier.Learner {

    private final Nonconformity.Learner learner;

    public Learner(Nonconformity.Learner learner) {
      this.learner = Objects.requireNonNull(learner);
    }

    @Override
    public ConformalClassifier fit(DataFrame x, Vector y) {
      Objects.requireNonNull(x, "Input data is required.");
      Objects.requireNonNull(y, "Input target is required.");
      Check.argument(x.rows() == y.size(), "The size of input data and input target don't match.");
      return new InductiveConformalClassifier(learner.fit(x, y), Vectors.unique(y));
    }

  }
}
