package org.briljantframework.matrix;

/**
 * Created by isak on 15/04/15.
 */
public interface Diagonal extends DoubleMatrix {

  int diagonalSize();

  double getDiagonal(int i);

  void setDiagonal(int i, double v);

}
