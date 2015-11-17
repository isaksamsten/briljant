DoubleArray a = Arrays.range(0, 10).reshape(5, 2).asDouble();
DoubleArray b = Arrays.linspace(0, 10, 50).reshape(10, 5);
DoubleArray c = Arrays.linspace(0, 2 * Math.PI, 100).map(Math::sqrt);
DoubleArray d = Arrays.newDoubleMatrix(new double[][]{
        new double[]{0, 5},
        new double[]{1, 6},
        new double[]{2, 7},
        new double[]{3, 8},
        new double[]{4, 9}});

DoubleArray e = DoubleArray.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9).reshape(5, 2);
Array<String> f = Array.of("a", "b", "c", "d").reshape(2, 2);
