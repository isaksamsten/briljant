package org.briljantframework.dataframe.transform;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.MixedDataFrame;
import org.briljantframework.distribution.Distribution;
import org.briljantframework.distribution.TriangleDistribution;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.Vec;
import org.junit.Test;

public class ZNormalizerTest {

  @Test
  public void testFit() throws Exception {
    DataFrame a = MixedDataFrame.of(
        "a", new DoubleVector(1, 2, 3, 4, DoubleVector.NA),
        "b", new DoubleVector(2, 3, 3, 4, 5)

    );

    System.out.println(a);
    DataFrame f = new ZNormalizer().fitTransform(a);
    System.out.println(Vec.std(f.get(0)));

    Distribution gaussian = new TriangleDistribution(10, 100, 50);
    DataFrame x = MixedDataFrame.of(
        "a", Vec.rand(10, gaussian),
        "b", Vec.rand(10, gaussian)
    );
    System.out.println(x);
    System.out.println(new ZNormalizer().fitTransform(x));
  }
}