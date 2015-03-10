package org.briljantframework.similiarity;

import org.briljantframework.vector.Vector;

/**
 * Created by isak on 10/03/15.
 */
public interface Similarity {
    double compute(Vector a, Vector b);
}
