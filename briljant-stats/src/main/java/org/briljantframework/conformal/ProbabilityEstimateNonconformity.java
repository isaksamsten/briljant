package org.briljantframework.conformal;

import java.util.Objects;

import org.briljantframework.Check;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.classification.Classifier;
import org.briljantframework.classification.ClassifierCharacteristic;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class ProbabilityEstimateNonconformity implements Nonconformity {

  private final Classifier classifier;
  private final ClassificationErrorFunction errorFunction;

  ProbabilityEstimateNonconformity(Classifier classifier,
                                   ClassificationErrorFunction errorFunction) {
    this.classifier = classifier;
    this.errorFunction = errorFunction;
  }

  @Override
  public double estimate(Vector example, Object label) {
    Objects.requireNonNull(example, "Require an example.");
    return errorFunction.apply(classifier.estimate(example), label, classifier.getClasses());
  }

  @Override
  public DoubleArray estimate(DataFrame x, Vector y) {
    Objects.requireNonNull(x, "Input data required.");
    Objects.requireNonNull(y, "Input target required.");
    Check.argument(x.rows() == y.size(), "The size of input data and input target don't match.");
    return errorFunction.apply(classifier.estimate(x), y, classifier.getClasses());
  }

  /**
   * @author Isak Karlsson <isak-kar@dsv.su.se>
   */
  public static class Learner implements Nonconformity.Learner {

    private final Classifier.Learner classifier;
    private final ClassificationErrorFunction errorFunction;

    public Learner(Classifier.Learner classifier,
                   ClassificationErrorFunction errorFunction) {
      this.classifier = Objects.requireNonNull(classifier, "A classifier is required.");
      this.errorFunction = Objects.requireNonNull(errorFunction, "An error function is required");

    }

    @Override
    public Nonconformity fit(DataFrame x, Vector y) {
      Objects.requireNonNull(x, "Input data is required.");
      Objects.requireNonNull(y, "Input target is required.");
      Check.argument(x.rows() == y.size(), "The size of input data and input target don't match");
      Classifier probabilityEstimator = classifier.fit(x, y);
      Check.state(
          probabilityEstimator != null
              && probabilityEstimator.getCharacteristics().contains(
                  ClassifierCharacteristic.ESTIMATOR),
          "The produced classifier can't estimate probabilities");
      return new ProbabilityEstimateNonconformity(probabilityEstimator, errorFunction);
    }

  }
}
