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
package com.atilika.kuromoji;

import com.atilika.kuromoji.AbstractTokenizer.Mode;
import com.atilika.kuromoji.dict.DynamicDictionaries;
import com.atilika.kuromoji.dict.UserDictionary;
import com.atilika.kuromoji.viterbi.ViterbiBuilder;
import com.atilika.kuromoji.viterbi.ViterbiFormatter;
import com.atilika.kuromoji.viterbi.ViterbiLattice;
import com.atilika.kuromoji.viterbi.ViterbiNode;
import com.atilika.kuromoji.viterbi.ViterbiSearcher;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DebugTokenizer {

    private ViterbiFormatter formatter;
    private ViterbiBuilder viterbiBuilder;
    private ViterbiSearcher viterbiSearcher;

    protected DebugTokenizer(DynamicDictionaries dictionaries, UserDictionary userDictionary, Mode mode) {

        this.viterbiBuilder = new ViterbiBuilder(dictionaries.getTrie(),
            dictionaries.getDictionary(),
            dictionaries.getUnknownDictionary(),
            userDictionary,
            mode);

        this.viterbiSearcher = new ViterbiSearcher(this.viterbiBuilder, mode, dictionaries.getCosts(), dictionaries.getUnknownDictionary());
        this.formatter = new ViterbiFormatter(dictionaries.getCosts());
    }

    public String debugTokenize(String text) {
        ViterbiLattice lattice = this.viterbiBuilder.build(text);
        List<ViterbiNode> bestPath = this.viterbiSearcher.search(lattice);
        return this.formatter.format(lattice, bestPath);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Mode mode = Mode.NORMAL;

        private UserDictionary userDictionary = null;

        private DynamicDictionaries dictionaries = null;
        /**
         * The default resource prefix, also configurable via
         * system property <code>com.atilika.kuromoji.dict.targetdir</code>.
         */
        private String defaultPrefix = System.getProperty(
            AbstractTokenizer.DEFAULT_DICT_PREFIX_PROPERTY,
            AbstractTokenizer.DEFAULT_DICT_PREFIX);

        /**
         * The default resource resolver (relative to this class).
         */
        private ResourceResolver resolver = new ClassLoaderResolver(this.getClass());

        public synchronized Builder mode(Mode mode) {
            this.mode = mode;
            return this;
        }

        public synchronized Builder userDictionary(InputStream userDictionaryInputStream) throws IOException {
            this.userDictionary = UserDictionary.read(userDictionaryInputStream);
            return this;
        }

        public synchronized Builder userDictionary(String userDictionaryPath) throws IOException {
            this.userDictionary(new BufferedInputStream(new FileInputStream(userDictionaryPath)));
            return this;
        }

        public synchronized DebugTokenizer build() {

            if (dictionaries == null) {
                if (defaultPrefix != null) {
                    resolver = new PrefixDecoratorResolver(defaultPrefix, resolver);
                }

                dictionaries = new DynamicDictionaries(resolver);
            }
            return new DebugTokenizer(dictionaries, userDictionary, mode);
        }
    }
}
