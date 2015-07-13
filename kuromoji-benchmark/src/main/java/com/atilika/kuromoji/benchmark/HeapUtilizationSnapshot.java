/**
 * Copyright © 2010-2015 Atilika Inc. and contributors (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the
 * License is distributed with this work in the LICENSE.md file.  You may
 * also obtain a copy of the License from
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.atilika.kuromoji.benchmark;

public class HeapUtilizationSnapshot {

    private static final long MB = 1024 * 1024;

    private Runtime runtime;

    private long usedMemory;

    private long freeMemory;

    private long totalMemory;

    private long maxMemory;

    public HeapUtilizationSnapshot() {
        this.runtime = Runtime.getRuntime();
        this.usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / MB;
        this.freeMemory = runtime.freeMemory() / MB;
        this.totalMemory = runtime.totalMemory() / MB;
        this.maxMemory = runtime.maxMemory() / MB;
    }

    public long getUsedMemory() {
        return usedMemory;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    @Override
    public String toString() {
        return "HeapUtilizationSnapshot{" +
            "usedMemory=" + usedMemory +
            ", freeMemory=" + freeMemory +
            ", totalMemory=" + totalMemory +
            ", maxMemory=" + maxMemory +
            '}';
    }
}

