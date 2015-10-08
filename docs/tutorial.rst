***********************
Tutorial
***********************

Getting started
===============

Installation
------------
Building from source

Introduction
============

Briljants main abstractions are the (nd)-array, ``DataFrame`` and
``Vector``.

* ``DataFrame`` is an immutable column wise heterogeneous data
  container and provides the essential tools for working with
  statistical data in Java. In particular, ``DataFrame`` provides

  * ``NA``, i.e. missing (or non-existing) values, distinct from
    e.g., ``NaN``.
  * Operations for working with tabular data, similar to those found
    in spreadsheets and databases.
  * Different implementations with varying performance
    characteristics.

* ``Vector`` is an immutable homogeneous data container. It supports
  reference types such as ``String`` (i.e. categorical values),
  ``Double`` (i.e. double precision floating point numbers),
  ``Binary`` (i.e. true/false/NA), ``Integer`` and ``Complex``
  numbers. All providing a unique ``NA`` representation.

* ``Array<T>``, ``DoubleArray``, ``IntArray``, ``LongArray``,
  ``ComplexArray`` and ``BitArray`` are d-dimensional data containers
  of, reference and numerical (primitive) elements supporting a
  multitude of linear algebra operations.

To get a first taste of the Briljant framework, we'll start by
exploring a few of its main primitives. The first primitive we
introduce is the ``Vector`` which, as explained above, is a
homogeneous ``NA`` supporting container type. Homogeneous mean that a
vector only contain values of one specific type; and ``NA`` supporting
that it is aware of missing values. For example, suppose we are given
a list of employees and want to know the frequency of each name. The
following code (assuming that Briljant is correctly installed and that
the correct packages are imported) creates a (``String``) vector
consisting of the names *Bob*, *Mary*, *Lisa*, etc.

.. code-block:: java
                
   Vector employees = Vector.of("Bob", "Mary", "Lisa", "John", "Lisa", "Mary", "Anna");

If we print the vector to the screen (e.g., using
``System.out.println(employees)``), we'll see something like this.
   
::
   
    0  Bob
    1  Mary
    2  Lisa
    3  John
    4  Lisa
    5  Mary
    6  Anna
    type: string

Now, suppose we are interested in the frequency of each name, the
traditional `Java` way would be something like this


.. code-block:: java

   Map<String, Integer> counts = new HashMap<>();
   for(String employee : employees) {
      Integer count = counts.get(employee);
      if(counts == null) {
        counts.put(employee, 1);
      } else {
        counts.put(employee, 1 + count);
      }
   }

   
While this code is fine in many circumstances, would it not be nice to
have a data-structure that supports such common statistical methods
directly and instead say:

.. code-block:: java
   
    Vector counts = employees.valueCounts();

Again, printing the vector (``counts`` in this case) produces something like this
    
::
   
     Bob   1
     John  1
     Anna  1
     Lisa  2
     Mary  2
     type: int

The first thing you might notice is that the first "column" have
changed from the numbers ``0..size()`` to instead be the names of the
employees. This is because in Briljant, vectors are *indexed* which
essentially means that they are somewhat like a combination of a
``Map`` and a ``List``. To get the count of ``Mary`` we simply say
``counts.getAsInt("Mary");`` which will give us the answer ``2``. To
treat the vector as a list and, hence, access values based on their
location we can say ``counts.loc().getAsInt(0)`` which will also
return ``2``.

In many cases ``Vectors`` will suffice, e.g., when storing the
evolution of stock prices

.. code-block:: java

   Vector.Builder spb = new DoubleVector.Builder();
   spb.set(LocalDateTime.of(2014, Month.JANUARY, 1, 10, 10, 30), 100);
   spb.set(LocalDateTime.of(2014, Month.JANUARY, 1, 10, 10, 31), 200);
   // ....
   spb.set(LocalDateTime.of(2014, Month.JANUARY, 1, 10, 11, 00), 199);
   Vector stockPrices = spb.build();

or the price of products

.. code-block:: java

   Vector.Builder pp = new DoubleVector.Builder();
   pp.set("iPad", 200);
   pp.set("iPhone", 300);
   // ...
   pp.set("Macbook Pro", 3000);
   Vector prices = pp.build();

But what if we want to track the price, size and number of units in
stock? Well, in those circumstances a common approach is to create a
spreadsheet or a ``DataFrame``. For example,

