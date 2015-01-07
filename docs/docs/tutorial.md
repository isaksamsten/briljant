# First taste of Briljant
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

## An example ##

```
import static org.briljantframework.matrix.Matrices.*;
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

!!! warning "Imports"

    In the examples below, `import org.briljantframework.matrix.*` and
    `import static org.briljantframework.matrix.Matrices.*` are
    implicit

!!! info "Matrix implementations"

    Matrix is a general interface of which several implementations
    exist. Most operations in `Matrices` either return a matrix of the
    same type as its input or
    `org.briljantframework.matrix.ArrayMatrix`.

### Creation ###

```
public class Ex2 {
  public static void main(String[] args) {
    Matrix a = range(0, 10).reshape(5, 2);
    Matrix b = matrix(new double[][] {
      new double[] {0, 5},
      new double[] {1, 6},
      new double[] {2, 7},
      new double[] {3, 8},
      new double[] {4, 9}
    });
    Matrix c = matrix(0, 1, 2, 3, 4, 5, 6, 7, 8, 9).reshape(5, 2);
    Matrix d = parseMatrix("0,5;1,6;2,7;3,8;4,9");
  }
}
```

In the example above, `a` is created using a range of numbers and
reshaping it to a matrix with 5 rows and 2 columns; `b` is created,
rather verbosely, using a multi-dimensional array; `c` is created
using a flat array and reshaping it to a 5 by 2 matrix. Finally, `d`
is created by parsing a string representation (similar to the Octave
equivalent).

`range(int, int)` creates a column-vector (i.e. a m-by-1 matrix) with
values between `start` and `end` with `1` increments. The 3-arity
version, `range(int, int, int)` can be used if another `step`-size is
needed. Since it's not possible to predict the number of values in
floating point ranges, `linspace(double, double, double)` should be
use instead. `linspace` receives an additional argument which denotes
the number elements returned. For example, `linspace(0, 10,
50).reshape(10, 5)` creates a 10-by-5 matrix.

In many cases, the size of a matrix is known but its contents is
not. Therefore, Briljant provides several ways of creating empty matrices with
placeholder values.

```
Matrix a = zeros(10, 10); // 10-by-10 matrix with elements set to 0
Matrix b = ones(10, 10);  // 10-by-10 matrix with elements set to 1
Matrix c = fill(10, 10, Double.NaN) // 10 by 10 matrix with elements set to NaN
Matrix d = linspace(0, 2 * Math.PI, 100).mapi(Math::sin);
```

#### See also ####

[Matrices#matrix](examples.md#matrix),
[Matrices#linspace](examples.md#linspace),
[Matrices#parseMatrix](examples.md#parseMatrix),
[Matrices#zeros](examples.md#zeros),
[Matrices#ones](examples.md#ones),
[Matrices#fill](examples.md#fill),
[Matrices#rand](examples.md#rand),
[Matrices#randn](examples.md#randn)

### Manipulation ###

An already created matrix can be assigned values via the `assign`
method. An example of this have already been seen above and a few more
can be seen below. Assign modifies and returns the receiver, this
makes it possible to reuse matrices (and hence avoid large memory
allocations).

```
public class Ex3 {
  public static void main(String[] args) {
    Matrix a = zeros(10, 10);
    Matrix b = randn(10, 10);

    a.assign(b); // a contains b
    a.assign(b, Math::sqrt); // assign b and square the elements
    a.assign(b.mapi(Math::sqrt));

    Matrix x = a.assign(b, e -> e * e).reshape(5, 20);

    // Take the first row
    Matrix firstRow = b.getRowView(0);

    // Assign zeroes to the first row
    firstRow.assign(zeros(1, 10));

    // Take the upper 4 elements of b
    Matrix up = b.getView(0, 0, 4, 4);

    // Square the upper left 4-by-4 corner
    b.getView(0, 0, 4, 4).mapi(x -> x * x);
  }
}
    
