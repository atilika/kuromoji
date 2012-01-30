README.txt
==========

This file describes how to build and run an example tokenizer in various modes.

1.  Build example program

    javac -cp lib/kuromoji-0.7.7.jar \
              src/main/java/org/atilika/kuromoji/example/TokenizerExample.java
            
2a. Run example program with UTF-8 file encoding

    java -Dfile.encoding=UTF-8 \
         -cp lib/kuromoji-0.7.7.jar:src/main/java \
             org.atilika.kuromoji.example.TokenizerExample

  Sample input 1: お寿司が美味しいです。
  Sample input 2: かにみそがおいしいです。
  Sample input 3: 毎日日本経済新聞を読みます。  

2b. Run example program with segmentation useful for search
  
    java -Dfile.encoding=UTF-8 \
       -cp lib/kuromoji-0.7.7.jar:src/main/java \
           org.atilika.kuromoji.example.TokenizerExample search

  Sample input 1: 毎日日本経済新聞を読みます。
  - You will see that 日本経済新聞 gets segmented as 日本 経済 新聞
  
  Sample input 2: 関西国際空港 (Kansai International Airport)
  - You will see that 関西国際空港 gets segmented as 関西 国際 空港

2c. Run example tokenizer with user dictionary (non search mode)

    java -Dfile.encoding=UTF-8 \
       -cp lib/kuromoji-0.7.7.jar:src/main/java \
           org.atilika.kuromoji.example.TokenizerExample normal \
              src/main/resources/userdict.txt

    Sample input 1: 毎日日本経済新聞を読みます。
    - Segmentation is identical to 2b because of a user dictionary entry
    
    Sample input 2: 時々銀座に朝青龍と飲みに行きます。
    - You should see custom reading アサショウリュウ for 朝青龍