.. code-block:: java

   DataFrame.Builder productBuilder = new MixedDataFrame.Builder();
   productBuilder.set("Price", Vector.of(200.0, 300.0, 3000.0));
   productBuilder.set("Size", Vector.of(150, 250, 2000));
   productBuilder.set("UnitsInStock", Vector.of(10, null, 10));
   productBuilder.set("Name", Vector.of("iPad", "iPhone", "Macbook Pro"));
                
   DataFrame products = productBuilder.build();

will create a data frame with four columns ("Price", "Size",
"UnitsInStock", "Name") and 3 rows. Again, printing the data frame
produces

::
  
       Price   Size  UnitsInStock  Name         
    0  200.0   150   10            iPad         
    1  300.0   250   NA            iPhone       
    2  3000.0  2000  10            Macbook Pro  

    [3 rows x 4 columns]

In the output, the first row of the output denotes the *column index*
and the first column denotes the *record index*. Using these indicies,
one can get a **record** (i.e. a row), a **column** or a
**value**. Records are retrived using the
``DataFrame#getRecord(Object)``-method, columns by the
``DataFrame#get(Object)` and values by
``DataFrame#get(Class,Object,Object)``. For example,

.. code-block:: java

   products.getRecord(0);

produces
   
::

   Price         200.0
   Size          150
   UnitsInStock  10
   Name          iPad

.. code-block:: java

   products.get("Price");

produces

::

   0  200.0
   1  300.0
   2  3000.0

and finally, ``products.getAsDouble(0, "Price")`` returns
``200.0``. Since every product is unique (a requirement for a valid
index), we can *re-index* our data frame on the ``Name``-column. We do
this with the ``DataFrame#indexOn(Object)``

.. code-block:: java

   products = products.indexOn("Name")

will create a new data frame with the column "Name" removed and made
the **record index**, producing.

::
   
                Price   Size  UnitsInStock  
   iPad         200.0   150   10            
   iPhone       300.0   250   NA            
   Macbook Pro  3000.0  2000  10            

   [3 rows x 3 columns]


Now we can get the price of an *iPad* by simply saying
``products.getAsDouble("iPad", "Price")`` and the entire *iPad*
product by saying ``products.getRecord("iPad")``. While impressive
(/s), the major benefits of data frames are the ability to
*aggregate*, *group* and *combine* them to form new data frames. For
example, we can compute the mean price and number of items in stock
for products with ``siz < 200`` and products with size ``size >= 200``:

.. code-block:: java

   products.groupBy(Integer.class, "Size", v -> v >= 200)
           .collect(Vector::mean);


which will produce

::

          Price     UnitsInStock  Name  
   false  200.000   10.000        NA
   true   1650.000  10.000        NA   

   [2 rows x 3 columns]

Note that ``Vector::mean`` is a function and ignores `NA`-values which
is why the mean for ``UnitsInStock`` is ``10``; also note that for
non-numerical vectors ``Vector#mean`` is undefined and returns ``NA``
which is why the mean of the ``Name``-column is ``NA``.

The array
---------

There are several ways to create matrices, of which most are implemented
in the ``org.briljantframework.array.Arrays`` class. For example, matrices can be
created from one and two dimensional arrays. The factory methods in
``Bj`` is delegated to an instance of
``org.briljantframework.matrix.api.MatrixFactory`` and decided based on
the ``MatrixBackend``. For details, please refer to the discussion on
`MatrixBackends <reference/matrix.md#backend>`__.

.. note:: In the examples below, `import
    org.briljantframework.matrix.*` and `import static
    org.briljantframework.matrix.Doubles.*` are implicit

.. note:: The choice of array implementation returned by `Bj` is
    decided by the `org.briljantframework.array.api.ArrayBackend`. The
    `ArrayBackend` is responsible for creating arrays and for
    computing BLAS and linear algebra routines.

.. note:: Briljant implements five difference matrix types for some
    common domains including the primitive types: `int`, `double`,
    `long` and `boolean` and for `Complex` numbers. There is also a
    generic array `Array<T>` for arbitary objects, e.g., `String`.

Creation
^^^^^^^^

.. literalinclude:: code/create-array.java
   :language: java
                

In the example above, ``a`` is created using a range of numbers and
reshaping it to a 2d-array with 5 rows and 2 columns; ``b`` is created,
rather verbosely, using a multi-dimensional array. Finally, ``c`` is
created using a flat array and reshaped to a 5-by-2 array. One thing to
note is that Briljant arrays are represented in column-major order,
i.e., the first dimension varies faster.

