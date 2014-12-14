package org.briljantframework.dataframe;

import java.io.BufferedInputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.briljantframework.io.DataInputStream;
import org.briljantframework.io.DelimitedInputStream;
import org.briljantframework.io.MatlabTextInputStream;
import org.briljantframework.vector.Type;

/**
 * This class provides some classical benchmarking datasets
 * 
 * Created by Isak Karlsson on 01/12/14.
 */
public class Datasets {

  protected static final String SYNTHETIC_CONTROL = "synthetic_control";
  private static final String CONNECT_4 = "connect-4";
  private static final String IRIS = "iris";
  private static final String DUMMY = "dummy";


  private static final String RESOURCE_PATTERN = "/org/briljantframework/datasets/%s.txt";
  private static final Map<String, DataFrame> DATA_CACHE = new HashMap<>();

  private Datasets() {}

  /**
   * Returns the same dataset as {@link #loadIris()}, but using a particular {@code DataFrame}
   * builder.
   * 
   * For example, if using the {@link MixedDataFrame}:
   * 
   * <pre>
   * DataFrame dataFrame = loadIris((names, types) -&gt; new MixedDataFrame.Builder(names, types));
   * // or simply
   * DataFrame iris = loadIris(MixedDataFrame.Builder::new);
   * </pre>
   * 
   * @param f the supplier
   * @return a new data frame constructed from the supplied builder
   * @see #loadIris()
   */
  public static DataFrame loadIris(
      BiFunction<Collection<String>, Collection<? extends Type>, DataFrame.Builder> f) {
    return load(f, DelimitedInputStream::new, IRIS);
  }

  /**
   * The Iris flower data set or Fisher's Iris data set is a multivariate data set introduced by Sir
   * Ronald Fisher (1936) as an example of discriminant analysis. It is sometimes called Anderson's
   * Iris data set because Edgar Anderson collected the data to quantify the morphologic variation
   * of Iris flowers of three related species.
   * 
   * The data set consists of 50 samples from each of three species of Iris (Iris setosa, Iris
   * virginica and Iris versicolor). Four features were measured from each sample: the length and
   * the width of the sepals and petals, in centimetres. Based on the combination of these four
   * features, Fisher developed a linear discriminant model to distinguish the species from each
   * other.
   *
   * <dl>
   * <dt>Description</dt>
   * <dd><a href="http://en.wikipedia.org/wiki/Iris_flower_data_set">Wikipedia</a></dd>
   * <dt>Source files</dt>
   * <dd><a href="https://archive.ics.uci.edu/ml/datasets/Iris">UCI</a></dd>
   * </dl>
   *
   * @return the iris dataset
   */
  public static DataFrame loadIris() {
    if (DATA_CACHE.containsKey(IRIS)) {
      return DATA_CACHE.get(IRIS);
    }
    DataFrame iris = loadIris(MixedDataFrame.Builder::new);
    DATA_CACHE.put(IRIS, iris);
    return iris;
  }

  /**
   * This database contains all legal 8-ply positions in the game of connect-4 in which neither
   * player has won yet, and in which the next move is not forced. x is the first player; o the
   * second.The outcome class is the game theoretical value for the first player.
   * 
   * <dl>
   * <dt>Description</dt>
   * <dd><a href="https://archive.ics.uci.edu/ml/datasets/Connect-4">UCI</a></dd>
   * <dt>Source files</dt>
   * <dd><a href="https://archive.ics.uci.edu/ml/datasets/Connect-4">UCI</a></dd>
   * </dl>
   * 
   * @return the connect-4 dataset
   */
  public static DataFrame loadConnect4() {
    if (DATA_CACHE.containsKey(CONNECT_4)) {
      return DATA_CACHE.get(CONNECT_4);
    }
    DataFrame frame = loadConnect4(MixedDataFrame.Builder::new);
    DATA_CACHE.put(CONNECT_4, frame);
    return frame;
  }

  /**
   * @param f the supplier
   * @return a new data frame constructed from the supplied builder
   * @see #loadConnect4()
   */
  public static DataFrame loadConnect4(
      BiFunction<Collection<String>, Collection<? extends Type>, DataFrame.Builder> f) {
    return load(f, DelimitedInputStream::new, CONNECT_4);
  }

  /**
   * 
   * @return
   */
  public static DataFrame loadSyntheticControl() {
    if (DATA_CACHE.containsKey(SYNTHETIC_CONTROL)) {
      return DATA_CACHE.get(SYNTHETIC_CONTROL);
    }
    DataFrame frame = loadSyntheticControl(MatrixDataFrame.ArrayBuilder::new);
    DATA_CACHE.put(SYNTHETIC_CONTROL, frame);
    return frame;
  }

  /**
   * 
   * @param f
   * @return
   */
  public static DataFrame loadSyntheticControl(
      BiFunction<Collection<String>, Collection<? extends Type>, DataFrame.Builder> f) {
    return load(f, MatlabTextInputStream::new, SYNTHETIC_CONTROL);
  }

  /**
   *
   * @return
   */
  public static DataFrame loadDummy() {
    if (DATA_CACHE.containsKey(DUMMY)) {
      return DATA_CACHE.get(DUMMY);
    }
    DataFrame frame = loadDummy(MixedDataFrame.Builder::new);
    DATA_CACHE.put(DUMMY, frame);
    return frame;
  }

  /**
   *
   * @param f
   * @return
   */
  public static DataFrame loadDummy(
      BiFunction<Collection<String>, Collection<? extends Type>, DataFrame.Builder> f) {
    return load(f, DelimitedInputStream::new, DUMMY);
  }

  public static DataFrame load(
      BiFunction<Collection<String>, Collection<? extends Type>, DataFrame.Builder> f,
      Function<InputStream, DataInputStream> fin, String name) {
    try (DataInputStream dfis = fin.apply(new BufferedInputStream(getResourceAsStream(name)))) {
      return DataFrames.load(f, dfis);
    } catch (IOException e) {
      throw new IOError(e);
    }
  }

  private static InputStream getResourceAsStream(String name) {
    InputStream inputStream =
        Datasets.class.getResourceAsStream(String.format(RESOURCE_PATTERN, name));

    if (inputStream == null) {
      throw new IOError(new IOException(String.format("Unable to find %s", name)));
    }
    return inputStream;
  }
}
