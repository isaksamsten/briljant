package org.briljantframework.dataseries;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.Record;
import org.briljantframework.vector.DoubleVector;
import org.junit.Test;

public class DataSeriesCollectionTest {

  @Test
  public void testDropRows() throws Exception {
    DataSeriesCollection.Builder builder = new DataSeriesCollection.Builder(DoubleVector.TYPE);
    builder.addRecord(DoubleVector.newBuilderWithInitialValues(1, 2, 3, 4, 5, 6))
        .addRecord(DoubleVector.newBuilderWithInitialValues(1, 2, 3, 4, 5, 6))
        .addRecord(DoubleVector.newBuilderWithInitialValues(1, 2, 3, 4, 5, 6));

    DataSeriesCollection collection = builder.build();
    collection.setColumnNames("a", "b", "c");

    DataFrame drop = collection.dropColumns(Arrays.asList(0, 1));

    assertEquals("b", drop.getColumnName(0));
    for (Record row : drop) {
      assertEquals(3, row.getAsDouble(0), 0.0001);
      assertEquals(4, row.getAsDouble(1), 0.0001);
      assertEquals(5, row.getAsDouble(2), 0.0001);
      assertEquals(6, row.getAsDouble(3), 0.0001);
    }
  }

  // @Test
  // public void testRenderLinearMeanLttbResamplers() throws Exception {
  //
  //
  // Map<String, Aggregator> resamplers = new HashMap<>();
  // int threshold = 100;
  // resamplers.put("linear", new LinearAggregator(threshold));
  // resamplers.put("mean", new MeanAggregator(threshold));
  // resamplers.put("lttb", new LeastTriagleThreeBucketAggregator(threshold));
  //
  // try (DataInputStream dfis =
  // new MatlabTextInputStream(new BufferedInputStream(new FileInputStream(
  // "/Users/isak/Desktop/ecgonly.txt")))) {
  // DataSeriesCollection.Builder builder = new DataSeriesCollection.Builder(DoubleVector.TYPE);
  // builder.read(dfis);
  // DataSeriesCollection coll = builder.build();
  // System.out.println(coll);
  //
  // Vector vec = coll.getRecord(0);
  //
  // for (Map.Entry<String, Aggregator> entry : resamplers.entrySet()) {
  // Vector resampled = entry.getValue().aggregate(vec);
  // System.out.println(resampled.size());
  // JFreeChart s = plot(linspace(0, resampled.size() - 1, resampled.size()), resampled);
  // Chartable.saveSVG("/Users/isak/Desktop/" + entry.getKey() + ".svg", s);
  // }
  //
  // // Shapelet rS = NormalizedShapelet.create(0, resampled.size(), resampled);
  //
  // // System.out.println(Matrices.mean(rS));
  // // System.out.println(Matrices.mean(NormalizedShapelet.create(0, vec.size(), vec)));
  //
  // JFreeChart chart = plot(linspace(0, vec.size() - 1, vec.size()), vec);
  // Chartable.saveSVG("/Users/isak/Desktop/full.svg", chart);
  //
  //
  //
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  //
  // DataSeriesCollection.Builder builder = new DataSeriesCollection.Builder(DoubleVector.TYPE);
  // builder.set(0, 0, 10);
  // builder.set(9, 9, 30);
  // DataSeriesCollection frame = builder.build();
  // System.out.println(frame);
  //
  // System.out.println(frame.getRecord(4));
  // }
  //
  // @Test
  // public void testBuilder() throws Exception {
  //
  // try (DataInputStream dfis =
  // new MatlabTextInputStream(new BufferedInputStream(new FileInputStream(
  // "/Users/isak/Desktop/ecgdata.txt")))) {
  // DataSeriesCollection.Builder builder = new DataSeriesCollection.Builder(DoubleVector.TYPE);
  // builder.read(dfis);
  // DataSeriesCollection coll = builder.build();
  // System.out.println(coll);
  //
  // Vector vec = coll.getRecord(77);
  //
  // Aggregator aggregator = new MeanAggregator(387);
  // Vector resampled = aggregator.aggregate(vec);
  //
  //
  // JFreeChart chart = plot(linspace(0, vec.size() - 1, vec.size()), vec);
  // Chartable.saveSVG("/Users/isak/Desktop/full.svg", chart);
  //
  // System.out.println(resampled.size());
  // chart = plot(linspace(0, resampled.size() - 1, resampled.size()), resampled);
  // Chartable.saveSVG("/Users/isak/Desktop/meanImputed.svg", chart);
  //
  // } catch (Exception e) {
  //
  // }
  //
  // // DataSeriesCollection.Builder builder = new DataSeriesCollection.Builder(DoubleVector.TYPE);
  // // builder.set(0, 0, 10);
  // // builder.set(9, 9, 30);
  // // DataSeriesCollection frame = builder.build();
  // // System.out.println(frame);
  // //
  // // System.out.println(frame.getRow(9));
  //
  //
  // assertEquals(1, 1, 1);
  // }
  //
  // // public JFreeChart plot(Vector x, Vector y) {
  // // XYSeriesCollection collection = new XYSeriesCollection();
  // // XYSeries series = new XYSeries("Line");
  // // for (int i = 0; i < x.size(); i++) {
  // // series.add(x.getAsDouble(i), y.getAsDouble(i));
  // // }
  // // collection.addSeries(series);
  // //
  // // JFreeChart chart =
  // // Chartable.applyTheme(ChartFactory.createXYLineChart(null, "Position", null, collection));
  // // chart.removeLegend();
  // // return chart;
  // // }
}
