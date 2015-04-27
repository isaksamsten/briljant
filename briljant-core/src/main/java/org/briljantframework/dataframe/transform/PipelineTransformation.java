package org.briljantframework.dataframe.transform;

import java.util.Arrays;
import java.util.List;

import org.briljantframework.dataframe.DataFrame;

/**
 * Created by Isak Karlsson on 17/12/14.
 */
public class PipelineTransformation implements Transformation {

  private final List<Transformation> transformations;

  public PipelineTransformation(List<Transformation> transformations) {
    this.transformations = transformations;
  }

  public static PipelineTransformation of(Transformation... transformations) {
    return new PipelineTransformation(Arrays.asList(transformations));
  }

  @Override
  public DataFrame transform(DataFrame x) {
    for (Transformation transformation : transformations) {
      x = transformation.transform(x);
    }
    return x;
  }
}
