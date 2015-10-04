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

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class Compiler {

    /**
     * 1 byte   node type: 0x01 = accept, 0x02 = match
     * 2 bytes  number of outgoing arcs
     * [
     *  (
     *   2 bytes label (char),
     *   4 bytes jump address,
     *   4 bytes accumlator
     *  )
     * ]
     */
    public static final byte STATE_TYPE_MATCH = 0x01;

    public static final byte STATE_TYPE_ACCEPT = 0x02;

    public static final int ARC_SIZE = 2 + 4 + 4;

    private ByteArrayOutputStream byteArrayOutput;

    private DataOutput dataOutput;

    private int written = 0;

    public Compiler() {
        byteArrayOutput = new ByteArrayOutputStream();
        dataOutput = new DataOutputStream(byteArrayOutput);
    }

    public void compileState(State state) throws IOException {
        writeStateArcs(state);
        writeStateType(state);

        if (state.getNewTargetAddress() == -1) {
            // The last arc is regarded as a state because we evaluate the FST backwards.
            state.setNewTargetAddress(written - 1);
        }
    }

    private void writeStateType(State state) throws IOException {
        if (state.isFinal()) {
            dataOutput.writeByte(STATE_TYPE_ACCEPT);
        } else {
            dataOutput.writeByte(STATE_TYPE_MATCH);
        }
        written += 1;
    }

    private void writeStateArcs(State state) throws IOException {
        List<Arc> arcs = state.arcs;

        for (Arc arc : arcs) {
            writeStateArc(arc);
        }

        dataOutput.writeShort(arcs.size());
        written += 2;
    }

    private void writeStateArc(Arc arc) throws IOException {
        State target = arc.getDestination();

        dataOutput.writeShort(arc.getLabel());
        dataOutput.writeInt(target.getNewTargetAddress());
        dataOutput.writeInt(arc.getOutput());

        written += ARC_SIZE;
    }

    public byte[] getByteArray() {
        return byteArrayOutput.toByteArray();
    }
}
