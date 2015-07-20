package org.briljantframework.example.matrix;

import org.briljantframework.Bj;
import org.briljantframework.array.DoubleMatrix;

/**
 * Created by Isak Karlsson on 02/01/15.
 */
public class Ex5 {

  public static void main(String[] args) {
    DoubleMatrix a = Bj.doubleMatrix(3, 3);
    System.out.println(a);

    a.set(0, 0, 10);
    a.set(0, 1, 9);
    a.set(0, 2, 8);
    for (int i = 0; i < a.size(); i++) {
      System.out.println(a.get(i));
    }
  }
}
