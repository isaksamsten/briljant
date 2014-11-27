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

package org.briljantframework.learning.ensemble;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.learning.Classifier;
import org.briljantframework.learning.Prediction;
import org.briljantframework.learning.example.Examples;
import org.briljantframework.vector.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <p>The ensemble classifier combines a set of several weak classifiers, built using the {@link
 * org.briljantframework.learning.ensemble.Ensemble.Member}******-builder.
 * <p>
 * <p> Selecting a {@link org.briljantframework.learning.tree.ClassificationTree} with a {@link org.briljantframework.learning.tree.RandomSplitter}
 * together with a {@link Bootstrap} creates a random forest
 * <p>
 * <p> All classifiers are built in parallel.
 * <p>
 * Created by Isak Karlsson on 10/09/14.
 */
public class Ensemble implements Classifier {

    private static final Logger logger = Logger.getLogger(Ensemble.class.getSimpleName());

    private final Sampler sampler;
    private final ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), r -> {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        return thread;
    });
    private final int size;

    private final Member member;

    /**
     * Instantiates a new Ensemble.
     *
     * @param builder the builder
     */
    protected Ensemble(Builder builder) {
        this.member = checkNotNull(builder.member, "Require a member");
        this.sampler = checkNotNull(builder.sampler, "Requires a sampler");
        this.size = builder.size;
    }

    /**
     * Create bootstrap.
     *
     * @param member the builder
     * @return the bootstrap
     */
    public static Builder withMember(Member member) {
        return new Builder(member);
    }

    public int size() {
        return size;
    }

    @Override
    public Model fit(DataFrame dataset, Vector target) {
        checkArgument(dataset.rows() > 0 && dataset.columns() > 0);
        checkArgument(target.size() > 0 && target.size() == dataset.rows());

        Examples examples = Examples.fromVector(target);

        List<org.briljantframework.learning.Model> models = new ArrayList<>();
        List<Future<org.briljantframework.learning.Model>> futures = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            futures.add(service.submit(() -> {
                Examples bootstrapped = sampler.sample(examples);
                Classifier classifier = member.create(bootstrapped);
                return classifier.fit(dataset, target);
            }));
        }

        for (int i = 0; i < size; i++) {
            try {
                models.add(futures.get(i).get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        return new Model(models, size);
    }

    /**
     * A member created from an {@link org.briljantframework.learning.ensemble.Ensemble.Member} is not allowed to have any persistent
     * state. That is, every call to {@link #create(org.briljantframework.learning.example.Examples)} should return a fresh {@link
     * org.briljantframework.learning.Classifier}
     * <p>
     * <p>
     * Created by Isak Karlsson on 15/09/14.
     */
    public static interface Member {

        /**
         * Create classifier.
         *
         * @param sample the sample
         * @return the classifier
         */
        Classifier create(Examples sample);

    }

    /**
     * The type Builder.
     */
    public static class Builder implements Classifier.Builder<Ensemble> {

        private int size = 100;
        private Member member;
        private Sampler sampler = Sampler.IDENTITY;

        private Builder(Member member) {
            this.member = member;
        }

        /**
         * Member builder.
         *
         * @param member the member
         * @return the builder
         */
        public Builder withMember(Member member) {
            this.member = member;
            return this;
        }

        /**
         * Size builder.
         *
         * @param size the size
         * @return the builder
         */
        public Builder withSize(int size) {
            this.size = size;
            return this;
        }

        /**
         * Randomizer builder.
         *
         * @param randomizer the sampler
         * @return the builder
         */
        public Builder withSampler(Sampler randomizer) {
            this.sampler = randomizer;
            return this;
        }

        @Override
        public Ensemble create() {
            return new Ensemble(this);
        }
    }

    /**
     * The type Model.
     * <p>
     * TODO(isak): predictions from members can be combined in different ways.
     */
    public static class Model implements org.briljantframework.learning.Model {

        private final List<? extends org.briljantframework.learning.Model> models;
        private final int size;


        /**
         * Instantiates a new Model.
         *
         * @param models the models
         * @param size   the i
         */
        private Model(List<? extends org.briljantframework.learning.Model> models, int size) {
            this.models = models;
            this.size = size;
        }

        @Override
        public Prediction predict(Vector row) {
            List<Prediction> predictions = models.parallelStream().map(model -> model.predict(row)).collect(Collectors.toList());
            return majority(predictions, size);
        }

        private Prediction majority(List<Prediction> predictions, int size) {
            Multiset<Prediction> targets = HashMultiset.create(predictions);
            List<String> values = new ArrayList<>();
            List<Double> probabilities = new ArrayList<>();
            for (Multiset.Entry<Prediction> kv : targets.entrySet()) {
                Prediction prediction = kv.getElement();
                values.add(prediction.getValue());
                probabilities.add(kv.getCount() / (double) size);
            }
            return Prediction.nominal(values, probabilities);
        }

        /**
         * Gets models.
         *
         * @return the models
         */
        public List<? extends org.briljantframework.learning.Model> getModels() {
            return Collections.unmodifiableList(models);
        }
    }
}
