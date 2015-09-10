/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.data.parser;

import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.dataframe.MixedDataFrame;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.function.Supplier;

/**
 * Created by isak on 09/09/15.
 */
public abstract class Parser {

  private final Supplier<DataFrame.Builder> builderFactory;

  protected Parser(Supplier<DataFrame.Builder> builderFactory) {
    this.builderFactory = builderFactory;
  }

  public Parser() {
    this(MixedDataFrame.Builder::new);
  }

  protected Supplier<DataFrame.Builder> getBuilderFactory() {
    return builderFactory;
  }

  public DataFrame parse(String fileName) throws IOException {
    return parse(new File(fileName));
  }

  public DataFrame parse(File file) throws IOException {
    return parse(new FileReader(file));
  }

  public DataFrame parse(InputStream inputStream) throws IOException {
    return parse(new InputStreamReader(inputStream));
  }

  abstract DataFrame parse(Reader reader) throws IOException;
}
