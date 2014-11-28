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

package org.briljantframework.io;


/**
 * Created by Isak Karlsson on 14/08/14.extends DatasetOutputStream
 */
public class CsvOutputStream {

  // public CSVOutputStream(OutputStream outputStream) {
  // super(outputStream);
  // }
  //
  // @Override
  // public void write(Traversable dataset) throws IOException {
  // BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
  // boxHeader(dataset, writer);
  // boxValues(dataset, writer);
  // writer.flush();
  // }
  //
  // private void boxHeader(Traversable<?> dataset, BufferedWriter writer)
  // throws IOException {
  // ArrayList<String> names = new ArrayList<>(dataset.columns());
  // ArrayList<String> types = new ArrayList<>(dataset.columns());
  // for (Type c : dataset.getTypes()) {
  // names.add(c.getName());
  // types.add(c.getDataType().toString());
  // }
  // writer.write(String.join(",", types));
  // writer.newLine();
  // writer.write(String.join(",", names));
  // writer.newLine();
  // }
  //
  // private void boxValues(Traversable<?> dataset, BufferedWriter writer)
  // throws IOException {
  // int cols = dataset.columns();
  // for (Row row : dataset) {
  // ArrayList<String> values = new ArrayList<>(cols);
  // for (int j = 0; j < row.size(); j++) {
  // if (row.getValue(j).na()) {
  // values.add("?");
  // } else {
  // values.add(row.getValue(j).toString());
  // }
  // }
  // writer.write(String.join(",", values));
  // writer.newLine();
  // }
  // }
}
