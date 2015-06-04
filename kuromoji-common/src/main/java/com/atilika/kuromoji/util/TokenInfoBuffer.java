/**
 * Copyright 2010-2015 Atilika Inc. and contributors (see CONTRIBUTORS.md)
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
package com.atilika.kuromoji.util;

import com.atilika.kuromoji.dict.BufferEntry;
import com.atilika.kuromoji.io.ByteBufferTool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;

public class TokenInfoBuffer {

    private static final int INTEGER_BYTES = Integer.SIZE / Byte.SIZE;
    private static final int SHORT_BYTES = Short.SIZE / Byte.SIZE;

    private ByteBuffer buffer;

    public TokenInfoBuffer(List<BufferEntry> entries) {
        putEntries(entries);
    }

    public TokenInfoBuffer(InputStream is) throws IOException {
        buffer = ByteBufferTool.read(is);
    }

    private void putEntries(List<BufferEntry> entries) {
        int size = calculateEntriesSize(entries) * 2;

        this.buffer = ByteBuffer.allocate(size + INTEGER_BYTES * 4);

        buffer.putInt(size);
        buffer.putInt(entries.size());
        BufferEntry firstEntry = entries.get(0);

        buffer.putInt(firstEntry.tokenInfo.size());
        buffer.putInt(firstEntry.posInfo.size());
        buffer.putInt(firstEntry.features.size());

        for (BufferEntry entry : entries) {
            for (Short s : entry.tokenInfo) {
                buffer.putShort(s);
            }

            for (Byte b : entry.posInfo) {
                buffer.put(b);
            }

            for (Integer feature : entry.features) {
                buffer.putInt(feature);
            }
        }
    }

    private int calculateEntriesSize(List<BufferEntry> entries) {
        if (entries.isEmpty()) {
            return 0;
        } else {
            int size = 0;
            BufferEntry entry = entries.get(0);
            size += entry.tokenInfo.size() * SHORT_BYTES + SHORT_BYTES;
            size += entry.posInfo.size();
            size += entry.features.size() * INTEGER_BYTES;
            size *= entries.size();
            return size;
        }
    }

    public BufferEntry lookupEntry(int offset) {
        BufferEntry entry = new BufferEntry();

        int tokenInfoCount = getTokenInfoCount();
        int posInfoCount = getPosInfoCount();
        int featureCount = getFeatureCount();

        entry.tokenInfos = new short[tokenInfoCount];
        entry.posInfos = new byte[posInfoCount];
        entry.featureInfos = new int[featureCount];

        int entrySize = getEntrySize(tokenInfoCount, posInfoCount, featureCount);
        int position = getPosition(offset, entrySize);

        for (int i = 0; i < tokenInfoCount; i++) {
            entry.tokenInfos[i] = buffer.getShort(position + i * SHORT_BYTES);
        }

        for (int i = 0; i < posInfoCount; i++) {
            entry.posInfos[i] = buffer.get(position + tokenInfoCount * SHORT_BYTES + i);
        }

        for (int i = 0; i < featureCount; i++) {
            entry.featureInfos[i] = buffer.getInt(position + tokenInfoCount * SHORT_BYTES + posInfoCount + i * INTEGER_BYTES);
        }

        return entry;
    }

    public int lookupTokenInfo(int offset, int i) {
        int tokenInfoCount = getTokenInfoCount();
        int posInfoCount = getPosInfoCount();
        int featureCount = getFeatureCount();

        int entrySize = getEntrySize(tokenInfoCount, posInfoCount, featureCount);
        int position = getPosition(offset, entrySize);
        return buffer.getShort(position + i * SHORT_BYTES);
    }

    public int lookupPosFeature(int offset, int i) {
        int tokenInfoCount = getTokenInfoCount();
        int posInfoCount = getPosInfoCount();
        int featureCount = getFeatureCount();

        int entrySize = getEntrySize(tokenInfoCount, posInfoCount, featureCount);
        int position = getPosition(offset, entrySize);

        return 0xff & buffer.get(position + tokenInfoCount * SHORT_BYTES + i);
    }

    public int lookupFeature(int offset, int i) {
        int tokenInfoCount = getTokenInfoCount();
        int posInfoCount = getPosInfoCount();
        int featureCount = getFeatureCount();

        int entrySize = getEntrySize(tokenInfoCount, posInfoCount, featureCount);
        int position = getPosition(offset, entrySize);

        return buffer.getInt(position + tokenInfoCount * SHORT_BYTES + posInfoCount + (i - posInfoCount) * INTEGER_BYTES);
    }

    public boolean isPosFeature(int i) {
        int posInfoCount = getPosInfoCount();
        return (i < posInfoCount);
    }

    private int getTokenInfoCount() {
        return buffer.getInt(INTEGER_BYTES * 2);
    }

    private int getPosInfoCount() {
        return buffer.getInt(INTEGER_BYTES * 3);
    }

    private int getFeatureCount() {
        return buffer.getInt(INTEGER_BYTES * 4);
    }

    private int getEntrySize(int tokenInfoCount, int posInfoCount, int featureCount) {
        return tokenInfoCount * SHORT_BYTES + posInfoCount + featureCount * INTEGER_BYTES;
    }

    private int getPosition(int offset, int entrySize) {
        return offset * entrySize + INTEGER_BYTES * 5;
    }

    public void write(OutputStream os) throws IOException {
        ByteBufferTool.write(os, buffer);
    }
}
