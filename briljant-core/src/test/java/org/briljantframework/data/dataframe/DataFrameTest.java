/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.data.dataframe;

import static org.briljantframework.data.Collectors.toDataFrame;
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.briljantframework.array.Array;
import org.briljantframework.array.BooleanArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;
import org.briljantframework.data.Collectors;
import org.briljantframework.data.Is;
import org.briljantframework.data.Na;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.index.NaturalOrdering;
import org.briljantframework.data.reader.DataEntry;
import org.briljantframework.data.reader.EntryReader;
import org.briljantframework.data.reader.StringDataEntry;
import org.briljantframework.data.series.DoubleSeries;
import org.briljantframework.data.series.IntSeries;
import org.briljantframework.data.series.Series;
import org.junit.Test;

// TODO ISSUE#13: edge cases
public abstract class DataFrameTest {

  @Test
  public void testSet_column() throws Exception {
    DataFrame df = getBuilder().set("A", IntSeries.of(1, 2, 3, 4)).build();
    DataFrame expected =
        getBuilder().set("A", IntSeries.of(1, 2, 3, 4)).set("B", IntSeries.of(1, 2, 3, 4)).build();
    DataFrame actual = df.set("B", IntSeries.of(1, 2, 3, 4)).set("A", IntSeries.of(1, 2, 3, 4));
    assertEquals(expected, actual);
  }

  abstract DataFrame.Builder getBuilder();

  @Test
  public void testSet_columns() throws Exception {
    Map<Object, Series> setter = new HashMap<>();
    setter.put("A", IntSeries.of(1, 2, 3, 4));
    setter.put("B", IntSeries.of(1, 2, 3, 4));
    DataFrame df = getBuilder().set("A", IntSeries.of(1, 2, 3, 4)).build();
    DataFrame expected =
        getBuilder().set("A", IntSeries.of(1, 2, 3, 4)).set("B", IntSeries.of(1, 2, 3, 4)).build();
    DataFrame actual = df.setAll(setter);
    assertEquals(expected, actual);
  }

  @Test
  public void testGet_columns() throws Exception {
    DataFrame df = getBuilder().set("A", IntSeries.of(1, 2, 3)).set("B", IntSeries.of(1, 2, 3))
        .set("C", IntSeries.of(1, 2, 3)).build();
    DataFrame expected =
        getBuilder().set("A", IntSeries.of(1, 2, 3)).set("B", IntSeries.of(1, 2, 3)).build();
    DataFrame actual = df.getAll(Arrays.asList("A", "B"));
    assertEquals(expected, actual);
  }

  @Test
  public void testSelect_key_keys() throws Exception {
    DataFrame df = getBuilder().set("A", IntSeries.of(1, 1, 1)).set("F", IntSeries.of(2, 2, 2))
        .set("B", IntSeries.of(3, 3, 3)).set("Q", IntSeries.of(4, 4, 4)).build();
    df = DataFrames.sortColumns(df, NaturalOrdering.ascending());
    System.out.println(df);

    System.out.println(df.getColumnIndex());
    System.out.println(df.loc().get(IntArray.of(1, 2, 0)));

  }

  @Test
  public void testWhere() throws Exception {
    DataFrame df = getBuilder().set("A", Series.of(1, 2, 3)).set("B", Series.of(1, 2, 3)).build();
    BooleanArray where = df.where(Integer.class, a -> a > 2);
    assertEquals(BooleanArray.of(0, 0, 1, 0, 0, 1).reshape(3, 2), where);
  }

  @Test
  public void testGet_BooleanArray() throws Exception {
    DataFrame df = getBuilder().set("A", Series.of(1, 2, 3)).set("B", Series.of(1, 2, 3)).build();
    DataFrame actual = df.get(df.where(Integer.class, i -> i > 1));
    DataFrame expected = getBuilder().set("A", IntSeries.of(Na.INT, 2, 3))
        .set("B", IntSeries.of(Na.INT, 2, 3)).build();

    assertEquals(expected, actual);
  }

  @Test
  public void testSet_BooleanArrayValue() throws Exception {
    DataFrame df = getBuilder().set("A", Series.of(1, 2, 3)).set("B", Series.of(1, 2, 3)).build();
    DataFrame expected =
        getBuilder().set("A", IntSeries.of(30, 2, 3)).set("B", IntSeries.of(30, 2, 3)).build();
    BooleanArray where = df.where(Integer.class, i -> i < 2);
    DataFrame actual = df.set(where, 30);

    assertEquals(expected, actual);
  }

