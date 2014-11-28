package org.briljantframework.io;

import org.briljantframework.vector.Type;

/**
 * Created by Isak Karlsson on 27/11/14.
 */
public interface TypeFactory {

  Type getTypeForName(String name);
}
