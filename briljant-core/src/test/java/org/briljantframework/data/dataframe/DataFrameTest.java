/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.data.dataframe;

import org.briljantframework.data.Collectors;
import org.briljantframework.data.dataframe.join.JoinType;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.reader.DataEntry;
import org.briljantframework.data.reader.EntryReader;
import org.briljantframework.data.reader.StringDataEntry;
import org.briljantframework.data.vector.Vector;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

// TODO ISSUE#13: edge cases
public abstract class DataFrameTest {

  abstract DataFrame.Builder getBuilder();

  @Test
  public void testHead() throws Exception {
    Vector first = Vector.of(1, 2, 3, 4, 5);
    Vector second = Vector.of(1, 2, 3);

    DataFrame df = getBuilder()
        .add(first)
        .add(second)
        .build();
    df.setColumnIndex(Index.of("123", "abc"));

    int n = 3;
    DataFrame head = df.head(n);

    assertEquals(n, head.rows());
    for (int i = 0; i < n; i++) {
      Vector a = head.get("123");
      Vector b = head.get("abc");

      assertEquals(first.loc().getAsInt(i), a.loc().getAsInt(i));
      assertEquals(second.loc().getAsInt(i), b.loc().getAsInt(i));
    }
  }

  @Test
  public void testBuildingNewDataFrameFromLocationSetterAndDataFrame() throws Exception {
    Vector a = Vector.of(1, 2, 3, 4);
    Vector b = Vector.of(1, 2, 3, 4);
    DataFrame df = getBuilder()
        .add(a)
        .add(b)
        .build();
    df.setColumnIndex(Index.of("a", "b"));

    DataFrame.Builder builder = df.newBuilder();
    for (int i = 0; i < df.rows(); i++) {
      for (int j = 0; j < df.columns(); j++) {
        builder.loc().set(i, j, df, i, j);
      }
    }
    DataFrame copy = builder.build();
    copy.setColumnIndex(df.getColumnIndex());
    assertEquals(df, copy);
  }

  @Test
  public void testBuildingNewDataFrameFromLocationSetterAndValues() throws Exception {
    List<List<Integer>> values = Arrays.asList(
        Arrays.asList(1, 2, 3, 4),
        Arrays.asList(1, 2, 3, 4)
    );

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
    Vector[] vectors = new Vector[]{
        Vector.of(1, 2, 3, 4),
        Vector.of(1, 2, 3, 4)
    };
    DataFrame.Builder builder = getBuilder();
    for (int i = 0; i < vectors.length; i++) {
      builder.loc().setRecord(i, vectors[i]);
    }
    DataFrame df = builder.build();

    assertEquals(vectors.length, df.rows());
    for (int i = 0; i < vectors.length; i++) {
      assertEquals(vectors[i], df.loc().getRecord(i));
    }
  }

  @Test
  public void testBuildNewDataFrameFromLocationSetterAndColumns() throws Exception {
    Vector[] vectors = new Vector[]{
        Vector.of(1, 2, 3, 4),
        Vector.of(1, 2, 3, 4)
    };

    DataFrame.Builder builder = getBuilder();
    for (int i = 0; i < vectors.length; i++) {
      builder.loc().set(i, vectors[i]);
    }
    DataFrame df = builder.build();

    assertEquals(vectors[0].size(), df.rows());
    for (int i = 0; i < vectors.length; i++) {
      assertEquals(vectors[i], df.loc().get(i));
    }
  }

  @Test
  public void testBuildNewDataFrameFromColumnAndKey() throws Exception {
    Vector actual = Vector.of(1, 2, 3, 4);
    DataFrame df = getBuilder()
        .set("abc", actual)
        .set("def", actual)
        .build();
    assertEquals(4, df.rows());
    assertEquals(actual, df.get("abc"));
    assertEquals(actual, df.get("def"));
  }

  @Test
  public void testBuildNewDataFrameFromCopyBuilderAndColumnAndKey() throws Exception {
    Vector actual = Vector.of(1, 2, 3, 4);
    Vector replace = Vector.of(4, 3, 2, 1);
    DataFrame df = getBuilder()
        .set("a", actual)
        .set("b", actual)
        .build()
        .newCopyBuilder()
        .set("c", actual)
        .set("b", replace)
        .build();

    assertEquals(4, df.rows());
    assertEquals(3, df.columns());
    assertEquals(actual, df.get("a"));
    assertEquals(replace, df.get("b"));
    assertEquals(actual, df.get("c"));
  }

  @Test
  public void testBuildNewDataFrameFromRecordAndKey() throws Exception {
    Vector actual = Vector.of(1, 2, 3, 4);
    DataFrame df = getBuilder()
        .setRecord("a", actual)
        .setRecord("b", actual)
        .build();

    assertEquals(4, df.columns());
    assertEquals(2, df.rows());
    assertEquals(actual, df.getRecord("a"));
    assertEquals(actual, df.getRecord("b"));
  }

