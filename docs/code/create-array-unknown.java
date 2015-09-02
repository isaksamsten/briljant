// 10-by-10 array with elements set to 0
DoubleArray a = Bj.doubleArray(10, 10);

// 10-by-10-by-10 array with elements set to 1
DoubleArray b = Bj.doubleMatrix(10, 10, 10).assign(1);

// 100 element vector with the sine of the values between 0 and 2 * PI
DoubleArray d = Bj.linspace(0, 2 * Math.PI, 100).map(Math::sin);
