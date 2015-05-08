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

import com.atilika.kuromoji.io.TwoDimensionalArrayTool;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class WordIdMap {

    private int[][] wordIds;

    public WordIdMap() {
        wordIds = new int[1][];
    }

    public WordIdMap(InputStream is) throws IOException {
        wordIds = TwoDimensionalArrayTool.read(is);
    }

    public int[] lookUp(int sourceId) {
        return wordIds[sourceId];
    }


    public void addMapping(int sourceId, int wordId) {
        if (wordIds.length <= sourceId) {
            int[][] newArray = new int[sourceId + 1][];
            System.arraycopy(wordIds, 0, newArray, 0, wordIds.length);
            wordIds = newArray;
        }

        // Prepare array -- extend the length of array by one
        int[] current = wordIds[sourceId];
        if (current == null) {
            current = new int[1];
        } else {
            int[] newArray = new int[current.length + 1];
            System.arraycopy(current, 0, newArray, 0, current.length);
            current = newArray;
        }
        wordIds[sourceId] = current;

        int[] targets = wordIds[sourceId];
        targets[targets.length - 1] = wordId;
    }

    public void write(FileOutputStream fos) throws IOException {
        TwoDimensionalArrayTool.write(fos, wordIds);
    }
}
