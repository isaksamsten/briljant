package org.briljantframework.example.matrix;

import static org.briljantframework.matrix.Doubles.*;

import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.ArrayComplexMatrix;
import org.briljantframework.matrix.ComplexMatrix;
import org.briljantframework.matrix.DoubleMatrix;


/**
 * Created by Isak Karlsson on 02/01/15.
 */
public class Ex6 {

  public static void main(String[] args) {
    DoubleMatrix a = linspace(0, 2 * Math.PI, 100);

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


    a = linspace(-2, 2, 100);
    ComplexMatrix m = new ArrayComplexMatrix(100, 1).assign(a, Complex::log);
    System.out.println(m);
    // ... etc
  }
}
