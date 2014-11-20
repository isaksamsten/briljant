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

package org.briljantframework.data.transform;

import org.briljantframework.data.DataFrame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Isak Karlsson on 18/08/14.
 */
public class PipelineTransformer<C extends DataFrame<?>> implements Transformer<C> {

    private final List<Transformer<C>> transformers;
    private final DataFrame.CopyTo<C> copyTo;

    public PipelineTransformer(DataFrame.CopyTo<C> copyTo, List<Transformer<C>> transformers) {
        this.transformers = transformers;
        this.copyTo = copyTo;
    }

    /**
     * Of pipeline transformer.
     *
     * @param <C>          the type parameter
     * @param transformers the transformers
     * @return the pipeline transformer
     */
    @SafeVarargs
    public static <C extends DataFrame<?>> PipelineTransformer<C> of(
            DataFrame.CopyTo<C> copyTo, Transformer<C>... transformers) {
        if (transformers.length < 1) {
            throw new IllegalArgumentException("Cannot construct a PipelineTransformer without transformers");
        }

        return new PipelineTransformer<>(copyTo, Arrays.asList(transformers));
    }

    @Override
    public Transformation<C> fit(C dataset) {
        List<Transformation<C>> transformations = new ArrayList<>();
        for (Transformer<C> transformer : transformers) {
            Transformation<C> transformation = transformer.fit(dataset);
            dataset = transformation.transform(dataset, copyTo);
            transformations.add(transformation);
        }

        return new PipelineTransformation<>(transformations);
    }

    private static class PipelineTransformation<E extends DataFrame<?>> implements Transformation<E> {

        private final List<Transformation<E>> transformations;

        public PipelineTransformation(List<Transformation<E>> transformations) {
            this.transformations = transformations;
        }

        @Override
        public E transform(E dataset, DataFrame.CopyTo<E> copyTo) {
            for (Transformation<E> transformation : transformations) {
                dataset = transformation.transform(dataset, copyTo);
            }
            return dataset;
        }
    }

}
