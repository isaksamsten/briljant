package org.briljantframework.similiarity;

import com.google.common.primitives.Doubles;

import org.briljantframework.Bj;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.Vector;

/**
 * @author Isak Karlsson
 */
public class SmithWatermanSimilarity implements Similarity {

  private final double match, miss, gap;

  public SmithWatermanSimilarity(double match, double miss, double gap) {
    this.match = match;
    this.miss = miss;
    this.gap = gap;
  }

  @Override
  public double compute(Vector a, Vector b) {
    DoubleMatrix h = Bj.doubleMatrix(a.size() + 1, b.size() + 1);
    double maxScore = Double.NEGATIVE_INFINITY;
    int maxI = 0, maxJ = 0;
    for (int i = 1; i < h.rows(); i++) {
      for (int j = 1; j < h.columns(); j++) {
        double sim = h.get(i - 1, j - 1) + (a.equals(i - 1, b, j - 1) ? match : miss);
        double left = h.get(i, j - 1) + gap;
        double up = h.get(i - 1, j) + gap;
        double score = Doubles.max(0, sim, up, left);
        h.set(i, j, score);
        if (score > maxScore) {
          maxScore = score;
          maxI = i;
          maxJ = j;
        }
      }
    }
//    int maxAlign = 0, length = 0;
//    int i = maxI, j = maxJ;
//    while (i > 0 && j > 0) {
//      double current = h.get(i, j);
//      double getDiagonal = h.get(i - 1, j - 1);
//      double up = h.get(i - 1, j);
//      double left = h.get(i, j - 1);
//      if (current == 0) {
//        break;
//      } else if (current == getDiagonal + (a.equals(i - 1, b, j - 1) ? match : miss)) {
//        length++;
//        if (length > maxAlign) {
//          maxAlign = length;
//        }
//        i--;
//        j--;
//      } else if (current == left + gap) {
//        j--;
//        length = 0;
//      } else if (current == up + gap) {
//        i--;
//        length = 0;
//      }
//    }
//    while (i > 0) {
//      // length++;
//      i--;
//    }
//    while (j > 0) {
//      // length++;
//      j--;
//    }

    // int i = maxI, j = maxJ;
    // Vector.Builder al = new StringVector.Builder();
    // Vector.Builder bl = new StringVector.Builder();
    // while (i > 0 && j > 0) {
    // double current = h.get(i, j);
    // double getDiagonal = h.get(i - 1, j - 1);
    // double up = h.get(i, j - 1);
    // double left = h.get(i - 1, j);
    // if (current == 0) {
    // break;
    // }
    // if (current == getDiagonal + (a.equals(i - 1, b, j - 1) ? match : miss)) {
    // al.add(a, i - 1);
    // bl.add(b, j - 1);
    // i--;
    // j--;
    // } else if (current == left + gap) {
    // al.add(a, i - 1);
    // bl.addNA();
    // i--;
    // } else if (current == up + gap) {
    // bl.add(b, j - 1);
    // al.addNA();
    // j--;
    // }
    // }
    // while (i > 0) {
    // al.add(a, i - 1);
    // bl.addNA();
    // i--;
    // }
    // while (j > 0) {
    // bl.add(b, j - 1);
    // al.addNA();
    // j--;
    // }
    // Vector na = al.build();
    // Vector nb = bl.build();
    //
    // if (na.size() > 10) {
    // System.out.println(na.size() + " " + nb.size());
    // System.out.println(" ---- start ----- ");
    // for (int k = na.size() - 1; k > 0; k--) {
    // System.out.println(na.toString(k) + " " + nb.toString(k));
    // }
    // System.out.println(" ---------- " + computeScore(na, nb) + " " + minDist);
    // }

    return maxScore; /*/ Math.max(a.size(), b.size());*/

  }
}
