package org.briljantframework.dataframe;

import org.briljantframework.function.Aggregates;
import org.briljantframework.function.Transforms;
import org.briljantframework.io.resolver.Resolver;
import org.briljantframework.io.resolver.Resolvers;
import org.briljantframework.io.resolver.StringDateConverter;
import org.briljantframework.vector.Bit;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.IntVector;
import org.briljantframework.vector.Vector;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.briljantframework.function.Aggregates.count;

public class DataFramesTest {

  DataFrame iris;

  @Test
  public void testLoadCsv() throws Exception {
    Resolver<LocalDate> dateResolver = Resolvers.find(LocalDate.class);
    dateResolver.put(String.class, new StringDateConverter("yyyy-MM-dd"));

    DataFrame open = DataFrames.loadCsv("/Users/isak-kar/Downloads/GOOG-NASDAQ_AAPL.csv")
        .groupBy(f -> f.get(LocalDate.class, 0).getMonth())
        .aggregate(Double.class, Aggregates.max())
        .sort(SortOrder.DESC, "Open");
    System.out.println(open);

    System.out.println(open.getAsDouble(Month.APRIL, "Open"));
  }

  @Test
  public void testFizzBuzz() throws Exception {
    IntVector.Builder b = new IntVector.Builder();
    for (int i = 1; i <= 100; i++) {
      b.set(i - 1, i);
    }
    DataFrame df = MixedDataFrame.of("number", b.build());

    System.out.println(
        df.transform(
            v -> v.transform(Integer.class, String.class,
                             i -> i % 15 == 0 ? "FizzBuzz" :
                                  i % 3 == 0 ? "Fizz" :
                                  i % 5 == 0 ? "Buzz" :
                                  String.valueOf(i)))
            .groupBy("number")
            .aggregate(Object.class, count())
            .sort(SortOrder.DESC, "number")
            .head(3)
    );

  }

  @Test
  public void testName() throws Exception {
    DataFrame df = MixedDataFrame.of(
        "Close", Vector.of(1, 1, 2, 3, 3, 4, 4, 4, 2, 3, 4, 6, 7, 9, 10)
    );

    Vector vec = df.get("Close");

    double mean = vec.aggregate(Integer.class, Aggregates.mean());
    System.out.println(mean);
    Vector satisfies = vec.satisfies(Integer.class, x -> x > mean);
    System.out.println(satisfies);
    Vector largerThanMean = vec.slice(satisfies);

    Vector l = vec.slice(vec.satisfies(Integer.class, vec, Integer::equals));

    System.out.println(
        vec.transform(Integer.class, Boolean.class, Transforms.lessThan(3)));

    System.out.println(vec.combine(Integer.class, Boolean.class, vec, Transforms.equal()));
    System.out.println(largerThanMean);

    Vector v = Vector.of(321.1, 1.2, 1.33214543542432, 1.4, 1.5);
    System.out.println(v.get(Bit.class, 0, () -> Bit.FALSE));

    Number aggregate = v.aggregate(Double.class, Aggregates.max());
    System.out.println(aggregate);

    System.out
        .println(v.aggregate(Double.class, Aggregates.repeat(DoubleVector.Builder::new, 2)).size());

//    System.out.println(v.collect(Double.class, Collectors.minBy(Double::compareTo)).get());
  }

  private void pass(Object d) {
  }

  @Before
  public void setUp() throws Exception {
//    iris = Datasets.loadIris();
  }

  @Test
  public void testShuffleConnect4() throws Exception {
    DataFrame iris = Datasets.loadIris();
    System.out.println(
        iris.groupBy("Class")
            .aggregate(Double.class, Aggregates.mean())
    );
  }
}
