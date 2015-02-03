package org.briljantframework.evaluation;

import static org.junit.Assert.assertTrue;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.Datasets;
import org.briljantframework.matrix.Slice;
import org.briljantframework.vector.Vector;
import org.junit.Test;

public class FoldPartitionerTest {

  @Test
  public void testPartition() throws Exception {
    DataFrame dummy = Datasets.loadConnect4();
    DataFrame x = dummy.takeColumns(Slice.slice(0, 2));
    Vector y = dummy.getColumn(2);

    System.out.println(x);

    Partitioner strategy = new FoldPartitioner(10);
    Iterable<Partition> partitionIterator = strategy.partition(x, y);
    int i = 0;
    for (Partition partition : partitionIterator) {
      System.out.println("Fold " + i++);
      System.out.println(partition.getTrainingData().rows());
      System.out.println(partition.getValidationData().rows());
    }


    // DataFrame iris = DataFrames.permuteRows(Datasets.loadIris());
    // DataFrame irisX = iris.takeColumns(IntRange.range(0, 4));
    // Vector irisY = iris.getColumn(4);
    //
    // RandomForest tree = RandomForest.withSize(100).build();
    //
    // long start = System.currentTimeMillis();
    // System.out.println(ClassificationEvaluators.crossValidation(10).evaluate(tree, irisX,
    // irisY));
    // System.out.println(System.currentTimeMillis() - start);

    // DataFrame synthetic = Datasets.loadSyntheticControl();
    // DataFrame x = synthetic.takeColumns(IntRange.range(1, synthetic.columns()));
    // Vector y = synthetic.getColumnView(0);
    //
    // RandomShapeletForest forest =
    // RandomShapeletForest.withSize(10).withInspectedShapelets(100).create();
    // System.out.println(Evaluators.crossValidation(10).evaluate(forest, x, y));
    //
    assertTrue(true);
  }
}
