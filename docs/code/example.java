private static final Random random = new Random()

private static double[] randmatstat_Briljant(int t) {
    Random random = new Random();
    int n = 5;
    DoubleArray p = DoubleArray.zeros(n, n * 4);
    DoubleArray q = DoubleArray.zeros(n * 2, n * 2);
    DoubleArray v = DoubleArray.zeros(t, 1);
    DoubleArray w = DoubleArray.zeros(t, 1);

    for (int i = 0; i < t; i++) {
        p.getView(0, 0, n, n).assign(random::nextGaussian);
        p.getView(0, n, n, n).assign(random::nextGaussian);
        p.getView(0, n * 2, n, n).assign(random::nextGaussian);
        p.getView(0, n * 3, n, n).assign(random::nextGaussian);

        q.getView(0, 0, n, n).assign(random::nextGaussian);
        q.getView(0, n, n, n).assign(random::nextGaussian);
        q.getView(n, 0, n, n).assign(random::nextGaussian);
        q.getView(n, n, n, n).assign(random::nextGaussian);


        DoubleArray x = Arrays.dot(Op.TRANSPOSE, Op.KEEP, p, p);
        DoubleArray t = Arrays.dot(Arrays.dot(x, x), x);
        v.set(i, Bj.trace(t));
        
        x = q.mmul(Op.TRANSPOSE, q, Op.KEEP);
        x = Arrays.dot(Op.TRANSPOSE, Op.KEEP, q, q);
        t = Arrays.dot(Arrays.dot(x, x), x);
        w.set(i, Bj.trace(x));
    }
    StatisticalSummary statV = v.collect(FastStatistics::new, FastStatistics::addValue);
    StatisticalSummary statW = w.collect(FastStatistics::new, FastStatistics::addValue);
    double meanv = statV.getMean();
    double stdv = statV.getStandardDeviation();
    double meanw = statW.getMean();
    double stdw = statW.getStandardDeviation();

    return new double[]{meanv, stdv, meanw, stdw};
}
