package org.briljantframework.data.column;

/**
 * Created by Isak Karlsson on 09/11/14.
 */
public interface CategoricColumn extends Column {

    /**
     * Gets int.
     *
     * @param id the id
     * @return the int
     */
    Object get(int id);
}
