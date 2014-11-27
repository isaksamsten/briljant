package org.briljantframework.io;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.MixedDataFrame;
import org.briljantframework.vector.Type;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.Collection;

public class DataFrameInputStreamTest {


    @Test
    public void testReadDataFrame() throws Exception {
        try (DataFrameInputStream dfis = new CSVInputStream(new FileInputStream("iris.txt"))) {
            Collection<Type> colTypes = dfis.readColumnTypes();
            Collection<String> colNames = dfis.readColumnNames();
            DataFrame.Builder builder = new MixedDataFrame.Builder(colNames, colTypes);
            dfis.read(builder);

            DataFrame iris = builder.create();
            System.out.println(iris);


            System.out.println(iris.newCopyBuilder().removeColumn(1).create());
        }


    }
}