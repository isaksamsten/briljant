package org.briljantframework.array;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Isak Karlsson
 */
@RunWith(Suite.class)
@SuiteClasses({
    IndexerSpec.class,
    BaseArraySpec.class,
    ReferenceArraySpec.class,
    NumericArraySpec.class
})
public class ArrayTestSuite {

}
