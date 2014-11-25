package org.briljantframework.dataframe;

import org.briljantframework.data.transform.RemoveIncompleteCases;
import org.briljantframework.vector.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MixedDataFrameTest {

    @Test
    public void testName() throws Exception {
        StringVector a = new StringVector.Builder()
                .add("a").add("b").add("c").create();
        DoubleVector b = new DoubleVector.Builder()
                .add(1).addNA().add(100.23).create();

        DataFrame frame = new MixedDataFrame(a, b);

        DataFrame.Builder copy = frame.newCopyBuilder();
        copy.addColumn(new DoubleVector.Builder().add(1).addNA().add(2));
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < copy.columns(); j++) {
                copy.add(j, 1);
            }
        }


        System.out.println(frame);
        System.out.println(copy.create());

        DataFrame.Builder builder = new MixedDataFrame.Builder(StringVector.TYPE, DoubleVector.TYPE);
        for (int i = 0; i < 10; i++) {
            builder.set(i + 10, 1, 32.2);
            builder.set(i + 10, 0, "hello");
        }

        System.out.println(builder.create());


        DataFrame.Builder bu = new MixedDataFrame.Builder(
                StringVector.newBuilderWithInitialValues("one", "two", "three", "four", "four"),
                BinaryVector.newBuilderWithInitialValues(Binary.TRUE, Binary.FALSE, Binary.NA, 1),
                IntVector.newBuilderWithInitialValues(1, 2, 3, 4, 5, 5, 6),
                ComplexVector.newBuilderWithInitialValues(Complex.I, new Complex(2, 3), null, null, Complex.ZERO, 0.0),
                DoubleVector.newBuilderWithInitialValues(0, 1, 2, 3, 4, 4, 5, 6));

        for (int i = 10; i < 20; i++) {
            for (int j = 0; j < bu.columns(); j++) {
                bu.set(i, j, "10");
            }
        }

        bu.set(22, 0, "hello");

        System.out.println(bu.rows());
        System.out.println(bu.columns());

        DataFrame ff = bu.create();
        System.out.println(ff);

        DataFrame.Builder simple = new MixedDataFrame.Builder(
                StringVector.newBuilderWithInitialValues("a", "b", "c"),
                IntVector.newBuilderWithInitialValues(1, 2, 3, 4)
        );
        DataFrame s = simple.create();
        System.out.println(s);

        System.out.println(new RemoveIncompleteCases().fitTransform(ff));


        assertEquals(1, 1, 1);
    }
}