  @Test
  public void testTranspose() throws Exception {
    DataFrame df = getBuilder().set("A", Series.of(1, 2, 3, 4)).build();
    df.setIndex(Index.of("a", "b", "c", "d"));
    DataFrame expected = getBuilder().setRow("A", IntSeries.of(1, 2, 3, 4)).build();
    expected.setColumnIndex(Index.of("a", "b", "c", "d"));
    DataFrame actual = df.transpose();
    assertEquals(expected, actual);
  }

  @Test
  public void testApply_Function() throws Exception {
    DataFrame df =
        getBuilder().set("A", IntSeries.of(1, 2, 3)).set("B", IntSeries.of(1, 2, 3)).build();
    DataFrame expected =
        getBuilder().set("A", IntSeries.of(2, 4, 6)).set("B", IntSeries.of(2, 4, 6)).build();
    DataFrame actual = df.apply(a -> a.times(2));
    assertEquals(expected, actual);
  }

  @Test
  public void testFilter_records() throws Exception {
    DataFrame df =
        getBuilder().set("A", IntSeries.of(1, 2, 3)).set("B", IntSeries.of(1, Na.INT, 3)).build();
    DataFrame expected =
        getBuilder().set("A", IntSeries.of(1, 3)).set("B", IntSeries.of(1, 3)).build();
    expected.setIndex(Index.of(0, 2));
    DataFrame actual = df.filter((vector) -> !vector.hasNA());
    assertEquals(expected, actual);
  }

  @Test
  public void testToArray_with_class() throws Exception {
    DataFrame df =
        getBuilder().set("A", Series.of("a", "b", "c")).set("B", Series.of(1, 2, 3)).build();
    DoubleArray expected =
        DoubleArray.of(Double.NaN, Double.NaN, Double.NaN, 1, 2, 3).reshape(3, 2);
    DoubleArray actual = DataFrames.toDoubleArray(df);
    assertEquals(expected, actual);
  }

  @Test
  public void testToDoubleArray_operator() throws Exception {
    DataFrame df = getBuilder().set("A", DoubleSeries.of(1, 2, 3, 4))
        .set("B", DoubleSeries.of(1, 2, Na.DOUBLE, 4)).build();
    DoubleArray expected = DoubleArray.of(1, 2, 3, 4, 1, 2, 3, 4).reshape(4, 2);
    DoubleArray actual = DataFrames.toDoubleArray(df, i -> Is.NA(i) ? 3 : i);
    assertEquals(expected, actual);
  }

  @Test
  public void testStream() throws Exception {
    DataFrame df = getBuilder().set("A", Series.of("a", "b", null))
        .set("B", IntSeries.of(1, Na.INT, 3)).build();
    DataFrame expected =
        getBuilder().set("A", Series.of("b", Na.ANY)).set("B", IntSeries.of(Na.INT, 3)).build();
    DataFrame actual = df.stream().filter(Series::hasNA).collect(toDataFrame(this::getBuilder));
    actual.setColumnIndex(Index.of("A", "B"));
    assertEquals(expected, actual);
  }

  @Test
  public void testApply_Collector() throws Exception {
    DataFrame df =
        getBuilder().set("A", IntSeries.of(1, 2, 3)).set("B", IntSeries.of(1, 2, 3)).build();
    DataFrame expected = getBuilder().set("A", IntSeries.of(1, 1, 2, 2, 3, 3))
        .set("B", IntSeries.of(1, 1, 2, 2, 3, 3)).build();
    DataFrame actual = df.apply(Integer.class, Collectors.each(2));
    assertEquals(expected, actual);
  }

