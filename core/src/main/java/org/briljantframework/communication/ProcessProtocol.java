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

package org.briljantframework.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by Isak Karlsson on 10/06/14.
 */
public class ProcessProtocol implements Protocol {

    private final String[] command;

    /**
     * Instantiates a new Process protocol.
     *
     * @param args the args
     */
    private ProcessProtocol(String... args) {
        if (args.length < 1)
            throw new IllegalArgumentException("command.length < 1");

        this.command = args;
    }

    /**
     * Create process protocol.
     *
     * @param args the args
     * @return the process protocol
     */
    public static ProcessProtocol create(String... args) {
        return new ProcessProtocol(args);
    }

    @Override
    public Streams create() throws IOException {
        return new ProcessStreams(this.command);
    }

    @Override
    public String toString() {
        return String.format("'%s'", Arrays.asList(command).stream().map(x -> x).collect(Collectors.joining(" ")));
    }

    /**
     * The type Process streams.
     */
    private static class ProcessStreams implements Streams {

        private final Process process;
        private final OutputStream output;
        private final InputStream error;
        private final InputStream input;

        /**
         * Instantiates a new Process streams.
         *
         * @param command the command
         * @throws IOException the iO exception
         */
        public ProcessStreams(String[] command) throws IOException {
            this.process = new ProcessBuilder(command).start();
            this.input = this.process.getInputStream();
            this.error = this.process.getErrorStream();
            this.output = this.process.getOutputStream();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return input;
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return output;
        }

        @Override
        public InputStream getErrorStream() {
            return error;
        }

        @Override
        public void close() throws IOException {
            try {
                if (process.waitFor() != 0) {
                    process.destroy();
                }
            } catch (InterruptedException e) {
                throw new IOException(e);
            }
        }
    }
}
