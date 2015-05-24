package org.briljantframework.function;

import com.sun.org.apache.xpath.internal.operations.Bool;

import org.briljantframework.stat.RunningStatistics;
import org.briljantframework.vector.Is;
import org.briljantframework.vector.Vector;
import org.junit.Test;

import java.io.Serializable;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static org.briljantframework.vector.Combine.ignoreNA;

public class TransformsTest {

  @Test
  public void testClip() throws Exception {
    Random rand = new Random(123);
    Vector a = Vector.of(rand::nextGaussian, 14);
//    double b = a.transform(Double.class, clip(-0.5, 0.5)).aggregate(Double.class, Aggregates.max());
//    Vector b = a.transform(Double.class, (v) -> {
//      double sqrt = Math.sqrt(v);
//      return Double.isNaN(sqrt) ? Na.of(Double.class) : Double.valueOf(sqrt);
//    });
    Vector b = Vector.of(1, 2, 3, 4, 5, 6, 7, 8, 9, null);
    System.out.println(a);
    System.out.println(a.combine(Double.class, Boolean.class, b, ignoreNA((x, y) -> x*4 > y)));

//    System.out.println(b);

    System.out.println(time(vod -> {
      double aggregate = b.aggregate(Double.class, Aggregates.mean());
    }));
    System.out.println(time(vod -> {
//      Vector filter = b.filter(Double.class, (o) -> !Is.NA(o));
//      double average = b.doubleStream().filter(
//          (value) -> !Is.NA(value)).summaryStatistics().getAverage();
      RunningStatistics stats = new RunningStatistics();
      for (int i = 0; i < b.size(); i++) {
        double v = b.getAsDouble(i);
        if (!Is.NA(v)) {
          stats.add(v);
        }
      }
      double avg = stats.getMean();
    }));

//    Vector c = a.combine(Double.class, a, (x, y) -> x * y);
//    System.out.println(c);
  }

  public double time(Consumer<Void> operator) {
    long start = System.nanoTime();
    operator.accept(null);
    return (System.nanoTime() - start) / 1e6;
  }
}