# First taste of Briljant
[TOC]

## Introduction

Briljants main abstractions are the [Matrix](#matrix),
[DataFrame](#dataframe) and [Vector](#vector).

* The `DataFrame` is an immutable column wise heterogeneous data
  container and provides the essential tools for working with
  statistical data in Java. In particular, `DataFrame` provides

    * `NA`, i.e. missing (or non-existing) values, distinct from e.g.,
      `NaN`.
    * Operations for working with tabular data, similar to those found
      in spreadsheets and databases.
    * Different implementations with varying performance
      characteristics.
  
* The `Vector` is an immutable homogeneous data container. It supports
  `String` (i.e. categorical values), `Double` (i.e. double precision
  floating point numbers), `Binary` (i.e. true/false), `Integer` and
  `Complex` numbers. All providing a unique `NA` representation.

* The `Matrix` is a 2-dimensional data container of double precision
  floating point numbers, supporting a multitude of linear algebra
  operations.

### An example

```
import static org.briljantframework.matrix.Matrices.zeros;
import java.util.Random;
import org.briljantframework.matrix.Matrix;

public class Ex1 {

  public static void main(String[] args) {
    Random random = new Random(123);
    Matrix m = zeros(3, 5);
    m.assign(random::nextGaussian);
    /*- =>
     * -1.4380   0.2775   1.3520   1.0175  -0.4671
     *  0.6342   0.1843   0.3592   1.3716  -0.6711
     *  0.2261  -0.3652  -0.2053  -1.8902  -1.6794
     * Shape: 3x5
     */
    m.getShape(); // => 3x5
    assert m.rows() == 3;
    assert m.columns() == 5;
    assert m.size() == 15;
    
    mean(m, Axis.ROW);
    /*- =>
     * -0.1926   0.0322   0.5020   0.1663  -0.9392  
     * Shape: 1x5
     */
  }
}
```

## Matrix

There are several ways to create matrices, of which most are
implemented in the `org.briljantframework.matrix.Matrices` class.

For example, matrices can be created from one dimensional arrays,
2-dimensional arrays, `Iterable<? extends Number>`, `String`
representations and function applications.

```
hjdsa
```

### Creation

### Manipulation

### Basic operations

### Compound operations

## DataFrame

## Vector
