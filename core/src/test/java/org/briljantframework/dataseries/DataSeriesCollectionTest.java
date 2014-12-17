package org.briljantframework.dataseries;

import static org.briljantframework.matrix.Matrices.linspace;
import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.briljantframework.chart.Chartable;
import org.briljantframework.io.DataInputStream;
import org.briljantframework.io.MatlabTextInputStream;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorLike;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.Test;

public class DataSeriesCollectionTest {

  @Test
  public void testRenderLinearMeanLttbResamplers() throws Exception {


    Map<String, Aggregator> resamplers = new HashMap<>();
    int threshold = 100;
    resamplers.put("linear", new LinearAggregator(threshold));
    resamplers.put("mean", new MeanAggregator(threshold));
    resamplers.put("lttb", new LeastTriagleThreeBucketAggregator(threshold));

    try (DataInputStream dfis =
        new MatlabTextInputStream(new BufferedInputStream(new FileInputStream(
            "/Users/isak/Desktop/ecgonly.txt")))) {
      DataSeriesCollection.Builder builder = new DataSeriesCollection.Builder(DoubleVector.TYPE);
      builder.read(dfis);
      DataSeriesCollection coll = builder.build();
      System.out.println(coll);

      Vector vec = coll.getRow(0);

      for (Map.Entry<String, Aggregator> entry : resamplers.entrySet()) {
        Vector resampled = entry.getValue().aggregate(vec);
        System.out.println(resampled.size());
        JFreeChart s = plot(linspace(resampled.size() - 1, resampled.size(), 0), resampled);
        Chartable.saveSVG("/Users/isak/Desktop/" + entry.getKey() + ".svg", s);
      }

      // Shapelet rS = NormalizedShapelet.create(0, resampled.size(), resampled);

      // System.out.println(Matrices.mean(rS));
      // System.out.println(Matrices.mean(NormalizedShapelet.create(0, vec.size(), vec)));

      JFreeChart chart = plot(linspace(vec.size() - 1, vec.size(), 0), vec);
      Chartable.saveSVG("/Users/isak/Desktop/full.svg", chart);



    } catch (Exception e) {

    }

    DataSeriesCollection.Builder builder = new DataSeriesCollection.Builder(DoubleVector.TYPE);
    builder.set(0, 0, 10);
    builder.set(9, 9, 30);
    DataSeriesCollection frame = builder.build();
    System.out.println(frame);

    System.out.println(frame.getRow(4));
  }

  @Test
  public void testBuilder() throws Exception {

    try (DataInputStream dfis =
        new MatlabTextInputStream(new BufferedInputStream(new FileInputStream(
            "/Users/isak/Desktop/ecgdata.txt")))) {
      DataSeriesCollection.Builder builder = new DataSeriesCollection.Builder(DoubleVector.TYPE);
      builder.read(dfis);
      DataSeriesCollection coll = builder.build();
      System.out.println(coll);

      Vector vec = coll.getRow(77);

      Aggregator aggregator = new MeanAggregator(387);
      Vector resampled = aggregator.aggregate(vec);


      JFreeChart chart = plot(linspace(vec.size() - 1, vec.size(), 0), vec);
      Chartable.saveSVG("/Users/isak/Desktop/full.svg", chart);

      System.out.println(resampled.size());
      chart = plot(linspace(resampled.size() - 1, resampled.size(), 0), resampled);
      Chartable.saveSVG("/Users/isak/Desktop/meanImputed.svg", chart);

    } catch (Exception e) {

    }

    // DataSeriesCollection.Builder builder = new DataSeriesCollection.Builder(DoubleVector.TYPE);
    // builder.set(0, 0, 10);
    // builder.set(9, 9, 30);
    // DataSeriesCollection frame = builder.build();
    // System.out.println(frame);
    //
    // System.out.println(frame.getRow(9));


    assertEquals(1, 1, 1);
  }

  public JFreeChart plot(VectorLike x, VectorLike y) {
    XYSeriesCollection collection = new XYSeriesCollection();
    XYSeries series = new XYSeries("Line");
    for (int i = 0; i < x.size(); i++) {
      series.add(x.getAsDouble(i), y.getAsDouble(i));
    }
    collection.addSeries(series);

    JFreeChart chart =
        Chartable.applyTheme(ChartFactory.createXYLineChart(null, "Position", null, collection));
    chart.removeLegend();
    return chart;
  }
}
