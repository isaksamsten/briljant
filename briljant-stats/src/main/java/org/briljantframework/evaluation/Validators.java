/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.briljantframework.evaluation;

import java.util.List;

import org.briljantframework.evaluation.partition.FoldPartitioner;
import org.briljantframework.evaluation.partition.LeaveOneOutPartitioner;
import org.briljantframework.evaluation.partition.SplitPartitioner;
import org.briljantframework.evaluation.result.Evaluator;

/**
 * @author Isak Karlsson
 */
public class Validators {

  public static Validator splitValidation(double testFraction) {
    return new DefaultValidator(new SplitPartitioner(testFraction));
  }

  public static Validator leaveOneOutValidation() {
    return new DefaultValidator(new LeaveOneOutPartitioner());
  }

  public static Validator crossValidation(int folds) {
    return new DefaultValidator(new FoldPartitioner(folds));
  }

  public static Validator crossValidation(List<Evaluator> measures, int folds) {
    return new DefaultValidator(measures, new FoldPartitioner(folds));
  }
}
