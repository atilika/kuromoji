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
package com.atilika.kuromoji.ipadic;

import com.carrotsearch.randomizedtesting.RandomizedTest;
import com.carrotsearch.randomizedtesting.annotations.Repeat;
import org.junit.Test;

import java.util.Random;

import static com.atilika.kuromoji.TestUtils.assertCanTokenizeString;
import static com.atilika.kuromoji.TestUtils.assertCanMultiTokenizeString;

public class RandomizedInputTest extends RandomizedTest {

    private static final int LENGTH = 512;

    private Tokenizer tokenizer = new Tokenizer();

    @Test
    @Repeat(iterations = 10)
    public void testRandomizedUnicodeInput() {
        assertCanTokenizeString(randomUnicodeOfLength(LENGTH), tokenizer);
    }

    @Test
    @Repeat(iterations = 10)
    public void testRandomizedRealisticUnicodeInput() {
        assertCanTokenizeString(randomRealisticUnicodeOfLength(LENGTH), tokenizer);
    }

    @Test
    @Repeat(iterations = 10)
    public void testRandomizedAsciiInput() {
        assertCanTokenizeString(randomAsciiOfLength(LENGTH), tokenizer);
    }

    @Test
    @Repeat(iterations = 10)
    public void testRandomizedUnicodeInputMultiTokenize() {
        Random rand = new Random();
        assertCanMultiTokenizeString(randomUnicodeOfLength(LENGTH), rand.nextInt(998) + 2, rand.nextInt(100000), tokenizer);
    }

    @Test
    @Repeat(iterations = 10)
    public void testRandomizedRealisticUnicodeInputMultiTokenize() {
        Random rand = new Random();
        assertCanMultiTokenizeString(randomRealisticUnicodeOfLength(LENGTH), rand.nextInt(998) + 2, rand.nextInt(100000), tokenizer);
    }

    @Test
    @Repeat(iterations = 10)
    public void testRandomizedAsciiInputMultiTokenize() {
        Random rand = new Random();
        assertCanMultiTokenizeString(randomAsciiOfLength(LENGTH), rand.nextInt(998) + 2, rand.nextInt(100000), tokenizer);
    }
}
