package org.briljantframework.example.matrix;

import org.briljantframework.Bj;
import org.briljantframework.complex.Complex;
import org.briljantframework.array.ComplexMatrix;
import org.briljantframework.array.DoubleMatrix;


/**
 * Created by Isak Karlsson on 02/01/15.
 */
public class Ex6 {

  public static void main(String[] args) {
    DoubleMatrix a = Bj.linspace(0, 2 * Math.PI, 100);

    a.map(Math::sqrt);
    a.map(Math::abs);
    a.map(Math::exp);
    a.map(Math::acos);
    a.map(Math::sin);
    a.map(Math::cos);
    a.map(value -> Math.pow(value, 10));

    a = Bj.linspace(-2, 2, 100);
    ComplexMatrix m = Bj.complexVector(100).assign(a, Complex::log);
    System.out.println(m);
    // ... etc
  }
}