In essences, ``Bj#range(int, int)`` creates a 1d-array (i.e. a vector)
with values between ``start`` and ``end`` with ``1`` increments. The
3-arity version, ``Bj#range(int, int, int)`` can be used if another
``step``-size is needed. Since it's not possible to predict the number
of values in floating point ranges, ``Bj#linspace(double, double,
double)`` should be use instead.  ``Bj#linspace`` receives an
additional argument which denotes the number elements returned. For
example, ``Bj#linspace(0, 10, 50).reshape(10, 5)`` creates a 10-by-5
array.

In many cases, the size of a matrix is known but its contents is not.
Therefore, Briljant provides several ways of creating empty matrices
with placeholder values.

.. literalinclude:: code/create-array-unknown.java
   :language: java


Manipulation
^^^^^^^^^^^^

An already created matrix can be assigned values via the ``assign``
method. An example of this have already been seen above and a few more
can be seen below. Assign modifies and returns the receiver, this makes
it possible to reuse matrices (and hence avoid large memory
allocations).

.. code-block:: java

   NormalDistribution sampler = new NormalDistribution(0, 1);
   DoubleArray a = Bj.doubleArray(3, 4);
   DoubleArray b = Bj.rand(3 * 4, sampler).reshape(3, 4);
   
   // Assign the values of b to a
   a.assign(b);
   System.out.println(a);
   
   // assigning b to a while taking the square root of the elements
   a.assign(b, Math::sqrt);
   System.out.println(a);
   
   // assign the values of b to a while multiplying
   // reshape a and return a view to x
   DoubleArray x = a.assign(b, v -> v * v).reshape(4, 3);
   System.out.println(x);
   
   // Note that the values of a are updated, but its shape is unchanged
   System.out.println(a);
   
   // Get a view of the first row of x
   System.out.println(x.getRow(0));
   
The code example above, would result in something like

::
   
   array([[-0.052, -0.665,  0.741, -0.700],
          [ 0.503, -0.818, -0.261,  1.940],
          [-1.424,  0.896, -0.576, -0.341]] type: double)

   array([[  NaN,   NaN, 0.861,   NaN],
          [0.709,   NaN,   NaN, 1.393],
          [  NaN, 0.946,   NaN,   NaN]] type: double)
          
   array([[0.003, 0.669, 0.332],
          [0.253, 0.802, 0.491],
          [2.029, 0.549, 3.762],
          [0.443, 0.068, 0.116]] type: double)
          
   array([[0.003, 0.443, 0.549, 0.491],
          [0.253, 0.669, 0.068, 3.762],
          [2.029, 0.802, 0.332, 0.116]] type: double)
          
   array([[0.003, 0.669, 0.332]] type: double)

.. note:: Note that modification of views propagates to the original
    data. To break a copy free from its original use
    ``Array#copy()``. By default, arrays are mutable (and hence unsafe
    to mutate in parallel).


.. note:: Matrices in Briljant are implemented in column-major order,
       which is a way of arranging multidimensional arrays in linear
       memory.

       In column-major order columns are stored contiguous in memory
       whereas in row-major order rows are stored contiguous in
       memory. For example, in the c programming language
       multidimensional arrays are stored in row-major order and in
       e.g., Fortran multidimensional arrays are stored in
       column-major order. An array with two rows `8, 9` and `3, 5`
       would be stored as:

       +------------+-----+-----+-----+-----+
       |Arrangement |0    |1    |2    |3    |
       +------------+-----+-----+-----+-----+
       |Column-major|8    |3    |9    |5    |
       +------------+-----+-----+-----+-----+
       |Row-major   |8    |9    |3    |5    |
       +------------+-----+-----+-----+-----+

       In Briljant, `Array#get(int)` and `Array#set(int, <Type>)` are
       expected to work as if the matrix was used in the column-major
       arrangement. Hence, for the array above `get(1)` should return `3`
       and `put(2, 10)` should change the `9` to a `10`.

       Due to cache locality, it is generally faster to traverse arrays
       in column-major order, i.e. while varying the left-most indices
       fastest.


