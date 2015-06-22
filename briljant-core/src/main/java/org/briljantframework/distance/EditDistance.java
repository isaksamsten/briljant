package org.briljantframework.distance;

import com.google.common.primitives.Doubles;

import org.briljantframework.Bj;
import org.briljantframework.matrix.DoubleArray;
import org.briljantframework.vector.Vector;

import java.util.function.DoubleSupplier;

/**
 * Created by isak on 24/03/15.
 */
public class EditDistance implements Distance {

  @Override
  public double compute(double a, double b) {
    return 0;
  }

  @Override
  public double compute(Vector a, Vector b) {
    if (a.size() < b.size()) {
      return compute(b, a);
    }

    if (b.size() == 0) {
      return a.size();
    }

    DoubleSupplier iter = new DoubleSupplier() {
      private int i = 0;

      @Override
      public double getAsDouble() {
        return i++;
      }
    };

    DoubleArray previousRow = Bj.doubleArray(b.size() + 1).assign(iter);
    for (int i = 0; i < a.size(); i++) {
      DoubleArray currentRow = Bj.doubleArray(b.size() + 1);
      currentRow.set(0, i + 1);
      for (int j = 0; j < b.size(); j++) {
        double insert = previousRow.get(j + 1) + 1;
        double delete = currentRow.get(j) + 1;
        double subs = previousRow.get(j) + (!a.equals(i, b, j) ? 1 : 0);
        currentRow.set(j + 1, Doubles.min(insert, delete, subs));
      }
      previousRow = currentRow;
    }

    return previousRow.get(previousRow.size() - 1);
  }

  @Override
  public double max() {
    return 0;
  }

  @Override
  public double min() {
    return 0;
  }
}
