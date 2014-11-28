package org.briljantframework.matrix.transformation;

import org.briljantframework.matrix.DenseMatrix;
import org.briljantframework.matrix.Matrix;
import org.junit.Before;
import org.junit.Test;

public class MeanImputerTest {
  Matrix matrix;

  @Before
  public void setUp() throws Exception {
    matrix = DenseMatrix.of(4, 4, 0, 2, Double.NaN, 1, 2, 2, 3, 2, 4, -3, 0, 1., 6, 1, -6, -5);
  }

  @Test
  public void testFit() throws Exception {
    // Frame dataset = new CSVInputStream(new FileInputStream("erlang/test.txt"))
    // .read(Frame.FACTORY);
    // ClassificationFrame frame = ClassificationFrame.create(dataset, Frame.FACTORY,
    // DefaultTarget.FACTORY);

    // List<Type> types = Stream
    // .generate(() -> new NumericType("temp"))
    // .limit(4)
    // .collect(Collectors.toCollection(ArrayList::new));
    //
    //
    // MatrixDataFrame frame = new MatrixDataFrame(new Types(types), matrix);

    // PipelineTransformer<MatrixDataFrame> pipe = PipelineTransformer.of(MatrixDataFrame.copyTo(),
    // new MeanImputer<>(), new ZNormalizer<>()
    // );

    // MatrixDataFrame transformation = pipe.fitTransform(frame, MatrixDataFrame.copyTo());
    // System.out.println(transformation);


    // System.out.println();
    // System.out.println(Matrices.mean(DenseMatrix::new, transformation, Axis.COLUMN));
    // System.out.println(Matrices.std(DenseMatrix::new, transformation, Axis.COLUMN));
    // System.out.println(Matrices.mmul(DenseMatrix::new, transformation, transformation));


    // Transformation<ClassificationFrame> pipeTrans = pipe.fit(frame);
    // System.out.println(pipeTrans.transform(frame, ClassificationFrame.FACTORY));

    // Transformation<Matrix> imputation = new MeanImputation().fit(Instances.of(matrix));
    // System.out.println(imputation.transform(matrix));
  }
}
