package org.briljantframework.example.matrix;

import static org.briljantframework.matrix.Matrices.zeros;

import org.briljantframework.matrix.Matrix;

/**
 * Created by Isak Karlsson on 02/01/15.
 */
public class Ex5 {

  public static void main(String[] args) {
    Matrix a = zeros(3, 3);
    System.out.println(a);

    a.put(0, 0, 10);
    a.put(0, 1, 9);
    a.put(0, 2, 8);
    for (int i = 0; i < a.size(); i++) {
      System.out.println(a.get(i));
    }
  }
}
