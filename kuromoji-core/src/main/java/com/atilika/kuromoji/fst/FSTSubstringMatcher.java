package com.atilika.kuromoji.fst;

import com.atilika.kuromoji.fst.vm.Program;
import com.atilika.kuromoji.fst.vm.VirtualMachine;

import java.util.ArrayList;
import java.util.List;

public class FSTSubstringMatcher {
    private int numLookups;

    FSTSubstringMatcher() {
        this.numLookups = 0;
    }

    /**
     * Matching all possible substrings
     *
     * @param vm
     * @param program
     * @return List of extracted tokens that are in a dictionary
     */
    public List matchAllSubstrings(String sentence, VirtualMachine vm, Program program) {
        List extractedTokens = new ArrayList();

        int hits = 0;
        int misses = 0;

        for (int i = 0; i < sentence.length(); i++) {
            for (int j = i + 1; j < sentence.length(); j++) {
                if (vm.run(program, sentence.substring(i, j)) != -1) {
//                    System.out.println(sentence.substring(i, j));
                    extractedTokens.add(sentence.substring(i, j));
                    hits++;
                } else {
                    misses++;
                }
            }
        }

        if (hits > 0 || misses > 0) {
            System.out.println("Hits: " + hits + ", misses: " + misses + ", ratio: " + (hits / (1.0 * hits + misses)));
        }
        this.numLookups += hits + misses;

        return extractedTokens;
    }


    public void lookupSentences(VirtualMachine vm, Program program, List<String> sentences) {
        List<String> tokens = new ArrayList<>();
        for (String sentence : sentences) {
            List extractedTokens = matchAllSubstrings(sentence, vm, program);
            tokens.addAll(extractedTokens);
        }
    }

    public int getNumLookups() {
        return this.numLookups;
    }

}
