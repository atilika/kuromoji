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

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class Compiler {

    /**
     * <pre>
     * {@code
     * 1 byte   bit 7: true - accept state, false - match state
     *          bits 3-6 indicate number of bytes in output value (m)
     *          bits 0-2 indicate number of bytes in jump address (n)
     * 2 bytes  number of outgoing arcs
     * [
     *  (
     *   2 bytes label (char),
     *   n bytes jump address,
     *   m bytes accumlator
     *  )
     * ]
     * }
     * </pre>
     */
    public static final byte STATE_TYPE_MATCH = (byte) 0x00;

    public static final byte STATE_TYPE_ACCEPT = (byte) 0x80;

    private ByteArrayOutputStream byteArrayOutput;

    private DataOutput dataOutput;

    private int written = 0;

    public Compiler() {
        byteArrayOutput = new ByteArrayOutputStream();
        dataOutput = new DataOutputStream(byteArrayOutput);
    }

    public void compileState(State state) throws IOException {
        if (state.getTargetJumpAddress() == -1) {
            int jumpBytes = findMaxJumpAddressBytes(state);
            int outputBytes = findMaxOutputBytes(state);

            writeStateArcs(state, outputBytes, jumpBytes);
            writeStateType(state, outputBytes, jumpBytes);

            // The last arc is regarded as a state because we evaluate the FST backwards.
            state.setTargetJumpAddress(written - 1);
        }
    }

    private void writeStateType(State state, int outputBytes, int jumpBytes) throws IOException {
        byte stateType;

        if (state.isFinal()) {
            stateType = STATE_TYPE_ACCEPT;
        } else {
            stateType = STATE_TYPE_MATCH;
        }

        stateType |= jumpBytes - 1;
        stateType |= outputBytes << 3;

        dataOutput.writeByte(stateType);

        written += 1;
    }

    private void writeStateArcs(State state, int outputBytes, int jumpBytes) throws IOException {
        List<Arc> arcs = state.arcs;

        for (Arc arc : arcs) {
            writeStateArc(arc, outputBytes, jumpBytes);
        }

        dataOutput.writeShort(arcs.size());
        written += 2;
    }

    private void writeStateArc(Arc arc, int outputBytes, int jumpBytes) throws IOException {
        State target = arc.getDestination();
        int arcSize = 2 + jumpBytes + outputBytes; // label + bytes for a jump + output

        dataOutput.writeShort(arc.getLabel());
        writeIntValue(target.getTargetJumpAddress(), jumpBytes);
        writeIntValue(arc.getOutput(), outputBytes);

        written += arcSize;
    }

    private void writeIntValue(int value, int bytes) throws IOException {
        switch (bytes) {
            case 0:
                break;

            case 1:
                dataOutput.writeByte(value & 0xff);
                break;

            case 2:
                dataOutput.writeByte((value >> 8) & 0xff);
                dataOutput.writeByte(value & 0xff);
                break;

            case 3:
                dataOutput.writeByte((value >> 16) & 0xff);
                dataOutput.writeByte((value >> 8) & 0xff);
                dataOutput.writeByte(value & 0xff);
                break;

            case 4:
                dataOutput.writeByte((value >> 24) & 0xff);
                dataOutput.writeByte((value >> 16) & 0xff);
                dataOutput.writeByte((value >> 8) & 0xff);
                dataOutput.writeByte(value & 0xff);
                break;

            default:
                throw new RuntimeException("Illegal int byte size: " + bytes);
        }
    }

    private int findMaxJumpAddressBytes(State state) {
        int maxJumpAddress = 0;

        for (Arc arc : state.arcs) {
            int jumpAddress = arc.getDestination().getTargetJumpAddress();

            if (maxJumpAddress < jumpAddress) {
                maxJumpAddress = jumpAddress;
            }
        }

        return findBytes(maxJumpAddress);
    }

    private int findMaxOutputBytes(State state) {
        int maxOutput = 0;

        for (Arc arc : state.arcs) {
            int output = arc.getOutput();

            if (maxOutput < output) {
                maxOutput = output;
            }
        }

        if (maxOutput == 0) {
            return 0;
        }

        return findBytes(maxOutput);
    }

    private int findBytes(int value) {
        if (value < 256) {
            return 1;
        }

        if (value < 65536) {
            return 2;
        }

        if (value < 16777216) {
            return 3;
        }

        return 4;
    }

    public byte[] getBytes() {
        return byteArrayOutput.toByteArray();
    }
}
