// 10-by-10 array with elements set to 0
DoubleArray a = DoubleArray.zeros(10, 10);

// 10-by-10-by-10 array with elements set to 1
DoubleArray b = DoubleArray.ones(10, 10, 10);

// 100 element vector with the sine of the values between 0 and 2 * PI
DoubleArray d = Arrays.linspace(0, 2 * Math.PI, 100).map(Math::sin);
