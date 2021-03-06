# Kuromoji Japanese Morphological Analyzer

Copyright 2010-2018 Atilika Inc. and contributors (see `CONTRIBUTORS.md`)

This software also includes a binary and/or source version of data from
`mecab-ipadic-2.7.0-20070801` and `mecab-ipadic-2.7.0-20070801-neologd-20171113`.

`mecab-ipadic-2.7.0-20070801` can be obtained from

- http://atilika.com/releases/mecab-ipadic/mecab-ipadic-2.7.0-20070801.tar.gz
- http://jaist.dl.sourceforge.net/project/mecab/mecab-ipadic/2.7.0-20070801/mecab-ipadic-2.7.0-20070801.tar.gz

and `mecab-ipadic-2.7.0-20070801-neologd-20171113` can be obtained from

- http://atilika.com/releases/mecab-ipadic-neologd/mecab-ipadic-2.7.0-20070801-neologd-20171113.tar.gz

The contents of `mecab-ipadic-2.7.0-20070801-neologd-20171113.tar.gz` also
contains `mecab-ipadic-2.7.0-20070801.tar.gz`. (The former file an archive
of mecab-ipadic-neologd's build directory with binary files removed.)

## mecab-ipadic-2.7.0-20070801 Notice

    Nara Institute of Science and Technology (NAIST),
    the copyright holders, disclaims all warranties with regard to this
    software, including all implied warranties of merchantability and
    fitness, in no event shall NAIST be liable for
    any special, indirect or consequential damages or any damages
    whatsoever resulting from loss of use, data or profits, whether in an
    action of contract, negligence or other tortuous action, arising out
    of or in connection with the use or performance of this software.

    A large portion of the dictionary entries
    originate from ICOT Free Software.  The following conditions for ICOT
    Free Software applies to the current dictionary as well.

    Each User may also freely distribute the Program, whether in its
    original form or modified, to any third party or parties, PROVIDED
    that the provisions of Section 3 ("NO WARRANTY") will ALWAYS appear
    on, or be attached to, the Program, which is distributed substantially
    in the same form as set out herein and that such intended
    distribution, if actually made, will neither violate or otherwise
    contravene any of the laws and regulations of the countries having
    jurisdiction over the User or the intended distribution itself.

    NO WARRANTY

    The program was produced on an experimental basis in the course of the
    research and development conducted during the project and is provided
    to users as so produced on an experimental basis.  Accordingly, the
    program is provided without any warranty whatsoever, whether express,
    implied, statutory or otherwise.  The term "warranty" used herein
    includes, but is not limited to, any warranty of the quality,
    performance, merchantability and fitness for a particular purpose of
    the program and the nonexistence of any infringement or violation of
    any right of any third party.

    Each user of the program will agree and understand, and be deemed to
    have agreed and understood, that there is no warranty whatsoever for
    the program and, accordingly, the entire risk arising from or
    otherwise connected with the program is assumed by the user.

    Therefore, neither ICOT, the copyright holder, or any other
    organization that participated in or was otherwise related to the
    development of the program and their respective officials, directors,
    officers and other employees shall be held liable for any and all
    damages, including, without limitation, general, special, incidental
    and consequential damages, arising out of or otherwise in connection
    with the use or inability to use the program or any product, material
    or result produced or otherwise obtained by using the program,
    regardless of whether they have been advised of, or otherwise had
    knowledge of, the possibility of such damages at any time during the
    project or thereafter.  Each user will be deemed to have agreed to the
    foregoing by his or her commencement of use of the program.  The term
    "use" as used herein includes, but is not limited to, the use,
    modification, copying and distribution of the program and the
    production of secondary products from the program.

    In the case where the program, whether in its original form or
    modified, was distributed or delivered to or received by a user from
    any person, organization or entity other than ICOT, unless it makes or
    grants independently of ICOT any specific warranty to the user in
    writing, such person, organization or entity, will also be exempted
    from and not be held liable to the user for any such damages as noted
    above as far as the program is concerned.

## mecab-ipadic-2.7.0-20070801-neologd-20171113 Notice

    Copyright (C) 2015-2017 Toshinori Sato (@overlast)
    
          https://github.com/neologd/mecab-ipadic-neologd
    
        i. 本データは、株式会社はてなが提供するはてなキーワード一覧ファイル
           中の表記、及び、読み仮名の大半を使用している。
    
           はてなキーワード一覧ファイルの著作権は、株式会社はてなにある。
    
           はてなキーワード一覧ファイルの使用条件に基づき、また、
           データ使用の許可を頂いたことに対する感謝の意を込めて、
           以下に株式会社はてなおよびはてなキーワードへの参照をURLで示す。
    
           株式会社はてな : http://hatenacorp.jp/information/outline
    
           はてなキーワード :
           http://developer.hatena.ne.jp/ja/documents/keyword/misc/catalog
    
       ii. 本データは、日本郵便株式会社が提供する郵便番号データ中の表記、
           及び、読み仮名を使用している。
    
           日本郵便株式会社は、郵便番号データに限っては著作権を主張しないと
           述べている。
    
           日本郵便株式会社の郵便番号データに対する感謝の意を込めて、
           以下に日本郵便株式会社および郵便番号データへの参照をURLで示す。
    
           日本郵便株式会社 :
             http://www.post.japanpost.jp/about/profile.html
    
           郵便番号データ :
             http://www.post.japanpost.jp/zipcode/dl/readme.html
    
      iii. 本データは、スナフキん氏が提供する日本全国駅名一覧中の表記、及び
           読み仮名を使用している。
    
           日本全国駅名一覧の著作権は、スナフキん氏にある。
    
           スナフキん氏は 「このデータを利用されるのは自由ですが、その際に
           不利益を被ったりした場合でも、スナフキんは一切責任は負えません
           ことをご承知おき下さい」と述べている。
    
           スナフキん氏に対する感謝の意を込めて、
           以下に日本全国駅名一覧のコーナーへの参照をURLで示す。
    
           日本全国駅名一覧のコーナー :
             http://www5a.biglobe.ne.jp/~harako/data/station.htm
    
       iv. 本データは、工藤拓氏が提供する人名(姓/名)エントリデータ中の、
           漢字表記の姓・名とそれに対応する読み仮名を使用している。
    
           人名(姓/名)エントリデータは被災者・安否不明者の人名の
           表記揺れ対策として、Mozcの人名辞書を活用できるという
           工藤氏の考えによって提供されている。
    
           工藤氏に対する感謝の意を込めて、
           以下にデータ本体と経緯が分かる情報への参照をURLで示す。
    
           人名(姓/名)エントリデータ :
             http://chasen.org/~taku/software/misc/personal_name.zip
    
           上記データが提供されることになった経緯
             http://togetter.com/li/111529
    
        v. 本データは、Web上からクロールした大量の文書データから抽出した
           表記とそれに対応する読み仮名のデータを含んでいる。
    
           抽出した表記とそれに対応する読み仮名の組は、上記の i. から iv.
           の言語資源の組み合わせによって得られる組のみを採録した。
    
           Web 上に文書データを公開して下さっている皆様に感謝いたします。
    
    Licensed under the Apache License, Version 2.0 (the &quot;License&quot;);
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
          http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.