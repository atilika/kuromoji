/**
 * Copyright © 2010-2017 Atilika Inc. and contributors (see CONTRIBUTORS.md)
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

import com.atilika.kuromoji.io.ByteBufferIO;
import com.atilika.kuromoji.util.ResourceResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class FST {

    public static final String FST_FILENAME = "fst.bin";

    private byte[] fst;

    private int[] jumpCache = new int[65536];

    private int[] outputCache = new int[65536];

    public FST(byte[] compiled) {
        this.fst = compiled;
        initCache();
    }

    public FST(InputStream input) throws IOException {
        this(ByteBufferIO.read(input).array());
    }

    private void initCache() {
        Arrays.fill(jumpCache, -1);
        Arrays.fill(outputCache, -1);

        int address = fst.length - 1;

        final byte stateType = Bits.getByte(fst, address);
        address -= 1;

        final int jumpBytes = (stateType & 0x03) + 1;
        final int outputBytes = (stateType & 0x03 << 3) >> 3;

        int arcs = Bits.getShort(fst, address);
        address -= 2;

        for (int i = 0; i < arcs; i++) {
            final int output = Bits.getInt(fst, address, outputBytes);
            address -= outputBytes;

            final int jump = Bits.getInt(fst, address, jumpBytes);
            address -= jumpBytes;

            final char label = (char) Bits.getShort(fst, address);
            address -= 2;

            jumpCache[label] = jump;
            outputCache[label] = output;
        }
    }

    public int lookup(String input) {
        final int length = input.length();
        int address = fst.length - 1;
        int accumulator = 0;
        int index = 0;

        while (true) {
            final byte stateTypByte = Bits.getByte(fst, address);

            // The number of bytes in the target address (always larger than zero)
            final int jumpBytes = (stateTypByte & 0x03) + 1;

            // The number of bytes in the output value
            int outputBytes = (stateTypByte & 0x03 << 3) >> 3;

            final int arcSize = 2 + jumpBytes + outputBytes;

            final byte stateType = (byte) (stateTypByte & 0x80);
            address -= 1;

            if (index == length) {
                if (stateType == Compiler.STATE_TYPE_MATCH) {
                    accumulator = 0; // Prefix match
                }
                return accumulator;
            }

            boolean matched = false;
            final char c = input.charAt(index);

            if (index == 0) {
                //
                // Processes cached root arcs - transition directly to the next state on a match
                //
                final int jump = jumpCache[c];

                if (jump == -1) {
                    return -1;
                }

                final int output = outputCache[c];
                accumulator += output;

                address = jump;
                matched = true;
            } else {
                //
                // Transition to the next state by binary searching the output arcs
                //
                final int numberOfArcs = Bits.getShort(fst, address);
                address -= 2;

                if (numberOfArcs == 0) {
                    return -1;
                }

                int high = numberOfArcs - 1;
                int low = 0;

                while (low <= high) {
                    final int middle = low + (high - low) / 2;
                    final int arcAddr = address - middle * arcSize;

                    final char label = getArcLabel(arcAddr, outputBytes, jumpBytes);

                    if (label == c) {
                        matched = true;
                        address = getArcJump(arcAddr, outputBytes, jumpBytes);
                        accumulator += getArcOutput(arcAddr, outputBytes, jumpBytes);
                        break;
                    } else if (label > c) {
                        low = middle + 1;
                    } else {
                        high = middle - 1;
                    }
                }
            }

            if (matched == false) {
                return -1;
            }

            index++;
        }
    }

    private char getArcLabel(final int arcAddress, final int accumulateBytes, final int jumpBytes) {
        return (char) Bits.getShort(fst, arcAddress - (accumulateBytes + jumpBytes));
    }

    private int getArcJump(final int arcAddress, final int accumulateBytes, final int jumpBytes) {
        return Bits.getInt(fst, arcAddress - accumulateBytes, jumpBytes);
    }

    private int getArcOutput(final int arcAddress, final int accumulateBytes, final int jumpBytes) {
        return Bits.getInt(fst, arcAddress, accumulateBytes);
    }

    public static FST newInstance(ResourceResolver resolver) throws IOException {
        return new FST(resolver.resolve(FST_FILENAME));
    }
}
