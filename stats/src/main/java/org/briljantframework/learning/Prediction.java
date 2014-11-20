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

package org.briljantframework.learning;

import com.google.common.base.Preconditions;
import com.google.common.math.DoubleMath;
import org.briljantframework.data.values.Numeric;
import org.briljantframework.data.values.Value;

import java.util.*;

/**
 * The type Prediction.
 */
public class Prediction {

    private final Value target;
    private final double probability;

    private final Map<Value, Double> values;

    // TODO - probability of alternative targets

    /**
     * Instantiates a new Prediction.
     *
     * @param target      the target
     * @param probability the probability
     * @param values      the alternatives
     */
    private Prediction(Value target, double probability, Map<Value, Double> values) {
        this.target = target;
        this.probability = probability;
        this.values = values;
    }

    /**
     * Unary prediction.
     *
     * @param a the a
     * @return the prediction
     */
    public static Prediction unary(Value a) {
        Map<Value, Double> values = new HashMap<>();
        values.put(a, 1.0);
        return new Prediction(a, 1.0, values);
    }

    /**
     * Create prediction.
     *
     * @param a  one class
     * @param pa one class probabilitye
     * @param b  other class
     * @param pb other class probability
     * @return the prediction
     */
    public static Prediction binary(Value a, double pa, Value b, double pb) {
        Preconditions.checkArgument(DoubleMath.fuzzyEquals(pa + pb, 1.0, 0.00001),
                "Probabilities have to sum to one (%s /= 1.0)", pa + pb);
        Map<Value, Double> values = new HashMap<>();
        values.put(a, pa);
        values.put(b, pb);

        Value major = a;
        double majorProb = pa;
        if (pb > pa) {
            major = b;
            majorProb = pb;
        }

        return new Prediction(major, majorProb, values);
    }

    /**
     * Numeric prediction.
     *
     * @param target the target
     * @return the prediction
     */
    public static Prediction numeric(Numeric target) {
        Map<Value, Double> values = new HashMap<>();
        values.put(target, 1.0);
        return new Prediction(target, 1.0, values);
    }

    /**
     * Create prediction.
     *
     * @param targets       the targets
     * @param probabilities the proablities
     * @return the prediction
     */
    public static Prediction nominal(List<Value> targets, List<Double> probabilities) {
        Preconditions.checkArgument(targets.size() == probabilities.size() && targets.size() > 0);

        Map<Value, Double> values = new HashMap<>();
        values.put(targets.get(0), probabilities.get(0));

        double maxProb = probabilities.get(0), sum = probabilities.get(0);
        Value mostProbable = targets.get(0);
        for (int i = 1; i < targets.size(); i++) {
            double prob = probabilities.get(i);
            Value value = targets.get(i);
            if (prob > maxProb) {
                maxProb = prob;
                mostProbable = value;
            }
            values.put(value, prob);
            sum += prob;
        }

        if (!DoubleMath.fuzzyEquals(sum, 1.0, 0.000001)) {
            throw new IllegalArgumentException(String.format("Probabilities have to sum to one (%f /= 1.0)", sum));
        }

        return new Prediction(mostProbable, maxProb, values);
    }

    /**
     * Gets target.
     *
     * @return the target
     */
    public Value getValue() {
        return target;
    }

    /**
     * Gets probability.
     *
     * @return the probability
     */
    public double getProbability() {
        return probability;
    }

    /**
     * Alternative values.
     *
     * @return the set
     */
    public Set<Map.Entry<Value, Double>> values() {
        return Collections.unmodifiableSet(values.entrySet());
    }

    /**
     * Gets targets.
     *
     * @return the targets
     */
    public Set<Value> getTargets() {
        return Collections.unmodifiableSet(values.keySet());
    }

    /**
     * Gets probability.
     *
     * @param value the value
     * @return the probability
     */
    public double getProbability(Value value) {
        return values.getOrDefault(value, 0d);
    }

    /**
     * Predicts boolean.
     *
     * @param value the value
     * @return the boolean
     */
    public boolean predicts(Value value) {
        return value.equals(target);
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof Prediction && target.equals(((Prediction) obj).target);
    }

    @Override
    public int hashCode() {
        return target.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Prediction(%s, %.2f)", target.toString(), probability);
    }
}
