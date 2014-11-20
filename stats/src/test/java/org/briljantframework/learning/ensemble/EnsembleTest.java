package org.briljantframework.learning.ensemble;

import org.junit.Test;

public class EnsembleTest {

    @Test
    public void testFit() throws Exception {
//        Container<DenseDataset, DefaultTarget> container = new CSVInputStream(new FileInputStream
//                ("erlang/adeb-rr/deps/rr/data/car.txt")).read(DenseDataset.FACTORY, DefaultTarget.FACTORY);
//
//        DecisionTree.Builder dt = DecisionTree.withSplitter(RandomSplitter
//                .withMaximumFeatures(2)
//                .setCriterion(Gain.with(Entropy.INSTANCE)));
//
//        Ensemble<Dataset> ensemble = Ensemble.withMember(dt)
//                .withSampler(Bootstrap.create())
//                .create();
//
//        long start = System.currentTimeMillis();
//        Ensemble.Model<Dataset> model = ensemble.fit(container);
//        System.out.println(System.currentTimeMillis() - start);
//
//        start = System.currentTimeMillis();
//        Predictions predictions = model.predict(container.getDataset());
//        System.out.println(System.currentTimeMillis() - start);
//
//        int correct = 0;
//        for (int i = 0; i < predictions.size(); i++) {
//            if (predictions.get(i).getValue().equals(container.getTarget().getValue(i))) {
//                correct++;
//            }
//        }
//        System.out.println((double) correct / container.rows());
//
//        SplitValidation.create().evaluate(ensemble, container);
    }
}