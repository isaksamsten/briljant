package org.briljantframework.matrix.analysis;

import org.briljantframework.matrix.ArrayMatrix;
import org.briljantframework.matrix.Matrix;
import org.junit.Before;
import org.junit.Test;

public class PrincipalComponentAnalyzerTest {

  Matrix matrix;

  @Before
  public void setUp() throws Exception {
    matrix = ArrayMatrix.of(5, 4, 0, 2, 0, 1, 2, 2, 3, 2, 4, -3, 0, 1., 6, 1, -6, -5, 1, 2, 3, 4);
  }

  @Test
  public void testTransform() throws Exception {
    // PrincipalComponentAnalyzer pca = new PrincipalComponentAnalyzer(2);
    // MatrixDataFrame frame = new MatrixDataFrame(Types.range(NumericType::new, 4), matrix);
    //
    //
    // System.out.println(frame);
    // Transformation<MatrixDataFrame> pcaTransformation = pca.fit(frame);
    // System.out.println(pcaTransformation.transform(frame, MatrixDataFrame.copyTo()));

  }

  @Test
  public void testDimensionalityReduction() throws Exception {
    // InvertibleTransformer<Matrix> dimensionalityReducer = new PrincipalComponentAnalyzer();
    // InvertibleTransformation<Matrix> invertibleTransformation =
    // dimensionalityReducer.fit(matrix);
    //
    // Matrix reduced = invertibleTransformation.transform(matrix);
    // System.out.println(invertibleTransformation.inverseTransform(reduced));
    // System.out.println(matrix);


    // System.out.println(invertibleTransformation.transform(matrix));


  }
}
