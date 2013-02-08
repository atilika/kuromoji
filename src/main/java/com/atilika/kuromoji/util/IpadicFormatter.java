package com.atilika.kuromoji.util;

public class IpadicFormatter implements Formatter{

    public IpadicFormatter() {
    }/*
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
        return features;
//        if (tokenInfoDictionaryBuilder.getFormat() == DictionaryFormat.IPADIC) {
//            return features;
//        } else {
//            return tokenInfoDictionaryBuilder.formatUniDicEntry(features);
//        }
    }
}