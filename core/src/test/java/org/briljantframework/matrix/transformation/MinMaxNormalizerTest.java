package org.briljantframework.matrix.transformation;

import org.briljantframework.matrix.DenseMatrix;
import org.junit.Before;
import org.junit.Test;

public class MinMaxNormalizerTest {
  DenseMatrix matrix;

  @Before
  public void setUp() throws Exception {
    matrix = DenseMatrix.of(4, 4, 0, 2, 0, 1, 2, 2, 3, 2, 4, -3, 0, 1., 6, 1, -6, 1);
  }

  @Test
  public void testMinMax() throws Exception {
    // Storage<Frame, Target> storage = Storage.create(StorageFactory.create(Frame.FACTORY,
    // BasicTarget.FACTORY),
    // new Frame(new ArrayList<>(), matrix));

    // Transformer<Dataset, Target> da = new RemoveIncompleteCases();
    //
    // storage = da.fitTransform(storage);

    // Transformation<Frame, Target> minMax = new ZNormalizer().fit(storage);
    // Storage<Frame, Target> normalized = minMax.transform(storage);
    // System.out.println(normalized.getDataset().getMatrix());


    // Transformation<Matrix> minMax = new MinMaxNormalizer().fit(matrix);
    // System.out.println(minMax.transform(matrix));
  }
}
