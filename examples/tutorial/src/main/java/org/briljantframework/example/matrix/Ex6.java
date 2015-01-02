package org.briljantframework.example.matrix;

import static org.briljantframework.matrix.Matrices.*;

import org.briljantframework.matrix.Matrix;

/**
 * Created by Isak Karlsson on 02/01/15.
 */
public class Ex6 {

  public static void main(String[] args) {
    Matrix a = linspace(0, 2 * Math.PI, 100);

    a.map(Math::sqrt);
    a.map(Math::abs);
    a.map(Math::exp);
    a.map(Math::acos);
    a.map(Math::sin);
    a.map(Math::cos);
    a.map(value -> Math.pow(value, 10));
    // ... etc

    signum(a);
    sqrt(a);
    pow(a, 10);
    // ... etc
  }
}
