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
package com.atilika.kuromoji.benchmark;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class UserDictionaryConverter {

    private Tokenizer tokenizer = new Tokenizer();

    public void convert(BufferedReader reader, Writer writer) throws IOException {
        String line;

        while ((line = reader.readLine()) != null) {
            String titleText = getTitleText(line);

            List<Token> tokens = tokenizer.tokenize(titleText);

            writer.write(escape(titleText));
            writer.write(",");
            writer.write(escape(titleText));
            writer.write(",");
            writer.write(escape(makeReading(tokens)));
            writer.write(",");
            writer.write("カスタム品詞");
            writer.write("\n");
        }
    }

    private String getTitleText(String line) {
        return EscapeUtils.unescape(line).split("\\t")[1];
    }

    private String makeReading(List<Token> tokens) {
        StringBuilder builder = new StringBuilder();

        for (Token token : tokens) {
            String reading = token.getReading();

            if (reading.equals("*")) {
                builder.append(token.getSurface());
            } else {
                builder.append(reading);
            }
        }

        return builder.toString();
    }

    private String escape(String string) {
        if (string.contains(",")) {
            return "\"" + string + "\"";
        }

        return string;
    }

    public static void main(String[] args) throws IOException {

        String inputFilename = args[0];
        String outputFilename = args[1];

        BufferedReader reader;

        if (inputFilename.endsWith(".gz")) {
            reader = new BufferedReader(
                new InputStreamReader(
                    new GZIPInputStream(
                        new FileInputStream(inputFilename)
                    ),
                    StandardCharsets.UTF_8
                )
            );
        } else {
            reader = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(inputFilename), StandardCharsets.UTF_8
                )
            );
        }

        BufferedWriter writer = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(outputFilename), StandardCharsets.UTF_8)
        );

        UserDictionaryConverter converter = new UserDictionaryConverter();

        converter.convert(reader, writer);

        reader.close();
        writer.close();
    }
}
