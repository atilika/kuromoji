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

public class BitsFormatterTest {

    BitsFormatter formatter = new BitsFormatter();

    @Test
    public void testFormatCompiled() throws IOException {
        String inputValues[] = {"cat", "cats", "rats"};
        int outputValues[] = {10, 20, 30};

        Builder builder = new Builder();
        builder.build(inputValues, outputValues);

        Compiler compiledFST = builder.getCompiler();

        assertEquals("" +
            "  50: MATCH\n" +
            "  47:\tr -> 30\t(JMP: 39)\n" +
            "  43:\tc -> 10\t(JMP: 21)\n" +
            "  39: MATCH\n" +
            "  36:\ta -> 0\t(JMP: 33)\n" +
            "  33: MATCH\n" +
            "  30:\tt -> 0\t(JMP: 27)\n" +
            "  27: MATCH\n" +
            "  24:\ts -> 0\t(JMP: 2)\n" +
            "  21: MATCH\n" +
            "  18:\ta -> 0\t(JMP: 15)\n" +
            "  15: MATCH\n" +
            "  12:\tt -> 0\t(JMP: 9)\n" +
            "   9: ACCEPT\n" +
            "   6:\ts -> 10\t(JMP: 2)\n" +
            "   2: ACCEPT\n",
            formatter.format(compiledFST.getBytes())
        );
    }
}