  @Test
  public void testGroupBy_column() throws Exception {
    DataFrame df = getBuilder().set("A", Series.of(1, 2, 1, 2))
        .set("B", Series.of(30.0, 2.0, 33.0, 6.0)).build();
    DataFrameGroupBy groups = df.groupBy("A");

    DataFrame expectedGroup1 =
        getBuilder().set("A", Series.of(1, 1)).set("B", Series.of(30.0, 33.0)).build();
    expectedGroup1.setIndex(Index.of(0, 2));
    DataFrame expectedGroup2 =
        getBuilder().set("A", Series.of(2, 2)).set("B", Series.of(2.0, 6.0)).build();
    expectedGroup2.setIndex(Index.of(1, 3));

    assertEquals(expectedGroup1, groups.get(1));
    assertEquals(expectedGroup2, groups.get(2));
  }

  @Test
  public void testGroupBy_apply() throws Exception {
    DataFrame df = getBuilder().set("A", Series.of(1, 2, 1, 2))
        .set("B", Series.of(30.0, 2.0, 33.0, 6.0)).build();
    DataFrame actual = df.groupBy("A").apply(v -> v.minus(v.mean()));

    DataFrame expected = getBuilder().set("A", Series.of(1, 2, 1, 2)).set("B",
        Series.of(30 - (30 + 33) / 2.0, 2 - (2 + 6) / 2.0, 33 - (30 + 33) / 2.0, 6 - (2 + 6) / 2.0))
        .build();
    assertEquals(expected, actual);
  }

  @Test
  public void testGroupBy_column_with_mapper() throws Exception {
    DataFrame df = getBuilder().set("A", Series.of(1, 2, 10, 20))
        .set("B", Series.of("a", "b", "c", "d")).build();

    DataFrame expectedGroup1 =
        getBuilder().set("A", Series.of(1, 2)).set("B", Series.of("a", "b")).build();
    DataFrame expectedGroup2 =
        getBuilder().set("A", Series.of(10, 20)).set("B", Series.of("c", "d")).build();
    expectedGroup2.setIndex(Index.of(2, 3));
    DataFrameGroupBy groups = df.groupBy(String.class, String::length, "A");

    assertEquals(expectedGroup1, groups.get(1));
    assertEquals(expectedGroup2, groups.get(2));
  }

  @Test
  public void testGroupBy_columns() throws Exception {
    // @formatter:off
    DataFrame df = getBuilder()
        .set("A", Series.of(1, 2, 3, 4))
        .set("B", Series.of(1, 1, 0, 4))
        .set("C", Array.of(1, 1, 0, 4))
        .build();
    // @formatter:on
    DataFrameGroupBy groups = df.groupBy(Series::mean, Series.of("A", "B"));
    DataFrame expected1_5 = getBuilder().set("A", Series.of(2, 3)).set("B", Series.of(1, 0))
        .set("C", Series.of(1, 0)).build();
    expected1_5.setIndex(Index.of(1, 2));


    DataFrame expected1 =
        getBuilder().set("A", Series.of(1)).set("B", Series.of(1)).set("C", Series.of(1)).build();
    expected1.setIndex(Index.of(0));


    DataFrame expected4 =
        getBuilder().set("A", Series.of(4)).set("B", Series.of(4)).set("C", Series.of(4)).build();
    expected4.setIndex(Index.of(3));

    assertEquals(expected1_5, groups.get(1.5));
    assertEquals(expected1, groups.get(1.0));
    assertEquals(expected4, groups.get(4.0));
  }

  @Test
  public void testHead() throws Exception {
    Series first = Series.of(1, 2, 3, 4, 5);
    Series second = Series.of(1, 2, 3);

    DataFrame df = getBuilder().add(first).add(second).build();
    df.setColumnIndex(Index.of("123", "abc"));

    int n = 3;
    DataFrame head = df.limit(n);

    assertEquals(n, head.size(0));
    for (int i = 0; i < n; i++) {
      Series a = head.get("123");
      Series b = head.get("abc");

      assertEquals(first.loc().getInt(i), a.loc().getInt(i));
      assertEquals(second.loc().getInt(i), b.loc().getInt(i));
    }
  }

  @Test
  public void testBuildingNewDataFrameFromLocationSetterAndDataFrame() throws Exception {
    Series a = Series.of(1, 2, 3, 4);
    Series b = Series.of(1, 2, 3, 4);
    DataFrame df = getBuilder().add(a).add(b).build();
    df.setColumnIndex(Index.of("a", "b"));

    DataFrame.Builder builder = df.newBuilder();
    for (int i = 0; i < df.size(0); i++) {
      for (int j = 0; j < df.size(1); j++) {
        builder.loc().set(i, j, df, i, j);
      }
    }
    DataFrame copy = builder.build();
    copy.setColumnIndex(df.getColumnIndex());
    assertEquals(df, copy);
  }

