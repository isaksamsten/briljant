package org.briljantframework.evaluation;

import static org.junit.Assert.assertTrue;

import org.briljantframework.IntRange;
import org.briljantframework.classification.RandomForest;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.DataFrames;
import org.briljantframework.dataframe.Datasets;
import org.briljantframework.vector.Vector;
import org.junit.Test;

public class RandomFoldPartitionerTest {

  @Test
  public void testPartition() throws Exception {
    // DataFrame dummy = Datasets.loadDummy();
    // DataFrame x = dummy.takeColumns(IntRange.closed(0, 2));
    // Vector y = dummy.getColumn(2);
    //
    // System.out.println(x);
    //
    // Partitioner strategy = new RandomFoldPartitioner(3);
    // Iterable<Partition> partitionIterator = strategy.partition(x, y);
    // int i = 0;
    // for (Partition partition : partitionIterator) {
    // System.out.println("Fold " + i++);
    // System.out.println(partition.getTrainingData());
    // System.out.println(partition.getValidationData());
    // }


    DataFrame iris = DataFrames.shuffle(Datasets.loadIris());
    DataFrame irisX = iris.takeColumns(IntRange.closed(0, 4));
    Vector irisY = iris.getColumn(4);

    RandomForest tree = RandomForest.withSize(100).build();

    long start = System.currentTimeMillis();
    System.out.println(Evaluators.crossValidation(10).evaluate(tree, irisX, irisY));
    System.out.println(System.currentTimeMillis() - start);

    // DataFrame synthetic = Datasets.loadSyntheticControl();
    // DataFrame x = synthetic.takeColumns(IntRange.closed(1, synthetic.columns()));
    // Vector y = synthetic.getColumn(0);
    //
    // RandomShapeletForest forest =
    // RandomShapeletForest.withSize(10).withInspectedShapelets(100).create();
    // System.out.println(Evaluators.crossValidation(10).evaluate(forest, x, y));
    //
    assertTrue(true);
  }
}
