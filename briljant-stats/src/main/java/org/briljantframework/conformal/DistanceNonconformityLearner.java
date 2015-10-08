package org.briljantframework.conformal;

import org.briljantframework.Bj;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;
import org.briljantframework.classification.KNearestNeighbors;
import org.briljantframework.data.Is;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.distance.Distance;
import org.briljantframework.distance.Euclidean;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class DistanceNonconformityLearner implements NonconformityLearner {

  private final KNearestNeighbors nearestNeighbors;
  private final int k;

  public DistanceNonconformityLearner(int k) {
    this(k, Euclidean.getInstance());
  }

  public DistanceNonconformityLearner(int k, Distance distance) {
    this.nearestNeighbors = new KNearestNeighbors(k, distance);
    this.k = k;
  }

  @Override
  public NonconformityScorer fit(DataFrame x, Vector y) {
    return new DistanceNonconformityScorer(nearestNeighbors.fit(x, y), k);
  }

  private static class DistanceNonconformityScorer implements NonconformityScorer {

    private final KNearestNeighbors.Predictor predictor;
    private final int k;

    public DistanceNonconformityScorer(KNearestNeighbors.Predictor predictor, int k) {
      this.predictor = predictor;
      this.k = k;
    }

    @Override
    public double nonconformity(Vector example, Object label) {
      Vector labels = predictor.getTarget();
      DoubleArray distances = predictor.distance(example);
      IntArray order = Bj.order(distances);
      double posDist = 0;
      double negDist = 0;
      int kp = 0;
      int kn = 0;
      for (int i = 0; i < order.size() && (kp < k || kn < k); i++) {
        int o = order.get(i);
        double distance = distances.get(o);
        if (Is.equal(labels.loc().get(o), label) && kp < k) {
          posDist += distance;
          kp++;
        } else if (!Is.equal(labels.loc().get(o), label) && kn < k) {
          negDist += distance;
          kn++;
        }
      }

      if (Double.isNaN(posDist)) {
        return Double.POSITIVE_INFINITY;
      } else if (Double.isNaN(negDist)) {
        return Double.NEGATIVE_INFINITY;
      }
      return (posDist / negDist) / example.size();
    }

    @Override
    public DoubleArray nonconformity(DataFrame x, Vector y) {
      DoubleArray array = Bj.doubleArray(x.rows());
      for (int i = 0; i < x.rows(); i++) {
        array.set(i, nonconformity(x.loc().getRecord(i), y.loc().get(i)));
      }
      return array;
    }
  }
}
