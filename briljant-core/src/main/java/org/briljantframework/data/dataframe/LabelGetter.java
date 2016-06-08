package org.briljantframework.data.dataframe;

import java.util.Collection;

/**
 * Created by isak on 08/06/16.
 */
public interface LabelGetter {

  DataFrame get(Collection<?> rows, Collection<?> columns);

}
