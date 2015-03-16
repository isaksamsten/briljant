package org.briljantframework.dataframe;

import org.briljantframework.complex.Complex;
import org.briljantframework.dataframe.transform.RemoveIncompleteCases;
import org.briljantframework.dataframe.transform.RemoveIncompleteColumns;
import org.briljantframework.io.reslover.Resolvers;
import org.briljantframework.io.reslover.StringDateConverter;
import org.briljantframework.vector.Bit;
import org.briljantframework.vector.BitVector;
import org.briljantframework.vector.ComplexVector;
import org.briljantframework.vector.Convert;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.GenericVector;
import org.briljantframework.vector.IntVector;
import org.briljantframework.vector.Is;
import org.briljantframework.vector.StringVector;
import org.briljantframework.vector.ValueVector;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;
import org.briljantframework.vector.Vectors;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MixedDataFrameTest {

  private DataFrame dataA, dataB;

  @Before
  public void setUp() throws Exception {
    dataA =
        new MixedDataFrame(new StringVector("a b c d e f".split(" ")),
                           new IntVector(1, 2, 3, 4, 5, 6));
    dataB =
        new MixedDataFrame(new StringVector("g h i j k l".split(" ")),
                           new DoubleVector(7, 8, 9, 10, 11, 12));
  }

  @Test
  public void testBuilderSetNA() throws Exception {
    DataFrame.Builder builder = new MixedDataFrame.Builder();
    builder.setNA(0, 0);
    builder.setNA(0, 4);
    builder.addColumnBuilder(Vectors.DOUBLE);
    builder.setNA(5, 5);

    DataFrame build = builder.build();
    assertEquals(Vectors.VARIABLE, build.getColumnType(0));
    assertEquals(Vectors.VARIABLE, build.getColumnType(1));
    assertEquals(Vectors.VARIABLE, build.getColumnType(2));
    assertEquals(Vectors.VARIABLE, build.getColumnType(3));
    assertEquals(Vectors.VARIABLE, build.getColumnType(4));
    assertEquals(Vectors.DOUBLE, build.getColumnType(5));
    assertTrue(Is.NA(build.getAsDouble(5, 5)));
  }

  @Test
  public void testBuilderSet() throws Exception {
    DataFrame.Builder builder = new MixedDataFrame.Builder();
    builder.set(0, 0, dataA, 0, 1);
    builder.set(0, 3, dataB, 0, 0);

    DataFrame build = builder.build();
    assertEquals(dataA.getColumnType(1), build.getColumnType(0));
    assertEquals(dataB.getColumnType(0), build.getColumnType(3));
  }

  @Test
  public void testBuilderSet1() throws Exception {
    DataFrame.Builder builder = new MixedDataFrame.Builder();
    builder.set(0, 0, dataA.getRecord(0), 0);
    builder.set(3, 3, dataB.getColumn(1), 2);
    builder.set(1, 1, dataA.get(0, 1));

    DataFrame build = builder.build();
    System.out.println(build);
    assertEquals(dataA.getColumnType(0), build.getColumnType(0));
    assertEquals(dataB.getColumnType(1), build.getColumnType(3));
    assertEquals(dataA.get(0, 0), build.get(0, 0));
    assertEquals(dataB.getColumn(1).get(2), build.get(3, 3));
    assertEquals(dataA.get(0, 1), build.get(1, 1));
  }

  @Test
  public void testBuilderSet2() throws Exception {
    DataFrame.Builder builder = new MixedDataFrame.Builder();
    builder.set(0, 0, 10.0);
    builder.set(1, 1, 10);
    builder.set(2, 2, "hello");
    builder.set(3, 3, Complex.ONE);
    builder.set(4, 4, true);
    builder.set(5, 5, null);
    builder.set(6, 6, new Date());

    DataFrame build = builder.build();
    assertEquals(Vectors.DOUBLE, build.getColumnType(0));
    assertEquals(Vectors.INT, build.getColumnType(1));
    assertEquals(Vectors.STRING, build.getColumnType(2));
    assertEquals(Vectors.COMPLEX, build.getColumnType(3));
    assertEquals(Vectors.BIT, build.getColumnType(4));
    assertEquals(Vectors.VARIABLE, build.getColumnType(5));
    assertEquals(VectorType.getInstance(Date.class), build.getColumnType(6));
  }

  @Test
  public void testBuilderAddColumn() throws Exception {
    DataFrame.Builder builder = new MixedDataFrame.Builder();
    builder.addColumn(new StringVector("1 2 3 4 5".split(" ")));
    builder.addColumn(new IntVector(1, 2, 3, 4, 5));
    builder.addColumn(new DoubleVector(1, 2, 3, 4, 5));
    builder.addColumn(new ComplexVector(Complex.I, Complex.I, Complex.I, Complex.I, Complex.I));
    builder.addColumn(new BitVector(true, true, false, false, false));
    builder.addColumn(new ValueVector(Arrays.asList(Convert.toValue(1),
                                                    Convert.toValue(3.2),
                                                    Convert.toValue("a"),
                                                    Convert.toValue(Bit.FALSE),
                                                    Convert.toValue(new Complex(3)))));

    DataFrame build = builder.build();
    assertEquals(Vectors.STRING, build.getColumnType(0));
    assertEquals(Vectors.INT, build.getColumnType(1));
    assertEquals(Vectors.DOUBLE, build.getColumnType(2));
    assertEquals(Vectors.COMPLEX, build.getColumnType(3));
    assertEquals(Vectors.BIT, build.getColumnType(4));
    assertEquals(Vectors.VARIABLE, build.getColumnType(5));

    assertEquals(1, build.getAsInt(0, 1));
    assertEquals("1", build.getAsString(0, 0));
    assertEquals(1, build.getAsDouble(0, 2), 0);
    assertEquals(Complex.I, build.getAsComplex(0, 3));
    assertEquals(Bit.TRUE, build.getAsBit(0, 4));
    assertEquals(Convert.toValue(1), build.get(0, 5));
  }

  @Test
  public void testBuilderAddBuilder() throws Exception {
    DataFrame.Builder builder = new MixedDataFrame.Builder();
    builder.addColumnBuilder(Vectors.STRING);
    builder.addColumnBuilder(Vectors.INT);
    builder.addColumnBuilder(Vectors.DOUBLE);
    builder.addColumnBuilder(Vectors.COMPLEX);
    builder.addColumnBuilder(Vectors.BIT);
    builder.addColumnBuilder(Vectors.VARIABLE);
    builder.set(0, 0, "hello")
        .set(0, 1, 1)
        .set(0, 2, 2)
        .set(0, 3, Complex.I)
        .set(0, 4, true)
        .set(0, 5, new Date());

    DataFrame build = builder.build();
    assertEquals(Vectors.STRING, build.getColumnType(0));
    assertEquals("hello", build.getAsString(0, 0));
    assertEquals(Vectors.INT, build.getColumnType(1));
    assertEquals(1, build.getAsInt(0, 1));
    assertEquals(Vectors.DOUBLE, build.getColumnType(2));
    assertEquals(2, build.getAsDouble(0, 2), 0);
    assertEquals(Vectors.COMPLEX, build.getColumnType(3));
    assertEquals(Complex.I, build.getAsComplex(0, 3));
    assertEquals(Vectors.BIT, build.getColumnType(4));
    assertEquals(Bit.TRUE, build.getAsBit(0, 4));
    assertEquals(Vectors.VARIABLE, build.getColumnType(5));
  }

  @Test
  public void testBuilderAddBuilder1() throws Exception {
    DataFrame.Builder builder = new MixedDataFrame.Builder();
    builder.addColumnBuilder(Vectors.STRING.newBuilder());
    builder.addColumnBuilder(Vectors.INT.newBuilder());
    builder.addColumnBuilder(Vectors.DOUBLE.newBuilder());
    builder.addColumnBuilder(Vectors.COMPLEX.newBuilder());
    builder.addColumnBuilder(Vectors.BIT.newBuilder());
    builder.addColumnBuilder(Vectors.VARIABLE.newBuilder());
    builder.addColumnBuilder(VectorType.getInstance(Date.class).newBuilder());

    builder.set(0, 0, "hello")
        .set(0, 1, 1)
        .set(0, 2, 2)
        .set(0, 3, Complex.I)
        .set(0, 4, true)
        .set(0, 5, "dsadsA")
        .set(0, 6, new Date(321321321738L))
        .set(1, 6, 1232L)
        .set(2, 6, "2015-03-15");

    DataFrame build = builder.build();
    System.out.println(build);
    assertEquals(Vectors.STRING, build.getColumnType(0));
    assertEquals("hello", build.getAsString(0, 0));
    assertEquals(Vectors.INT, build.getColumnType(1));
    assertEquals(1, build.getAsInt(0, 1));
    assertEquals(Vectors.DOUBLE, build.getColumnType(2));
    assertEquals(2, build.getAsDouble(0, 2), 0);
    assertEquals(Vectors.COMPLEX, build.getColumnType(3));
    assertEquals(Complex.I, build.getAsComplex(0, 3));
    assertEquals(Vectors.BIT, build.getColumnType(4));
    assertEquals(Bit.TRUE, build.getAsBit(0, 4));
    assertEquals(Vectors.VARIABLE, build.getColumnType(5));
    assertEquals(VectorType.getInstance(Date.class), build.getColumnType(6));
    assertEquals(new Date(321321321738L), build.getColumn(6).get(Date.class, 0));
    assertEquals(1, (int) build.get(Integer.class, 0, 1));
  }

  @Test
  public void testName() throws Exception {
    StringVector a = new StringVector.Builder().add("a").add("b").add("c").build();
    DoubleVector b = new DoubleVector.Builder().add(1).addNA().add(100.23).build();

    DataFrame frame = new MixedDataFrame(a, b);
    frame.setColumnName(0, "isak").setColumnName(1, "lisa");

    DataFrame.Builder copy = frame.newCopyBuilder();
    copy.addColumnBuilder(new DoubleVector.Builder().add(1).addNA().add(2));
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < copy.columns(); j++) {
        copy.set(i, j, 1);
      }
    }

    System.out.println(frame);
    System.out.println(copy.build());

    DataFrame.Builder builder = new MixedDataFrame.Builder(StringVector.TYPE, DoubleVector.TYPE);
    for (int i = 0; i < 10; i++) {
      builder.set(i + 3, 1, 32.2);
      builder.set(i + 3, 0, "hello");
    }

    System.out.println(builder.build());

    System.out.println(new MixedDataFrame(new IntVector(1, 2, 3, 4), new StringVector("a", "b",
                                                                                      "c", "d")));

    DataFrame.Builder bu =
        new MixedDataFrame.Builder(StringVector.newBuilderWithInitialValues("one", "two", "three",
                                                                            "four", "four"),
                                   BitVector.newBuilderWithInitialValues(Bit.TRUE, Bit.FALSE,
                                                                         Bit.TRUE, 1),
                                   IntVector.newBuilderWithInitialValues(1, 2, 3, 4, 5, 5, 6),
                                   ComplexVector
                                       .newBuilderWithInitialValues(Complex.I, new Complex(2, 3),
                                                                    new Complex(2,
                                                                                2), null,
                                                                    Complex.ZERO, 0.0),
                                   DoubleVector.newBuilderWithInitialValues(0, 1, 2, 3,
                                                                            4, 4, 5, 6));

    for (int i = 10; i < 20; i++) {
      for (int j = 0; j < bu.columns(); j++) {
        bu.set(i, j, "10");
      }
    }

    bu.set(22, 0, "hello");

    System.out.println(bu.rows());
    System.out.println(bu.columns());

    DataFrame ff = bu.build();
    System.out.println(ff);

    DataFrame.Builder simple =
        new MixedDataFrame.Builder(StringVector.newBuilderWithInitialValues("a", "b", "c"),
                                   IntVector.newBuilderWithInitialValues(
                                       IntStream.range(0, 1000).toArray()));
    DataFrame s = simple.build();
    s.setColumnNames("String", "LongInt");
    System.out.println(s);

    System.out.println(new RemoveIncompleteCases().transform(ff));

    assertEquals(1, 1, 1);
  }

  @Test
  public void testBuilderConcat() throws Exception {
    DataFrame.Builder builderA = dataA.newCopyBuilder();
    DataFrame concatAB = builderA.concat(dataB).concat(3, new IntVector(1, 2)).build();
    System.out.println(concatAB);
    assertEquals("g", concatAB.getAsString(0, 2));
    assertTrue(concatAB.isNA(0, 5));
  }

  @Test
  public void testBuilderStack() throws Exception {
    DataFrame.Builder builderA = dataA.newCopyBuilder();
    DataFrame stackAB = builderA.stack(dataB).stack(1, new IntVector(2, 3, 4)).build();
//    System.out.println(stackAB);
    assertEquals("e", stackAB.getAsString(4, 0));
    assertTrue(stackAB.isNA(12, 0));

  }

  @Test
  public void testMapConstructor() throws Exception {
    Map<String, Vector> vectors = new HashMap<>();
    vectors.put("engines", new StringVector("hybrid", "electric", "electric", "steam"));
    vectors.put("bhp", new IntVector(150, 130, 75, IntVector.NA));
    vectors.put("brand", new StringVector("toyota", "tesla", "tesla", "volvo"));

    DataFrame frame = new MixedDataFrame(vectors);
    System.out.println(frame);
  }

  @Test
  public void testRemoveColumnUsingBuilder() throws Exception {
    Resolvers.find(Date.class)
        .put(String.class, new StringDateConverter(new SimpleDateFormat("yyyy-MM-dd")));
    StringVector a = new StringVector.Builder().add("a").add("b").add(32).addNA().build();
    DoubleVector b = new DoubleVector.Builder().add(1).add(1).add(2).add(100.23).build();
    Vector c = new GenericVector.Builder(Date.class)
        .add("2011-03-23")
        .add(1000L)
        .add(new Date())
        .add(new Date())
        .build();

    DataFrame frame = new MixedDataFrame(a, b, c);
    frame.setColumnNames("a", "b");
    frame = new RemoveIncompleteColumns().transform(frame);
    System.out.println(frame);

    System.out.println(frame.getColumn("b"));
    assertEquals("The second column should be removed", 2, frame.columns());
    assertEquals("The column names should be retained", "b", frame.getColumnName(0));
  }
}
