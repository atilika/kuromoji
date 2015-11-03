/**
 * Copyright © 2010-2015 Atilika Inc. and contributors (see CONTRIBUTORS.md)
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
package com.atilika.kuromoji.buffer;

import com.atilika.kuromoji.io.ByteBufferIO;
import com.atilika.kuromoji.util.ScriptUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

public class StringValueMapBuffer {

    private static final int INTEGER_BYTES = Integer.SIZE / Byte.SIZE;

    private static final int SHORT_BYTES = Short.SIZE / Byte.SIZE;

    private static final short KATAKANA_FLAG = (short) 0x8000;

    private static final short KATAKANA_LENGTH_MASK = (short) 0x7fff;

    private static final char KATAKANA_BASE = '\u3000'; // Katakana start at U+30A0

    private ByteBuffer buffer;

    private int size;

    public StringValueMapBuffer(TreeMap<Integer, String> features) {
        put(features);
    }

    public StringValueMapBuffer(InputStream is) throws IOException {
        buffer = ByteBufferIO.read(is);
        size = buffer.getInt();
    }

    public String get(int key) {
        assert key >= 0 && key < size;

        final int keyIndex = (key + 1) * INTEGER_BYTES;
        final int valueIndex = buffer.getInt(keyIndex);
        int length = buffer.getShort(valueIndex);

        if ((length & KATAKANA_FLAG) != 0) {
            length &= KATAKANA_LENGTH_MASK;
            return getKatakanaString(valueIndex + SHORT_BYTES, length);
        } else {
            return getString(valueIndex + SHORT_BYTES, length);
        }
    }

    private String getKatakanaString(final int valueIndex, final int length) {
        final char[] string = new char[length];
        final byte[] array = buffer.array();

        for (int i = 0; i < length; i++) {
            string[i] = (char) (KATAKANA_BASE + (array[valueIndex + i] & 0xff));
        }

        return new String(string);
    }

    private String getString(final int valueIndex, final int length) {
        return new String(buffer.array(), valueIndex, length, StandardCharsets.UTF_16);
    }

    public void write(OutputStream output) throws IOException {
        ByteBufferIO.write(output, buffer);
    }

    private void put(TreeMap<Integer, String> strings) {
        final int bufferSize = calculateSize(strings);
        size = strings.size();

//        System.out.println("bufferSize: " + bufferSize + ", size: " + size);

        buffer = ByteBuffer.wrap(new byte[bufferSize]);
        buffer.putInt(0, size); // Set entries

        int keyIndex = INTEGER_BYTES; // First key index is past size
        int entryIndex = keyIndex + size * INTEGER_BYTES;

        for (String string : strings.values()) {
            buffer.putInt(keyIndex, entryIndex);
            entryIndex = put(entryIndex, string);
            keyIndex += INTEGER_BYTES;
        }
    }

    private int calculateSize(TreeMap<Integer, String> values) {
        int size = INTEGER_BYTES + values.size() * INTEGER_BYTES;

        for (String value : values.values()) {
            size += SHORT_BYTES + getByteSize(value);
        }
        return size;
    }

    private int getByteSize(String string) {
        if (ScriptUtils.isKatakana(string)) {
            return string.length();
        }

        return getBytes(string).length;
    }

    private int put(int index, String value) {
        boolean katakana = ScriptUtils.isKatakana(value);
        byte[] bytes;
        short length;

        if (katakana) {
            bytes = getKatakanaBytes(value);
            length = (short) (bytes.length | KATAKANA_FLAG & 0xffff);
        } else {
            bytes = getBytes(value);
            length = (short) bytes.length;
        }

//        System.out.println("index: " + index + ", value: " + value + ", kataka: " + katakana + ", length: " + bytes.length);

        assert bytes.length < Short.MAX_VALUE;

        buffer.position(index);
        buffer.putShort(length);
        buffer.put(bytes);

        return index + SHORT_BYTES + bytes.length;
    }

    private byte[] getKatakanaBytes(String string) {
        final int length = string.length();
        final byte[] bytes = new byte[length];

        for (int i = 0; i < length; i++) {
            char c = string.charAt(i);

            bytes[i] = (byte) (c - KATAKANA_BASE);
        }

        return bytes;
    }

    private byte[] getBytes(String string) {
        return string.getBytes(StandardCharsets.UTF_16);
    }
}
