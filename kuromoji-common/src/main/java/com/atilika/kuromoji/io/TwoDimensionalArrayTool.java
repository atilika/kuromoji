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
package com.atilika.kuromoji.io;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TwoDimensionalArrayTool {

    public static int[][] read(InputStream is) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        DataInputStream dais = new DataInputStream(bis);

        int[][] arr = new int[dais.readInt()][];
        int index;
        while ((index = dais.readInt()) >= 0) {
            int length = dais.readInt();
            arr[index] = new int[length];
            for (int j = 0; j < length; j++) {
                arr[index][j] = dais.readInt();
            }
        }

        return arr;
    }

    public static void write(FileOutputStream fos, int[][] arr) throws IOException {
        DataOutputStream daos = new DataOutputStream(fos);
        daos.writeInt(arr.length);

        // The array is mostly sparse so we'll save only non-null members.
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != null) {
                int[] innerArray = arr[i];
                daos.writeInt(i);
                daos.writeInt(innerArray.length);
                for (int j : innerArray) daos.writeInt(j);
            }
        }

        daos.writeInt(-1); // End index marker.
        daos.flush();
        daos.close();
    }
}
