package org.briljantframework.matrix;

import org.briljantframework.Check;
import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.storage.Storage;

/**
 * @author Isak Karlsson
 */
abstract class AsComplexMatrix extends AbstractComplexMatrix {

  public AsComplexMatrix(MatrixFactory bj, int rows, int columns) {
    super(bj, rows, columns);
  }

  @Override
  public ComplexMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new AsComplexMatrix(getMatrixFactory(), rows, columns) {
      @Override
      public void set(int index, Complex value) {
        AsComplexMatrix.this.set(index, value);
      }

      @Override
      public void set(int row, int column, Complex value) {
        AsComplexMatrix.this.set(row, column, value);
      }

      @Override
      public Complex get(int i, int j) {
        return AsComplexMatrix.this.get(i, j);
      }

      @Override
      public Complex get(int index) {
        return AsComplexMatrix.this.get(index);
      }

      @Override
      public Storage getStorage() {
        return AsComplexMatrix.this.getStorage();
      }
    };
  }

  @Override
  public ComplexMatrix newEmptyMatrix(int rows, int columns) {
    return bj.complexMatrix(rows, columns);
  }

  @Override
  public boolean isView() {
    return true;
  }


}
