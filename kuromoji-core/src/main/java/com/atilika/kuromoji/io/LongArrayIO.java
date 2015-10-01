/**
 * Copyright © 2010-2015 Atilika Inc. and contributors (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the
 * License is distributed with this work in the LICENSE.md file.  You may
 * also obtain a copy of the License from
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class LongArrayIO {

    private static final int LONG_BYTES = Long.SIZE / Byte.SIZE;

    public static long[] readArray(InputStream input) throws IOException {
        DataInputStream dataInput = new DataInputStream(input);

        int length = dataInput.readInt();

        ByteBuffer tmpBuffer = ByteBuffer.allocate(length * LONG_BYTES);
        ReadableByteChannel channel = Channels.newChannel(dataInput);
        channel.read(tmpBuffer);
        channel.close();

        tmpBuffer.rewind();
        LongBuffer longBuffer = tmpBuffer.asLongBuffer();

        long[] array = new long[length];
        longBuffer.get(array);

        return array;
    }

    public static void writeArray(OutputStream output, long[] array, int length) throws IOException {
        DataOutputStream dataOutput = new DataOutputStream(output);

        dataOutput.writeInt(length);

        ByteBuffer tmpBuffer = ByteBuffer.allocate(length * LONG_BYTES);
        LongBuffer longBuffer = tmpBuffer.asLongBuffer();

        tmpBuffer.rewind();
        longBuffer.put(array, 0, length);

        WritableByteChannel channel = Channels.newChannel(dataOutput);
        channel.write(tmpBuffer);
    }
}
