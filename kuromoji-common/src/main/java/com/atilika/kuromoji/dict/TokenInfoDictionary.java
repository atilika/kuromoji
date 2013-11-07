/**
 * Copyright © 2010-2013 Atilika Inc. and contributors (CONTRIBUTORS.txt)
 *
 * Atilika Inc. licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with
 * the License.  A copy of the License is distributed with this work in the
 * LICENSE.txt file.  You may also obtain a copy of the License from
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.atilika.kuromoji.dict;

import com.atilika.kuromoji.ClassLoaderResolver;
import com.atilika.kuromoji.ResourceResolver;
import com.atilika.kuromoji.util.CSVUtil;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenInfoDictionary implements Dictionary {

	public static final String FILENAME = "tid.dat";

	public static final String TARGETMAP_FILENAME = "tid_map.dat";

    public static final String PART_OF_SPEECH_FILENAME = "tid_pos.dat";

    public static final int POS_OFFSET = 6;

    public static final int SIZE_OFFSET = POS_OFFSET + 2;

    public static final int FEATURE_OFFSET = SIZE_OFFSET + 2;

	protected ByteBuffer buffer;

	protected int[][] targetMap;

    protected Map<String, Short> pos;

    protected List<String> posList;
    
	public TokenInfoDictionary() {
        pos = new HashMap<String, Short>();
        posList = new ArrayList<String>();
        targetMap = new int[1][];
	}

	public TokenInfoDictionary(int size) {
        this();
		buffer = ByteBuffer.allocate(size);
	}

	/**
	 * put the entry in map
	 * @param entry
	 * @return current position of buffer, which will be wordId of next entry
	 */
	public int put(String[] entry) {
        int posStart = 4;
        int featureStart = 10;//entry.length - 3;

		short leftId = Short.parseShort(entry[1]);
		short rightId = Short.parseShort(entry[2]);
		short wordCost = Short.parseShort(entry[3]);

        String posFeatures = extractPosFeatures(entry, posStart, featureStart);
        short partOfSpeechId = createPartOfSpeech(posFeatures);
        String features = extractFeatures(entry, featureStart, entry.length);
        int featuresSize = features.length()* 2;
        int otherFieldSize = 2 * 5; // Buffer space needed by leftId, rightId, wordCost, partOfSpeechId and featuresSize

        extendBufferIfNecessary(featuresSize + otherFieldSize);

        buffer.putShort(leftId);
        buffer.putShort(rightId);
        buffer.putShort(wordCost);

        buffer.putShort(partOfSpeechId);

		buffer.putShort((short)featuresSize);

        for (char c : features.toCharArray()){
			buffer.putChar(c);
		}

		return buffer.position();
	}

    private String extractFeatures(String[] entry, int start, int end) {
        StringBuilder sb = new StringBuilder();

        int readingIndex = start + 1;
        String baseForm = (end > start) ? entry[start] : null;
        String reading = (end > readingIndex) ? entry[readingIndex] : null;

        for (int i = start; i < end; i++) {
            if (entry[i].equals(baseForm) && i > readingIndex) {
                sb.append(REPEATED_BASEFORM);
            } else if (entry[i].equals(reading) && i > readingIndex) {
                sb.append(REPEATED_TERM);
            } else {
                sb.append(entry[i]);
            }

            if (i < end - 1) {
                sb.append(INTERNAL_SEPARATOR);
            }
        }

        return sb.toString();
    }

    private String extractPosFeatures(String[] entry, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++) {
            sb.append(entry[i]);

            if (i < end - 1) {
                sb.append(INTERNAL_SEPARATOR);
            }
        }
		return sb.toString();
    }

    private void extendBufferIfNecessary(int neededSize) {
        int leftInBuffer = buffer.limit() - buffer.position();

        if (neededSize > leftInBuffer) { // four short and features
            ByteBuffer newBuffer = ByteBuffer.allocate(buffer.limit() * 2);
            buffer.flip();
            newBuffer.put(buffer);
            buffer = newBuffer;
        }
    }

    protected short createPartOfSpeech(String features) {
        Short posId = pos.get(features);
        
        if (posId == null) {
            posId = (short) pos.size();
            pos.put(features, posId);
            posList.add(posId, features);
        }
        return posId;
    }

    public void addMapping(int sourceId, int wordId) {
		if(targetMap.length <= sourceId) {
			int[][] newArray = new int[sourceId + 1][];
			System.arraycopy(targetMap, 0, newArray, 0, targetMap.length);
			targetMap = newArray;
		}

		// Prepare array -- extend the length of array by one
		int[] current = targetMap[sourceId];
		if (current == null) {
			current = new int[1];
		} else {
			int[] newArray = new int[current.length + 1];
			System.arraycopy(current, 0, newArray, 0, current.length);
			current = newArray;
		}
		targetMap[sourceId] = current;

		int[] targets = targetMap[sourceId];
		targets[targets.length - 1] = wordId;
	}

	public int[] lookupWordIds(int sourceId) {
		return targetMap[sourceId];
	}

	@Override
	public int getLeftId(int wordId) {
		return buffer.getShort(wordId);
	}

	@Override
	public int getRightId(int wordId) {
		return buffer.getShort(wordId + 2);	// Skip left id
	}

	@Override
	public int getWordCost(int wordId) {
		return buffer.getShort(wordId + 4);	// Skip left id and right id
	}


    @Override
    public String[] getAllFeaturesArray(int wordId) {
        List<String> features = new ArrayList<String>(16);

        attachPosInfo(wordId, features);
        attachFeatures(wordId, features);

        return features.toArray(new String[features.size()]);
    }

    private void attachFeatures(int wordId, List<String> features) {
        int size = buffer.getShort(wordId + SIZE_OFFSET) / 2; // Read length of feature String. Skip 6 bytes, see data structure.
        int offset = wordId + FEATURE_OFFSET;
        char[] charBuffer = new char[size];
        int position = 0;

        String reading = null;
        String baseForm = null;
        String feature = null;

        for (int i = 0; i < size; i++) {
            char c = buffer.getChar(offset + i * 2);
            if (c == INTERNAL_SEPARATOR) {
                feature = new String(charBuffer, 0, position);
                if (features.size() == 6) {
                    baseForm = feature;
                } else if (features.size() == 7) {
                    reading = feature;
                }
                if (features.size() > 6) {
                    if (charBuffer[0] == REPEATED_TERM) {
                        feature = reading;
                    } else if (charBuffer[0] == REPEATED_BASEFORM) {
                        feature = baseForm;
                    }
                }
                features.add(feature);
                position = 0;
            } else {
                charBuffer[position++] = c;
            }
        }

        if (position > 0) {
            feature = new String(charBuffer, 0, position);
            if (features.size() > 7) {
                if (charBuffer[0] == REPEATED_TERM) {
                    feature = reading;
                } else if (charBuffer[0] == REPEATED_BASEFORM) {
                    feature = baseForm;
                }
            }
            features.add(feature);
        }
    }

    private void attachPosInfo(int wordId, List<String> features) {
        int posDetail = buffer.getShort(wordId + POS_OFFSET);
        String posInfo = posList.get(posDetail);

        int size = posInfo.length();
        char[] charBuffer = new char[size];
        int position = 0;

        for (int i = 0; i < size; i++){
            char c = posInfo.charAt(i);
            if (c == INTERNAL_SEPARATOR) {
                features.add(new String(charBuffer, 0, position));
                position = 0;
            } else {
                charBuffer[position++] = c;
            }
        }

        if (position > 0) {
            features.add(new String(charBuffer, 0, position));
        }
    }

    @Override
	public String getFeature(int wordId, int... fields) {
		String[] allFeatures = getAllFeaturesArray(wordId);
		StringBuilder sb = new StringBuilder();

		if(fields.length == 0){ // All features
			for(String feature : allFeatures) {
				sb.append(CSVUtil.quoteEscape(feature)).append(",");
			}
		} else if(fields.length == 1) { // One feature doesn't need to escape value
			sb.append(allFeatures[fields[0]]).append(",");
		} else {
			for(int field : fields){
				sb.append(CSVUtil.quoteEscape(allFeatures[field])).append(",");
			}
		}

		return sb.deleteCharAt(sb.length() - 1).toString();
	}

	@Override
	public String getReading(int wordId) {
		return getFeature(wordId, 7);
	}

	@Override
	public String getAllFeatures(int wordId) {
		return getFeature(wordId);
	}

	@Override
	public String getPartOfSpeech(int wordId) {
		return getFeature(wordId, 0, 1, 2, 3);
	}

	@Override
	public String getBaseForm(int wordId) {
		return getFeature(wordId, 6);
	}

	/**
	 * Write dictionary in file
	 * Dictionary format is:
	 * [Size of dictionary(int)], [entry:{left id(short)}{right id(short)}{word cost(short)}{length of pos info(short)}{pos info(char)}], [entry...], [entry...].....
	 * @param directoryName
	 * @throws IOException
	 */
	public void write(String directoryName) throws IOException {
		writeDictionary(directoryName + File.separator + FILENAME);
		writeTargetMap(directoryName + File.separator + TARGETMAP_FILENAME);
        writePosVector(directoryName + File.separator + PART_OF_SPEECH_FILENAME);
	}

    protected void writeDictionary(String filename) throws IOException {
		FileOutputStream fos = new FileOutputStream(filename);
		DataOutputStream dos = new DataOutputStream(fos);
		dos.writeInt(buffer.position());
		WritableByteChannel channel = Channels.newChannel(fos);
		// Write Buffer
		buffer.flip();  // set position to 0, set limit to current position
		channel.write(buffer);
		fos.close();
	}

	/**
	 * Read dictionary into directly allocated buffer.
	 * @return TokenInfoDictionary instance
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static TokenInfoDictionary newInstance(ResourceResolver resolver) throws IOException, ClassNotFoundException {
		TokenInfoDictionary dictionary = new TokenInfoDictionary();
		dictionary.loadDictionary(resolver.resolve(FILENAME));
		dictionary.loadTargetMap(resolver.resolve(TARGETMAP_FILENAME));
        dictionary.loadPosVector(resolver.resolve(PART_OF_SPEECH_FILENAME));
		return dictionary;
	}

    public static TokenInfoDictionary newInstance() throws IOException, ClassNotFoundException {
        return newInstance(new ClassLoaderResolver(TokenInfoDictionary.class));
    }

	protected void writeTargetMap(String filename) throws IOException {
		DataOutputStream daos = new DataOutputStream(new FileOutputStream(filename));
		daos.writeInt(targetMap.length);
		// The array is mostly sparse so we'll save only non-null members.
		for (int i = 0; i < targetMap.length; i++) {
			if (targetMap[i] != null) {
				int[] arr = targetMap[i];
				daos.writeInt(i);
				daos.writeInt(arr.length);
				for (int j : arr) daos.writeInt(j);
			}
		}
		daos.writeInt(-1); // End index marker.
		daos.close();
	}

    protected void writePosVector(String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);
        for (String s : posList) {
            writer.write(s);
            writer.write('\n');
        }
        writer.close();
    }

	protected void loadTargetMap(InputStream is) throws IOException, ClassNotFoundException {
		DataInputStream dais = new DataInputStream(new BufferedInputStream(is));
		targetMap = new int [dais.readInt()][];
		int index;
		while ((index = dais.readInt()) >= 0) {
			int length = dais.readInt();
			targetMap[index] = new int[length];
			for (int j = 0; j < length; j++) {
				targetMap[index][j] = dais.readInt();
			}
		}
	}

	protected void loadDictionary(InputStream is) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
		DataInputStream dis = new DataInputStream(bis);
		int size = dis.readInt();

		ByteBuffer tmpBuffer = ByteBuffer.allocateDirect(size);

		ReadableByteChannel channel = Channels.newChannel(bis);
		channel.read(tmpBuffer);
		dis.close();
		buffer = tmpBuffer.asReadOnlyBuffer();
	}

    protected void loadPosVector(InputStream is) throws IOException {
        InputStreamReader isr = new InputStreamReader(new BufferedInputStream(is));
        LineNumberReader reader = new LineNumberReader(isr);
        String line;
        List<String> partOfSpeech = new ArrayList<String>();
        while ((line = reader.readLine()) != null) {
            partOfSpeech.add(line);
        }
        posList = partOfSpeech;
        isr.close();
    }
}
