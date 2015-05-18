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

* The `AnyMatrix`, `DoubleMatrix`, `IntMatrix` and `ComplexMatrix` are
  2-dimensional data containers of double precision floating point
  numbers, integers or complex numbers, supporting a multitude of
  linear algebra operations.

## An example ##

```
Random random = new Random(123);
DoubleMatrix m = (3, 5);
m.assign(random::nextGaussian);
Bj.mean(m, Dim.R);
```

## Matrix

There are several ways to create matrices, of which most are
implemented in the `org.briljantframework.Bj` class. For example,
matrices can be created from one and two dimensional arrays. The
factory methods in `Bj` is delegated to an instance of
`org.briljantframework.matrix.api.MatrixFactory` and decided based on
the `MatrixBackend`. For details, please refer to the discussion on
[MatrixBackends](reference/matrix.md#backend).

!!! warning "Imports"

    In the examples below, `import org.briljantframework.matrix.*` and
    `import static org.briljantframework.matrix.Doubles.*` are
    implicit

!!! info "Matrix implementations"

    The choice of matrix implementation returned by `Bj` is decided by
    the `org.briljantframework.matrix.api.MatrixBackend`. The
    `MatrixBackend` is responsible for creating matrices and for 
    computing BLAS and linear algebra routines.

!!! info "Matrix Types"

    Briljant implements five difference matrix types for a wide range
    of domains including the primitive types: `int`, `double`, `long`
    and `boolean` and for `Complex` numbers.

### Creation ###

```
DoubleMatrix a = Bj.range(0, 10).reshape(5, 2).asDoubleMatrix();
DoubleMatrix b = Bj.linspace(0, 10, 50).reshape(10, 5);
DoubleMatrix c = Bj.linspace(0, 2 * Math.PI, 100).map(Math::sqrt);
DoubleMatrix d = Bj.matrix(new double[][]{
    new double[]{0, 5},
    new double[]{1, 6},
    new double[]{2, 7},
    new double[]{3, 8},
    new double[]{4, 9}
});

DoubleMatrix e = Bj.matrix(new double[]{
    0, 1, 2, 3, 4, 5, 6, 7, 8, 9
}).reshape(5, 2);
```

In the example above, `a` is created using a range of numbers and
reshaping it to a matrix with 5 rows and 2 columns; `b` is created,
rather verbosely, using a multi-dimensional array. Finally, `c` is
created using a flat array and reshaping it to a 5 by 2 matrix.

`Bj#range(int, int)` creates a column-vector (i.e. a m-by-1 matrix)
with values between `start` and `end` with `1` increments. The 3-arity
version, `Bj#range(int, int, int)` can be used if another
`step`-size is needed. Since it's not possible to predict the number
of values in floating point ranges, `Bj#linspace(double, double,
double)` should be use instead. `Bj#linspace` receives an
additional argument which denotes the number elements returned. For
example, `Bj#linspace(0, 10, 50).reshape(10, 5)` creates a
10-by-5 matrix.

In many cases, the size of a matrix is known but its contents is
not. Therefore, Briljant provides several ways of creating empty matrices with
placeholder values.

```
// 10-by-10 matrix with elements set to 0
DoubleMatrix a = Bj.doubleMatreix(10, 10);

// 10-by-10 matrix with elements set to 1
DoubleMatrix b = Bj.doubleMatrix(10, 10).assign(1);

// 10 by 10 matrix with elements set to NaN
DoubleMatrix c = Bj.doubleMatrix(10, 10).assign(Double.NaN);
DoubleMatrix d = Bj.linspace(0, 2 * Math.PI, 100).map(Math::sin);
```

#### See also ####

[Bj#matrix](examples.md#matrix),
[Bj#linspace](examples.md#linspace),
[Bj#doubleMatrix](examples.md#zeros),
[Bj#rand](examples.md#rand),
[Bj#randn](examples.md#randn)

### Manipulation ###

An already created matrix can be assigned values via the `assign`
method. An example of this have already been seen above and a few more
can be seen below. Assign modifies and returns the receiver, this
makes it possible to reuse matrices (and hence avoid large memory
allocations).

```
NormalDistribution sampler = new NormalDistribution(-1, 1);
DoubleMatrix a = Bj.doubleMatrix(10, 10);
DoubleMatrix b = Bj.rand(100, sampler).reshape(10, 10);
a.assign(b);
a.assign(b, Math::sqrt);
DoubleMatrix x = a.assign(b, e -> e * e).reshape(5, 20);
System.out.println(x);

// Take the first row
DoubleMatrix firstRow = b.getRowView(0);

// Modifications views share data with the original
firstRow.assign(Bj.doubleMatrix(10, 10).getRowView(0));

// Take the upper 4 elements of b
DoubleMatrix up = b.getView(0, 0, 4, 4);
System.out.println(up);
```

Note that modification of views propagates to the original data. To
break a copy free from its original use `Matrix#copy()`. By default,
matrices are mutable (and hence unsafe to mutate in parallel).


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
DoubleMatrix a = Bj.doubleMatrix(3, 3);
System.out.println(a);

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

Unlike most scientific languages, Java does not support user defined
operators. Hence, addition, subtraction, multiplication and division
are performed using named method calls. For brevity, these are
shortened to `mul` (for multiplication), `mmul` (for
matrix-multiplication), `add` (for addition) and `sub` (for
subtraction).

!!! info "Performance notice"

    As stated previously, Briljant provides several matrix
    implementations. The most common is provided by the
    `Netlib`-backend, implemented in
    `org.briljantframework.netlib.NetlibMatrixBackend`.


The most common matrix routines (e.g., BLAS level 1, 2 and 3) are
provided by the `org.briljantframework.matrix.api.MatrixRoutines`
class and the most common linear algebra routines are provided by the
`org.briljantframework.linalg.api.LinearAlgebraRoutines`. By default,
`Bj` delegates to, for the current platform chose,
implementation. However, the user can freely choose between
implementation by constructing an instance of a `MatrixBackend`. For
example:

```
MatrixBackend mb = new NetlibMatrixBackend();
MatrixFactory bj = mb.getMatrixFactory();
MatrixRoutines bjr = mb.getMatrixRoutines();
LinearAlgebraRoutines linalg = mb.getLinearAlgebraRoutines();

DoubleMatrix x = bj.matrix(new double[]{
    1, 5, 9,
    2, 6, 10,
    3, 7, 11,
    4, 8, 12
}).reshape(4, 3);

DoubleMatrix c = bj.doubleMatrix(3, 3);
bjr.gemm(T.YES, T.NO, 1, x, x, 1, c);

double sum = bjr.sum(c);
// 1526
```

Note that the usage of the matrix routines closely resembles
traditional BLAS implementations, e.g., by the use of output
parameters (`c` in the example above).


```
Utils.setRandomSeed(123);
DoubleMatrix a = Bj.doubleVector(9).assign(2).reshape(3, 3);
DoubleMatrix b = Bj.rand(9, new NormalDistribution(-1, 1)).reshape(3, 3);
b.add(a);
b.sub(a);
b.mul(a);
b.mmul(a);
b.add(1, a, -1);
b.add(1, a, -1).satisfies(b.add(a.mul(-1)), (x, y) -> x == y);
```

##### See also #####

[Matrix#mul](examples.md#mul), [Matrix#add](examples.md#add),
[Matrix#sub](examples.md#sub), [Matrix#div](examples.md#div),
[Matrix#mmul](examples.md#mmul)

### Element wise operations ###

Briljant reuses a large number of element wise operations found for
`double` values in Java. In Briljant, these are referred to as element
wise functions and produces new matrices as output.

```
DoubleMatrix a = Bj.linspace(0, 2 * Math.PI, 100);
a.map(Math::sqrt);
a.map(Math::abs);
a.map(Math::exp);
a.map(Math::acos);
a.map(Math::sin);
a.map(Math::cos);
a.map(value -> Math.pow(value, 10));
```

!!! success "In-place element wise operations"

    Remember that `Matrix#assign` can be used to mutate the matrix.

!!! warning "Complex matrices"

    Remember that, for example, `Math.sqrt(-2)` return NaN. Complex
    numbers allow one to solve equations without real solutions. In
    Briljant, complex numbers are implemented in the
    `org.briljantframework.complex.Complex` class and matrices of such
    values are implemented in
    `org.briljantframework.matrix.ComplexMatrix`. Given, `DoubleMatrix
    x = rand(10, 10).muli(-10)` the element wise square root can be
    calculated (it the complex plane) as `ComplexMatrix z =
    Bj.complexMatrix(10, 10).assign(x, Complex::sqrt)`


#### Example ####

As an example of Briljants expressiveness, one of Julias performance
tests is implemented below. Due to the performance overhead of copying
an array from the JVM to Fortran, its performance is competitive, but
not as fast as its Julia counterpart.


```
private static final Random random = new Random()
private static double[] randmatstat_Briljant(int t) {
    int n = 5;
    DoubleMatrix p = Bj.doubleMatrix(n, n * 4);
    DoubleMatrix q = Bj.doubleMatrix(n * 2, n * 2);
    DoubleMatrix v = Bj.doubleMatrix(t, 1);
    DoubleMatrix w = Bj.doubleMatrix(t, 1);

    for (int i = 0; i < t; i++) {
      p.getView(0, 0, n, n).assign(random::nextGaussian);
      p.getView(0, n, n, n).assign(random::nextGaussian);
      p.getView(0, n * 2, n, n).assign(random::nextGaussian);
      p.getView(0, n * 3, n, n).assign(random::nextGaussian);

      q.getView(0, 0, n, n).assign(random::nextGaussian);
      q.getView(0, n, n, n).assign(random::nextGaussian);
      q.getView(n, 0, n, n).assign(random::nextGaussian);
      q.getView(n, n, n, n).assign(random::nextGaussian);

      DoubleMatrix x = p.mmul(T.YES, p, T.NO);
      v.set(i, Bj.trace(x.mmul(x).mmul(x)));

      x = q.mmul(T.YES, q, T.NO);
      w.set(i, Bj.trace(x.mmul(x).mmul(x)));
    }
    DescriptiveStatistics statV = v.collect(
        RunningStatistics::new, RunningStatistics::add
    );
    DescriptiveStatistics statW = w.collect(
        RunningStatistics::new, RunningStatistics::add
    );
    double meanv = statV.getMean();
    double stdv = statV.getStandardDeviation();
    double meanw = statW.getMean();
    double stdw = statW.getStandardDeviation();

    return new double[]{meanv, stdv, meanw, stdw};
  }
```

The `assign`-method is called on several views each occupying a 5-by-5
matrix in `p` and `q`. The call to `#collect` above takes two
functions, one supplying an initial value (usually a mutable
container) and the second performs a collect call for each value in
the matrix. Above, an instance of `RunningStatistics`
(`RunningStatistics::new`) is supplied and updated
(`RunningStatistics::add`) with every value in the matrix.

### Compound operations ###

Some other operations?

## Vector ##

Vectors are Briljants main abstraction and resembles immutable lists
of homogenoues values. Vectors come in six homogeneous flavors, all of
which resides in the namespace `org.briljantframework.vector`. The
flavors are

* `DoubleVector` for storing real number (`double`).
* `ComplexVector` for storing complex numbers.
  (`org.briljantframework.complex.Complex`).
* `Intvector` for storing integers (`int`).
* `BitVector` for storing binary values
  (`org.briljantframework.vector.Bit`)
* `GenericVector(Class<? extends T>)` for storing values of `T`.

In addition to values, each vector can store a distinct value, called
`NA`, which represents the absence of a value.

In this introduction we'll start with a quick overview of the `Vector`
data structure. For most applications in Briljant, `Vector` will be a
trusty companion. To get started, we import

```
import org.briljantframework.vector.*;
import org.briljantframework.[functions].*;
```

### Creating vectors ###

Since vectors in Briljant are immutable, new vectors are created using
a `Vector.Builder`-object.

```
IntVector.Builder vb = new IntVector.Builder();
for(int i = 0; i < 10; i++)
    vb.add(i);
Vector zeroToNine = vb.build();
//[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
```

In the example above, we create a new `IntVector` with the values from
0 to 9.

Every vector has a type, accessible from `Vector#getType()`. The
returned type object contains information about, for example, the
underlying type and how to copy, compare and identify `NA`-values.

Perhaps the simplest, and most common, way of creating a vector is to
call `Vector.of(data)`. Here, `data` can be:

* An `Array`; or
* an `Iterable`

The type of vector is inferred from the argument to `#of`. For
example,

```
// i is an IntVector
Vector i = Vector.of(1,2,3,4,5);

// d is a DoubleVector
Vector d = Vector.of(1.1, 1.2, 1.3);

// n is a GenericVector(Number.class)
Vector n = Vector.of(1.1, 1, 2, 3, 4);

// data is a GenericVector(String.class)
Vector data = Vector.of("Hello", "World");
```

!!! info "Missing values"

    In Briljant, `NA` denote the absence of a value. The particular
    value denoting `NA` changes based on vector type. For the
    reference types (e.g., `GenericVector` and `ComplexVector`), `NA`
    is the same as `null`. For the primitive types, however, `NA` is
    represented differently depending on type. For the `IntVector`,
    `Integer.MAX_VALUE` denote `NA` and for the `DoubleVector` a value
    in the `NaN`-range (`0x7ff0000000000009L`) denote `NA`. To
    simplify `NA`-checking, use `Is.NA(...)`.

To create a vector with missing values, `Vector.Builder#addNA` can be
used. For example:

```
Vector.Builder vb = new DoubleVector.Builder();
for (int i = 0; i < 10; i++) {
    if(i % 2 == 0) {
        vb.addNA();
    } else {
        vb.add(i);
    }
}
Vector v = vb.build();
// [NA, 1, NA, 3, NA, 5, NA, 7, NA, 9]
```

A vector acts very similar to a `List<T>` with the difference that it
natively handles primitive types (using `#getAsInt(int)` and
`#getAsDouble(int)`).

```
Vector v = Vector.of(1.1, 1.2, 1.3, 1.4, 1.5);
double i = v.getAsDouble(0);
// 1.1

v.slice(Arrays.asList(1, 2, 3));
// [1.2, 1.3, 1.4]

double mean = vec.aggregate(Double.class, Aggregates.mean());
v.slice(v.satisfies(Double.class, x -> x > mean);
// [1.4, 1.5];

List<Double> l = vec.asList(Double.class);
// [1.1, 1.2, 1.3, 1.4, 1.5]

v instanceof DoubleVector // true

Vector nv = Vector.of("Hello", "World");
nv instanceof GenericVector // true

double i = nv.getAsDouble(0);
Is.NA(i); // true

String s = nv.aggregate(String.class, Aggregates.join(" "));
// "Hello World"

LocalDate d = nv.get(LocalDate.class, 0, LocalDate::now); // now

```

## DataFrame ##

Data frames are Briljants most 