  @Test
  public void testBuildingNewDataFrameFromLocationSetterAndValues() throws Exception {
    List<List<Integer>> values =
        Arrays.asList(Arrays.asList(1, 2, 3, 4), Arrays.asList(1, 2, 3, 4));

    DataFrame.Builder builder = getBuilder();
    for (int j = 0; j < values.size(); j++) {
      List<Integer> column = values.get(j);
      for (int i = 0; i < column.size(); i++) {
        builder.loc().set(i, j, column.get(i));
      }
    }
    DataFrame df = builder.build();
    assertEquals(values.get(0), df.loc().get(0).asList(Integer.class));
    assertEquals(values.get(1), df.loc().get(1).asList(Integer.class));
  }

  @Test
  public void testBuildNewDataFrameFromLocationSetterAndRecords() throws Exception {
    Series[] series = new Series[] {Series.of(1, 2, 3, 4), Series.of(1, 2, 3, 4)};
    DataFrame.Builder builder = getBuilder();
    for (int i = 0; i < series.length; i++) {
      builder.loc().setRecord(i, series[i]);
    }
    DataFrame df = builder.build();

    assertEquals(series.length, df.size(0));
    for (int i = 0; i < series.length; i++) {
      assertEquals(series[i], df.loc().getRow(i));
    }
  }

  @Test
  public void testBuildNewDataFrameFromLocationSetterAndColumns() throws Exception {
    Series[] series = new Series[] {Series.of(1, 2, 3, 4), Series.of(1, 2, 3, 4)};

    DataFrame.Builder builder = getBuilder();
    for (int i = 0; i < series.length; i++) {
      builder.add(series[i]);
    }
    DataFrame df = builder.build();

    assertEquals(series[0].size(), df.size(0));
    for (int i = 0; i < series.length; i++) {
      assertEquals(series[i], df.loc().get(i));
    }
  }

  @Test
  public void testBuildNewDataFrameFromColumnAndKey() throws Exception {
    Series actual = Series.of(1, 2, 3, 4);
    DataFrame df = getBuilder().set("abc", actual).set("def", actual).build();
    assertEquals(4, df.size(0));
    assertEquals(actual, df.get("abc"));
    assertEquals(actual, df.get("def"));
  }

  @Test
  public void testBuildNewDataFrameFromCopyBuilderAndColumnAndKey() throws Exception {
    Series actual = Series.of(1, 2, 3, 4);
    Series replace = Series.of(4, 3, 2, 1);
    DataFrame df = getBuilder().set("a", actual).set("b", actual).build().newCopyBuilder()
        .set("c", actual).set("b", replace).build();

    assertEquals(4, df.size(0));
    assertEquals(3, df.size(1));
    assertEquals(actual, df.get("a"));
    assertEquals(replace, df.get("b"));
    assertEquals(actual, df.get("c"));
  }

  @Test
  public void testBuildNewDataFrameFromRecordAndKey() throws Exception {
    Series actual = Series.of(1, 2, 3, 4);
    DataFrame df = getBuilder().setRow("a", actual).setRow("b", actual).build();

    assertEquals(4, df.size(1));
    assertEquals(2, df.size(0));
    assertEquals(actual, df.getRow("a"));
    assertEquals(actual, df.getRow("b"));
  }

  @Test
  public void testBuildNewDataFrameFromCopyBuilderAndRecordAndKey() throws Exception {
    Series actual = Series.of(1, 2, 3, 4);
    Series replace = Series.of(4, 3, 2, 1);
    DataFrame df = getBuilder().setRow("a", actual).setRow("b", actual).build()
        .newCopyBuilder().setRow("c", actual).setRow("b", replace).build();

    assertEquals(4, df.size(1));
    assertEquals(3, df.size(0));
    assertEquals(actual, df.getRow("a"));
    assertEquals(replace, df.getRow("b"));
    assertEquals(actual, df.getRow("c"));
  }

