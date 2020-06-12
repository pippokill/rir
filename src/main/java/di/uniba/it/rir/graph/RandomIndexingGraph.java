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
package di.uniba.it.rir.graph;

import di.uniba.it.vectors.RealVector;
import di.uniba.it.vectors.Vector;
import di.uniba.it.vectors.VectorFactory;
import di.uniba.it.vectors.VectorStoreUtils;
import di.uniba.it.vectors.VectorType;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author pierpaolo
 */
public class RandomIndexingGraph {

    private boolean directed = true;

    private int dimension = 300;

    private int seed = 10;

    private int minCount = 5;

    private int reflective = 1;

    private boolean textFormat = false;

    private long randomSeed = System.currentTimeMillis();

    private static final Logger LOG = Logger.getLogger(RandomIndexingGraph.class.getName());

    private Map<Integer, Vector> count(File inputfile) throws IOException {
        LOG.log(Level.INFO, "Min cout: {0}", minCount);
        Random rnd = new Random(randomSeed);
        LOG.log(Level.INFO, "Open file: {0}", inputfile.getName());
        BufferedReader reader = null;
        if (inputfile.getName().endsWith(".gz")) {
            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(inputfile))));
        } else {
            reader = new BufferedReader(new FileReader(inputfile));
        }
        long v = 0;
        long e = 0;
        Map<String, Integer> cmap = new Object2IntOpenHashMap<>();
        while (reader.ready()) {
            String line = reader.readLine();
            if (!line.startsWith("#")) {
                String[] split = line.split("\\s+");
                if (split.length > 1) {
                    Integer v1 = cmap.get(split[0]);
                    if (v1 == null) {
                        cmap.put(split[0], 1);
                        v++;
                    } else {
                        cmap.put(split[0], v1 + 1);
                    }

                    Integer v2 = cmap.get(split[1]);
                    if (v2 == null) {
                        cmap.put(split[1], 1);
                        v++;
                    } else {
                        cmap.put(split[1], v2 + 1);
                    }
                    e++;
                }
            }
        }
        reader.close();
        LOG.log(Level.INFO, "Vertices: {0}", v);
        LOG.log(Level.INFO, "Edges: {0}", e);
        v = 0;
        Map<Integer, Vector> s = new Int2ObjectOpenHashMap<>();
        for (String k : cmap.keySet()) {
            if (cmap.get(k) >= minCount) {
                s.put(Integer.parseInt(k), VectorFactory.generateRandomVector(VectorType.REAL, dimension, seed, rnd));
                v++;
            }
        }
        LOG.log(Level.INFO, "Vertices (filtered): {0}", v);
        return s;
    }

    public void process(File inputfile, File outfile) throws IOException {
        Map<Integer, Vector> baseVectors = count(inputfile);
        for (int r = 0; r < reflective; r++) {
            Map<Integer, Vector> s = new Int2ObjectOpenHashMap<>();
            LOG.log(Level.INFO, "Open file: {0}", inputfile.getName());
            LOG.log(Level.INFO, "Step: {0}", r);
            BufferedReader reader = null;
            if (inputfile.getName().endsWith(".gz")) {
                reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(inputfile))));
            } else {
                reader = new BufferedReader(new FileReader(inputfile));
            }
            while (reader.ready()) {
                String line = reader.readLine();
                if (!line.startsWith("#")) {
                    String[] split = line.split("\\s+");
                    if (split.length > 1) {
                        int v1 = Integer.parseInt(split[0]);
                        int v2 = Integer.parseInt(split[1]);
                        if (baseVectors.containsKey(v1) && baseVectors.containsKey(v2)) {
                            Vector sv1 = s.get(v1);
                            if (sv1 == null) {
                                sv1 = VectorFactory.createZeroVector(VectorType.REAL, dimension);
                                s.put(v1, sv1);
                            }
                            sv1.superpose(baseVectors.get(v2), 1, null);
                            if (!directed) {
                                Vector sv2 = s.get(v2);
                                if (sv2 == null) {
                                    sv2 = VectorFactory.createZeroVector(VectorType.REAL, dimension);
                                    s.put(v2, sv2);
                                }
                                sv2.superpose(baseVectors.get(v1), 1, null);
                            }
                        }
                    }
                }
            }
            reader.close();
            if (r == reflective - 1) {
                LOG.info("Save vectors...");
                if (textFormat) {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
                    Set<Integer> keySet = s.keySet();
                    for (Integer key : keySet) {
                        Vector v = s.get(key);
                        if (!v.isZeroVector()) {
                            v.normalize();
                            writer.append(key.toString());
                            float[] coordinates = ((RealVector) v).getCoordinates();
                            for (float c : coordinates) {
                                writer.append(" ").append(String.valueOf(c));
                            }
                            writer.newLine();
                        }
                    }
                    writer.close();
                } else {
                    DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outfile)));
                    String header = VectorStoreUtils.createHeader(VectorType.REAL, dimension, seed);
                    outputStream.writeUTF(header);
                    for (Map.Entry<Integer, Vector> entry : s.entrySet()) {
                        if (!entry.getValue().isZeroVector()) {
                            entry.getValue().normalize();
                            outputStream.writeUTF(entry.getKey().toString());
                            entry.getValue().writeToStream(outputStream);
                        }
                    }
                    outputStream.close();
                }
            } else {
                //copy vectors
                baseVectors.clear();
                System.gc();
                for (Map.Entry<Integer, Vector> entry : s.entrySet()) {
                    baseVectors.put(entry.getKey(), entry.getValue().copy());
                }
            }
            System.gc();
        }
    }

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public int getMinCount() {
        return minCount;
    }

    public void setMinCount(int minCount) {
        this.minCount = minCount;
    }

    public int getReflective() {
        return reflective;
    }

    public void setReflective(int reflective) {
        this.reflective = reflective;
    }

    public boolean isTextFormat() {
        return textFormat;
    }

    public void setTextFormat(boolean textFormat) {
        this.textFormat = textFormat;
    }

    public long getRandomSeed() {
        return randomSeed;
    }

    public void setRandomSeed(long randomSeed) {
        this.randomSeed = randomSeed;
    }

}
