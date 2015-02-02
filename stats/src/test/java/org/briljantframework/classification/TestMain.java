package org.briljantframework.classification;


import static org.briljantframework.matrix.Matrices.randn;

import org.briljantframework.matrix.DefaultDoubleMatrix;
import org.briljantframework.matrix.DoubleMatrix;

/**
 * Created by Isak Karlsson on 13/10/14.
 */
public class TestMain {

  public static void main(String[] args) throws Exception {
    // System.setProperty("apple.laf.useScreenMenuBar", "true");
    // System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Test");
    // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    //
    // CSVInputStream in = new CSVInputStream(new
    // FileInputStream("erlang/adeb-rr/deps/rr/data/iris.txt"));
    // Dataset dataset = in.read(DenseDataset.getFactory());


    DoubleMatrix a = randn(10000, 10000);
    // Matrix b = randn(10, 10000);
    // RealVector.Builder builder = new RealVector.Builder();
    // for (int i = 0; i < b.size(); i++) {
    // builder.add(b.get(i));
    // }
    //
    // VectorMatrix matrix = new VectorMatrix(builder.build(), 10, 10000);
    // long s = System.currentTimeMillis();
    // for (int i = 0; i < 10; i++) {
    // a.mmul(b);
    // }
    // System.out.println((System.currentTimeMillis() - s) / (double) 1);


    // double v = matrix.mapReduce(0, (d, f) -> d + f, x -> x);


    // Matrix m = Matrices.parseMatrix("1,2,3;4,5,6;7,8,9");
    // for (int i = 0; i < m.size(); i++) {
    // System.out.println(m.get(i));
    // }
    //
    // // for (int i = 0; i < x.size(); i++) {
    // // System.out.println(x.get(i));
    // // }
    //
    //
    // Matrix y = m.getView(1, 1, 2, 2);
    // System.out.println(y);
    // for (int i = 0; i < y.size(); i++) {
    // System.out.println(y.get(i));
    // }
    //
    //
    // Matrix mat = Matrices.parseMatrix("1,2,3,4;5,6,7,8;9,10,11,12;13,14,15,16");
    //
    // Matrix d = mat.getView(1, 1, 3, 3);
    // System.out.println(d);
    // for (int i = 0; i < d.size(); i++) {
    // System.out.println(d.get(i));
    // }
    // System.out.println(m);
    // for (int i = 0; i < m.columns(); i++) {
    // System.out.println(m.getColumnView(i));
    // }


    // System.out.println(m.getView(1, 1, 2, 2));



    long s = System.currentTimeMillis();
    // Matrix rowMean = a.reduceRows(x -> x.mapReduce(0, Double::sum, xy -> xy) / x.size());

    DoubleMatrix rowMeans = new DefaultDoubleMatrix(1, a.rows());
    double div = a.columns();
    for (int j = 0; j < a.columns(); j++) {
      double mean = 0;
      for (int i = 0; i < a.rows(); i++) {
        mean += a.get(i, j);
      }
      rowMeans.set(j, mean / div);
    }
    // for (int i = 0; i < a.rows(); i++) {
    // double mean = 0;
    // for (int j = 0; j < a.columns(); j++) {
    // mean += a.get(i, j);
    // }
    // rowMeans.put(i, mean / a.columns());
    // }

    System.out.println(System.currentTimeMillis() - s);
    System.out.println(rowMeans);
    //
    // System.out.println(v);


    // TargetContainer container = TargetContainer.create(dataset, "Class",
    // DenseDataset.FACTORY, DefaultTarget.FACTORY);
    //
    // DecisionTree.Builder dt = DecisionTree.withSplitter(
    // RandomSplitter.withMaximumFeatures(3)
    // .withCriterion(Gain.with(Entropy.INSTANCE))
    // );
    // Ensemble<TargetableContainer> ensemble = Ensemble.withMember(dt)
    // .withSampler(Bootstrap.create())
    // .create();


    // Result result = Evaluators.crossValidation(ensemble, container, 10);
    // Chartable.saveSVG("/Users/isak/Desktop/test.svg", result.getChart(), 600, 400);
    // Chartable.saveSVG("/Users/isak/Desktop/confusion.svg",
    // result.getAverageConfusionMatrix().getChart(), 600,
    // 450);
    //

    // JPanel grid = new JPanel(new GridLayout(1, 2));
    // grid.add(new ChartPanel(result.getAverageConfusionMatrix().getPrecisionRecallChart()));
    // grid.add(new ChartPanel(result.getAverageConfusionMatrix().getChart()));
    //
    // JPanel master = new JPanel(new GridLayout(2, 1));
    // master.add(new ChartPanel(result.getChart()));
    // master.add(grid);
    //
    // JFrame frame = new JFrame();
    // frame.add(master);
    // frame.pack();
    // frame.setVisible(true);
    // frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


    // CategoryPlot aucPlot = (CategoryPlot) result.get(AreaUnderCurve.class).getPlot();
    // CategoryPlot errorPlot = (CategoryPlot)
    // result.get(org.adeb.learning.evaluation.result.Error.class).getPlot();
    // CategoryPlot accuracyPlot = (CategoryPlot) result.get(Accuracy.class).getPlot();

    // CombinedDomainCategoryPlot combinedDomainCategoryPlot = new CombinedDomainCategoryPlot();
    // combinedDomainCategoryPlot.setOrientation(PlotOrientation.HORIZONTAL);
    // combinedDomainCategoryPlot.add(aucPlot);
    // combinedDomainCategoryPlot.add(errorPlot);
    // combinedDomainCategoryPlot.add(accuracyPlot);

    // JFreeChart chart = new JFreeChart(aucPlot);


    // System.out.println(System.currentTimeMillis() - start);
    //
    // ArrayList<Chart> charts = new ArrayList<>();
    // charts.addAll(result.plot(AreaUnderCurve.class));
    // charts.addAll(result.plot(Accuracy.class));
    // charts.addAll(result.plot(Error.class));
    // new SwingWrapper(charts).displayChartMatrix("Result Matrix");

    // Storage<Frame, Target> train = DataSeriesInputStream.load
    // ("/Users/isak/Downloads/dataset/synthetic_control/synthetic_control_TRAIN",
    // Frame.FACTORY, BasicTarget.FACTORY).permute();
    // Storage<Frame, Target> test = DataSeriesInputStream.load
    // ("/Users/isak/Downloads/dataset/synthetic_control/synthetic_control_TEST",
    // Frame.FACTORY, BasicTarget.FACTORY);

    // RandomShapeletForest f = RandomShapeletForest
    // .withSize(4)
    // .withLowerLength(2)
    // .withUpperLength(10)
    // .withInspectedShapelets(1)
    // .withSampler(Bootstrap.create())
    // .create();
    // Result result = Evaluators.holdOutValidation(f, train, test);
    //
    // JPanel grid = new JPanel(new GridLayout(1, 2));
    // grid.add(new ChartPanel(result.getAverageConfusionMatrix().getChart()));
    // grid.add(new ChartPanel(result.getAverageConfusionMatrix().getHeatMap()));
    //
    // JPanel master = new JPanel(new GridLayout(2, 1));
    // master.add(new ChartPanel(result.getChart()));
    // master.add(grid);
    //
    // JFrame frame = new JFrame();
    // frame.add(master);
    // frame.pack();
    // frame.setVisible(true);
    // frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    // System.out.println(result);

    // System.out.println(result);
    //
    // ChartPanel panel = new ChartPanel(result.get(AreaUnderCurve.class).getPerValueChart());
    // JPanel grid = new JPanel(new GridLayout(2, 1));
    // grid.add(panel);
    // grid.add(new ChartPanel(result.getChart()));
    // grid.setBackground(Color.WHITE);
    //
    //
    // JFrame frame = new JFrame();
    // JMenuBar bar = new JMenuBar();
    // JMenu menu = new JMenu("Hello world");
    // bar.add(menu);
    // frame.setJMenuBar(bar);
    // frame.add(grid);
    // frame.pack();
    // frame.setVisible(true);
    // frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    //
    //
    // DynamicTimeWarping dwt1 = DynamicTimeWarping.create(Distance.EUCLIDEAN, 1);
    // DynamicTimeWarping dwt4 =
    // DynamicTimeWarping.withDistance(Distance.EUCLIDEAN).withConstraint(4).create();
    // DynamicTimeWarping dwt10 =
    // DynamicTimeWarping.withDistance(Distance.EUCLIDEAN).withConstraint(10).create();
    // DynamicTimeWarping dwt20 =
    // DynamicTimeWarping.withDistance(Distance.EUCLIDEAN).withConstraint(20).create();


    // long start = System.currentTimeMillis();
    // Storages.stack(train, test);
    // System.out.println(System.currentTimeMillis() - start);


    // Configurations<KNearestNeighbors> knn = Tuners.split(
    // KNearestNeighbors.builder().withDistance(dwt4),
    // Storages.stack(train, test),
    // Configuration.metricComparator(org.adeb.learning.evaluation.result.Error.class),
    // 0.3,
    // Updaters.range("Neighbors", (KNearestNeighbors.Builder b, Integer constraint) ->
    // b.withDistance
    // (DynamicTimeWarping.withDistance(Distance.EUCLIDEAN).withConstraint(constraint).create()), 1,
    // 5, 1)
    // );
    // System.out.println(knn);
    // Configurations<RandomShapeletForest> forest = Tuners.split(
    // RandomShapeletForest.withSize(100).withLowerLength(2).withSampler(Bootstrap.create())
    // .withInspectedShapelets(100), train, Configuration.metricComparator(Accuracy.class), 0.3,
    // Updaters.range("MaxLength", RandomShapeletForest.Builder::withUpperLength, 5, 60, 5)
    // );
    // System.out.println(Evaluators.holdOutValidation(
    // RandomShapeletForest.withSize(100)
    // .withLowerLength(2)
    // .withUpperLength(-1)
    // .withSampler(Bootstrap.create())
    // .withInspectedShapelets(100)
    // .create(), train, test));


    // ChartPanel panel = new ChartPanel(knn.getChartForParameter("Neighbors", Error.class));
    //
    // JFrame frame = new JFrame();
    // frame.add(panel);
    // frame.pack();
    // frame.setVisible(true);
    // frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

  }

  private static void blackbox(double sum) {

  }
}
