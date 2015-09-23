package com.atilika.kuromoji.fst;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FSTUtil {

    List<String> inputStrings = new ArrayList<>();

    /**
     * Sort the input into Java-lexical order
     *
     * @param inputStream
     * @throws IOException
     */
    public void sortInput(InputStream inputStream) throws IOException {

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        String line;
        while ((line = reader.readLine()) != null) {

            if (line.trim().length() == 0) {
                continue;
            }
            inputStrings.add(line.trim());
        }
        reader.close();

        Collections.sort(inputStrings);

    }

    public void sortInputFile(String filename) throws IOException {
        sortInput(new FileInputStream(filename));
    }

    /**
     * Writing input that is sorted in Java-lexical order
     *
     * @param output
     * @throws IOException
     */
    private void writeSortedInput(OutputStream output) throws IOException {

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(output);
        BufferedWriter writer = new BufferedWriter(outputStreamWriter);

        writeSortedInput(writer);

    }

    public void writeSortedInput(Writer writer) throws IOException {
        for (String inputString : inputStrings) {
            writer.write(inputString + "\n");
        }
        writer.close();

    }

    public void writeSortedInput(String filename) throws IOException {
        writeSortedInput(new FileOutputStream(filename));
    }

}