  @Test
  public void testBuildNewDataFrameFromCopyBuilderAndRecordAndKey() throws Exception {
    Vector actual = Vector.of(1, 2, 3, 4);
    Vector replace = Vector.of(4, 3, 2, 1);
    DataFrame df = getBuilder()
        .setRecord("a", actual)
        .setRecord("b", actual)
        .build()
        .newCopyBuilder()
        .setRecord("c", actual)
        .setRecord("b", replace)
        .build();

    assertEquals(4, df.columns());
    assertEquals(3, df.rows());
    assertEquals(actual, df.getRecord("a"));
    assertEquals(replace, df.getRecord("b"));
    assertEquals(actual, df.getRecord("c"));
  }

  @Test
  public void testBuildNewDataFrameByAddingColumns() throws Exception {
    Vector actual = Vector.of(1, 2, 3, 4, 5);

    DataFrame df = getBuilder()
        .add(actual)
        .add(actual)
        .add(actual)
        .build();

    assertEquals(5, df.rows());
    assertEquals(3, df.columns());
    for (int i = 0; i < 3; i++) {
      assertEquals(actual, df.get(i));
    }
  }

  @Test
  public void testBuildNewDataFrameByAddingRecords() throws Exception {
    Vector actual = Vector.of("a", "b", "c");
    DataFrame df = getBuilder()
        .addRecord(actual)
        .addRecord(actual)
        .addRecord(actual)
        .build();

    assertEquals(3, df.rows());
    assertEquals(3, df.columns());
    for (int i = 0; i < 3; i++) {
      // For MixedDataFrame the type of a record is always Object
      assertEquals(actual.asList(String.class), df.getRecord(i).asList(String.class));
    }
  }

  @Test
  public void testBuildNewDataFrameFromValuesUsingKey() throws Exception {
    DataFrame df = getBuilder()
        .set("a", "id", 4).set("a", "age", 32)
        .set("b", "id", 37).set("b", "age", 44)
        .build();

    assertEquals(2, df.rows());
    assertEquals(2, df.columns());

    assertEquals(Vector.of(4, 32).asList(Integer.class), df.getRecord("a").asList(Integer.class));
    assertEquals(Vector.of(37, 44).asList(Integer.class), df.getRecord("b").asList(Integer.class));

    assertEquals(Vector.of(4, 37).asList(Integer.class), df.get("id").asList(Integer.class));
    assertEquals(Vector.of(32, 44).asList(Integer.class), df.get("age").asList(Integer.class));
  }

