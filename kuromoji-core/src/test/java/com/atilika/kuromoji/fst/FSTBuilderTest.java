package com.atilika.kuromoji.fst;

import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FSTBuilderTest {

    @Test
    public void testCreateDictionary() throws Exception {
        // referring to https://lucene.apache.org/core/4_3_0/core/org/apache/lucene/util/fst/package-summary.html to make a simple test
        String inputValues[] = {"cat", "cats", "dog", "dogs", "friday", "friend", "pydata"};
        int outputValues[] = {1, 2, 3, 4, 20, 42, 43};


        FSTBuilder fstBuilder = new FSTBuilder();
        fstBuilder.createDictionary(inputValues, outputValues);

        for (int i = 0; i < inputValues.length; i++) {
            assertEquals(outputValues[i], fstBuilder.transduce(inputValues[i]));
        }
    }
}
