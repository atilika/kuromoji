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

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class FSTTest {

    @Test
    public void testFST() throws IOException {
        String inputValues[] = {
            "brats", "cat", "dog", "dogs", "rat",
        };

        int outputValues[] = {
            1, 3, 5, 7, 11
        };

        Builder builder = new Builder();
        builder.build(inputValues, outputValues);

        for (int i = 0; i < inputValues.length; i++) {
            assertEquals(outputValues[i], builder.transduce(inputValues[i]));
        }

        Compiler compiledFST = builder.getCompiler();
        FST fst = new FST(compiledFST.getBytes());

        assertEquals(0, fst.lookup("brat")); // Prefix match
        assertEquals(1, fst.lookup("brats"));
        assertEquals(3, fst.lookup("cat"));
        assertEquals(5, fst.lookup("dog"));
        assertEquals(7, fst.lookup("dogs"));
        assertEquals(11, fst.lookup("rat"));
        assertEquals(-1, fst.lookup("rats")); // No match
    }
}
