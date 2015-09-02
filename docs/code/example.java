private static final Random random = new Random()

private static double[] randmatstat_Briljant(int t) {
    Random random = new Random();
    int n = 5;
    DoubleArray p = Bj.doubleArray(n, n * 4);
    DoubleArray q = Bj.doubleArray(n * 2, n * 2);
    DoubleArray v = Bj.doubleArray(t, 1);
    DoubleArray w = Bj.doubleArray(t, 1);

    for (int i = 0; i < t; i++) {
        p.getView(0, 0, n, n).assign(random::nextGaussian);
        p.getView(0, n, n, n).assign(random::nextGaussian);
        p.getView(0, n * 2, n, n).assign(random::nextGaussian);
        p.getView(0, n * 3, n, n).assign(random::nextGaussian);

        q.getView(0, 0, n, n).assign(random::nextGaussian);
        q.getView(0, n, n, n).assign(random::nextGaussian);
        q.getView(n, 0, n, n).assign(random::nextGaussian);
        q.getView(n, n, n, n).assign(random::nextGaussian);
      
        DoubleArray x = p.mmul(Op.TRANSPOSE, p, Op.KEEP);
        v.set(i, Bj.trace(x.mmul(x).mmul(x)));
        
        x = q.mmul(Op.TRANSPOSE, q, Op.KEEP);
        w.set(i, Bj.trace(x.mmul(x).mmul(x)));
    }
    StatisticalSummary statV = v.collect(FastStatistics::new, FastStatistics::addValue);
    StatisticalSummary statW = w.collect(FastStatistics::new, FastStatistics::addValue);
    double meanv = statV.getMean();
    double stdv = statV.getStandardDeviation();
    double meanw = statW.getMean();
    double stdw = statW.getStandardDeviation();

    return new double[]{meanv, stdv, meanw, stdw};
}
