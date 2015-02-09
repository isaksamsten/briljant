package org.briljantframework.matrix;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Isak Karlsson
 */
@RunWith(Suite.class)
@SuiteClasses({AbstractIntMatrixTest.class, AbstractLongMatrixTest.class,
    AbstractDoubleMatrixTest.class, AbstractComplexMatrixTest.class, AbstractBitMatrixTest.class})
public class MatrixTests {

}
