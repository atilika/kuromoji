package com.atilika.kuromoji.fst;

import com.atilika.kuromoji.fst.vm.Program;
import com.atilika.kuromoji.fst.vm.VirtualMachine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;

public class FSTTestHelper {

    public Program getProgram(String[] inputValues, int[] outputValues) {
        FSTBuilder fstBuilder = new FSTBuilder();
        fstBuilder.createDictionary(inputValues, outputValues);

        for (int i = 0; i < inputValues.length; i++) {
            assertEquals(outputValues[i], fstBuilder.transduce(inputValues[i]));
        }

        // Test whether the program is correctly made.
        return fstBuilder.getFstCompiler().getProgram();
    }

    public FSTBuilder readIncremental(InputStream is) throws IOException {

        FSTBuilder fstBuilder = new FSTBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        fstBuilder.createDictionaryIncremental(reader);

        return fstBuilder;
    }

    public FSTBuilder readIncremental(String resource) throws IOException {
        return readIncremental(getResource(resource));
    }

    public void checkOutputWordByWord(String resource, Program program, VirtualMachine vm) throws IOException {
        int wordIDExpected = 1;
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(getResource(resource), "UTF-8"));
        String line;
        while ((line = reader.readLine()) != null) {
            // Remove comments
            line = line.replaceAll("#.*$", "");

            // Skip empty lines or comment lines
            if (line.trim().length() == 0) {
                continue;
            }
            int wordID = vm.run(program, line);
            assertEquals(wordIDExpected, wordID);
            wordIDExpected++;
        }
        reader.close();
    }

    private InputStream getResource(String s) {
        return this.getClass().getClassLoader().getResourceAsStream(s);
    }

}
