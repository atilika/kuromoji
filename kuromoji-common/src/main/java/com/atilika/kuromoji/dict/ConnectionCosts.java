/**
 * Copyright 2010-2013 Atilika Inc. and contributors (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the
 * License is distributed with this work in the LICENSE.md file.  You may
 * also obtain a copy of the License from
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.atilika.kuromoji.dict;

import com.atilika.kuromoji.ClassLoaderResolver;
import com.atilika.kuromoji.ResourceResolver;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class ConnectionCosts {

    public static final String FILENAME = "cc.dat";

    private int dimension;

    private ShortBuffer costs;

    public ConnectionCosts(int forwardSize, int backwardSize) {
        this.costs = ShortBuffer.allocate(backwardSize * forwardSize);
        this.dimension = backwardSize;
    }

    public void add(short forwardId, short backwardId, short cost) {
        this.costs.put(backwardId + forwardId * dimension, cost);
    }

    public int get(int forwardId, int backwardId) {
        return costs.get(backwardId + forwardId * dimension);
    }

    public static ConnectionCosts newInstance(ResourceResolver resolver) throws IOException, ClassNotFoundException {
        return read(resolver.resolve(FILENAME));
    }

    public static ConnectionCosts newInstance() throws IOException, ClassNotFoundException {
        return newInstance(new ClassLoaderResolver(ConnectionCosts.class));
    }

    public void write(OutputStream stream) throws IOException {
        DataOutputStream daos = new DataOutputStream(stream);
        daos.writeShort(dimension);
        daos.writeInt(costs.array().length * 2);

        ByteBuffer outBuffer = ByteBuffer.allocate(costs.array().length * 2);

        for (short s : costs.array()) {
            outBuffer.putShort(s);
        }

        WritableByteChannel channel = Channels.newChannel(stream);

        outBuffer.flip();
        channel.write(outBuffer);
        stream.close();
    }

    public static ConnectionCosts read(InputStream is) throws IOException, ClassNotFoundException {
        BufferedInputStream bis = new BufferedInputStream(is);
        DataInputStream dais = new DataInputStream(bis);

        ConnectionCosts instance = new ConnectionCosts(0, 0);
        instance.dimension = dais.readShort();
        int size = dais.readInt();

        ByteBuffer tmpBuffer = ByteBuffer.allocate(size);
        ReadableByteChannel channel = Channels.newChannel(bis);

        channel.read(tmpBuffer);
        dais.close();

        tmpBuffer.rewind();
        instance.costs = tmpBuffer.asShortBuffer();

        return instance;
    }
}
