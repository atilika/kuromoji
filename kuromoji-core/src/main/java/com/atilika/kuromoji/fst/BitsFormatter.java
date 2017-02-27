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
        byte stateTypByte = Bits.getByte(fst, address);
        int jumpBytes = (stateTypByte & 0x03) + 1;
        int accumulateBytes = (stateTypByte & 0x03 << 3) >> 3;

        return 1 + 2 + Bits.getShort(fst, address - 1) * (2 + accumulateBytes + jumpBytes);
    }

    public String formatState(byte[] fst, int address) {
        StringBuilder builder = new StringBuilder();

        byte stateByte = Bits.getByte(fst, address);
        byte stateType = (byte) (stateByte & 0x80);
        int jumpBytes = (stateByte & 0x03) + 1;
        int accumulateBytes = (stateByte & 0x03 << 3) >> 3;

        builder.append(formatStateType(stateType, address));
        builder.append(formatArcs(fst, address - 1, accumulateBytes, jumpBytes));

        return builder.toString();
    }

    public String formatStateType(byte stateByte, int address) {
        StringBuilder builder = new StringBuilder();
        builder.append(formatAddress(address));
        builder.append(" ");

        if (stateByte == Compiler.STATE_TYPE_ACCEPT) {
            builder.append("ACCEPT");
        } else if (stateByte == Compiler.STATE_TYPE_MATCH) {
            builder.append("MATCH");
        } else {
            throw new IllegalStateException("Illegal state type: " + stateByte);
        }

        builder.append("\n");
        return builder.toString();
    }

    public String formatAddress(int address) {
        return String.format("%4d:", address);
    }

    public String formatArcs(byte[] fst, int address, int accumulateBytes, int jumpBytes) {
        StringBuilder builder = new StringBuilder();
        int arcs = Bits.getShort(fst, address);

        address -= 2;

        for (int i = 0; i < arcs; i++) {
            builder.append(formatAddress(address));
            builder.append(formatArc(fst, address, accumulateBytes, jumpBytes));
            builder.append("\n");
            address -= 2 + accumulateBytes + jumpBytes;
        }

        return builder.toString();
    }

    public String formatArc(byte[] fst, int address, int accumulateBytes, int jumpBytes) {
        StringBuilder builder = new StringBuilder();
        int output = Bits.getInt(fst, address, accumulateBytes);
        address -= accumulateBytes;

        int jumpAddress = Bits.getInt(fst, address, jumpBytes);
        address -= jumpBytes;

        char label = (char) Bits.getShort(fst, address);
//        address -= 1;

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