.. code-block:: java
       
   DoubleArray a = Bj.doubleArray(3, 3);
   a.set(0, 0, 10); 
   a.set(0, 1, 9); 
   a.set(0, 2, 8); 
   
   System.out.println(a);
   
   // Iterating the array in column-major linearized order
   for (int i = 0; i < a.size(); i++) { 
     System.out.println(a.get(i)); 
   }
   

As indicated in the information box, matrices are stored in
column-major order. Hence, the output of the example is

::

   array([[10.000, 9.000, 8.000],
          [ 0.000, 0.000, 0.000],
          [ 0.000, 0.000, 0.000]] type: double)
          
   10.0
   0.0
   0.0
   9.0
   0.0
   0.0
   8.0
   0.0
   0.0



Basic operations
^^^^^^^^^^^^^^^^

Unlike most scientific languages, Java does not support user defined
operators. Hence, addition, subtraction, multiplication and
division are performed using named method calls. For brevity,
these are shortened to `mul` (for multiplication), `mmul` (for
matrix-multiplication), `add` (for addition) and `sub` (for
subtraction).

.. note:: As stated previously, Briljant provides several array
        implementations. The most common is provided by the
        `Netlib`-backend, implemented in
        `org.briljantframework.array.netlib.NetlibArrayBackend`.


The most common matrix routines (e.g., BLAS level 1, 2 and 3) are
provided by the ``org.briljantframework.array.api.ArrayRoutines`` class
and the most common linear algebra routines are provided by the
``org.briljantframework.linalg.api.LinearAlgebraRoutines``. By default,
``Bj`` delegates to, for the current platform chose,
implementation. However, the user can freely choose between
implementation by constructing an instance of a ``ArrayBackend``. For
example:

.. literalinclude:: code/array-backend.java
   :language: java

which results in the following output

.. literalinclude:: out/array-backend.txt              
                
Note that the usage of the matrix routines closely resembles
traditional BLAS implementations, e.g., by the use of output
parameters (`c` in the example above). To simplify common use-cases,
Briljant provides many convenience methods over the BLAS routines. 

::
   
   DoubleArray a = Bj.doubleVector(9).assign(2).reshape(3, 3);
   DoubleArray b = Bj.rand(9, new NormalDistribution(-1, 1)).reshape(3, 3);

   // element-wise addition
   b.add(a);

   // element-wise subtraction
   b.sub(a);

   // element-wise multiplication
   b.mul(a);

   // generalized matrix-matrix multiplication
   b.mmul(a);

   
Element wise operations
^^^^^^^^^^^^^^^^^^^^^^^

Briljant reuses a large number of element wise operations found for
`double` values in Java. In Briljant, these are referred to as element
wise functions and produces new matrices as output.

::
   
   DoubleArray a = Bj.linspace(0, 2 * Math.PI, 100);

   // create a new array with the square root of each element
   a.map(Math::sqrt);

   // ... and so on for various java.lang.Math methodsg
   a.map(Math::abs);
   a.map(Math::exp);
   a.map(Math::acos);
   a.map(Math::sin);
   a.map(Math::cos);
   a.map(value -> Math.pow(value, 10));

.. note:: Remember that ``assign`` can be used to mutate the matrix.

          
.. note::

   Remember that, for example, ``Math.sqrt(-2)`` return
   ``Double.NaN``. Complex numbers allow one to solve equations
   without real solutions. In Briljant, complex numbers are
   implemented in the ``org.apache.commons.math3.complex.Complex``
   class and arrays of such values are implemented in
   ``org.briljantframework.array.ComplexMatrix``. Given, ``DoubleArray
   x = rand(10, 10).muli(-10)`` the element wise square root can be
   calculated (in the complex plane) as ``ComplexMatrix z =
   Bj.complexMatrix(10, 10).assign(x, Complex::sqrt)``


Example
^^^^^^^

As an example of Briljants expressiveness, one of Julias performance
tests is implemented below. Due to the performance overhead of copying
an array from the JVM to Fortran, its performance is competitive, but
not as fast as its Julia counterpart.

.. literalinclude:: code/example.java
   :language: java

The ``assign``-method is called on several views each occupying a
5-by-5 matrix in ``p`` and ``q``. The call to ``#collect`` above takes
two functions, one supplying an initial value (usually a mutable
container) and the second performs a collect call for each value in
the matrix. Above, an instance of ``RunningStatistics``
(``FastStatistics::new``) is supplied and updated
(``FastStatistics::add``) with every value in the matrix.

