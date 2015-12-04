/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.data.dataframe.transform;

import static org.junit.Assert.assertEquals;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.random.Well1024a;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.briljantframework.data.Collectors;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public abstract class TransformationTests {

  private DataFrame train, test;

  @Before
  public void setUp() throws Exception {
    NormalDistribution distribution =
        new NormalDistribution(new Well1024a(100), 10, 2,
            NormalDistribution.DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
    train =
        getBuilder().set("a", Vector.fromSupplier(distribution::sample, 100))
            .set("b", Vector.fromSupplier(distribution::sample, 100))
            .set("c", Vector.fromSupplier(distribution::sample, 100)).build();

    test =
        getBuilder().set("a", Vector.fromSupplier(distribution::sample, 100))
            .set("c", Vector.fromSupplier(distribution::sample, 100))
            .set("b", Vector.fromSupplier(distribution::sample, 80)).build();

  }

  abstract DataFrame.Builder getBuilder();

  @Test
  public void testFit_ZNormalization() throws Exception {
    Transformation normalizer = new ZNormalizer();
    Transformer transformer = normalizer.fit(train);

    Vector trainMean = transformer.transform(train).reduce(Vector::mean);
    assertEquals(0, trainMean.getAsDouble("a"), 1e-6);
    assertEquals(0, trainMean.getAsDouble("b"), 1e-6);
    assertEquals(0, trainMean.getAsDouble("c"), 1e-6);

    DataFrame normalizedTest = transformer.transform(test);
    // assertTrue(normalizedTest.get("b").select(80, 100).all(Is::NA));

    Vector testMean = normalizedTest.reduce(Vector::mean);
    assertEquals(0, testMean.getAsDouble("a"), 3e-1);
    assertEquals(0, testMean.getAsDouble("b"), 3e-1);
    assertEquals(0, testMean.getAsDouble("c"), 3e-1);
  }

  @Test
  public void testFit_MinMaxNormalization() throws Exception {
    Transformation normalizer = new MinMaxNormalizer();
    Transformer transformer = normalizer.fit(train);
    Vector trainSummary =
        transformer.transform(train).collect(Double.class, Collectors.statisticalSummary());

    System.out.println(trainSummary);
    assertEquals(1, trainSummary.get(StatisticalSummary.class, "a").getMax(), 0);
    assertEquals(1, trainSummary.get(StatisticalSummary.class, "b").getMax(), 0);
    assertEquals(1, trainSummary.get(StatisticalSummary.class, "c").getMax(), 0);

    assertEquals(0, trainSummary.get(StatisticalSummary.class, "a").getMin(), 0);
    assertEquals(0, trainSummary.get(StatisticalSummary.class, "b").getMin(), 0);
    assertEquals(0, trainSummary.get(StatisticalSummary.class, "c").getMin(), 0);
  }

  @Test
  public void testFit_MeanImputer() throws Exception {
    MeanImputer imputer = new MeanImputer();
    Transformer t = imputer.fit(train);
    DataFrame imputed = t.transform(test);

    // assertEquals(train, imputed.getColumnIndex());
    // assertEquals(test, imputed.getIndex());
  }
}
