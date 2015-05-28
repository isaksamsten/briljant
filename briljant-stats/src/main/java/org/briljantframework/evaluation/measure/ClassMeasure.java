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

package org.briljantframework.evaluation.measure;

import org.briljantframework.evaluation.result.Sample;
import org.briljantframework.vector.Vector;

/**
 * @author Isak Karlsson
 */
public interface ClassMeasure {

  default Vector get(String value) {
    return get(Sample.OUT, value);
  }

  Vector get(Sample sample, String value);

  default double getAverage(String value) {
    return getAverage(Sample.OUT, value);
  }

  double getAverage(Sample sample, String value);

  default double getStandardDeviation(String value) {
    return getStandardDeviation(Sample.OUT, value);
  }

  double getStandardDeviation(Sample sample, String value);

  default double getMin(String value) {
    return getMin(Sample.OUT, value);
  }

  double getMin(Sample out, String value);

  default double getMax(String value) {
    return getMax(Sample.OUT, value);
  }

  double getMax(Sample out, String value);
}
