package com.atilika.kuromoji.fst;

import org.junit.Ignore;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class FSTUtilTest {

    @Test
    public void testSortInputFile() throws IOException {
        File output = File.createTempFile("fst-", ".input");
        output.deleteOnExit();

        FileWriter fileWriter = new FileWriter(output);
        String a = "a";
        String surrogateOne = "𥝱"; // U+25771
        String b = "b";
        String mo = "ﾓ"; // U+FF93

        fileWriter.write(a + "\n");
        fileWriter.write(surrogateOne + "\n");
        fileWriter.write(b + "\n");
        fileWriter.write(mo + "\n");
        fileWriter.close();

        FSTUtil fstUtil = new FSTUtil();
        fstUtil.sortInputFile(output.getAbsolutePath());

        File sortedOutput = File.createTempFile("fst-", ".sorted");
        sortedOutput.deleteOnExit();
        fstUtil.writeSortedInput(sortedOutput.getAbsolutePath());

        FileReader fileReader = new FileReader(sortedOutput);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        assertEquals(a, bufferedReader.readLine().trim());
        assertEquals(b, bufferedReader.readLine().trim());
        assertEquals(surrogateOne, bufferedReader.readLine().trim()); // because higher surrogate < U+FF93
        assertEquals(mo, bufferedReader.readLine().trim());

    }
}