# Matrix operations

These operations are found in `org.briljantframework.matrix.Matrix`
and `org.briljantframework.matrix.Matrices`.

## matrix
```
Matrix a = matrix(new double[]{
    new double[]{1,2,3},
    new double[]{1,2,3}
});

Matrix b = matrix(1,1,2,2,3,3).reshape(2, 3);
Matrix c = matrix(10, random::nextGaussian).reshape(5, 2);
```
