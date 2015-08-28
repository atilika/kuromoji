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

import com.atilika.kuromoji.TokenBase;
import com.atilika.kuromoji.TokenizerBase;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.GZIPInputStream;

public class Benchmark {

    private final AtomicLong documents = new AtomicLong(0);

    private final AtomicLong characters = new AtomicLong(0);

    private final AtomicLong tokens = new AtomicLong(0);

    private long startTimeMillis;

    private final TokenizerBase tokenizer;

    private final File inputFile;

    private final File outputFile;

    private final File statisticsFile;

    private final File validationFile;

    private final boolean outputStatistics;

    private final long count;

    private Benchmark(Builder builder) {
        this.tokenizer = builder.tokenizer;
        this.inputFile = builder.inputFile;
        this.outputFile = builder.outputFile;
        this.statisticsFile = builder.statisticsFile;
        this.validationFile = builder.validationFile;
        this.outputStatistics = builder.outputStatistics;
        this.count = builder.count;
    }

    public void benchmark() throws IOException {

        BufferedReader reader;

        if (inputFile.getName().endsWith(".gz")) {
            reader = new BufferedReader(
                new InputStreamReader(
                    new GZIPInputStream(
                        new FileInputStream(inputFile)
                    ),
                    StandardCharsets.UTF_8
                )
            );
        } else {
            reader = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(inputFile), StandardCharsets.UTF_8
                )
            );
        }

        Writer writer;

        if (outputFile == null) {
            writer = new NullWriter();
        } else {
            writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8)
            );
        }

        Writer statisticsWriter;

        if (statisticsFile == null) {
            statisticsWriter = new NullWriter();
        } else {
            statisticsWriter = new FileWriter(statisticsFile);
        }

        tokenize(reader, writer, statisticsWriter);

        reader.close();
        writer.close();
        statisticsWriter.close();
    }

    public void tokenize(BufferedReader reader, Writer writer, Writer statisticsWriter) throws IOException {

        writeStatisticsHeader(statisticsWriter);

        startTimeMillis = System.currentTimeMillis();

        String line;

        while ((line = reader.readLine()) != null) {
            String text = EscapeUtils.unescape(line);

            tokenizeDocument(writer, text);

            if ((documents.get() % 1000) == 0) {
                writeStatistics(statisticsWriter);
            }

            if (0 < count && documents.get() == count) {
                break;
            }
        }
    }

    private void tokenizeDocument(Writer writer, String text) throws IOException {
        List<? extends TokenBase> tokens = tokenizer.tokenize(text);

        updateStatistics(text, tokens);

        for (int i = 0; i < tokens.size(); i++) {
            TokenBase token = tokens.get(i);

            writeToken(writer, token);

            if (i == tokens.size() - 1) {
                writer.write('\n');
            }
        }
    }

    private void updateStatistics(String text, List<? extends TokenBase> tokens) {
        this.documents.incrementAndGet();
        this.characters.getAndAdd(text.length());
        this.tokens.getAndAdd(tokens.size());
    }

    private float getMetricPerSecond(long metric) {
        long durationTimeMillis = System.currentTimeMillis() - startTimeMillis;
        return metric / (durationTimeMillis / 1000);
    }

    private void writeStatisticsHeader(Writer writer) throws IOException {
        String header = format(
            "docs",
            "tokens",
            "chars",
            "docs/s",
            "chars/s",
            "used_mb",
            "free_mb",
            "total_mb",
            "max_mb"
        );

        writeRecord(writer, header);
    }

    private void writeStatistics(Writer writer) throws IOException {
        HeapUtilizationSnapshot heapSnapshot = new HeapUtilizationSnapshot();
        String record = format(
            documents.get(),
            tokens.get(),
            characters.get(),
            getMetricPerSecond(documents.get()),
            getMetricPerSecond(characters.get()),
            heapSnapshot.getUsedMemory(),
            heapSnapshot.getFreeMemory(),
            heapSnapshot.getTotalMemory(),
            heapSnapshot.getMaxMemory()
        );

        writeRecord(writer, record);
    }

    private void writeRecord(Writer writer, String record) throws IOException {
        writer.write(record);
        writer.write('\n');
        writer.flush();

        if (outputStatistics) {
            System.out.println(record);
        }
    }

    private String format(Object... fields) {
        StringBuilder builder = new StringBuilder();
        char separator = '\t';

        for (int i = 0; i < fields.length; i++) {
            String value = fields[i].toString();
            builder.append(value);

            if (i < fields.length - 1) {
                builder.append(separator);
            }
        }
        return builder.toString();
    }

    public void writeToken(Writer writer, TokenBase token) throws IOException {
        writer.write(token.getSurface());
        writer.write('\t');
        writer.write(token.getAllFeatures());
        writer.write('\n');
    }

    public static class Builder {

        private TokenizerBase tokenizer;

        private File inputFile;

        private File outputFile;

        private File statisticsFile;

        private File validationFile;

        private File userDictionaryFile;

        private boolean outputStatistics;

        private long count = 0;

        public Builder tokenizer(TokenizerBase tokenizer) {
            this.tokenizer = tokenizer;
            return this;
        }

        public Builder setOutputStatistiscs(boolean outputStatistiscs) {
            this.outputStatistics = outputStatistiscs;
            return this;
        }

        public Builder outputStatisticsFile(File file) {
            this.statisticsFile = file;
            return this;
        }

        public Builder userDictionaryFile(File file) {
            this.userDictionaryFile = file;
            return this;
        }

        public Builder inputFile(File file) {
            this.inputFile = file;
            return this;
        }

        public Builder outputFile(File file) {
            this.outputFile = file;
            return this;
        }

        public Builder validationFile(File file) {
            this.validationFile = file;
            return this;
        }

        public Builder count(long count) {
            this.count = count;
            return this;
        }

        public Benchmark build() {
            return new Benchmark(this);
        }
    }

    public static void main(String[] args) throws IOException {
        Options options = new Options();
        options.addOption("h", "help", false, "Display this help message and exit");
        options.addOption("t", "tokenizer", true, "Tokenizer class to use");
        options.addOption("u", "user-dictionary", true, "Optional user dictionary filename to use");
        options.addOption("c", "count", true, "Number of documents ot process (Default: 0, which means all");
//        options.addOption("v", "validation-input", true, "Validation filename");
        options.addOption("o", "output", true, "Output filename.  If unset, segmentation is done, but the result is discarded");
        options.addOption(null, "benchmark-output", true, "Benchmark metrics output filename filename");

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(options, args);

            args = commandLine.getArgs();

            if (args.length != 1) {
                throw new ParseException("A single input filename is required");
            }
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            usage(options);
        }

        String inputFilename = args[0];

        String className = commandLine.getOptionValue("t", "com.atilika.kuromoji.ipadic.Tokenizer");

        className += "$Builder";

        String userDictionaryFilename = commandLine.getOptionValue("u");

        TokenizerBase tokenizer = null;
        try {
            Class clazz = Class.forName(className);

            // Make builder
            Object builder = clazz.getDeclaredConstructor(null)
                .newInstance();

            // Set user dictionary
            if (userDictionaryFilename != null) {
                builder.getClass()
                    .getMethod("userDictionary", String.class)
                    .invoke(builder, userDictionaryFilename);
            }

            // Build tokenizer
            tokenizer = (TokenizerBase) builder.getClass()
                .getMethod("build")
                .invoke(builder);

        } catch (Exception e) {
            System.err.println("Could not create tokenizer. Got " + e);
            e.printStackTrace();
            System.exit(1);
        }

        File outputFile = null;
        String outputFilename = commandLine.getOptionValue("o");

        if (outputFilename != null) {
            outputFile = new File(outputFilename);
        }

        File statisticsFile = null;
        String statisticsFilename = commandLine.getOptionValue("benchmark-output");

        if (statisticsFilename != null) {
            statisticsFile = new File(statisticsFilename);
        }

        long count = Long.parseLong(
            commandLine.getOptionValue("c", "0")
        );

        Benchmark benchmark = new Builder()
            .tokenizer(tokenizer)
            .inputFile(new File(inputFilename))
            .outputFile(outputFile)
            .outputStatisticsFile(statisticsFile)
            .setOutputStatistiscs(true)
            .count(count)
            .build();

        benchmark.benchmark();
    }

    public static void usage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("benchmark [options] inputfilename", "", options, "");
        System.exit(1);
    }
}
