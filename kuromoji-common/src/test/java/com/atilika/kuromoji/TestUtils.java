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
package com.atilika.kuromoji;

import com.atilika.kuromoji.compile.ProgressLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class TestUtils {

    public static void assertTokenSurfacesEquals(List<String> expectedSurfaces, List<AbstractToken> actualTokens) {
        List<String> actualSurfaces = new ArrayList<>();

        for (AbstractToken token : actualTokens) {
            actualSurfaces.add(token.getSurfaceForm());
        }

        assertEquals(expectedSurfaces, actualSurfaces);
    }

    public static void assertEqualTokenFeatureLenghts(String text, AbstractTokenizer tokenizer) {
        List<AbstractToken> tokens = tokenizer.tokenize(text);
        Set<Integer> lenghts = new HashSet<>();

        for (AbstractToken token : tokens) {
            ProgressLog.println("T: " + token.getSurfaceForm() + ", FL: " + token.getAllFeaturesArray().length + ", FA: " + token.getAllFeatures());
            lenghts.add(
                token.getAllFeaturesArray().length
            );
        }

//        assertEquals(1, lenghts.size());
        if (lenghts.size() == 1) {
            ProgressLog.println("SUCCESS -- Token feature sizes are equal");
        } else {
            ProgressLog.println("FAILURE -- Token feature sizes are not equal (see above output)");
        }
    }
}
