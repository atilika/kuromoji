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
package com.atilika.kuromoji.fst;

public class Bits {

    public static byte getByte(byte[] array, int index) {
        return array[index];
    }

    public static int getShort(byte[] bytes, int index) {
        return (bytes[index - 1] & 0xff) << 8 | (bytes[index] & 0xff);
    }

    public static int getInt(byte[] bytes, int index) {
        return (bytes[index - 3] & 0xff) << 24 | (bytes[index - 2] & 0xff) << 16 | (bytes[index - 1] & 0xff) << 8 | (bytes[index] & 0xff);
    }

    public static int getInt(byte[] bytes, int index, int intBytes) {
        switch (intBytes) {
            case 0:
                return 0;

            case 1:
                return bytes[index] & 0xff;

            case 2:
                return (bytes[index - 1] & 0xff) << 8 | (bytes[index] & 0xff);

            case 3:
                return (bytes[index - 2] & 0xff) << 16 | (bytes[index - 1] & 0xff) << 8 | (bytes[index] & 0xff);

            case 4:
                return (bytes[index - 3] & 0xff) << 24 | (bytes[index - 2] & 0xff) << 16 | (bytes[index - 1] & 0xff) << 8 | (bytes[index] & 0xff);

            default:
                throw new RuntimeException("Illegal int byte size: " + intBytes);
        }
    }

    public static void putInt(byte[] bytes, int index, int value, int intBytes) {
        switch (intBytes) {
            case 1:
                bytes[index] = (byte) (value & 0xff);
                break;

            case 2:
                bytes[index - 1] = (byte) (value >> 8 & 0xff);
                bytes[index] = (byte) (value & 0xff);
                break;

            case 3:
                bytes[index - 2] = (byte) (value >> 16 & 0xff);
                bytes[index - 1] = (byte) (value >> 8 & 0xff);
                bytes[index] = (byte) (value & 0xff);

            case 4:
                bytes[index - 3] = (byte) (value >> 24 & 0xff);
                bytes[index - 2] = (byte) (value >> 16 & 0xff);
                bytes[index - 1] = (byte) (value >> 8 & 0xff);
                bytes[index] = (byte) (value & 0xff);

            default:
                throw new RuntimeException("Illegal int byte size: " + intBytes);
        }
    }
}
