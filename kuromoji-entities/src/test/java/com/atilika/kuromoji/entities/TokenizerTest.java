/**
 * Copyright 2010-2013 Atilika Inc. and contributors (see CONTRIBUTORS.md)
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

package com.atilika.kuromoji.entities;

import com.atilika.kuromoji.Token;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TokenizerTest {

    @Test
    public void testRomajiSegmentation() {
        Tokenizer tokenizer = new Tokenizer.Builder().build();
        List<Token> tokens = tokenizer.tokenize("1234");

        // If any of the assertions below fail, the dictionary filter wasn't applies correctly
        assertEquals(1, tokens.size());

        Token token = tokens.get(0);

        assertEquals("1234", token.getSurfaceForm());
        assertTrue(token.isUnknown());
    }
}
