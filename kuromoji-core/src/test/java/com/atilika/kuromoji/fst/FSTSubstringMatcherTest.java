package com.atilika.kuromoji.fst;

import com.atilika.kuromoji.fst.vm.Program;
import com.atilika.kuromoji.fst.vm.VirtualMachine;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FSTSubstringMatcherTest {

    @Ignore("Needs to support prefix matching")
    @Test
    public void testExtractTwoTokens() throws Exception {
        String sampleSentence = "寿司が食べたい"; // "I want to eat sushi." in Japanese

        String[] tokens = {"寿司", "食べ"};
        int[] outputValues = {1, 2};

        FSTBuilder fstBuilder = new FSTBuilder();
        fstBuilder.createDictionary(tokens, outputValues);

        VirtualMachine vm = new VirtualMachine();
        Program program = fstBuilder.getFstCompiler().getProgram();

        FSTSubstringMatcher fstSubstringMatcher = new FSTSubstringMatcher();
        List extractedTokens = fstSubstringMatcher.matchAllSubstrings(sampleSentence, vm, program);

        assertEquals(Arrays.asList(tokens), extractedTokens);
    }

    @Ignore("Needs to support prefix matching")
    @Test
    public void testExtractLongSentence() throws Exception {
        String sampleSentence = "寿司の、寿司による、寿司のための寿司。";

        String[] tokens = {"寿司", "食べ"};
        int[] outputValues = {1, 2};

        FSTBuilder fstBuilder = new FSTBuilder();
        fstBuilder.createDictionary(tokens, outputValues);

        VirtualMachine vm = new VirtualMachine();
        Program program = fstBuilder.getFstCompiler().getProgram();

        FSTSubstringMatcher fstSubstringMatcher = new FSTSubstringMatcher();
        List extractedTokens = fstSubstringMatcher.matchAllSubstrings(sampleSentence, vm, program);
        String[] expectedTokens = {"寿司", "寿司", "寿司", "寿司"};
        assertEquals(Arrays.asList(expectedTokens), extractedTokens);
    }
}
