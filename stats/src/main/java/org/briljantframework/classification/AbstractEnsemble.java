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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import org.briljantframework.vector.Vector;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

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
   * @throws Exception
   */
  protected static <T extends ClassifierModel> List<T> execute(
      Collection<? extends Callable<T>> callables) throws Exception {
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

  /**
   * The type Model.
   * <p>
   */
  protected static class Model implements ClassifierModel {

    private final List<? extends ClassifierModel> models;


    /**
     * Instantiates a new Model.
     * 
     * @param models the models
     * 
     */
    public Model(List<? extends ClassifierModel> models) {
      this.models = models;
    }

    @Override
    public Label predict(Vector row) {
      List<String> predictions =
          models.parallelStream().map(model -> model.predict(row).getPredictedValue())
              .collect(Collectors.toList());
      return majority(predictions);
    }

    protected Label majority(Collection<String> predictions) {
      Multiset<String> targets = HashMultiset.create(predictions);
      List<String> values = new ArrayList<>();
      List<Double> probabilities = new ArrayList<>();
      for (Multiset.Entry<String> kv : targets.entrySet()) {
        String prediction = kv.getElement();
        values.add(prediction);
        probabilities.add(kv.getCount() / (double) predictions.size());
      }
      return Label.nominal(values, probabilities);
    }

    /**
     * Gets models.
     *
     * @return the models
     */
    public List<? extends ClassifierModel> getModels() {
      return Collections.unmodifiableList(models);
    }
  }
}
