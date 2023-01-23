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
package com.atilika.kuromoji.compile;

import com.atilika.kuromoji.dict.CharacterDefinitions;
import com.atilika.kuromoji.dict.ConnectionCosts;
import com.atilika.kuromoji.dict.UnknownDictionary;
import com.atilika.kuromoji.fst.FST;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public abstract class DictionaryCompilerBase {

    public void build(String inputDirname, String outputDirname, String encoding, String regexFilter) throws IOException {
        File outputDir = new File(outputDirname);
        outputDir.mkdirs();
        buildTokenInfoDictionary(inputDirname, outputDirname, encoding, regexFilter);
        buildUnknownWordDictionary(inputDirname, outputDirname, encoding);
        buildConnectionCosts(inputDirname, outputDirname);
    }

    private void buildTokenInfoDictionary(String inputDirname, String outputDirname, String encoding, String regexFilter) throws IOException {
        ProgressLog.begin("compiling tokeninfo dict");
        TokenInfoDictionaryCompilerBase tokenInfoCompiler = getTokenInfoDictionaryCompiler(encoding, regexFilter);

        ProgressLog.println("analyzing dictionary features");
        tokenInfoCompiler.analyzeTokenInfo(
            tokenInfoCompiler.combinedSequentialFileInputStream(new File(inputDirname))
        );
        ProgressLog.println("reading tokeninfo");
        tokenInfoCompiler.readTokenInfo(
            tokenInfoCompiler.combinedSequentialFileInputStream(new File(inputDirname))
        );
        tokenInfoCompiler.compile();

        @SuppressWarnings("unchecked")
        List<String> surfaces = tokenInfoCompiler.getSurfaces();

        ProgressLog.begin("compiling fst");

        FSTCompiler fstCompiler = new FSTCompiler(
            new BufferedOutputStream(
                new FileOutputStream(
                    new File(outputDirname, FST.FST_FILENAME)
                )
            ),
            surfaces
        );

        fstCompiler.compile();

        ProgressLog.println("validating saved fst");

        FST fst = new FST(
            new BufferedInputStream(
                new FileInputStream(
                    new File(outputDirname, FST.FST_FILENAME)
                )
            )
        );

        for (String surface : surfaces) {
            if (fst.lookup(surface) < 0) {
                ProgressLog.println("failed to look up [" + surface + "]");
            }
        }

        ProgressLog.end();

        ProgressLog.begin("processing target map");

        for (int i = 0; i < surfaces.size(); i++) {
            int id = fst.lookup(surfaces.get(i));
            assert id > 0;
            tokenInfoCompiler.addMapping(id, i);
        }

        tokenInfoCompiler.write(outputDirname); // TODO: Should be refactored -Christian
        ProgressLog.end();

        ProgressLog.end();
    }

    abstract protected TokenInfoDictionaryCompilerBase getTokenInfoDictionaryCompiler(String encoding, String regexFilter);

    protected void buildUnknownWordDictionary(String inputDirname, String outputDirname, String encoding) throws IOException {
        ProgressLog.begin("compiling unknown word dict");

        CharacterDefinitionsCompiler charDefCompiler = new CharacterDefinitionsCompiler(
            new BufferedOutputStream(
                new FileOutputStream(
                    new File(outputDirname, CharacterDefinitions.CHARACTER_DEFINITIONS_FILENAME)
                )
            )
        );
        charDefCompiler.readCharacterDefinition(
            new BufferedInputStream(
                new FileInputStream(
                    new File(inputDirname, "char.def")
                )
            ),
            encoding
        );
        charDefCompiler.compile();

        UnknownDictionaryCompiler unkDefCompiler = new UnknownDictionaryCompiler(
            charDefCompiler.makeCharacterCategoryMap(),
            new FileOutputStream(
                new File(outputDirname, UnknownDictionary.UNKNOWN_DICTIONARY_FILENAME)
            )
        );

        unkDefCompiler.readUnknownDefinition(
            new BufferedInputStream(
                new FileInputStream(
                    new File(inputDirname, "unk.def")
                )
            ),
            encoding
        );

        unkDefCompiler.compile();

        ProgressLog.end();
    }

    private void buildConnectionCosts(String inputDirname, String outputDirname) throws IOException {
        ProgressLog.begin("compiling connection costs");
        ConnectionCostsCompiler connectionCostsCompiler = new ConnectionCostsCompiler(
            new FileOutputStream(new File(outputDirname, ConnectionCosts.CONNECTION_COSTS_FILENAME))
        );
        connectionCostsCompiler.readCosts(
            new FileInputStream(new File(inputDirname, "matrix.def"))
        );
        connectionCostsCompiler.compile();

        ProgressLog.end();
    }

    protected void build(String[] args) throws IOException {
        String inputDirname = args[0];
        String outputDirname = args[1];
        String inputEncoding = args[2];
        String regexFilter = null;

        if (args.length > 3) {
            regexFilter = args[3];
        }

        ProgressLog.println("dictionary compiler");
        ProgressLog.println("");
        ProgressLog.println("input directory: " + inputDirname);
        ProgressLog.println("output directory: " + outputDirname);
        ProgressLog.println("input encoding: " + inputEncoding);
        ProgressLog.println("regex filter: " + regexFilter);
        ProgressLog.println("");

        build(inputDirname, outputDirname, inputEncoding, regexFilter);
    }
}
