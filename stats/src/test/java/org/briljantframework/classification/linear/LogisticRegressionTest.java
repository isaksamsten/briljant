package org.briljantframework.classification.linear;

import org.junit.Test;

public class LogisticRegressionTest {

  @Test
  public void testLogisticRegression() throws Exception {
    // // Load dataset from a CSV-file
    // StorageInputStream sis = new CSVInputStream(new FileInputStream("erlang/test.txt"));
    // Container<Frame, DefaultTarget> container = sis.read(Frame.FACTORY, DefaultTarget.FACTORY);
    //
    // // Create two shuffled partitions, for training and testing
    // Split<Frame, DefaultTarget> split = Containers.split(container.permute(), 0.3);
    // Container<Frame, DefaultTarget> train = split.getTrainingSet();
    // Container<Frame, DefaultTarget> test = split.getValidationSet();
    //
    // // Setup a transformation pipeline that remove incomplete cases and normalizes the input
    // // with normalization parameters learnt from the training data
    // Transformer<Frame, DefaultTarget> pipeline = PipelineTransformer.of(new
    // RemoveIncompleteCases(),
    // new ZNormalizer());
    // Transformation<Frame, DefaultTarget> normalize = pipeline.fit(train);
    //
    // // Construct a LogisticRegression classifier
    // LogisticRegression lr = LogisticRegression
    // .withIterations(100)
    // .withLearningRate(0.01)
    // .withRegularization(1)
    // .create();
    //
    // // Fit the classifier to
    // long start = System.currentTimeMillis();
    // LogisticRegression.Model model = lr.fit(normalize.transform(train));
    // System.out.println(System.currentTimeMillis() - start);
    // System.out.println(model.theta());
    //
    // test = normalize.transform(test);
    // Predictions predictions = model.predict(test);
    // ConfusionMatrix confusionMatrix = ConfusionMatrix.create(predictions, test.getTarget());
    // System.out.println(confusionMatrix.getError());
    //
    //
    // // This is of course illegal
    // Container<Frame, DefaultTarget> newContainer = pipeline.fitTransform(container);
    // Result result = Evaluators.crossValidation(lr, newContainer.permute(), 10);
    // System.out.println(result);
    //
    // System.out.println(result.getMetrics().stream().map(x ->
    // x.getAverage(Metric.Sample.IN)).collect(Collectors
    // .toList()));
    //
    //
    // LogisticRegression.Builder lr2 = LogisticRegression
    // .withIterations(100)
    // .withLearningRate(0.01)
    // .withRegularization(1);
    //
    // Ensemble<Frame> lrEnsemble = Ensemble.withMember(lr2)
    // .withSampler(Bootstrap.create())
    // .create();
    //
    // System.out.println(Evaluators.splitValidation(lrEnsemble, newContainer, 0.3));

    // Configurations<LogisticRegression> optimalLr = Tuners.crossValidation(
    // LogisticRegression.builder(),
    // newStorage,
    // Configuration.metricComparator(org.adeb.learning.evaluation.result.Error.class),
    // 10,
    // range("Iterations", LogisticRegression.Builder::setIterations, 10, 200, 10),
    // range("Regularization", LogisticRegression.Builder::setRegularization, 0.1, 1, 0.1)
    // );
    //
    // System.out.println(optimalLr);


    // List<TargetView<Matrix.Entry>> split = TargetView.wrap(dataset, 4).shuffle().partition(2);
    // TargetView<Matrix.Entry> train = split.get(0);
    // TargetView<Matrix.Entry> test = split.get(1);
    //
    // LogisticRegression lr = LogisticRegression.iterations(0.001).iterations(1000).build();
    // long start = System.currentTimeMillis();
    // LogisticRegressionModel model = lr.fit(train);
    // for (int i = 0; i < 9; i++) {
    // model = lr.fit(train);
    // }
    // System.out.println((System.currentTimeMillis() - start) / (double) 1000);
    //
    // Matrix v = Matrix.zero(1, 4);
    // int correct = 0;
    // for (Target<Matrix.Entry> e : test) {
    // for (int i = 0; i < 4; i++) {
    // v.put(i, dataset.get(e.id(), i));
    // }
    // Prediction p = model.predictVector(v);
    // if (p.target().equals(e.target())) {
    // correct += 1;
    // }
    // }
    // System.out.println("Accuracy: " + ((double) correct) / test.rows());
  }

  // @Test
  // public void testPipeline() throws Exception {
  // Pipeline p = Pipeline.with(InputDataset.withFile("erlang/test.txt").unboxer(new
  // RuleDiscoveryUnboxer<>
  // (Matrix.factory())))
  // .next(CrossValidation
  // .withClassifier(
  // LogisticRegression.iterations(0.001).iterations(1000).build()))
  // .next(Write.withStream(System.out)).build();
  //
  // p.validate().ifPresent(System.out::println);
  // p.exec(Context.init());
  //
  //
  // }
}
