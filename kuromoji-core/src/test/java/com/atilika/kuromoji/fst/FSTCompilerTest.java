package com.atilika.kuromoji.fst;

import com.atilika.kuromoji.fst.vm.Instruction;
import com.atilika.kuromoji.fst.vm.Program;
import com.atilika.kuromoji.fst.vm.VirtualMachine;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FSTCompilerTest {

    @Test
    public void testCreateDictionaryWithFSTCompiler() throws Exception {
        // referring to https://lucene.apache.org/core/4_3_0/core/org/apache/lucene/util/fst/package-summary.html to make a simple test
        String inputValues[] = {"cat", "cats", "dog", "dogs", "friday", "friend", "pydata"};
        int outputValues[] = {1, 2, 3, 4, 20, 42, 43};

        VirtualMachine vm = new VirtualMachine();
        Program program = new FSTTestHelper().getProgram(inputValues, outputValues);

        List<Instruction> instructionsForDebug = program.dumpInstructions();
        for (int i = 0; i < inputValues.length; i++) {
            assertEquals(outputValues[i], vm.run(program, inputValues[i]));
        }

        assertEquals(-1, vm.run(program, "thursday"));
    }

    @Test
    public void testSuffixStatesMerged() throws Exception {
        String inputValues[] = {"cat", "cats", "dog", "dogs"};
        int outputValues[] = {1, 2, 3, 4};
        FSTBuilder fstBuilder = new FSTBuilder();
        fstBuilder.createDictionary(inputValues, outputValues);

        Program program = fstBuilder.getFstCompiler().getProgram();
        List<Instruction> instructionsForDebug = program.dumpInstructions();

//      There should be only one instruction with the label 's' and the output 1
        List<Instruction> instructionsToSameState = new ArrayList<>();

        int numInstructionWithTransitionCharS = 0;
        for (Instruction instruction : instructionsForDebug) {
            if (instruction.getArg1() == 's') {
                numInstructionWithTransitionCharS++;
            }
            if (instruction.getArg1() == 'g' || instruction.getArg1() == 't') {
                instructionsToSameState.add(instruction);
            }
        }
        assertEquals(1, numInstructionWithTransitionCharS); // 1, since states are equivalent.
        // pointing to the same Instruction address
        assertEquals(instructionsToSameState.get(0).getArg2(), instructionsToSameState.get(1).getArg2());
    }

    @Test
    public void testJapaneseBasics() throws Exception {
        String inputValues[] = {"すし", "すめし", "さしみ", "寿司", "寿", "さんま", "さかな"};
        int outputValues[] = {1, 2, 3, 4, 20, 42, 43};

        List<String> inputs = Arrays.asList(inputValues);
        Collections.sort(inputs);

        String sortedInput[] = new String[inputs.size()];
        for (int i = 0; i < inputs.size(); i++) {
            sortedInput[i] = inputs.get(i);
        }

        VirtualMachine vm = new VirtualMachine();
        Program program = new FSTTestHelper().getProgram(sortedInput, outputValues);
        List<Instruction> instructionsDebug = program.dumpInstructions();
        for (int i = 0; i < sortedInput.length; i++) {
            assertEquals(outputValues[i], vm.run(program, sortedInput[i]));
        }
        assertEquals(-1, vm.run(program, "まぐろ"));
    }

    @Test
    public void testKotobuki() throws Exception {
        String inputValues[] = {"さかな", "寿", "寿司"};
        int outputValues[] = {0, 1, 2};

        VirtualMachine vm = new VirtualMachine();
        Program program = new FSTTestHelper().getProgram(inputValues, outputValues);

        List<Instruction> instructionsDebug = program.dumpInstructions();

        for (int i = 0; i < inputValues.length; i++) {
            assertEquals(outputValues[i], vm.run(program, inputValues[i]));
        }
        assertEquals(-1, vm.run(program, "まぐろ"));
        assertEquals(-1, vm.run(program, "寿司が食べたい"));
        assertEquals(-1, vm.run(program, "寿司が"));
    }

    @Test
    public void testDescendingOutputs() throws Exception {
        String inputValues[] = {"さかな", "寿", "寿司"};
        int outputValues[] = {2, 1, 0};

        // Test whether the program is correctly made.
        VirtualMachine vm = new VirtualMachine();
        Program program = new FSTTestHelper().getProgram(inputValues, outputValues);

        List<Instruction> instructionsDebug = program.dumpInstructions();

        for (int i = 0; i < inputValues.length; i++) {
            assertEquals(outputValues[i], vm.run(program, inputValues[i]));
        }
        assertEquals(-1, vm.run(program, "まぐろ"));
        assertEquals(-1, vm.run(program, "寿司が食べたい"));
        assertEquals(-1, vm.run(program, "寿司が"));
    }

    @Test
    public void testWordsWithWhiteSpace() throws Exception {
        String inputValues[] = {"!", "!!!"};
        int outputValues[] = {0, 1};

        VirtualMachine vm = new VirtualMachine();
        Program program = new FSTTestHelper().getProgram(inputValues, outputValues);

        for (int i = 0; i < inputValues.length; i++) {
            assertEquals(outputValues[i], vm.run(program, inputValues[i]));
        }
        assertEquals(-1, vm.run(program, "まぐろ"));

    }
}
