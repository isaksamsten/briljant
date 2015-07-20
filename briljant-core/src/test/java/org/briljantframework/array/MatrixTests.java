package org.briljantframework.array;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Isak Karlsson
 */
@RunWith(Suite.class)
@SuiteClasses({AbstractIntArrayTest.class, AbstractLongArrayTest.class,
    AbstractDoubleArrayTest.class, AbstractComplexArrayTest.class, AbstractBitArrayTest.class})
public class MatrixTests {

}
