package com.atilika.kuromoji.fst;

import org.junit.BeforeClass;
import org.junit.Ignore;
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

    @Ignore("Replace this with prefix match tests")
    @Test
    public void testIPADIC() throws IOException {
        FST ipadic = new FST(new FileInputStream(new File("./kuromoji-ipadic/src/main/resources/com/atilika/kuromoji/ipadic/fst.bin")));

        debugLookup(ipadic, "馬鹿馬鹿しい");
        debugLookup(ipadic, "ア");
        debugLookup(ipadic, "アテ");
        debugLookup(ipadic, "アティ");
        debugLookup(ipadic, "株");
        debugLookup(ipadic, "株式");
        debugLookup(ipadic, "会社");
        debugLookup(ipadic, "株式会");
        debugLookup(ipadic, "株式会社");
        debugLookup(ipadic, "ステーシ");
        debugLookup(ipadic, "ステーション");
    }

    public void debugLookup(FST fst, String key) {
        int value = fst.lookup(key);
        System.out.println("\t" + key + " -> " + value);
    }
}
