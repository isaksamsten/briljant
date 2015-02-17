/*
 * ADEB - machine learning pipelines made easy Copyright (C) 2014 Isak Karlsson
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.briljantframework.classification;

import static org.briljantframework.evaluation.result.Sample.OUT;
import static org.briljantframework.matrix.Matrices.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.Record;
import org.briljantframework.evaluation.measure.BaseAccuracy;
import org.briljantframework.evaluation.measure.EnsembleBias;
import org.briljantframework.evaluation.measure.EnsembleVariance;
import org.briljantframework.evaluation.result.EvaluationContext;
import org.briljantframework.matrix.Axis;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.Matrices;
import org.briljantframework.vector.Value;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.Vectors;

import com.carrotsearch.hppc.ObjectDoubleMap;
import com.carrotsearch.hppc.ObjectDoubleOpenHashMap;

/**
 * 
 * @author Isak Karlsson
 */
public abstract class AbstractEnsemble implements Classifier {

  private final static ThreadPoolExecutor THREAD_POOL;
  private final static int CORES;

  static {
    CORES = Runtime.getRuntime().availableProcessors();
    if (CORES <= 1) {
      THREAD_POOL = null;
    } else {
      THREAD_POOL = (ThreadPoolExecutor) Executors.newFixedThreadPool(CORES, r -> {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        return thread;
      });
    }
  }
  protected final int size;

  protected AbstractEnsemble(int size) {
    this.size = size;
  }

  /**
   * Executes {@code callable} either sequential or in parallel depending on the number of available
   * cores.
   *
   * @param callables the callables
   * @param <T> the models produced
   * @return a list of produced models
   * @throws Exception if something goes wrong
   */
  protected static <T extends Predictor> List<T> execute(Collection<? extends Callable<T>> callables)
      throws Exception {
    List<T> models = new ArrayList<>();
    if (THREAD_POOL != null && THREAD_POOL.getActiveCount() < CORES) {
      for (Future<T> future : THREAD_POOL.invokeAll(callables)) {
        models.add(future.get());
      }
    } else {
      System.out.println("running seq");
      for (Callable<T> callable : callables) {
        models.add(callable.call());
      }
    }
    return models;
  }

  /**
   * Get the number of members in the ensemble
   *
   * @return the size of the ensemble
   */
  public int size() {
    return size;
  }

  public static class AbstractEnsemblePredictor extends AbstractPredictor implements
      EnsemblePredictor {

    private final List<? extends Predictor> models;

    public AbstractEnsemblePredictor(Vector classes, List<? extends Predictor> models) {
      super(classes);
      this.models = models;
    }

    @Override
    public void evaluation(EvaluationContext ctx) {
      super.evaluation(ctx);
      EnsemblePredictor predictor = (EnsemblePredictor) ctx.getPredictor();
      List<Predictor> members = predictor.getPredictors();
      DataFrame x = ctx.getPartition().getValidationData();
      Vector y = ctx.getPartition().getValidationTarget();
      Vector classes = predictor.getClasses();
      double meanVar = 0;
      double meanBias = 0;
      double baseAccuracy = 0;

      DoubleMatrix prob = newDoubleMatrix(members.size(), classes.size());

      for (int i = 0; i < x.rows(); i++) {
        Record record = x.getRecord(i);
        DoubleMatrix c = newDoubleVector(classes.size());
        for (int j = 0; j < classes.size(); j++) {
          if (classes.equals(j, y, i)) {
            c.set(j, 1);
          }
        }
        for (int j = 0; j < members.size(); j++) {
          Predictor member = members.get(j);
          prob.setRow(j, member.estimate(record));
        }

        DoubleMatrix mean = mean(prob, Axis.COLUMN);
        double var = 0;
        double bias = 0;
        double accuracy = 0;
        for (int j = 0; j < prob.rows(); j++) {
          DoubleMatrix r = prob.getRowView(j);
          var += norm(mean, r, 2);
          bias += norm(c, r, 2);
          accuracy += Matrices.argmax(r) == Vectors.find(classes, y.getAsValue(i)) ? 1 : 0;
        }
        meanVar += var / members.size();
        meanBias += bias / members.size();
        baseAccuracy += accuracy / members.size();
      }
      ctx.getOrDefault(EnsembleVariance.class, EnsembleVariance.Builder::new).add(OUT,
          meanVar / x.rows());
      ctx.getOrDefault(EnsembleBias.class, EnsembleBias.Builder::new).add(OUT, meanBias / x.rows());
      ctx.getOrDefault(BaseAccuracy.class, BaseAccuracy.Builder::new).add(OUT,
          baseAccuracy / x.rows());
    }

    @Override
    public List<Predictor> getPredictors() {
      return Collections.unmodifiableList(models);
    }

    @Override
    public DoubleMatrix estimate(Vector row) {
      List<Value> predictions =
          models.parallelStream().map(model -> model.predict(row)).collect(Collectors.toList());
      ObjectDoubleMap<String> votes = new ObjectDoubleOpenHashMap<>();
      for (Value prediction : predictions) {
        votes.putOrAdd(prediction.getAsString(), 1, 1);
      }

      int estimators = getPredictors().size();
      Vector classes = getClasses();
      DoubleMatrix m = Matrices.newDoubleVector(classes.size());
      for (int i = 0; i < classes.size(); i++) {
        m.set(i, votes.getOrDefault(classes.getAsString(i), 0) / estimators);
      }
      return m;
    }
  }
}
