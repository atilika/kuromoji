package com.atilika.kuromoji.fst;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class FSTTest {

    private static FST fst;

    @BeforeClass
    public static void setUp() throws IOException {
        File output = File.createTempFile("fst-", ".fst");
        output.deleteOnExit();

        String[] keys = new String[]{"cat", "cats", "dog"};
        int[] values = new int[]{1, 2, 4};

        FSTBuilder builder = new FSTBuilder();

        builder.createDictionary(keys, values);
        builder.getFstCompiler().getProgram().outputProgramToStream(
            new FileOutputStream(output)
        );

        fst = new FST(new FileInputStream(output));
    }

    @Test
    public void testFST() throws IOException {
        assertEquals(4, fst.lookup("dog"));
        assertEquals(1, fst.lookup("cat"));
        assertEquals(2, fst.lookup("cats"));
    }

    @Test
    public void testMiss() throws Exception {
        assertEquals(-1, fst.lookup("mouse"));
    }
}
