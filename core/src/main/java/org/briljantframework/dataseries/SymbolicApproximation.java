package org.briljantframework.dataseries;

import static org.briljantframework.distribution.NormalDistribution.ppf;
import static org.briljantframework.vector.Vectors.linspace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.transform.Transformation;
import org.briljantframework.vector.StringVector;
import org.briljantframework.vector.Vector;

import com.google.common.base.Preconditions;

/**
 * @author Isak Karlsson
 */
public class SymbolicApproximation implements Transformation {

  private final Vector alphabet;
  private final Vector thresholds;

  public SymbolicApproximation(Vector alphabet) {
    this.alphabet = Preconditions.checkNotNull(alphabet, "Requires an alphabet");
    double prob = 1.0 / alphabet.size();
    int length = alphabet.size() - 1;
    this.thresholds = ppf(linspace(prob, 1.0 - prob, length));

    System.out.println(thresholds);
  }

  public SymbolicApproximation(List<String> alphabet) {
    this(new StringVector(alphabet));
  }

  public SymbolicApproximation(String... alphabet) {
    this(new StringVector(alphabet));
  }

  public static Map<String, Map<String, Double>> lookupTable(List<String> alphabet) {
    double prob = 1.0 / alphabet.size();
    int length = alphabet.size() - 1;
    Vector thresholds = ppf(linspace(prob, 1.0 - prob, length));

    Map<String, Map<String, Double>> tab = new HashMap<>();
    int index = 0;
    for (int r = 0; r < alphabet.size(); r++) {
      Map<String, Double> sub = new HashMap<>();
      tab.put(alphabet.get(r), sub);
      for (int c = 0; c < alphabet.size(); c++) {
        if (Math.abs(r - c) <= 1) {
          sub.put(alphabet.get(c), 0.0);
        } else {
          sub.put(alphabet.get(c),
              thresholds.getAsDouble(Math.max(r, c) - 1) - thresholds.getAsDouble(Math.min(r, c)));
        }
      }
    }
    return tab;
  }

  @Override
  public DataFrame transform(DataFrame x) {
    DataSeriesCollection.Builder collection = new DataSeriesCollection.Builder(StringVector.TYPE);
    for (int i = 0; i < x.rows(); i++) {
      Vector series = x.getRow(i);
      StringVector.Builder sax = new StringVector.Builder(0, series.size());
      for (int j = 0; j < series.size(); j++) {
        double value = series.getAsDouble(j);
        if (value <= thresholds.getAsDouble(0)) {
          sax.set(j, alphabet.getAsString(0));
        } else if (value >= thresholds.getAsDouble(thresholds.size() - 1)) {
          sax.set(j, alphabet.getAsString(alphabet.size() - 1));
        } else {
          int index = 0;
          for (int k = 0; k < thresholds.size(); k++) {
            if (thresholds.getAsDouble(k) <= value) {
              index = k;
            } else {
              break;
            }
          }
          sax.set(j, alphabet.getAsString(index + 1));
        }
      }
      collection.addRow(sax);
    }
    return collection.build();
  }
}
