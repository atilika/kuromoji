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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitsFormatter {

    private ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();

    public String format(InputStream input) throws IOException {
        BufferedInputStream bufferedInput = new BufferedInputStream(input);
        int nextByte;

        while ((nextByte = bufferedInput.read()) >= 0) {
            byteOutput.write(nextByte);
        }

        return format(byteOutput.toByteArray());
    }

    public String format(byte[] fst) {
        StringBuilder builder = new StringBuilder();

        int address = fst.length - 1;

        while (address > 0) {
            builder.append(formatState(fst, address));

            address -= stateSize(fst, address);
        }
        return builder.toString();
    }

    public int stateSize(byte[] fst, int address) {
        return 1 + 2 + Bits.getShort(fst, address - 1) * Compiler.ARC_SIZE;
    }

    public String formatState(byte[] fst, int address) {
        StringBuilder builder = new StringBuilder();

        builder.append(formatStateType(fst, address));
        builder.append(formatArcs(fst, address - 1));

        return builder.toString();
    }

    public String formatStateType(byte[] fst, int address) {
        StringBuilder builder = new StringBuilder();
        builder.append(formatAddress(address));
        builder.append(" ");

        byte type = Bits.getByte(fst, address);

        if (type == Compiler.STATE_TYPE_ACCEPT) {
            builder.append("ACCEPT");
        } else if (type == Compiler.STATE_TYPE_MATCH) {
            builder.append("MATCH");
        } else {
            throw new IllegalStateException("Illegal state type: " + type);
        }

        builder.append("\n");
        return builder.toString();
    }

    public String formatAddress(int address) {
        return String.format("%4d:", address);
    }

    public String formatArcs(byte[] fst, int address) {
        StringBuilder builder = new StringBuilder();
        int arcs = Bits.getShort(fst, address);

        address -= 2;

        for (int i = 0; i < arcs; i++) {
            builder.append(formatAddress(address));
            builder.append(formatArc(fst, address));
            builder.append("\n");
            address -= Compiler.ARC_SIZE;
        }

        return builder.toString();
    }

    public String formatArc(byte[] fst, int address) {
        StringBuilder builder = new StringBuilder();
        int output = Bits.getInt(fst, address);
        address -= 4;

        int jumpAddress = Bits.getInt(fst, address);
        address -= 4;

        char label = (char) Bits.getShort(fst, address);

        builder.append('\t');
        builder.append(label);
        builder.append(" -> ");
        builder.append(output);
        builder.append("\t(JMP: ");
        builder.append(jumpAddress);
        builder.append(')');
        return builder.toString();
    }
}
