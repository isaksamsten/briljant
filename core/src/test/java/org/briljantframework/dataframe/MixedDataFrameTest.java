package org.briljantframework.dataframe;

import org.briljantframework.transform.RemoveIncompleteCases;
import org.briljantframework.transform.RemoveIncompleteColumns;
import org.briljantframework.vector.*;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

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

        System.out.println(new MixedDataFrame(
                new IntVector(1, 2, 3, 4),
                new StringVector("a", "b", "c", "d")
        ));


        DataFrame.Builder bu = new MixedDataFrame.Builder(
                StringVector.newBuilderWithInitialValues("one", "two", "three", "four", "four"),
                BinaryVector.newBuilderWithInitialValues(Binary.TRUE, Binary.FALSE, Binary.TRUE, 1),
                IntVector.newBuilderWithInitialValues(1, 2, 3, 4, 5, 5, 6),
                ComplexVector.newBuilderWithInitialValues(Complex.I, new Complex(2, 3), new Complex(2, 2), null, Complex.ZERO, 0.0),
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
                IntVector.newBuilderWithInitialValues(IntStream.range(0, 1000).toArray())
        );
        DataFrame s = simple.create();
        System.out.println(s);

        System.out.println(new RemoveIncompleteCases().fitTransform(ff));


        assertEquals(1, 1, 1);
    }

    @Test
    public void testMapConstructor() throws Exception {
        Map<String, Vector> vectors = new HashMap<>();
        vectors.put("engines", StringVector.newBuilderWithInitialValues("hybrid", "electric", "electric", "steam").create());
        vectors.put("bhp", IntVector.newBuilderWithInitialValues(150, 130, 75).addNA().create());
        vectors.put("brand", StringVector.newBuilderWithInitialValues("toyota", "tesla", "tesla", "volvo").create());

        DataFrame frame = new MixedDataFrame(vectors);
        System.out.println(new RemoveIncompleteColumns().fitTransform(frame));
        System.out.println(new RemoveIncompleteCases().fitTransform(frame));

        //        for (Sequence sequence : frame) {
        //            System.out.println(StreamSupport.stream(sequence.spliterator(), false).map(Object::toString).collect(Collectors.joining(",")));
        //        }

        System.out.println(frame);
        DataFrame c2 = new MixedDataFrame(frame);

        System.out.println(c2.asMatrix());
    }

    @Test
    public void testRemoveColumnUsingBuilder() throws Exception {
        StringVector a = new StringVector.Builder()
                .add("a").add("b").add("c").addNA().create();
        DoubleVector b = new DoubleVector.Builder()
                .add(1).add(1).add(2).add(100.23).create();

        DataFrame frame = new MixedDataFrame(a, b);
        frame = new RemoveIncompleteColumns().fitTransform(frame);
        System.out.println(frame);
        assertEquals("The second column should be removed", 1, frame.columns());
        assertEquals("The column names should be retained", "1", frame.getColumnName(0));
    }
}