```

Note that modification of views propagates to the original data. To
break a copy free from its original use `Matrix#copy()`.

Matrices are mutable (and hence unsafe to mutate in
parallel). Mutations are done using operations prepended with **i**,
`put(int, int, double)` and `put(int, double)`.


!!! info "Column-major or row-major"

    Matrices in Briljant are implemented in column-major order, which
    is a way of arranging multidimensional arrays in linear memory.

    In column-major order columns are stored contiguous in memory
    whereas in row-major order rows are stored contiguous in
    memory. For example, in the c programming language
    multidimensional arrays are stored in row-major order and in e.g.,
    Fortran multidimensional arrays are stored in column-major
    order. An array with two rows `8, 9` and `3, 5` would be stored
    as:

    | Arrangement        | 0 | 1 | 2 | 3 |
    | ------------------ | - | - | - | - |
    | Column-major value | 8 | 3 | 9 | 5 |
    | Row-major value    | 8 | 9 | 3 | 5 |

    In Briljant, `Matrix#get(int)` and `Matrix#put(int, double)` are
    expected to work as if the matrix was used in the column-major
    arrangement. Hence, for the array above `get(1)` should return `3`
    and `put(2, 10)` should change the `9` to a `10`.

    Due to cache locality, it is always faster to traverse arrays in
    column-major order, i.e. while varying columns slower.

```
Matrix a = zeros(3, 3);

System.out.println(a);k-b

a.put(0, 0, 10);
a.put(0, 1, 9);
a.put(0, 2, 8);
for (int i = 0; i < a.size(); i++) {
  System.out.println(a.get(i));
}
```

As indicated in the information box, matrices are stored in
column-major order. Hence, the output of the example is

```
 0.0000   0.0000   0.0000  
 0.0000   0.0000   0.0000  
 0.0000   0.0000   0.0000  
Shape: 3x3
10.0
0.0
0.0
9.0
0.0
0.0
8.0
0.0
0.0
```

#### See also ####

