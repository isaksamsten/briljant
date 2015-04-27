package org.briljantframework.classification.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataseries.Approximations;
import org.briljantframework.shapelet.IndexSortedNormalizedShapelet;
import org.briljantframework.shapelet.Shapelet;
import org.briljantframework.vector.Vector;

/**
 * @author Isak Karlsson
 */
public class AggregateRandomShapeletSplitter extends RandomShapeletSplitter {

  private double aggregateFraction = 0.5;

  public AggregateRandomShapeletSplitter(Builder builder) {
    super(builder);
  }

  @Override
  public TreeSplit<ShapeletThreshold> find(ClassSet classSet, DataFrame x, Vector y) {
    int timeSeriesLength = x.columns();
    int upper = getUpperLength();
    int lower = getLowerLength();

    if (upper < 0) {
      upper = timeSeriesLength;
    }
    if (lower < 2) {
      lower = 2;
    }

    if (Math.addExact(upper, lower) > timeSeriesLength) {
      upper = timeSeriesLength - lower;
    }

    int size = (int) Math.round(timeSeriesLength * aggregateFraction);
    DataFrame paaX = Approximations.paa(x, size);
    List<Shapelet> shapelets = new ArrayList<>();
    for (int i = 0; i < getInspectedShapelets(); i++) {
      int index = classSet.getRandomSample().getRandomExample().getIndex();
      Vector t = paaX.getRecord(index);
      int length = random.nextInt(upper) + lower;
      int start = random.nextInt(timeSeriesLength - length);
      shapelets.add(new DownsampledShapelet(index, aggregateFraction, start, length, t));
    }

    TreeSplit<ShapeletThreshold> threshold = super.findBestSplit(classSet, paaX, y, shapelets);
    DownsampledShapelet best = (DownsampledShapelet) threshold.getThreshold().getShapelet();

    IndexSortedNormalizedShapelet s =
        new IndexSortedNormalizedShapelet(best.start, best.length, x.getRecord(best.index));

    return super.findBestSplit(classSet, x, y, Arrays.asList(s));
  }


  private static class DownsampledShapelet extends IndexSortedNormalizedShapelet {
    private final int start;
    private final int length;
    private final int index;

    public DownsampledShapelet(int index, double agg, int start, int length, Vector vector) {
      super((int) Math.round(start * agg), (int) Math.round(length * agg), vector);

      this.start = start;
      this.length = length;
      this.index = index;
    }
  }
}
