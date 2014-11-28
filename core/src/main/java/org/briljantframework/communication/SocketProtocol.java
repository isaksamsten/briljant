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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Isak Karlsson on 10/06/14.
 */
public class SocketProtocol implements Protocol {

  private final int port;
  private final String address;

  /**
   * Instantiates a new Socket protocol.
   *
   * @param port the port
   * @param address the address
   */
  private SocketProtocol(int port, String address) {
    this.port = port;
    this.address = address;
  }

  /**
   * Create socket protocol.
   *
   * @param port the port
   * @param address the address
   * @return the socket protocol
   */
  public static SocketProtocol create(int port, String address) {
    return new SocketProtocol(port, address);
  }

  @Override
  public Streams create() throws IOException {
    return new SocketStreams(port, address);
  }

  /**
   * The type Socket streams.
   */
  private static class SocketStreams implements Streams {
    private String address;
    private int port;
    private Socket socket;

    /**
     * Instantiates a new Socket streams.
     *
     * @param port the port
     * @param address the address
     * @throws IOException the iO exception
     */
    public SocketStreams(int port, String address) throws IOException {
      this.port = port;
      this.address = address;
    }

    @Override
    public InputStream getInputStream() throws IOException {
      socket = new Socket(address, port);
      return socket.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
      socket = new Socket(address, port);
      return socket.getOutputStream();
    }

    @Override
    public InputStream getErrorStream() {
      return null;
    }

    @Override
    public void close() throws IOException {
      socket.close();
    }
  }
}