  @Test
  public void testBuildNewDataFrameFromEntryReader() throws Exception {
    EntryReader entryReader = new EntryReader() {
      private final DataEntry[] entries = new DataEntry[]{
          new StringDataEntry("1", "2", "3"),
          new StringDataEntry("3", "2", "1")
      };
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

    assertEquals(2, df.rows());
    assertEquals(3, df.columns());

    assertEquals(Arrays.asList("1", "2", "3"), df.getRecord(0).asList(String.class));
    assertEquals(Arrays.asList("3", "2", "1"), df.getRecord(1).asList(String.class));
  }

  @Test
  public void testRemoveColumnUsingLocationIndex() throws Exception {
    DataFrame.Builder builder = getBuilder()
        .set("a", Vector.of(1, 2, 3, 4))
        .set("b", Vector.of(1, 2, 3, 4))
        .set("c", Vector.of(1, 2, 3, 4));

    builder.loc().remove(0);
    DataFrame df = builder.build();
    assertEquals(2, df.columns());
    assertEquals(4, df.rows());
  }

  @Test
  public void testReduceBinaryOpWithInit() throws Exception {
    DataFrame df = getBuilder()
        .set("i", Vector.of(1, 2, 3, 4, 5))
        .set("k", Vector.of(1, 2, 3, 4, 5))
        .set("d", Vector.of(1, 2, 3, 4, 5))
        .build();

    Vector sums = df.reduce(Integer.class, 0, Integer::sum);
    assertEquals(15, sums.getAsInt("i"));
    assertEquals(15, sums.getAsInt("k"));
    assertEquals(15, sums.getAsInt("d"));
  }

  @Test
  public void testReduceWithVectorFunction() throws Exception {
    DataFrame df = getBuilder()
        .set("i", Vector.of(1, 2, 3, 4, 5))
        .set("k", Vector.of(1, 2, 3, 4, 5))
        .set("d", Vector.of(1, 2, 3, 4, 5))
        .build();

    Vector sums = df.reduce(Vector::sum);
    assertEquals(15, sums.getAsInt("i"));
    assertEquals(15, sums.getAsInt("k"));
    assertEquals(15, sums.getAsInt("d"));
  }

  @Test
  public void testCollectWithCollector() throws Exception {
    DataFrame df = getBuilder()
        .set("i", Vector.of(1, 2, 3, 4, 5))
        .set("k", Vector.of(1, 2, 3, 4, 5))
        .set("d", Vector.of(1, 2, 3, 4, 5))
        .build();

    Vector sums = df.collect(Double.class, Collectors.sum());
    assertEquals(15, sums.getAsInt("i"));
    assertEquals(15, sums.getAsInt("k"));
    assertEquals(15, sums.getAsInt("d"));
  }

  @Test
  public void testMap() throws Exception {
    DataFrame df = getBuilder()
        .set("i", Vector.of(1, 2, 3, 4, 5))
        .set("k", Vector.of(1, 2, 3, 4, 5))
        .set("d", Vector.of(1, 2, 3, 4, 5))
        .build();

    DataFrame dfs = df.map(Integer.class, a -> a * 2);
    assertEquals(Arrays.asList(2, 4, 6, 8, 10), dfs.get("i").asList(Integer.class));
    assertEquals(Arrays.asList(2, 4, 6, 8, 10), dfs.get("k").asList(Integer.class));
    assertEquals(Arrays.asList(2, 4, 6, 8, 10), dfs.get("d").asList(Integer.class));
  }

  @Test
  public void testGroupByObjectKey() throws Exception {
    DataFrame df = getBuilder()
        .set("i", Vector.of(1, 1, 1, 2, 2, 2))
        .set("j", Vector.of(1, 2, 3, 3, 3, 3))
        .build();

    DataFrame sums = df.groupBy("i").collect(Vector::sum);
    assertEquals(9, sums.getAsInt(2, "j"));
    assertEquals(6, sums.getAsInt(1, "j"));
  }

  @Test
  public void testGroupByTransform() throws Exception {
    DataFrame df = getBuilder()
        .set("i", Vector.of(1, 1, 2, 2, 3, 3))
        .set("j", Vector.of(10, 10, 20, 20, 30, null))
        .build();

    DataFrame replaced = df.groupBy("i")
        .apply(v -> v.collect(Collectors.fillNa(22)));
    assertEquals(6, replaced.rows());
    assertEquals(2, replaced.columns());
    assertEquals(22, replaced.get("j").getAsInt(5));
  }

  @Test
  public void testGroupByYearFromLocalDate() throws Exception {
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DataFrame df = getBuilder()
        .setRecord(LocalDate.parse("2010-02-22", format), Vector.of(1, 2, 3))
        .setRecord(LocalDate.parse("2011-03-10", format), Vector.of(11, 22, 33))
        .setRecord(LocalDate.parse("2011-03-11", format), Vector.of(11, 22, 33))
        .setColumnIndex("A", "B", "C")
        .build();
    DataFrame sums = df.groupBy(v -> LocalDate.class.cast(v).getYear()).collect(Vector::sum);
    assertEquals(2, sums.rows());
    assertEquals(3, sums.columns());
    assertEquals(66, sums.get("C").getAsInt(2011));
    assertEquals(1, sums.getAsInt(2010, "A"));
  }

  @Test
  public void testInnerJoin() throws Exception {
    Vector values = Vector.of(10, 20, 30, 10, 20);
    DataFrame a = getBuilder()
        .set("a", Vector.of(1, 2, 3, 4, 5))
        .set("left", values)
        .build();

    DataFrame b = getBuilder()
        .set("a", Vector.of(5, 2, 3, 2, 1))
        .set("right", values)
        .build();

    DataFrame join = a.join(JoinType.INNER, b, "a");
    Vector on = Vector.of(1, 2, 2, 3, 5);
    Vector actualLeftAndRight = Vector.of(10, 20, 20, 30, 20);
    assertEquals(on, join.get("a"));
    assertEquals(actualLeftAndRight, join.get("left"));
    assertEquals(actualLeftAndRight, join.get("right"));
  }

  @Test
  public void testResetIndex() throws Exception {
    DataFrame df = getBuilder()
        .setRecord("a", Vector.of(1, 2, 3))
        .setRecord("b", Vector.of(1, 2, 3))
        .setRecord("c", Vector.of(1, 2, 3))
        .setRecord("d", Vector.of(1, 2, 3))
        .setRecord("e", Vector.of(1, 2, 3))
        .build();

    DataFrame actual = df.resetIndex();
    assertEquals(Vector.of("a", "b", "c", "d", "e"), actual.get("index"));
    assertEquals(Arrays.<Object>asList(0, 1, 2, 3, 4), actual.getIndex().asList());

  }

}
