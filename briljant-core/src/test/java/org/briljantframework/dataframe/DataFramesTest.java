package org.briljantframework.dataframe;

import org.briljantframework.io.reslover.Resolver;
import org.briljantframework.io.reslover.Resolvers;
import org.briljantframework.io.reslover.StringDateConverter;
import org.briljantframework.vector.Vec;
import org.briljantframework.vector.Vector;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

public class DataFramesTest {

  DataFrame iris;

  @Test
  public void testLoadCsv() throws Exception {
    Resolver<LocalDate> dateResolver = Resolvers.find(LocalDate.class);
    dateResolver.put(String.class, new StringDateConverter("yyyy-MM-dd"));

    DataFrame df = DataFrames.loadCsv(
        "/Users/isak-kar/Downloads/GOOG-NASDAQ_AAPL.csv"
    );
    System.out.println(
        df.groupBy(f -> f.get(LocalDate.class, 0).getDayOfMonth())
            .aggregate(Double.class, Aggregates.max())
            .resetIndex()
            .sortBy(SortOrder.DESC, "Open")
    );

  }

  @Test
  public void testFizzBuzz() throws Exception {
    DataFrame.Builder builder = new MixedDataFrame.Builder(
        Vec.typeOf(Integer.class)
    );
    for (int i = 1; i <= 100; i++) {
      builder.set(i - 1, 0, i);
    }
    DataFrame df = builder.build().setColumnIndex(HashIndex.from("number"));
    System.out.println(
        df.transform(
            v -> v.transform(Integer.class, String.class,
                             i -> i % 15 == 0 ? "FizzBuzz" :
                                  i % 3 == 0 ? "Fizz" :
                                  i % 5 == 0 ? "Buzz" :
                                  String.valueOf(i)))
            .groupBy("number")
            .aggregate(Object.class, Aggregates.count())
            .sortBy(SortOrder.DESC, "number")
            .head(3)
    );

  }

  @Test
  public void testName() throws Exception {
    DataFrame df = MixedDataFrame.of(
        "Close", Vec.of(1, 1, 2, 3, 3, 4, 4, 4, 2, 3, 4, 6, 7, 9, 10)
    );

    Vector vec = df.get("Close");
    System.out.println(vec.aggregate(Double.class, Aggregates.join(", ", "[", "]")));

//    DoubleVector.Builder db = new DoubleVector.Builder();
//    List<Double> dl = new ArrayList<>();
//    for (int i = 0; i < 1000000; i++) {
//      db.add(i);
//      dl.add((double) i);
//    }
//    Vector vec = db.build();
//    long s = System.nanoTime();
//    Object d = null;
//    for (int i = 0; i < 100; i++) {
////      d = vec.aggregate(Double.class, Aggregates.mean());
////      RunningStatistics rs = new RunningStatistics();
////      for (int j = 0; j < vec.size(); j++) {
////        rs.add(vec.get(Double.class, j));
////      }
////      d = rs.getMean();
////      d=dl.stream().map(x -> x * 2).collect(Collectors.toList());
////      d = vec.transform(Double.class, x -> x * 2);
////     d= vec.stream(Double.class).map(x -> x * 2).collect(Vec.collector(DoubleVector.Builder::new));
////      d = vec.doubleStream().map(x -> x * 2).collect(
////          DoubleVector.Builder::new,
////          DoubleVector.Builder::add,
////          (DoubleVector.Builder left, DoubleVector.Builder right) -> left.addAll(right))
////          .build();
//
//    }
//
//    System.out.println((System.nanoTime() - s) / 1e6 / 100);
//    pass(d);
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
