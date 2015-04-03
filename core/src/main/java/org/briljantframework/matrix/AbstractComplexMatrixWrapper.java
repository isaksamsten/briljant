package org.briljantframework.matrix;

import org.briljantframework.Check;
import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.storage.Storage;

/**
 * @author Isak Karlsson
 */
abstract class AbstractComplexMatrixWrapper extends AbstractComplexMatrix {

  public AbstractComplexMatrixWrapper(int rows, int columns) {
    super(rows, columns);
  }

  @Override
  public ComplexMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new AbstractComplexMatrixWrapper(rows, columns) {
      @Override
      public void set(int index, Complex value) {
        AbstractComplexMatrixWrapper.this.set(index, value);
      }

      @Override
      public void set(int row, int column, Complex value) {
        AbstractComplexMatrixWrapper.this.set(row, column, value);
      }

      @Override
      public Storage getStorage() {
        return AbstractComplexMatrixWrapper.this.getStorage();
      }

      @Override
      public Complex get(int i, int j) {
        return AbstractComplexMatrixWrapper.this.get(i, j);
      }

      @Override
      public Complex get(int index) {
        return AbstractComplexMatrixWrapper.this.get(index);
      }


    };
  }

  @Override
  public ComplexMatrix newEmptyMatrix(int rows, int columns) {
    return ComplexMatrix.newMatrix(rows, columns);
  }

  @Override
  public boolean isView() {
    return true;
  }

  @Override
  public boolean isArrayBased() {
    return false;
  }


}
