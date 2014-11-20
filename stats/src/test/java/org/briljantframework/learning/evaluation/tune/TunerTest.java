package org.briljantframework.learning.evaluation.tune;

import org.junit.Test;

public class TunerTest {

    @Test
    public void testOptimize() throws Exception {
//        Container<Frame, DefaultTarget> container = new DataSeriesInputStream(new FileInputStream
//                ("/Users/isak/Downloads/dataset/synthetic_control/synthetic_control_TRAIN"))
//                .read(Frame.FACTORY, DefaultTarget.FACTORY)
//                .permute();

//        Frame frame = DataSeriesInputStream.load("/Users/isak/Downloads/dataset/" +
//                "synthetic_control/synthetic_control_TRAIN", Frame.FACTORY);
//        ClassificationFrame container = ClassificationFrame.create(frame, Frame.FACTORY, DefaultTarget.FACTORY);
//
//        DynamicTimeWarping dwt1 = DynamicTimeWarping.create(Distance.EUCLIDEAN, 1);
//        DynamicTimeWarping dwt4 = DynamicTimeWarping.withDistance(Distance.EUCLIDEAN).withConstraint(4).create();
//        DynamicTimeWarping dwt10 = DynamicTimeWarping.withDistance(Distance.EUCLIDEAN).withConstraint(10).create();
//        DynamicTimeWarping dwt20 = DynamicTimeWarping.withDistance(Distance.EUCLIDEAN).withConstraint(20).create();

//
//        Configurations<KNearestNeighbors> knn = Tuners.split(
//                KNearestNeighbors.builder(), container,
//                range("Neighbors", KNearestNeighbors.Builder::withNeighbors, 1, 8, 1),
//                enumeration("Distance", KNearestNeighbors.Builder::withDistance, Distance.EUCLIDEAN,
//                        Distance.MANHATTAN, dwt1, dwt4, dwt10, dwt20)
//        );
//        Configurations<KNearestNeighbors> knn = Tuners.crossValidatation(
//                KNearestNeighbors.builder(),
//                storage,
//                Configuration.metricComparator(Accuracy.class),
//                10,
//                range("Neighbors", KNearestNeighbors.Builder::neighbours, 1, 8, 1),
//                enumeration("Distance", KNearestNeighbors.Builder::distance, Distance.EUCLIDEAN, Distance.MANHATTAN)
//        );
//        System.out.println(knn);
//

//        Configurations<RandomShapeletForest> forest = Tuners.split(
//                RandomShapeletForest.withSize(50), container,
//                range("Inspected Shapelets", RandomShapeletForest.Builder::withInspectedShapelets, 10, 40, 10)
//        );
//
//        System.out.println(forest);
//
//        KNearestNeighbours c = knn.best().getClassifier();
//        System.out.println(Evaluators.crossValidation(c, storage, 10));
//
//
//        RemoteClassifier.Builder randomForest = RemoteClassifier.with("RandomForest",
//                ProcessProtocol.create("erlang/adeb-rr/rr_learn"),
//                ProcessProtocol.create("erlang/adeb-rr/rr_predict")
//        ).set("no_features", "default");
//
//        Configurations<RemoteClassifier> rm = Tuners.crossValidatation(randomForest, storage,
//                Configuration.metricComparator(AreaUnderCurve.class), 10,
//                enumeration("no_trees", (toUpdate, value) -> toUpdate.set("no_trees", value), 10, 20, 30, 40),
//                enumeration("no_features", (toUpdate, value) -> toUpdate.set("no_features", value), "default",
// "sqrt", 1, 2, 3)
//        );
//
//        System.out.println(rm);
//
//        RemoteClassifier bestRandomForest = rm.best().getClassifier();
//        System.out.println(CrossValidation.withFolds(10).evaluate(bestRandomForest, storage));


//        String name = "MoteStrain";
//        String datasetPath = String.format("/Users/isak/Downloads/dataset2/%s/%s_", name, name);

//        Storage<Frame, Target> train = DataSeries.load(datasetPath + "TRAIN", Frame.FACTORY, BasicTarget.FACTORY);
//        Storage<Frame, Target> test = DataSeries.load(datasetPath + "TEST", Frame.FACTORY, BasicTarget.FACTORY);
//
//        Configurations<RandomShapeletForest> forestConfigurations = Tuners.split(RandomShapeletForest.size(100)
// .setInspectedShapelets(100).setLowerLength(2), train,
//                range("shapelets", RandomShapeletForest.Builder::setUpperLength, 10, 50, 10)
//        );
//        RandomShapeletForest forest = RandomShapeletForest.size(100).setInspectedShapelets(100).setLowerLength(2)
// .setUpperLength(50).create();
//        System.out.println(forestConfigurations);
//        System.out.println(Evaluators.holdOutValidation(forest, train, test));
//
//        KNearestNeighbours optimize = Optimizer.split(
//                KNearestNeighbours.builder(), storage,
//                range("neighbors", KNearestNeighbours.Builder::neighbors, 3, 1, -1));

//        optimize = Optimizer.split(LogisticRegression.builder(), storage,
//                range("learningRate", LogisticRegression.Builder::learningRate, 0.01, 0.11, 0.05),
//                range("iterations", LogisticRegression.Builder::iterations, 1000, 3000, 500));

    }


}