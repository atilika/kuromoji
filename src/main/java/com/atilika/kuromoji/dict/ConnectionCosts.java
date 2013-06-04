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
package com.atilika.kuromoji.dict;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.atilika.kuromoji.ClassLoaderResolver;
import com.atilika.kuromoji.ResourceResolver;

public class ConnectionCosts {
	public static final String FILENAME = "cc.dat";
		
	private short[][] costs; // array is backward IDs first since get is called using the same backward ID consecutively. maybe doesn't matter.

    public ConnectionCosts(int forwardSize, int backwardSize) {
		this.costs = new short[backwardSize][forwardSize]; 
	}

	public void add(int forwardId, int backwardId, int cost) {
		this.costs[backwardId][forwardId] = (short)cost;
	}
	
	public int get(int forwardId, int backwardId) {
		return costs[backwardId][forwardId];
	}

	public static ConnectionCosts newInstance(ResourceResolver resolver) throws IOException, ClassNotFoundException {
		return read(resolver.resolve(FILENAME));
	}

    public static ConnectionCosts newInstance() throws IOException, ClassNotFoundException {
        return newInstance(new ClassLoaderResolver(ConnectionCosts.class));
    }

	public void write(OutputStream stream) throws IOException {
		DataOutputStream daos = new DataOutputStream(stream);
		daos.writeInt(costs.length);
		for (short [] cost : costs) {
			daos.writeInt(cost.length);
			for (short s : cost) daos.writeShort(s);
		}
	}

	public static ConnectionCosts read(InputStream is) throws IOException, ClassNotFoundException {
		DataInputStream dais = new DataInputStream(is);
		ConnectionCosts instance = new ConnectionCosts(0, 0);
		instance.costs = new short [dais.readInt()][];
		for (int i = 0; i < instance.costs.length; i++) {
			instance.costs[i] = new short[dais.readInt()];
			for (int j = 0, max = instance.costs[i].length; j < max; j++) {
				instance.costs[i][j] = dais.readShort();				
			}
		}
		return instance;
	}
}
