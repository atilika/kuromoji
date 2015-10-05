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

    public FST(byte[] compiled) throws IOException {
        this.fst = compiled;
        initCache();
    }

    public FST(InputStream input) throws IOException {
        this.fst = ByteBufferIO.read(input).array();
        initCache();
    }

    private void initCache() {
        Arrays.fill(jumpCache, -1);

        int address = fst.length - 1 - 1;

        int arcs = Bits.getShort(fst, address);
        address -= 2;

        for (int i = 0; i < arcs; i++) {

            final int output = Bits.getInt(fst, address);
            address -= 4;

            final int jump = Bits.getInt(fst, address);
            address -= 4;

            final char label = (char) Bits.getShort(fst, address);
            address -= 2;

            jumpCache[label] = address + Compiler.ARC_SIZE;
        }
    }

    public int lookup(String input) {
        final int length = input.length();
        int address = fst.length - 1;
        int accumulator = 0;
        int index = 0;

        while (true) {
            final byte stateType = Bits.getByte(fst, address);
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
                int jump = jumpCache[c];

                if (jump == -1) {
                    return -1;
                }

                address = jump;

                final int output = Bits.getInt(fst, address);
                address -= 4;

                accumulator += output;

                address = Bits.getInt(fst, address);
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
                    final int arcAddr = address - middle * Compiler.ARC_SIZE;
                    final char label = (char) Bits.getShort(fst, arcAddr - 8);

                    if (label == c) {
                        matched = true;
                        address = Bits.getInt(fst, arcAddr - 4);
                        accumulator += Bits.getInt(fst, arcAddr);
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

    public static FST newInstance(ResourceResolver resolver) throws IOException {
        return new FST(resolver.resolve(FST_FILENAME));
    }
}