[Matrix#assign](examples.md#assign),
[Matrix#getRowView](examples.md#getRowView),
[Matrix#getColumnView](examples.md#getColumnView),
[Matrix#reshape](examples.md#reshape),
[Matrix#getView](examples.md#getView)

### Basic operations ###

Unlike most matrix languages, Java does not support user defined
operators. Hence, addition, subtraction, multiplication and division
are performed using named method calls. For brevity, these are
shortened to `mul` (for multiplication), `mmul` (for
matrix-multiplication), `add` (for addition) and `sub` (for
subtraction). The `Matrix` interface provides several overloads to is
some cases improve performance.

!!! info "Performance notice"

    Briljant provides two default implementations of `Matrix`:
    `ArrayMatrix` and `HashMatrix`, where the former is backed by a
    1-dimensional double array and the latter by an efficient
    primitive hash map. A `Matrix` implementation should override
    `Matrix#isArrayBased` to denote the fact that
    `Matrix#asDoubleArray` returns the backing array. For
    `ArrayMatrix` `isArrayBased` returns true and for `HashMatrix` it
    returns false. The `ArrayMatrix` specializes the naive default
    matrix-matrix multiplication and delegates this operation to a
    highly optimized Fortran implementations. For this reason,
    `ArrayMatrix` should be preferred in almost all cases.

```
public class Ex4 {
  public static void main(String[] args) {
    Utils.setRandomSeed(123);
    Matrix a = fill(3, 3, 2);
    Matrix b = randn(3, 3);
    b.add(a);
    b.sub(a);
    b.mul(a);
    b.mmul(a);
    b.add(1, a, -1); // == b.add(a.mul(-1));
    b.add(1, a, -1).equalsTo(b.add(a.mul(-1)));
    /*-
     * true  true  true  
     * true  true  true  
     * true  true  true  
     * Shape: 3x3
     */
  }
}
```

##### See also #####

[Matrix#mul](examples.md#mul), [Matrix#add](examples.md#add),
[Matrix#sub](examples.md#sub), [Matrix#div](examples.md#div),
[Matrix#mmul](examples.md#mmul),[Matrix#muli](examples.md#muli),
[Matrix#addi](examples.md#addi), [Matrix#subi](examples.md#subi),
[Matrix#divi](examples.md#divi), [Matrix#lessThan](examples.md#lessThan),
[Matrix#greaterThan](examples.md#greaterThan),
[Matrix#equalsTo](examples.md#equalsTo)

### Element wise operations ###

Briljant reuses a large number of element wise operations found for
`double` values in Java. In Briljant, these are referred to as element
wise functions and produces new matrices as output.

```
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
```

!!! success "In-place element wise operations"

    Remember that `Matrix#mapi` can be used to mutate the matrix.

!!! warning "Complex matrices"

    Remember that, for example, `Math.sqrt(-2)` return NaN. Complex
    numbers allow one to solve equations without real solutions. In
    Briljant, complex numbers are implemented in the
    `org.briljantframework.complex.Complex` class and matrices of such
    values are implemented in
    `org.briljantframework.matrix.ComplexMatrix`. Given, `Matrix x =
    rand(10, 10).muli(-10)` the element wise square root can be
    calculated (it the complex plane) as `ComplexMatrix z = new
    ArrayComplexMatrix(10, 10).assign(x, Complex::sqrt)`


#### Example ####

As an example of Briljants expressiveness, one of Julias performance
tests is implemented below. Due to the performance overhead of copying
an array from the JVM to Fortran, its performance is competitive, but
not as fast as its Julia counterpart.


```
private static Random random = new Random();

private static double[] randmatstat_Briljant(int t) {
  int n = 5;
  Matrix p = zeros(n, n * 4);
  Matrix q = zeros(n * 2, n * 2);
  Matrix v = zeros(t, 1);
  Matrix w = zeros(t, 1);

  for (int i = 0; i < t; i++) {
    p.getView(0, 0, n, n).assign(random::nextGaussian);
    p.getView(0, n, n, n).assign(random::nextGaussian);
    p.getView(0, n * 2, n, n).assign(random::nextGaussian);
    p.getView(0, n * 3, n, n).assign(random::nextGaussian);

    q.getView(0, 0, n, n).assign(random::nextGaussian);
    q.getView(0, n, n, n).assign(random::nextGaussian);
    q.getView(n, 0, n, n).assign(random::nextGaussian);
    q.getView(n, n, n, n).assign(random::nextGaussian);

    Matrix x = p.mmul(Transpose.YES, p, Transpose.NO);
    v.put(i, trace(x.mmul(x).mmul(x)));

    x = q.mmul(Transpose.YES, q, Transpose.NO);
    w.put(i, trace(x.mmul(x).mmul(x)));
  }

  double meanv = mean(v);
  double stdv = std(v, meanv);
  double meanw = mean(w);
  double stdw = std(w, meanw);
  return new double[] {meanv, stdv, meanw, stdw};
}
```

The `assign`-method is called on several views each occupying a 5-by-5
matrix in `p` and `q`.

!!! info "Static imports"

    Please note that `Matrices` are statically imported in the
    example.

### Compound operations ###

## Vector ##

Vectors are Briljants main abstraction and resembles immutable lists
of homogenoues values. Vectors come in 5 homogeneous flavors, all of
which resides in the namespace `org.briljantframework.vector`. The
flavors are

* `StringVector` for storing string values (`java.lang.String`)
* `DoubleVector` for storing real number (`double`)
* `ComplexVector` for storing complex numbers (`org.briljantframework.complex.Complex`)
* `Intvector` for storing integers (`int`)
* `BinaryVector` for storing binary values (`org.briljantframework.vector.Binary`)

In addition to values, each vector can store a distinct value which
represent the absence of a value called `NA`.

## DataFrame ##

Data frames are Briljants most 

