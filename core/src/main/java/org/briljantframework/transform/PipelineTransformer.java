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

package org.briljantframework.transform;

import org.briljantframework.dataframe.DataFrame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Isak Karlsson on 18/08/14.
 */
public class PipelineTransformer implements Transformer {

    private final List<Transformer> transformers;

    public PipelineTransformer(List<Transformer> transformers) {
        this.transformers = transformers;
    }

    /**
     * Of pipeline transformer.
     *
     * @param transformers the transformers
     * @return the pipeline transformer
     */
    public static PipelineTransformer of(Transformer... transformers) {
        if (transformers.length < 1) {
            throw new IllegalArgumentException("Cannot construct a PipelineTransformer without transformers");
        }

        return new PipelineTransformer(Arrays.asList(transformers));
    }

    @Override
    public Transformation fit(DataFrame dataset) {
        List<Transformation> transformations = new ArrayList<>();
        for (Transformer transformer : transformers) {
            Transformation transformation = transformer.fit(dataset);
            dataset = transformation.transform(dataset);
            transformations.add(transformation);
        }

        return new PipelineTransformation(transformations);
    }

    private static class PipelineTransformation implements Transformation {

        private final List<Transformation> transformations;

        public PipelineTransformation(List<Transformation> transformations) {
            this.transformations = transformations;
        }

        @Override
        public DataFrame transform(DataFrame dataset) {
            for (Transformation transformation : transformations) {
                dataset = transformation.transform(dataset);
            }
            return dataset;
        }
    }

}