  @Test
  public void testBuildNewDataFrameByAddingColumns() throws Exception {
    Series actual = Series.of(1, 2, 3, 4, 5);

    DataFrame df = getBuilder().add(actual).add(actual).add(actual).build();

    assertEquals(5, df.size(0));
    assertEquals(3, df.size(1));
    for (int i = 0; i < 3; i++) {
      assertEquals(actual, df.get(i));
    }
  }

  @Test
  public void testBuildNewDataFrameByAddingRecords() throws Exception {
    Series actual = Series.of("a", "b", "c");
    DataFrame df = getBuilder().addRow(actual).addRow(actual).addRow(actual).build();

    assertEquals(3, df.size(0));
    assertEquals(3, df.size(1));
    for (int i = 0; i < 3; i++) {
      // For MixedDataFrame the type of a record is always Object
      assertEquals(actual.asList(String.class), df.getRow(i).asList(String.class));
    }
  }

  @Test
  public void testBuildNewDataFrameFromValuesUsingKey() throws Exception {
    DataFrame df = getBuilder().set("a", "id", 4).set("a", "age", 32).set("b", "id", 37)
        .set("b", "age", 44).build();

    assertEquals(2, df.size(0));
    assertEquals(2, df.size(1));

    assertEquals(Series.of(4, 32).asList(Integer.class), df.getRow("a").asList(Integer.class));
    assertEquals(Series.of(37, 44).asList(Integer.class), df.getRow("b").asList(Integer.class));

    assertEquals(Series.of(4, 37).asList(Integer.class), df.get("id").asList(Integer.class));
    assertEquals(Series.of(32, 44).asList(Integer.class), df.get("age").asList(Integer.class));
  }

  @Test
  public void testBuildNewDataFrameFromEntryReader() throws Exception {
    EntryReader entryReader = new EntryReader() {
      private final DataEntry[] entries =
          new DataEntry[] {new StringDataEntry("1", "2", "3"), new StringDataEntry("3", "2", "1")};
      private int current = 0;

      @Override
      public List<Class<?>> getTypes() {
        return Arrays.asList(String.class, String.class, String.class);
      }

      @Override
      public DataEntry next() {
        return entries[current++];
      }

      @Override
      public boolean hasNext() {
        return current < entries.length;
      }
    };

    DataFrame df = getBuilder().readAll(entryReader).build();

    assertEquals(2, df.size(0));
    assertEquals(3, df.size(1));

    assertEquals(Arrays.asList("1", "2", "3"), df.getRow(0).asList(String.class));
    assertEquals(Arrays.asList("3", "2", "1"), df.getRow(1).asList(String.class));
  }

  @Test
  public void testRemoveColumnUsingLocationIndex() throws Exception {
    DataFrame.Builder builder = getBuilder().set("a", Series.of(1, 2, 3, 4))
        .set("b", Series.of(1, 2, 3, 4)).set("c", Series.of(1, 2, 3, 4));

    builder.loc().remove(0);
    DataFrame df = builder.build();
    assertEquals(2, df.size(1));
    assertEquals(4, df.size(0));
  }

  @Test
  public void testReduceBinaryOpWithInit() throws Exception {
    DataFrame df = getBuilder().set("i", Series.of(1, 2, 3, 4, 5))
        .set("k", Series.of(1, 2, 3, 4, 5)).set("d", Series.of(1, 2, 3, 4, 5)).build();

    Series sums = df.reduce(Integer.class, 0, Integer::sum);
    assertEquals(15, sums.getInt("i"));
    assertEquals(15, sums.getInt("k"));
    assertEquals(15, sums.getInt("d"));
  }

  @Test
  public void testReduceWithVectorFunction() throws Exception {
    DataFrame df = getBuilder().set("i", Series.of(1, 2, 3, 4, 5))
        .set("k", Series.of(1, 2, 3, 4, 5)).set("d", Series.of(1, 2, 3, 4, 5)).build();

    Series sums = df.reduce(Series::sum);
    assertEquals(15, sums.getInt("i"));
    assertEquals(15, sums.getInt("k"));
    assertEquals(15, sums.getInt("d"));
  }

  @Test
  public void testCollectWithCollector() throws Exception {
    DataFrame df = getBuilder().set("i", Series.of(1, 2, 3, 4, 5))
        .set("k", Series.of(1, 2, 3, 4, 5)).set("d", Series.of(1, 2, 3, 4, 5)).build();

    Series sums = df.collect(Double.class, Collectors.sum());
    assertEquals(15, sums.getInt("i"));
    assertEquals(15, sums.getInt("k"));
    assertEquals(15, sums.getInt("d"));
  }

