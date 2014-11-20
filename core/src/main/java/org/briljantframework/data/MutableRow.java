package org.briljantframework.data;

import org.briljantframework.data.values.Value;

/**
 * Created by Isak Karlsson on 19/11/14.
 */
public interface MutableRow extends Row {

    /**
     * Put void.
     *
     * @param index the index
     * @param value the value
     */
    void put(int index, Value value);

    /**
     * Add void.
     *
     * @param value the value
     */
    void add(Value value);
}
