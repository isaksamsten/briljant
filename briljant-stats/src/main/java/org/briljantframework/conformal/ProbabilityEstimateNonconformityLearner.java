package org.briljantframework.conformal;

import java.util.Objects;

import org.briljantframework.Check;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.classification.Classifier;
import org.briljantframework.classification.Predictor;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class ProbabilityEstimateNonconformityLearner implements NonconformityLearner {

  private final Classifier classifier;
  private final ClassifierErrorFunction errorFunction;

  public ProbabilityEstimateNonconformityLearner(Classifier classifier,
      ClassifierErrorFunction errorFunction) {
    this.classifier = Objects.requireNonNull(classifier, "A classifier is required.");
    this.errorFunction = Objects.requireNonNull(errorFunction, "An error function is required");

  }

  @Override
  public NonconformityScorer fit(DataFrame x, Vector y) {
    Objects.requireNonNull(x, "Input data is required.");
    Objects.requireNonNull(y, "Input target is required.");
    Check.argument(x.rows() == y.size(), "The size of input data and input target don't match");
    Predictor probabilityEstimator = classifier.fit(x, y);
    Check.state(
        probabilityEstimator != null
            && probabilityEstimator.getCharacteristics().contains(
                Predictor.Characteristics.ESTIMATOR),
        "The produced predictor can't estimate probabilities");
    return new ProbEstNonconformityScorer(probabilityEstimator, errorFunction);
  }

  private static class ProbEstNonconformityScorer implements NonconformityScorer {

    private final Predictor predictor;
    private final ClassifierErrorFunction errorFunction;

    private ProbEstNonconformityScorer(Predictor predictor, ClassifierErrorFunction errorFunction) {
      this.predictor = predictor;
      this.errorFunction = errorFunction;
    }

    @Override
    public double nonconformity(Vector example, Object label) {
      Objects.requireNonNull(example, "Require an example.");
      return errorFunction.apply(predictor.estimate(example), label, predictor.getClasses());
    }

    @Override
    public DoubleArray nonconformity(DataFrame x, Vector y) {
      Objects.requireNonNull(x, "Input data required.");
      Objects.requireNonNull(y, "Input target required.");
      Check.argument(x.rows() == y.size(), "The size of input data and input target don't match.");
      return errorFunction.apply(predictor.estimate(x), y, predictor.getClasses());
    }
  }
}
