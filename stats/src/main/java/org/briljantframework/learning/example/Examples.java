/*
 * ADEB - machine learning pipelines made easy
 * Copyright (C) 2014  Isak Karlsson
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.briljantframework.learning.example;


import org.briljantframework.vector.Vector;

import java.util.*;

/**
 * Created by Isak Karlsson on 08/09/14.
 */
public final class Examples implements Iterable<Example> {

    private static final Random RANDOM = new Random();

    private final Map<String, Sample> samples;

    private final List<String> targets;

    private Examples(Examples examples) {
        samples = new HashMap<>(examples.samples);
        targets = new ArrayList<>(examples.targets);
    }

    /**
     * Instantiates new Examples from Targets
     *
     * @param column the target
     */
    private Examples(Vector column) {
        this();
        for (int i = 0; i < column.size(); i++) {
            add(column.getAsString(i), i, 1);
        }
    }

    private Examples() {
        samples = new HashMap<>();
        targets = new ArrayList<>();
    }

    /**
     * From target.
     *
     * @param targets the targets
     * @return the examples
     */
    public static Examples fromVector(Vector targets) {
        return new Examples(targets);
    }

    /**
     * Empty examples.
     *
     * @return the examples
     */
    public static Examples create() {
        return new Examples();
    }

    /**
     * Add void.
     *
     * @param target the target
     * @param index  the index
     * @param weight the weight
     */
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

    /**
     * Add class set.
     *
     * @param sample the class set
     */
    public void add(Sample sample) {
        String target = sample.getTarget();
        targets.add(target);
        samples.put(target, sample);
    }

    /**
     * Entry set.
     *
     * @return the set
     */
    public Collection<Sample> samples() {
        return samples.values();
    }

    /**
     * Size int.
     *
     * @return the int
     */
    public int size() {
        int i = 0;
        for (Map.Entry<String, Sample> kv : samples.entrySet()) {
            i += kv.getValue().size();
        }
        return i;
    }

    /**
     * Gets most probable.
     *
     * @return the most probable
     */
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

    /**
     * Get class set.
     *
     * @param target the target
     * @return the class set
     */
    public Sample get(String target) {
        return samples.get(target);
    }

    /**
     * Gets random.
     *
     * @return the random
     */
    public Sample getRandomSample() {
        return samples.get(targets.get(RANDOM.nextInt(targets.size())));
    }

    /**
     * Is empty.
     *
     * @return the boolean
     */
    public boolean isEmpty() {
        return targets.isEmpty();
    }

    /**
     * Gets targets.
     *
     * @return the targets
     */
    public List<String> getTargets() {
        return Collections.unmodifiableList(targets);
    }

    /**
     * Get relative frequencies.
     *
     * @return the double [ ]
     */
    public double[] getRelativeFrequencies() {
        double size = getTotalWeight();
        double[] rel = new double[samples.size()];
        int i = 0;
        for (Sample c : samples.values()) {
            rel[i++] = c.getWeight() / size;
        }
        return rel;
    }

    /**
     * Gets total count.
     *
     * @return the total count
     */
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

    /**
     * Gets target count.
     *
     * @return the target count
     */
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

    /**
     * Sample subset.
     *
     * @param sampleSize the sample size
     * @return the examples
     */
    public Examples sampleSubset(int sampleSize) {
        //        List<Iterator<Example>> exampleIterators = new ArrayList<>();
        //        List<Sample> newSamples = new ArrayList<>();
        //        for (Sample s : samples()) {
        //            exampleIterators.add(s.iterator());
        //            newSamples.add(Sample.create(s.getTarget()));
        //        }
        //
        //        Examples out = Examples.create();
        //        for (int i = 0; i < exampleIterators.size(); i++) {
        //            Iterator<Example> it = exampleIterators.get(i);
        //            Sample sample = newSamples.get(i);
        //            if (it.hasNext()) {
        //                sample.add(it.next());
        //            }
        //        }


        return this;
    }

    /**
     * Copy examples.
     *
     * @return the examples
     */
    public Examples copy() {
        return new Examples(this);
    }

    /**
     * The type Class set.
     */
    public static final class Sample implements Iterable<Example> {

        private final String target;
        private final ArrayList<Example> examples;
        private double weight;

        /**
         * Instantiates a new Class set.
         *
         * @param target the target
         */
        private Sample(String target) {
            this.target = target;
            this.examples = new ArrayList<>();
            this.weight = 0;
        }

        /**
         * Empty sample.
         *
         * @param target the target
         * @return the sample
         */
        public static Sample create(String target) {
            return new Sample(target);
        }

        /**
         * Gets target.
         *
         * @return the target
         */
        public String getTarget() {
            return target;
        }

        /**
         * Get example.
         *
         * @param index the index
         * @return the example
         */
        public Example get(int index) {
            return examples.get(index);
        }

        /**
         * Size int.
         *
         * @return the int
         */
        public int size() {
            return examples.size();
        }

        /**
         * Is empty.
         *
         * @return the boolean
         */
        public boolean isEmpty() {
            return getWeight() == 0;
        }

        /**
         * Gets weight.
         *
         * @return the weight
         */
        public double getWeight() {
            return weight;
        }

        /**
         * Add void.
         *
         * @param example the example
         */
        public void add(Example example) {
            examples.add(example);
            weight += example.getWeight();
        }

        /**
         * Gets random.
         *
         * @return the random
         */
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
