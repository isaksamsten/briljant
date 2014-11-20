package org.briljantframework.data.column;

/**
 * Created by Isak Karlsson on 08/11/14.
 */
public interface NumericColumn extends Column {

    /**
     * Get double.
     *
     * @param id the instance id
     * @return the double value for instance with <code>id</code>
     */
    double get(int id);
}
