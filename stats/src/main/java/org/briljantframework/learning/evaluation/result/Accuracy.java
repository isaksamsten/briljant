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

package org.briljantframework.learning.evaluation.result;

import com.google.common.base.Preconditions;
import org.briljantframework.learning.Predictions;
import org.briljantframework.vector.Vector;

/**
 * Created by Isak Karlsson on 06/10/14.
 */
public class Accuracy extends AbstractMetric {


    /**
     * Instantiates a new Accuracy.
     *
     * @param producer the producer
     */
    private Accuracy(AbstractMetric.Producer producer) {
        super(producer);
    }

    /**
     * @return the factory
     */
    public static Factory getFactory() {
        return Producer::new;
    }

    @Override
    public String getName() {
        return "Accuracy";
    }

    private static final class Producer extends AbstractMetric.Producer {

        @Override
        public Metric.Producer add(Sample sample, Predictions predictions, Vector column) {
            Preconditions.checkArgument(predictions.size() == column.size());

            double accuracy = 0.0;
            for (int i = 0; i < predictions.size(); i++) {
                if (predictions.get(i).getValue().equals(column.getAsString(i))) {
                    accuracy++;
                }
            }

            add(sample, accuracy / predictions.size());
            return this;
        }

        @Override
        public Metric produce() {
            return new Accuracy(this);
        }
    }
}
