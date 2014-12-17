package org.briljantframework.dataseries;

import org.briljantframework.vector.Vector;

/**
 *
 * @author Isak Karlsson
 */
public interface Resampler {

  Vector.Builder mutableTransform(Vector in);

  default Vector transform(Vector in) {
    return mutableTransform(in).build();
  }
}
