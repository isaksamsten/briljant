package org.briljantframework.data.vector;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class DoubleVectorTest extends VectorTest {

  @Override
  protected Vector.Builder getBuilder() {
    return new DoubleVector.Builder();
  }

}
