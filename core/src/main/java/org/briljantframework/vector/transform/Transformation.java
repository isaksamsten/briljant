package org.briljantframework.vector.transform;

import org.briljantframework.vector.Vector;

/**
 *
 *
 * @author Isak Karlsson
 */
public interface Transformation {

  Vector transform(Vector in);
}
