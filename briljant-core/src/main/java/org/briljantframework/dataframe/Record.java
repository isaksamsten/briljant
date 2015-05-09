package org.briljantframework.dataframe;

import org.briljantframework.vector.VariableVector;

/**
 * Row inside a DataFrame
 * 
 * @author Isak Karlsson
 */
public interface Record extends VariableVector {

  /**
   * Get the column name for the value at {@code index}
   *
   * @param index the index
   * @return the column name
   */
//  String getColumnName(int index);
}
