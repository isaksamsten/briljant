package org.briljantframework.distance;

import java.util.stream.DoubleStream;

import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.Matrices;
import org.briljantframework.vector.Vector;

/**
 * <pre>
 * System.out.println(h);
 * int i = maxI, j = maxJ;
 * Vector.Builder al = new StringVector.Builder();
 * Vector.Builder bl = new StringVector.Builder();
 * while (i &gt; 0 &amp;&amp; j &gt; 0) {
 *   double current = h.get(i, j);
 *   double diag = h.get(i - 1, j - 1);
 *   double up = h.get(i, j - 1);
 *   double left = h.get(i - 1, j);
 *   if (current == 0) {
 *     break;
 *   }
 *   if (current == diag + (a.equals(i - 1, b, j - 1) ? match : miss)) {
 *     al.add(a, i - 1);
 *     bl.add(b, j - 1);
 *     i--;
 *     j--;
 *   } else if (current == left + gap) {
 *     al.add(a, i - 1);
 *     bl.addNA();
 *     i--;
 *   } else if (current == up + gap) {
 *     bl.add(b, j - 1);
 *     al.addNA();
 *     j--;
 *   }
 * }
 * while (i &gt; 0) {
 *   al.add(a, i - 1);
 *   bl.addNA();
 *   i--;
 * }
 * while (j &gt; 0) {
 *   bl.add(b, j - 1);
 *   al.addNA();
 *   j--;
 * }
 * Vector na = al.build();
 * Vector nb = bl.build();
 * for (int k = na.size() - 1; k &gt;= 0; k--) {
 *   System.out.println(na.toString(k) + &quot;\t&quot; + nb.toString(k));
 * }
 * assert maxScore == computeScore(na, nb);
 * </pre>
 * 
 * <pre>
 * private double computeScore(Vector na, Vector nb) {
 *   double score = 0;
 *   for (int i = 0; i &lt; na.size(); i++) {
 *     if (na.isNA(i) || nb.isNA(i)) {
 *       score += gap;
 *     } else if (na.equals(i, nb, i)) {
 *       score += match;
 *     }
 *   }
 *   return score;
 * }
 * </pre>
 * 
 * Created by isak on 26/02/15.
 */
public class SmithWatermanDistance implements Distance {

  private final double match, miss, gap;

  public SmithWatermanDistance(double match, double miss, double gap) {
    this.match = match;
    this.miss = miss;
    this.gap = gap;
  }


  @Override
  public double compute(double a, double b) {
    throw new UnsupportedOperationException();
  }

  @Override
  public double compute(Vector a, Vector b) {
    DoubleMatrix h = Matrices.newDoubleMatrix(a.size() + 1, b.size() + 1);
    double minDist = Double.POSITIVE_INFINITY;
    for (int i = 1; i < h.rows(); i++) {
      for (int j = 1; j < h.columns(); j++) {
        double sim = h.get(i - 1, j - 1) + (a.equals(i - 1, b, j - 1) ? match : miss);
        double left = h.get(i - 1, j) + gap;
        double up = h.get(i, j - 1) + gap;
        double score = DoubleStream.of(0, sim, up, left).min().getAsDouble();
        h.set(i, j, score);
        if (score < minDist) {
          minDist = score;
        }
      }
    }
    return minDist;
  }

  @Override
  public double max() {
    throw new UnsupportedOperationException();
  }

  @Override
  public double min() {
    throw new UnsupportedOperationException();
  }
}
