package org.briljantframework.dataframe.transform;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.Datasets;
import org.briljantframework.dataframe.MixedDataFrame;
import org.briljantframework.vector.DoubleVector;
import org.junit.Before;
import org.junit.Test;

public class MeanImputerTest {

  @Before
  public void setUp() throws Exception {}

  @Test
  public void testFit() throws Exception {
    DataFrame frame =
        new MixedDataFrame(DoubleVector.wrap(1, 2, 3, DoubleVector.NA), DoubleVector.wrap(3, 3, 3,
            DoubleVector.NA), DoubleVector.wrap(DoubleVector.NA, 2, 2, DoubleVector.NA));

    System.out.println(frame);

    MeanImputer imputer = new MeanImputer();
    Transformation t = imputer.fit(frame);

    System.out.println(t.transform(frame));


    DataFrame iris = Datasets.loadIris();
    DataFrame x = iris.dropColumn(4);

    t = imputer.fit(x);

    Transformer pipe = PipelineTransformer.of(new MeanImputer(), new MinMaxNormalizer());
    System.out.println(pipe.fitTransform(x));



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
