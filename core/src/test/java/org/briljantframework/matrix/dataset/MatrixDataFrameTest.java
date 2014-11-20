package org.briljantframework.matrix.dataset;

import org.briljantframework.data.Datasets;
import org.briljantframework.data.DenseDataFrame;
import org.briljantframework.data.types.NumericType;
import org.briljantframework.data.types.Types;
import org.briljantframework.matrix.Matrices;
import org.junit.Test;

public class MatrixDataFrameTest {

    @Test
    public void testCreateFrame() throws Exception {
        MatrixDataFrame frame = new MatrixDataFrame(Types.range(NumericType::new, 10), Matrices.randn(100, 10));
        System.out.println(frame);


        System.out.println(
                frame.stream()
                        .limit(3)
                        .collect(Datasets.collect(() -> new DenseDataFrame.Builder(frame.getTypes()))));
        System.out.println(Datasets.as(frame, DenseDataFrame.copyTo()));

    }
}