Compound operations
-------------------

    Some other operations?
    

The Vector
==========

Vectors are Briljants main abstraction and resembles immutable lists
of homogenoues values. Vectors come in six homogeneous flavors, all of
which resides in the namespace
``org.briljantframework.data.vector``. The flavors are:

* ``DoubleVector`` for storing real number (``double``).
* ``ComplexVector`` for storing complex numbers.
  (``org.briljantframework.complex.Complex``).
* ``Intvector`` for storing integers (``int``).
* ``GenericVector(Class<? extends T>)`` for storing values of ``T``.

In addition to values, each vector can store a distinct value, called
``NA``, which represents the absence of a value. For the reference
types (apart from ``Complex`` and ``Logical``), the ``NA``-value is
``null``. To generically check for ``NA``-values, use the
``Is#NA(Object)`` method.

In this introduction we'll start with a quick overview of the ``Vector``
data structure. For most applications in Briljant, ``Vector`` will be a
trusty companion. To get started, we import

::
   
   import org.briljantframework.data.vector.*;
   import org.briljantframework.functions.Aggregates;

.. note:: Primitive values vs. boxed references

    In Java there is a distinction between primitive types and
    reference types, where the former are the basic numerical types
    such as ``int``, ``double`` and ``boolean`` and the latter any
    compound type. To support functions with ``Object`` parameters,
    the primitive values can be *boxed* into a reference type. This is
    a rather costly operation. To avoid the cost of boxing, Briljant
    provide specialized ``Vector``-classes for some of the most
    commonly used primitive types: ``int`` and ``double``. To
    illustrate the cost of boxing, consider the following two examples
    of computing the mean of 1000 element vector:


    .. code-block:: java
                
        // Construct a vector of random numbers. Negative numbers are NA.
        Vector b = Vector.of(rand::nextGaussian, 1000).map(Double.class, v -> {
            double sqrt = Math.sqrt(v);
            return Double.isNaN(sqrt) ? Na.of(Double.class) : sqrt;
        });

        // Example 1 RunningStatistics stats = new RunningStatistics();
        for (int i = 0, size= b.size(); i < size; i++) {
            double v = b.getAsDouble(i);
            if (!Is.NA(v)) {
                stats.add(v);
            }
        }
        double avg = stats.getMean();

        // Example 2
        double avg = b.aggregate(Double.class, Aggregates.mean());

        // Example 3
        double avg = b.mean();
   
    Above, Example 1 (which takes 0.1 ms) is roughly 20 times faster
    on a recent machine than Example 2 (which takes 2 ms). Example 2,
    however, is vastly more readable and should be preferred in all
    but hot inner loops. Note that all of the performance overhead of
    Example 2 does not come from boxing, but rather from the use of
    lambdas. If we change ``b.getAsDouble(i)``, to ``get(Double.class,
    i)``, the performance difference is only a factor of two. For many
    common operations, the ``Vector``-interface provides a default
    implementation and, optionally optimized implementations. For
    ``double``-values, the ``mean()``-method (Example 3) has
    equivalent performance to Example 1, and should be **preferred**.

    
Creating vectors
----------------

Since vectors in Briljant are immutable (which is a great way of
supporting simple concurrent programming), new vectors are created
using a ``Vector.Builder``-object.

.. code-block:: java
             
   Vector.Builder vb = new IntVector.Builder();
   for(int i = 0; i < 10; i++)
      vb.add(i);

   Vector zeroToNine = vb.build();

In the example above, we create a new ``IntVector`` with the values
from 0 to 9.

Every vector has a type, accessible from ``Vector#getType()``. The
returned type object contains information about, for example, the
underlying type and how to copy, compare and identify ``NA``-values.

Perhaps the simplest, and most common, way of creating a vector is to
call ``Vector.of(data)``. Here, ``data`` can be:

* A ``T[]``
* a ``Supplier<T>``; or
* an ``Iterable<T>``

The type of vector is inferred from the argument to ``#of``. For
example:

.. code-block:: java
             
   // i is an IntVector
   Vector i = Vector.of(1,2,3,4,5);

   // d is a DoubleVector
   Vector d = Vector.of(1.1, 1.2, 1.3);

   // n is a GenericVector(Number.class)
   Vector n = Vector.of(1.1, 1, 2, 3, 4);

   // data is a GenericVector(String.class)
   Vector data = Vector.of("Hello", "World");


