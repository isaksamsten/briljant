package org.briljantframework.dataseries;

import org.briljantframework.vector.Vector;

/**
 * Created by Isak Karlsson on 12/12/14.
 */
public interface DataSeriesResampler {

  Vector resample(Vector in);
}
