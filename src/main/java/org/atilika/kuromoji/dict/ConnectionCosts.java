/**
 * Copyright © 2010-2011 Atilika Inc.  All rights reserved.
 *
 * See the NOTICE.txt file distributed with this work for additional
 * information regarding copyright ownership.
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
package org.atilika.kuromoji.dict;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author Masaru Hasegawa
 * @author Christian Moen
 */
public class ConnectionCosts implements Serializable{

	private static final long serialVersionUID = -7704592689635266457L;

	public static final String FILENAME = "cc.dat";
		
	private short[][] costs; // array is right first since get is called using the same right ID consecutively. maybe doesn't matter.
	
//	private int leftSize = 0;
//	
//	private int rightSize = 0;
	
//	private ShortBuffer costBuffer;
	
	public ConnectionCosts() {
		
	}
	
	public ConnectionCosts(int leftSize, int rightSize) {
//		this.leftSize = leftSize;
//		this.rightSize = rightSize;
		this.costs = new short[rightSize][leftSize]; 
	}

	public void add(int leftId, int rightId, int cost) {
		this.costs[rightId][leftId] = (short)cost;
	}
	
//	public int get(int leftId, int rightId) {
//		return costBuffer.get(leftId * rightSize + rightId);
//	}

	public int get(int leftId, int rightId) {
		return costs[rightId][leftId];
	}

	public void write(String directoryname) throws IOException {
		String filename = directoryname + File.separator + FILENAME;
		ObjectOutputStream outputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
		outputStream.writeObject(this);
		outputStream.close();
	}

//	public void write(String filename) throws IOException {
//		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
//		dos.writeInt(leftSize);
//		dos.writeInt(rightSize);
//		for(short[] costArr : costs) {
//			for(short cost : costArr){
//				dos.writeInt(cost);
//			}
//		}
//		dos.close();
//	}

//	public static ConnectionCosts read(InputStream is) throws IOException {
//		ConnectionCosts cc = new ConnectionCosts();
//		DataInputStream dis = new DataInputStream(is);
//		cc.leftSize = dis.readInt();
//		cc.rightSize = dis.readInt();
//		System.out.println(cc.leftSize + ": " + cc.rightSize);
//		
//		ByteBuffer tmpCostBuffer = ByteBuffer.allocateDirect(cc.leftSize * cc.rightSize * 2);
//		byte[] tmpArr = new byte[cc.rightSize * 2];
//		for(int i = 0; i < cc.leftSize; i++) {
//			dis.read(tmpArr);
//			tmpCostBuffer.put(tmpArr, 0, tmpArr.length);
//		}
//		dis.close();
//		tmpCostBuffer.rewind();
//		cc.costBuffer = tmpCostBuffer.asShortBuffer().asReadOnlyBuffer();
//		return cc;
//	}

	public static ConnectionCosts getInstance() throws IOException, ClassNotFoundException {
		InputStream is = ConnectionCosts.class.getClassLoader().getResourceAsStream(FILENAME);
		return read(is);
	}
	
	public static ConnectionCosts read(InputStream is) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(is));
		ConnectionCosts instance = (ConnectionCosts) ois.readObject();
		ois.close();
		return instance;
	}

}
