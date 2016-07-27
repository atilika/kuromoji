package com.atilika.kuromoji.multisearch;

import com.atilika.kuromoji.viterbi.MultiSearchMerger;
import com.atilika.kuromoji.viterbi.MultiSearchResult;
import com.atilika.kuromoji.viterbi.ViterbiNode;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class MultiSearchMergerTest {

    @Test
    public void testMerger() {
        int maxCount = 3;
        int costSlack = 8;
        MultiSearchMerger merger = new MultiSearchMerger(maxCount, costSlack);
        List<MultiSearchResult> results = new ArrayList<>();

        String[][] surfaces1 = {{"a", "b"}, {"c", "d"}, {"e", "f"}};
        int[] costs1 = {1, 2, 3};
        results.add(makeResult(surfaces1, costs1));

        String[][] surfaces2 = {{"a", "b"}, {"c", "d"}};
        int[] costs2 = {1, 2};
        results.add(makeResult(surfaces2, costs2));

        MultiSearchResult mergedResult = merger.merge(results);
        assertEquals(3, mergedResult.size());
        assertEquals(2, mergedResult.getCost(0));
        assertEquals(3, mergedResult.getCost(1));
        assertEquals(3, mergedResult.getCost(2));
        assertEquals("a b a b", getSpaceSeparatedTokens(mergedResult.getTokenizedResult(0)));
        assertEquals("c d a b", getSpaceSeparatedTokens(mergedResult.getTokenizedResult(1)));
        assertEquals("a b c d", getSpaceSeparatedTokens(mergedResult.getTokenizedResult(2)));
    }

    @Test
    public void testMergerTooFew() {
        int maxCount = 5;
        int costSlack = 3;
        MultiSearchMerger merger = new MultiSearchMerger(maxCount, costSlack);
        List<MultiSearchResult> results = new ArrayList<>();

        String[][] surfaces1 = {{"a", "b"}, {"c", "d"}, {"e", "f"}};
        int[] costs1 = {1, 2, 5};
        results.add(makeResult(surfaces1, costs1));

        String[][] surfaces2 = {{"a", "b"}, {"c", "d"}};
        int[] costs2 = {1, 2};
        results.add(makeResult(surfaces2, costs2));

        String[][] surfaces3 = {{"a", "b"}};
        int[] costs3 = {5};
        results.add(makeResult(surfaces3, costs3));

        MultiSearchResult mergedResult = merger.merge(results);
        assertEquals(4, mergedResult.size());
        assertEquals(7, mergedResult.getCost(0));
        assertEquals(8, mergedResult.getCost(1));
        assertEquals(8, mergedResult.getCost(2));
        assertEquals(9, mergedResult.getCost(3));
        assertEquals("a b a b a b", getSpaceSeparatedTokens(mergedResult.getTokenizedResult(0)));
        assertEquals("c d a b a b", getSpaceSeparatedTokens(mergedResult.getTokenizedResult(1)));
        assertEquals("a b c d a b", getSpaceSeparatedTokens(mergedResult.getTokenizedResult(2)));
        assertEquals("c d c d a b", getSpaceSeparatedTokens(mergedResult.getTokenizedResult(3)));
    }

    private MultiSearchResult makeResult(String[][] surfaces, int[] cost) {
        MultiSearchResult ret = new MultiSearchResult();
        for (int i = 0; i < surfaces.length; i++) {
            ret.add(makeNodes(surfaces[i]), cost[i]);
        }
        return ret;
    }

    private List<ViterbiNode> makeNodes(String[] surfaces) {
        List<ViterbiNode> ret = new ArrayList<>();
        for (String s : surfaces) {
            ret.add(new ViterbiNode(0, s, 0, 0, 0, 0, ViterbiNode.Type.KNOWN));
        }
        return ret;
    }

    private String getSpaceSeparatedTokens(List<ViterbiNode> nodes) {
        if (nodes.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(nodes.get(0).getSurface());
        for (int i = 1; i < nodes.size(); i++) {
            sb.append(" ");
            sb.append(nodes.get(i).getSurface());
        }
        return sb.toString();
    }
}
