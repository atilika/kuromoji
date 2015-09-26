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
package com.atilika.kuromoji.compile;

import com.atilika.kuromoji.fst.FSTBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class FSTCompiler implements Compiler {

    private final OutputStream output;

    private final HashSet<String> surfaces;

    public FSTCompiler(OutputStream output, List<String> surfaces) {
        this.output = output;
        this.surfaces = new HashSet<>(surfaces);
    }

    @Override
    public void compile() throws IOException {
        FSTBuilder fstBuilder = new FSTBuilder();

        int size = surfaces.size();
        String[] surfacesArray = surfaces.toArray(new String[size]);
        int[] outputsArray = new int[size];

        for (int i = 0; i < size; i++) {
            outputsArray[i] = i + 1; // Value should not start at 0 because that means a prefix match
        }

        Arrays.sort(surfacesArray);
        fstBuilder.createDictionary(surfacesArray, outputsArray);
        fstBuilder.getFstCompiler().getProgram().outputProgramToStream(output); // Closes stream
    }
}
