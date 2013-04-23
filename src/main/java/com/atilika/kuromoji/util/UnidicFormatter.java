package com.atilika.kuromoji.util;

public class UnidicFormatter implements Formatter {
    public UnidicFormatter() {
    }

    /*
      * IPADIC features
      *
      * 0	- surface
      * 1	- left cost
      * 2	- right cost
      * 3	- word cost
      * 4-9	- pos
      * 10	- base form
      * 11	- reading
      * 12	- pronounciation
      *
      * UniDic features
      *
      * 0	- surface
      * 1	- left cost
      * 2	- right cost
      * 3	- word cost
      * 4-9	- pos
      * 10	- base form reading
      * 11	- base form
      * 12	- surface form
      * 13	- surface reading
      */

    public String[] formatEntry(String[] features) {
        String[] uniDicFeatures = new String[features.length];

        for (int i = 0; i < 9; i++) {
            uniDicFeatures[i] = features[i];
        }

        uniDicFeatures[10] = features[11];

        // If the surface reading is non-existent, use surface form for reading and pronunciation.
        // This happens with punctuation in UniDic and there are possibly other cases as well
        if (features[13].length() == 0) {
            uniDicFeatures[11] = features[0];
            uniDicFeatures[12] = features[0];
        } else {
            uniDicFeatures[11] = features[13];
            uniDicFeatures[12] = features[13];
        }

        for (int i = 13; i < features.length; i++) {
            uniDicFeatures[i] = features[i];
        }

        return uniDicFeatures;
    }
}