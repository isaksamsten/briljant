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

package org.briljantframework.communication;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Isak Karlsson on 10/06/14.
 */
public interface Streams extends Closeable {

  /**
   * Gets input stream.
   *
   * @return the input stream
   * @throws IOException the iO exception
   */
  InputStream getInputStream() throws IOException;

  /**
   * Gets output stream.
   *
   * @return the output stream
   * @throws IOException the iO exception
   */
  OutputStream getOutputStream() throws IOException;

  /**
   * Gets error stream.
   *
   * @return the error stream
   */
  InputStream getErrorStream();

  /**
   * Can report error.
   *
   * @return the boolean
   */
  default boolean canReportError() {
    return false;
  }
}
