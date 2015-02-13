package org.briljantframework.evaluation.result;

import java.util.List;

import org.briljantframework.classification.AbstractEnsemble;
import org.briljantframework.classification.Predictor;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.Record;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.Matrices;
import org.briljantframework.vector.Vector;

/**
 * @author Isak Karlsson
 */
public class EnsembleVariance extends AbstractMeasure {
  protected EnsembleVariance(Builder builder) {
    super(builder);
  }

  @Override
  public String getName() {
    return "Ensemble Variance";
  }

  public static class Builder extends AbstractMeasure.Builder {

    protected Builder(Vector domain) {
      super(domain);
    }

    @Override
    public void compute(Sample sample, Predictor predictor, DataFrame x, Vector predicted,
        DoubleMatrix probabilities, Vector truth) {
      if (predictor instanceof AbstractEnsemble.Model) {
        List<? extends Predictor> members = ((AbstractEnsemble.Model) predictor).getModels();
        double meanVar = 0;
        int rows = x.rows();

        for (Record record : x) {
          DoubleMatrix mean = Matrices.newDoubleVector(predictor.getClasses().size());
          for (Predictor member : members) {
            mean.assign(member.predictProba(record), (acc, v) -> acc + (v / members.size()));
          }
          double var = 0;
          for (Predictor member : members) {
            var += Matrices.norm(mean, member.predictProba(record), 2);
          }
          meanVar += var / members.size();
        }


        addComputedValue(sample, meanVar / rows);
      } else {
        addComputedValue(sample, Double.NaN);
      }
    }

    @Override
    public Measure build() {
      return new EnsembleVariance(this);
    }
  }
}
