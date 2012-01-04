/**
 * Copyright © 2010-2012 Atilika Inc.  All rights reserved.
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
		
	private short[][] costs; // array is backward IDs first since get is called using the same backward ID consecutively. maybe doesn't matter.
	
	public ConnectionCosts() {
		
	}
	
	public ConnectionCosts(int forwardSize, int backwardSize) {
		this.costs = new short[backwardSize][forwardSize]; 
	}

	public void add(int forwardId, int backwardId, int cost) {
		this.costs[backwardId][forwardId] = (short)cost;
	}
	
	public int get(int forwardId, int backwardId) {
		return costs[backwardId][forwardId];
	}

	public void write(String directoryname) throws IOException {
		String filename = directoryname + File.separator + FILENAME;
		ObjectOutputStream outputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
		outputStream.writeObject(this);
		outputStream.close();
	}

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
