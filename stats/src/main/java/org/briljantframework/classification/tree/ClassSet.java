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

package org.briljantframework.classification.tree;


import java.util.*;

import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.Vector;

/**
 * @author Isak Karlsson
 */
public final class ClassSet implements Iterable<Example> {

  private static final Random RANDOM = new Random();

  private final Map<String, Sample> samples;

  private final List<String> targets;
  private final Vector domain;

  public ClassSet(Vector column, Vector domain) {
    this(domain);
    for (int i = 0; i < column.size(); i++) {
      add(column.getAsString(i), i, 1);
    }
  }

  public ClassSet(Vector domain) {
    samples = new HashMap<>();
    targets = new ArrayList<>();
    this.domain = domain;
  }

  public void add(String target, int index, double weight) {
    Sample sample = samples.get(target);
    if (sample == null) {
      sample = new Sample(target);
      sample.add(new Example(index, weight));
      samples.put(target, sample);
      targets.add(target);
    } else {
      sample.add(new Example(index, weight));
    }
  }

  public void add(Sample sample) {
    String target = sample.getTarget();
    targets.add(target);
    samples.put(target, sample);
  }

  public Collection<Sample> samples() {
    return samples.values();
  }

  public int size() {
    int i = 0;
    for (Map.Entry<String, Sample> kv : samples.entrySet()) {
      i += kv.getValue().size();
    }
    return i;
  }

  public String getMostProbable() {
    double max = Double.NEGATIVE_INFINITY;
    String target = null;
    for (Map.Entry<String, Sample> kv : samples.entrySet()) {
      double weight = kv.getValue().getWeight();
      if (weight > max) {
        target = kv.getKey();
        max = weight;
      }
    }
    return target;
  }

  public Vector getDomain() {
    return domain;
  }

  public Sample get(String target) {
    return samples.get(target);
  }

  public Sample getRandomSample() {
    return samples.get(targets.get(RANDOM.nextInt(targets.size())));
  }

  public boolean isEmpty() {
    return targets.isEmpty();
  }

  public List<String> getTargets() {
    return Collections.unmodifiableList(targets);
  }

  public DoubleMatrix getRelativeFrequencies() {
    double size = getTotalWeight();
    double[] rel = new double[samples.size()];
    int i = 0;
    for (Sample c : samples.values()) {
      rel[i++] = c.getWeight() / size;
    }
    return DoubleMatrix.of(rel);
  }

  public double getTotalWeight() {
    double size = 0;
    for (Map.Entry<String, Sample> kv : samples.entrySet()) {
      size += kv.getValue().getWeight();
    }

    return size;
  }

  @Override
  public String toString() {
    return String.format("Examples(%.2f, %d, %d)", getTotalWeight(), getTargetCount(),
        System.identityHashCode(this));
  }

  public int getTargetCount() {
    return targets.size();
  }

  @Override
  public Iterator<Example> iterator() {
    return new Iterator<Example>() {
      Iterator<Sample> sampleIterator = samples.values().iterator();
      Iterator<Example> exampleIterator = sampleIterator.next().iterator();

      @Override
      public boolean hasNext() {
        return exampleIterator != null && exampleIterator.hasNext();
      }

      @Override
      public Example next() {
        Example example = exampleIterator.next();
        if (!exampleIterator.hasNext()) {
          if (!sampleIterator.hasNext()) {
            exampleIterator = null;
          } else {
            exampleIterator = sampleIterator.next().iterator();
          }
        }
        return example;
      }
    };

  }

  public static final class Sample implements Iterable<Example> {

    private final String target;
    private final ArrayList<Example> examples;
    private double weight;

    private Sample(String target) {
      this.target = target;
      this.examples = new ArrayList<>();
      this.weight = 0;
    }

    public static Sample create(String target) {
      return new Sample(target);
    }

    public String getTarget() {
      return target;
    }

    public Example get(int index) {
      return examples.get(index);
    }

    public int size() {
      return examples.size();
    }

    public boolean isEmpty() {
      return getWeight() == 0;
    }

    public double getWeight() {
      return weight;
    }

    public void add(Example example) {
      examples.add(example);
      weight += example.getWeight();
    }

    public Example getRandomExample() {
      return examples.get(RANDOM.nextInt(examples.size()));
    }

    @Override
    public Iterator<Example> iterator() {
      return examples.iterator();
    }

    @Override
    public String toString() {
      return String.format("Examples(%.2f)", getWeight());
    }
  }

}
