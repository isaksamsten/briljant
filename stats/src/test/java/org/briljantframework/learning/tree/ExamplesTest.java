package org.briljantframework.learning.tree;

public class ExamplesTest {

  // @Test
  // public void testSplit() throws Exception {
  // long start = System.currentTimeMillis();
  // Container<DenseDataset, DefaultTarget> container = new CSVInputStream(new FileInputStream
  // ("erlang/adeb-rr/deps/rr/data/connect-4.txt")).read(DenseDataset.FACTORY,
  // DefaultTarget.FACTORY);
  // System.out.println(System.currentTimeMillis() - start);
  //
  // Target target = container.getTarget();
  //
  // System.out.println(container);
  // Random random = new Random();
  // start = System.currentTimeMillis();
  //
  // Examples examples = Examples.create();
  // for (int i = 0; i < target.size(); i++) {
  // if (random.nextDouble() > 0.3)
  // examples.add(target.getValue(i), i, 1.0f);
  // }
  //
  // System.out.println(examples.getTotalWeight());
  // System.out.println(System.currentTimeMillis() - start);
  //
  // start = System.currentTimeMillis();
  //
  // DecisionTree tree = DecisionTree.withSplitter(RandomSplitter
  // .withMaximumFeatures(2)
  // .setCriterion(Gain.GINI))
  // .create();
  // System.out.println(Evaluators.crossValidation(tree, container, 10));
  // // DecisionTree.Model mdl = tree.fit(storage);
  //
  // // mdl = tree.fit(storage);
  // // mdl = tree.fit(storage);
  //
  // // List<Prediction> predictions = mdl.predict(storage.getDataset());
  // // int correct = 0;
  // // for (int i = 0; i < predictions.size(); i++) {
  // //// System.out.println(predictions.get(i) + " :: " + storage.getTarget().getValue(i));
  // // if (predictions.get(i).getValue().equals(storage.getTarget().getValue(i))) {
  // // correct++;
  // // }
  // // }
  // // System.out.println((double) correct / storage.rows());
  // // System.out.println(System.currentTimeMillis() - start);
  // }
  //
  // @Test
  // public void testIterator() throws Exception {
  // Container<DenseDataset, DefaultTarget> container = new CSVInputStream(new FileInputStream
  // ("erlang/adeb-rr/deps/rr/data/connect-4.txt")).read(DenseDataset.FACTORY,
  // DefaultTarget.FACTORY);
  // Examples examples = Examples.fromContainer(container);
  // int i = 0;
  // for (Example e : examples) {
  // container.getTarget().getValue(e.getIndex());
  // i++;
  // }
  //
  // Assert.assertEquals(container.rows(), i);
  // }
}
