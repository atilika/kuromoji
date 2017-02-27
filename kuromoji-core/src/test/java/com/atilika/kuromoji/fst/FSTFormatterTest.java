/**
 * Copyright © 2010-2017 Atilika Inc. and contributors (see CONTRIBUTORS.md)
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

import org.junit.Ignore;
import org.junit.Test;

public class FSTFormatterTest {

    @Ignore
    @Test
    public void testFormat() throws Exception {
        String inputValues[] = {"cat", "cats", "dog", "dogs", "friday", "friend", "padata"};
        int outputValues[] = {0, 1, 2, 3, 4, 20, 42};

//        String inputValues[] = {"さかな", "寿", "寿司"};
//        int outputValues[] = {0, 1, 2};

        Builder builder = new Builder();
        builder.build(inputValues, outputValues);

        FSTFormatter fstFormatter = new FSTFormatter();
        fstFormatter.format(builder, "LinearSearchFiniteStateTransducerOutput.txt");
//        fstFormatter.format(builder, "FSTsimpleDescendingOutput.txt");
    }
}
