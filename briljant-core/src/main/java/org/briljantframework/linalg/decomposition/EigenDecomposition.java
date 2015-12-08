package org.briljantframework.linalg.decomposition;

import org.apache.commons.math3.util.Precision;
import org.briljantframework.Check;
import org.briljantframework.array.Arrays;
import org.briljantframework.array.DoubleArray;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public abstract class EigenDecomposition {
  private static final double EPSILON = 1e-12;

  public DoubleArray getD() {
    DoubleArray realEigenvalues = getRealEigenvalues();
    DoubleArray imagEigenvalues = getImagEigenvalues();
    Check.state(realEigenvalues.size() == imagEigenvalues.size());
    int m = realEigenvalues.size();
    DoubleArray d = Arrays.newDoubleArray(m, m);
    for (int i = 0; i < m; i++) {
      if (Precision.compareTo(imagEigenvalues.get(i), 0.0, EPSILON) > 0) {
        d.set(i, i + 1, imagEigenvalues.get(i));
      } else if (Precision.compareTo(imagEigenvalues.get(i), 0.0, EPSILON) < 0) {
        d.set(i, i - 1, imagEigenvalues.get(i));
      }
    }
    d.getDiagonal().assign(realEigenvalues);
    return d;
  }

  public abstract DoubleArray getRealEigenvalues();

  public abstract DoubleArray getImagEigenvalues();

  public DoubleArray getV() {
    return getEigenVectors();
  }

  public abstract DoubleArray getEigenVectors();
}
