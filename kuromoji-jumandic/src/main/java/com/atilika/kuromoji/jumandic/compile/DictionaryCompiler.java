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

package com.atilika.kuromoji.jumandic.compile;

import com.atilika.kuromoji.compile.TokenInfoDictionaryCompilerBase;
import com.atilika.kuromoji.compile.DictionaryCompilerBase;

import java.io.IOException;

public class DictionaryCompiler extends DictionaryCompilerBase {

    @Override
    protected TokenInfoDictionaryCompilerBase getTokenInfoDictionaryCompiler(String encoding, String regexFilter) {
        return new TokenInfoDictionaryCompiler(encoding, regexFilter);
    }

    public static void main(String[] args) throws IOException {
        DictionaryCompiler dictionaryBuilder = new DictionaryCompiler();
        dictionaryBuilder.build(args);
    }
}
