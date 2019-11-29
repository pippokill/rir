/**
 * Copyright (c) 2018, the Revised Random Indexing AUTHORS.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the University of Bari nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * GNU GENERAL PUBLIC LICENSE - Version 3, 29 June 2007
 *
 */
package di.uniba.it.vectors;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Properties;

/**
 *
 * @author pierpaolo
 */
public class VectorStoreUtils {

    /**
     *
     * @param type
     * @param dimension
     * @param seed
     * @return
     */
    public static String createHeader(VectorType type, int dimension, int seed) {
        StringBuilder sb = new StringBuilder();
        sb.append("-type\t").append(type.name());
        sb.append("\t-dim\t").append(String.valueOf(dimension));
        sb.append("\t-seed\t").append(String.valueOf(seed));
        return sb.toString();
    }

    /**
     *
     * @param line
     * @return
     * @throws IllegalArgumentException
     */
    public static Properties readHeader(String line) throws IllegalArgumentException {
        Properties props = new Properties();
        String[] split = line.split("\t");
        if (split.length % 2 == 0) {
            for (int i = 0; i < split.length; i = i + 2) {
                props.put(split[i], split[i + 1]);
            }
        } else {
            throw new IllegalArgumentException("Not valid header: " + line);
        }
        return props;
    }

    /**
     *
     * @param outputFile
     * @param vectors
     * @param type
     * @param dimension
     * @param seed
     * @throws IOException
     */
    public static void saveSpace(File outputFile, Map<String, Vector> vectors, VectorType type, int dimension, int seed) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
        String header = VectorStoreUtils.createHeader(VectorType.REAL, dimension, seed);
        outputStream.writeUTF(header);
        for (Entry<String, Vector> entry : vectors.entrySet()) {
            outputStream.writeUTF(entry.getKey());
            entry.getValue().writeToStream(outputStream);
        }
        outputStream.close();
    }

    /**
     * Return the list of the n nearest vectors given a word
     *
     * @param store The VectorReader that contains vectors
     * @param word The word
     * @param n The number of nearest vectors
     * @return The list of the n nearest vectors
     * @throws IOException
     */
    public static List<ObjectVector> getNearestVectors(VectorReader store, String word, int n) throws IOException {
        Vector vector = store.getVector(word);
        if (vector != null) {
            return getNearestVectors(store, vector, n);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Return the list of the n nearest vectors given a vector
     *
     * @param store The VectorReader that contains vectors
     * @param vector The vector
     * @param n The number of nearest vectors
     * @return The list of the n nearest vectors
     * @throws IOException
     */
    public static List<ObjectVector> getNearestVectors(VectorReader store, Vector vector, int n) throws IOException {
        PriorityQueue<ObjectVector> queue = new PriorityQueue<>();
        Iterator<ObjectVector> allVectors = store.getAllVectors();
        while (allVectors.hasNext()) {
            ObjectVector ov = allVectors.next();
            double overlap = ov.getVector().measureOverlap(vector);
            ov.setScore(overlap);
            if (queue.size() <= n) {
                queue.offer(ov);
            } else {
                queue.poll();
                queue.offer(ov);
            }
        }
        queue.poll();
        List<ObjectVector> list = new ArrayList<>(queue);
        Collections.sort(list, new ReverseObjectVectorComparator());
        return list;
    }

}