  @Test
  public void testMap() throws Exception {
    DataFrame df = getBuilder().set("i", Series.of(1, 2, 3, 4, 5))
        .set("k", Series.of(1, 2, 3, 4, 5)).set("d", Series.of(1, 2, 3, 4, 5)).build();

    DataFrame dfs = df.map(Integer.class, a -> a * 2);
    assertEquals(Arrays.asList(2, 4, 6, 8, 10), dfs.get("i").asList(Integer.class));
    assertEquals(Arrays.asList(2, 4, 6, 8, 10), dfs.get("k").asList(Integer.class));
    assertEquals(Arrays.asList(2, 4, 6, 8, 10), dfs.get("d").asList(Integer.class));
  }

  @Test
  public void testGroupByObjectKey() throws Exception {
    DataFrame df = getBuilder().set("i", Series.of(1, 1, 1, 2, 2, 2))
        .set("j", Series.of(1, 2, 3, 3, 3, 3)).build();

    DataFrame sums = df.groupBy("i").collect(Series::sum);
    assertEquals(9, sums.getAsInt(2, "j"));
    assertEquals(6, sums.getAsInt(1, "j"));
  }

  @Test
  public void testGroupByTransform() throws Exception {
    DataFrame df = getBuilder().set("i", Series.of(1, 1, 2, 2, 3, 3))
        .set("j", Series.of(10, 10, 20, 20, 30, null)).build();

    DataFrame replaced = df.groupBy("i").apply(v -> v.collect(Collectors.fillNa(22)));
    assertEquals(6, replaced.size(0));
    assertEquals(2, replaced.size(1));
    assertEquals(22, replaced.get("j").getInt(5));
  }

  @Test
  public void testGroupByYearFromLocalDate() throws Exception {
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DataFrame df = getBuilder().setRow(LocalDate.parse("2010-02-22", format), Series.of(1, 2, 3))
        .setRow(LocalDate.parse("2011-03-10", format), Series.of(11, 22, 33))
        .setRow(LocalDate.parse("2011-03-11", format), Series.of(11, 22, 33)).build();
    df.setColumnIndex(Index.of("A", "B", "C"));
    DataFrame sums = df.groupBy(v -> LocalDate.class.cast(v).getYear()).collect(Series::sum);
    assertEquals(2, sums.size(0));
    assertEquals(3, sums.size(1));
    assertEquals(66, sums.get("C").getInt(2011));
    assertEquals(1, sums.getAsInt(2010, "A"));
  }

  @Test
  public void testInnerJoin() throws Exception {
    Series values = Series.of(10, 20, 30, 10, 20);
    DataFrame a = getBuilder().set("a", Series.of(1, 2, 3, 4, 5)).set("left", values).build();
    DataFrame b = getBuilder().set("a", Series.of(5, 2, 3, 2, 1)).set("right", values).build();

    DataFrame join = Join.inner(a, b).on("a");
    Series on = Series.of(1, 2, 2, 3, 5);
    Series expectedLeft = Series.of(10, 20, 20, 30, 20);
    Series expectedRight = Series.of(20, 20, 10, 30, 10);
    assertEquals(on, join.get("a"));
    assertEquals(expectedLeft, join.get("left"));
    assertEquals(expectedRight, join.get("right"));
  }

  @Test
  public void testResetIndex() throws Exception {
    DataFrame df = getBuilder().setRow("a", Series.of(1, 2, 3))
        .setRow("b", Series.of(1, 2, 3)).setRow("c", Series.of(1, 2, 3))
        .setRow("d", Series.of(1, 2, 3)).setRow("e", Series.of(1, 2, 3)).build();

    DataFrame df2 =
        DataFrame.builder().setRow("a", Series.of(1, 2)).setRow("b", Series.of(1, 2)).build();
    System.out.println(df2.resetIndex());
    DataFrame actual = df.resetIndex();
    assertEquals(Series.of("a", "b", "c", "d", "e"), actual.get("index"));
    assertEquals(Index.of(0, 1, 2, 3, 4), actual.getIndex());

  }

}