.. note:: Missing values

   In Briljant, ``NA`` denote the absence of a value. The particular
   value denoting ``NA`` changes based on vector type. For the
   reference types (e.g., ``GenericVector`` and ``ComplexVector``),
   ``NA`` is the same as ``null``. For the primitive types, however,
   ``NA`` is represented differently depending on type. For the
   ``IntVector``, ``Integer.MAX_VALUE`` denote ``NA`` and for the
   ``DoubleVector`` a value in the ``NaN``-range
   (``0x7ff0000000000009L``) denote ``NA``. To simplify
   ``NA``-checking, use ``Is.NA(...)``.

To create a vector with missing values, ``Vector.Builder#addNA`` can
be used. For example:

.. code-block:: java
   
   Vector.Builder vb = new DoubleVector.Builder();
   for (int i = 0; i < 10; i++) {
       if(i % 2 == 0) {
           vb.addNA();
       } else {
           vb.add(i);
       }
   }
   Vector v = vb.build(); // [NA, 1, NA, 3, NA, 5, NA, 7, NA, 9]
   Vector v = Vector.of(1,null, 3, null, 4, null); // [1, NA, 3, NA, 4, NA]
   

A vector acts very similar to a ``List<T>`` with the difference that
it natively handles primitive types (using ``#getAsInt(int)`` and
``#getAsDouble(int)``).

.. code-block:: java
                
   Vector v = Vector.of(1.1, 1.2, 1.3, 1.4, 1.5);

   // No boxing!
   double i = v.getAsDouble(0); // 1.1

   double mean = vec.aggregate(Double.class, Aggregates.mean());

   // Get a List<T> view
   List<Double> l = vec.asList(Double.class); // [1.1, 1.2, 1.3, 1.4, 1.5]

   v instanceof DoubleVector // true
   
   Vector nv = Vector.of("Hello", "World");
   nv instanceof GenericVector // true

   // We can get double values from GenericVectors as well...
   double i = nv.getAsDouble(0);

   // however, the value is NA
   Is.NA(i); // true

   String s = nv.collect(String.class, Collectors.joining(" ")); // "Hello World"

   
Transforming vectors
--------------------

A common operation in statistical languages is the ability to
transform vectors, for example by performing some element-wise
operations. In Briljant, the simplest way to transform a vector is by
using the ``Vector#map`` method which takes as arguments the
type of value we are interested in transforming and a lambda that
perform the operation. For example:

.. code-block:: java
                
   Vector v = Vector.of(rand::nextGaussian, 100);
   UnaryOperator<Double> abs = Math::abs;

   Vector absSqrt = v.map(Double.class, abs.andThen(Math::sqrt));
   Vector pow = v.map(Double.class, v -> Math.pow(v, 3));

   // 'clip' the values in the -1 and 1 range
   Vector ne = v.map(Double.class, Transformations.clip(-1, 1));


.. warning::

   If performance is a concern, consider performing the operation in a
   ``for``-loop:

   .. code-block:: java
                   
      Vector.Builder builder = new DoubleVector.Builder();
      for(int i = 0, size = v.size(); i < size; i++) {
          builder.add(Math.sqrt(Math.abs(v.getAsDouble(i))));
      }

      Vector absSqrt = builder.build();


   This is, of course, more verbose but perform vastly better since it
   avoids unboxing. Many operations, such as ``clip``, is therefore
   also provided as library functions.

Aggregating vectors
-------------------

We have already seen examples of aggregating operations before, e.g.,
for computing the mean of a vector. We have, however, not yet seen how
they work. Imaging that we are interested in transforming a vector of
``String`` into an ``ArrayList<String>``, clearly it would be
impossible to used the ``transform`` function. So, do we have to rely
on the tedious and sometimes error-prone ``for``-loop?

.. code-block:: java
                
   Vector v = Vector.of("A", "B", "Cat", "Dog", "E", "F");
   ArrayList strings = new ArrayList<>();
   for(int i = 0; i < v.size(); i++) {
       strings.add(v.get(String.class, i));
   }


What if we want to collect our ``String``-vector into a single string?


.. code-block:: java
                
   StringBuilder builder = new StringBuilder();
   for(int i = 0; i < v.size(), i++) {
       builder.append(v.get(String.class, i));
   }

   
Fortunately not! Instead, Briljant exposes an ``Vector#collect``
function which is a very general method for the purposes outlined
above. For example:

.. code-block:: java
                
   ArrayList<String> strings = vector.collect(
       String.class, ArrayList::new, ArrayList::add);

   StringBuilder builder = vector.collect(
       String.class, StringBuilder::new, StringBuilder::append);
       
   double mean = vector.aggregate(Double.class, Aggregates.mean());

Collectors (i.e. aggregate operations) are, however, more general than
that! The ``collect``-method of ``Vector`` (and as we will see later,
data frames and grouped data frames) accepts as arguments a type ``E``
(in the example above ``String.class``) and, either a ``Supplier<T>``
and a ``BiConsumer<T, E>``, or an instance of ``Collector<T, R, C>``
where each type-argument in order denotes the type of value, the
return type and the mutable container type. The ``Collector<T, R,
C>``, is a mutable reduction operator that accumulates values into a
mutable container. We can specify an ``Collector`` by four functions:

* creation of the result container
* modification of the result container
* merging of result containers
* and finalization of the result container

Since ``Collectors`` are very general, operations such as ``repeat``,
``each``, ``isNA`` and ``test`` can be implemented.

.. code-block:: java
                
   import static org.briljantframework.functions.Aggregates.*;

   Vector v = Vector.of(1,2,3,4, null);

   // Repeat the vector n times
   Vector v2Times = v.collect(repeat(IntVector.Builder::new, 2));
   // [1,2,3,4,NA,1,2,3,4,NA]

   // Repeat each element n times
   Vector each2times = v.collect(each(IntVector.Builder::new, 2));
   //[1,1,2,2,3,3,4,4,NA,NA]

   // Indicator of which values are NA
   Vector nas = v.collect(isNA());
   // [FALSE, FALSE, FALSE, FALSE, TRUE]

   // all non-na values larger than 2
   Vector largerThan2 = v.collect(test(v -> !Is.NA(v) && v > 2));
   // [FALSE, FALSE, TRUE, TRUE, TRUE]


Most of the ``Collector``s in ``Aggregates`` naturally handles missing
values by either exluding them (in the case of e.g., ``mean()``) and
propagate them (in the case of e.g., ``repeat``). When implementing
collectors, one should take care to correctly (and consistently)
handle missing values.

Combining vectors
-----------------

Another line of common operations to perform with vectors is to
combine them into new vectors, e.g., adding, concatenating or
multiplying them.

Using the straight forward approach we could addition two vector by
just using a simple ``for``-loop:

.. code-block:: java
                
   Vector a = Vector.of(1.1, 1.2, 1.3);
   Vector b = Vector.of(() -> 2.0, 3);
   
   DoubleVector.Builder result = new DoubleVector.Builder();
   for(int i = 0; i < a.size(); i++) {
       result.add(a.getAsDouble(i) + b.getAsDouble(i));
   }
   Vector c = result.build(); // [3.1, 3.2, 3.3]

   
The example above is both rather verbose and in some cases
error-prone, e.g., what if ``a`` and ``b`` are of unequal length?  To
simplify and generalize the use-case outlined above, Briljant provides
``Vector#combine``. For example, the example could be written as:

.. code-block:: java
                
   Vector c = a.combine(Double.class, b, (x, y) -> x + y);
   // [3.1, 3.2, 3.3]

.. warning:: Indexed vectors

   Since vectors can be indexed (using the ``Vector#setIndex``)
   method, the first example only works for `int`-indexed vectors. To
   combine over locations (i.e. physical access locations) use
   ``Vector#loc()``.
   

Now, what if one of the vector contains ``NA`` values?

.. code-block:: java
                
   Vector a = Vector.of(1, 2, 3, null);
   Vector b = Vector.of(1, 2, null, 3);
   Vector c = a.combine(Integer.class, b, Combine.add()); // (or Vector c = a.add(b);)
   // [2, 4, null, null]

   Vector c = a.combine(Integer.class, b, Combine.add(1));
   // [2, 4, 4, 5]

   Vector c = a.combine(Integer.class, b, Na.ignore((x, y) -> x + y))
   // [2, 4, null, null]
   

``Combine#add(Number)`` returns a ``BiFunction<Number, Number,
Number>`` wrapped in a ``Na#ignore``, which - as the name
implies - ignores ``NA`` values by keeping the ``NA``-value from
either vector.

Calculations with missing values
--------------------------------

The DataFrame
=============

Data frames are Briljants most
