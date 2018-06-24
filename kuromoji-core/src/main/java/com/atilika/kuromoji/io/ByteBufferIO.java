/**
 * Copyright © 2010-2018 Atilika Inc. and contributors (see CONTRIBUTORS.md)
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class ByteBufferIO {

    public static ByteBuffer read(InputStream input) throws IOException {
        DataInputStream dataInput = new DataInputStream(input);

        int size = dataInput.readInt();
        ByteBuffer buffer = ByteBuffer.allocate(size);

        ReadableByteChannel channel = Channels.newChannel(dataInput);
        while (buffer.hasRemaining()) {
            channel.read(buffer);
        }

        ((Buffer) buffer).rewind();
        return buffer;
    }

    public static void write(OutputStream output, ByteBuffer buffer) throws IOException {
        DataOutputStream dataOutput = new DataOutputStream(output);

        buffer = buffer.duplicate();
        ((Buffer) buffer).rewind();

        dataOutput.writeInt(buffer.capacity());

        WritableByteChannel channel = Channels.newChannel(dataOutput);
        channel.write(buffer);
        dataOutput.flush(); // TODO: Do we need this?
    }
}
