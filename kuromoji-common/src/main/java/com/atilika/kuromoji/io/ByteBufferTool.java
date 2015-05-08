/**
 * Copyright 2010-2015 Atilika Inc. and contributors (see CONTRIBUTORS.md)
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

package com.atilika.kuromoji.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class ByteBufferTool {

    public static ByteBuffer read(InputStream is) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        DataInputStream dis = new DataInputStream(bis);

        int size = dis.readInt();

        ByteBuffer buffer = ByteBuffer.wrap(new byte[size]);

        ReadableByteChannel channel = Channels.newChannel(bis);
        channel.read(buffer);

        buffer.rewind();

        dis.close();

        return buffer;
    }

    public static void write(OutputStream os, ByteBuffer buffer) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(os);
        DataOutputStream dos = new DataOutputStream(bos);

        dos.writeInt(buffer.position());
        buffer.flip();

        WritableByteChannel channel = Channels.newChannel(dos);
        channel.write(buffer);
        dos.flush();
        dos.close();
    }
}
