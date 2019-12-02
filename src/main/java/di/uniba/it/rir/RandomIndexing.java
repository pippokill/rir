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
package di.uniba.it.rir;

import di.uniba.it.vectors.RealVector;
import di.uniba.it.vectors.Vector;
import di.uniba.it.vectors.VectorFactory;
import di.uniba.it.vectors.VectorStoreUtils;
import di.uniba.it.vectors.VectorType;
import di.uniba.it.vectors.VectorUtils;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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
import java.util.ArrayList;
import java.util.List;
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
public class RandomIndexing {

    private static final Logger LOG = Logger.getLogger(RandomIndexing.class.getName());

    private int dimension = 200;

    private int seed = 10;

    private int minCount = 100000;

    private long totalOcc = 0;

    private double t = 0.001;

    //negative sampling
    private int ns = 0;

    private static final int NS_SIZE = 100000000;

    private File stopwordfile = null;

    private boolean saveRandom = false;

    private boolean letterFilter = false;

    private boolean txtFormat = false;

    private long randomSeed = System.currentTimeMillis();

    private int windowSize = 5;

    private boolean useStandardAnalyzer = false;

    private Random rndDownsample;

    /**
     *
     */
    public RandomIndexing() {
    }

    /**
     *
     * @param dimension
     */
    public RandomIndexing(int dimension) {
        this.dimension = dimension;
    }

    /**
     *
     * @param dimension
     * @param seed
     */
    public RandomIndexing(int dimension, int seed) {
        this.dimension = dimension;
        this.seed = seed;
    }

    /**
     *
     * @return
     */
    public int getDimension() {
        return dimension;
    }

    /**
     *
     * @param dimension
     */
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    /**
     *
     * @return
     */
    public int getSeed() {
        return seed;
    }

    /**
     *
     * @param seed
     */
    public void setSeed(int seed) {
        this.seed = seed;
    }

    /**
     *
     * @return
     */
    public double getT() {
        return t;
    }

    /**
     *
     * @param t
     */
    public void setT(double t) {
        this.t = t;
    }

    /**
     *
     * @return
     */
    public int getNs() {
        return ns;
    }

    /**
     *
     * @param ns
     */
    public void setNs(int ns) {
        this.ns = ns;
    }

    /**
     *
     * @return
     */
    public int getMinCount() {
        return minCount;
    }

    /**
     *
     * @param minCount
     */
    public void setMinCount(int minCount) {
        this.minCount = minCount;
    }

    /**
     *
     * @return
     */
    public File getStopwordfile() {
        return stopwordfile;
    }

    /**
     *
     * @param stopwordfile
     */
    public void setStopwordfile(File stopwordfile) {
        this.stopwordfile = stopwordfile;
    }

    /**
     *
     * @return
     */
    public boolean isSaveRandom() {
        return saveRandom;
    }

    /**
     *
     * @param saveRandom
     */
    public void setSaveRandom(boolean saveRandom) {
        this.saveRandom = saveRandom;
    }

    /**
     *
     * @return
     */
    public boolean isLetterFilter() {
        return letterFilter;
    }

    /**
     *
     * @param letterFilter
     */
    public void setLetterFilter(boolean letterFilter) {
        this.letterFilter = letterFilter;
    }

    /**
     *
     * @return
     */
    public boolean isTxtFormat() {
        return txtFormat;
    }

    /**
     *
     * @param txtFormat
     */
    public void setTxtFormat(boolean txtFormat) {
        this.txtFormat = txtFormat;
    }

    /**
     *
     * @param randomSeed
     */
    public void setRandomSeed(long randomSeed) {
        this.randomSeed = randomSeed;
    }

    /**
     *
     * @return
     */
    public int getWindowSize() {
        return windowSize;
    }

    /**
     *
     * @param windowSize
     */
    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    public boolean isUseStandardAnalyzer() {
        return useStandardAnalyzer;
    }

    public void setUseStandardAnalyzer(boolean useStandardAnalyzer) {
        this.useStandardAnalyzer = useStandardAnalyzer;
    }

    private Map<String, Integer> buildDictionary(File inputfile) throws IOException {
        LOG.log(Level.INFO, "Building dictionary: {0}", inputfile.getAbsolutePath());
        Set<String> stopword = null;
        if (stopwordfile != null) {
            stopword = Utils.loadWordset(stopwordfile);
        }
        Map<String, Integer> dict = new Object2IntOpenHashMap<>();
        BufferedReader reader;
        if (inputfile.getName().endsWith(".gz")) {
            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(inputfile))));
        } else {
            reader = new BufferedReader(new FileReader(inputfile));
        }
        while (reader.ready()) {
            List<String> tokens;
            if (useStandardAnalyzer) {
                tokens = Utils.getTokens(reader.readLine());
            } else {
                tokens = Utils.getTokens(reader.readLine(), new MyAnalyzer());
            }
            if (letterFilter) {
                Utils.letterfilter(tokens);
            }
            if (stopwordfile != null) {
                Utils.stopwordFilter(tokens, stopword);
            }
            for (String token : tokens) {
                Integer c = dict.get(token);
                if (c == null) {
                    dict.put(token, 1);
                } else {
                    dict.put(token, c + 1);
                }
            }
        }
        reader.close();
        String[] words = dict.keySet().toArray(new String[dict.size()]);
        for (String word : words) {
            if (dict.get(word) < minCount) {
                dict.remove(word);
            }
        }
        return dict;
    }

    private List<String> getContext(List<String> tokens, int offset, Map<String, Integer> dict) {
        if (rndDownsample == null) {
            rndDownsample = new Random(randomSeed);
        }
        List<String> context = new ArrayList<>();
        int i = offset - 1;
        int a = 0;
        while (i >= 0 && a < windowSize) {
            if (dict.containsKey(tokens.get(i))) {
                double f = dict.get(tokens.get(i)).doubleValue();
                double p = (Math.sqrt(f / (t * totalOcc)) + 1) * (t * totalOcc) / f;
                if (rndDownsample.nextDouble() > p) {
                    context.add(tokens.get(i));
                    a++;
                }
            }
            i--;
        }
        i = offset + 1;
        a = 0;
        while (i < tokens.size() && a < windowSize) {
            if (dict.containsKey(tokens.get(i))) {
                double f = dict.get(tokens.get(i)).doubleValue();
                double p = (Math.sqrt(f / (t * totalOcc)) + 1) * (t * totalOcc) / f;
                if (rndDownsample.nextDouble() > p) {
                    context.add(tokens.get(i));
                    a++;
                }
            }
            i++;
        }
        return context;
    }

    /**
     *
     * @param inputfile
     * @param outputfile
     * @throws IOException
     */
    public void run(File inputfile, File outputfile) throws IOException {
        LOG.log(Level.INFO, "Min count {0}", minCount);
        Map<String, Integer> dict = buildDictionary(inputfile);
        LOG.log(Level.INFO, "Dictionary size {0}", dict.size());
        LOG.log(Level.INFO, "Negative sampling: {0}", ns);
        Map<String, Vector> elementalSpace = new Object2ObjectOpenHashMap<>();
        Map<String, Vector> semanticSpace = new Object2ObjectOpenHashMap<>();
        //create random vectors space
        LOG.info("Building vectors...");
        totalOcc = 0;
        Random random = new Random(randomSeed);
        for (String word : dict.keySet()) {
            elementalSpace.put(word, VectorFactory.generateRandomVector(VectorType.REAL, dimension, seed, random));
            semanticSpace.put(word, VectorFactory.createZeroVector(VectorType.REAL, dimension));
            //compute total occurrences taking into account the dictionary
            totalOcc += dict.get(word);
        }
        //init negative sample
        String[] dictArray = null;
        int[] na = null;
        if (ns > 0) {
            dictArray = new String[dict.size()];
            na = new int[NS_SIZE];
            dictArray = dict.keySet().toArray(dictArray);
            double normd = Math.pow(totalOcc, 0.75);
            int i = 0;
            double d1 = Math.pow(dict.get(dictArray[i]).doubleValue(), 0.75) / normd;
            for (int a = 0; a < na.length; a++) {
                na[a] = i;
                if (((double) a / (double) NS_SIZE) > d1) {
                    i++;
                    d1 += Math.pow(dict.get(dictArray[i]).doubleValue(), 0.75) / normd;
                }
                if (i >= dictArray.length) {
                    i = dictArray.length - 1;
                }
            }
        }
        LOG.log(Level.INFO, "Total occurrences {0}", totalOcc);
        LOG.log(Level.INFO, "Building spaces: {0}", inputfile.getAbsolutePath());
        Set<String> stopword = null;
        if (stopwordfile != null) {
            stopword = Utils.loadWordset(stopwordfile);
        }
        BufferedReader reader;
        if (inputfile.getName().endsWith(".gz")) {
            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(inputfile))));
        } else {
            reader = new BufferedReader(new FileReader(inputfile));
        }
        while (reader.ready()) {
            List<String> tokens;
            if (useStandardAnalyzer) {
                tokens = Utils.getTokens(reader.readLine());
            } else {
                tokens = Utils.getTokens(reader.readLine(), new MyAnalyzer());
            }
            if (letterFilter) {
                Utils.letterfilter(tokens);
            }
            if (stopwordfile != null) {
                Utils.stopwordFilter(tokens, stopword);
            }
            for (int offset = 0; offset < tokens.size(); offset++) {
                if (dict.containsKey(tokens.get(offset))) {
                    Vector v = semanticSpace.get(tokens.get(offset));
                    List<String> context = getContext(tokens, offset, dict);
                    for (String cw : context) {
                        Vector ev = elementalSpace.get(cw);
                        if (ev != null) {
                            /*double f = dict.get(cw).doubleValue() / (double) totalOcc; //downsampling
                            double p = 1;
                            if (f > t) { //if word frequency is greater than the threshold, compute the probability of consider the word 
                                p = Math.sqrt(t / f);
                            }*/
                            v.superpose(ev, 1, null);
                        }
                    }
                }
            }
        }
        reader.close();
        if (ns > 0) {
            LOG.log(Level.INFO, "Performing negative sampling...");
            for (String word : dictArray) {
                Vector sv = semanticSpace.get(word);
                if (sv != null) {
                    int k = 0;
                    while (k < ns) {
                        int idx = random.nextInt(na.length);
                        String negword = dictArray[na[idx]];
                        if (!negword.equals(word)) {
                            Vector nw = semanticSpace.get(negword);
                            List<Vector> lv = new ArrayList<>();
                            lv.add(nw);
                            lv.add(sv);
                            VectorUtils.orthogonalizeVectors(lv);
                            k++;
                        }
                    }
                }
            }
        }
        LOG.log(Level.INFO, "Saving vectors in dir: {0}", outputfile.getAbsolutePath());
        if (txtFormat) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputfile));
            Set<String> keySet = semanticSpace.keySet();
            for (String key : keySet) {
                Vector v = semanticSpace.get(key);
                if (!v.isZeroVector()) {
                    v.normalize();
                    writer.append(key);
                    float[] coordinates = ((RealVector) v).getCoordinates();
                    for (float c : coordinates) {
                        writer.append(" ").append(String.valueOf(c));
                    }
                    writer.newLine();
                }
            }
            writer.close();
        } else {
            VectorStoreUtils.saveSpace(outputfile, semanticSpace, VectorType.REAL, dimension, seed);
        }
        if (saveRandom) {
            LOG.log(Level.INFO, "Saving random vectors in dir: {0}", outputfile.getAbsolutePath());
            if (txtFormat) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputfile));
                Set<String> keySet = elementalSpace.keySet();
                for (String key : keySet) {
                    Vector v = semanticSpace.get(key);
                    if (!v.isZeroVector()) {
                        v.normalize();
                        writer.append(key);
                        float[] coordinates = ((RealVector) v).getCoordinates();
                        for (float c : coordinates) {
                            writer.append(" ").append(String.valueOf(c));
                        }
                        writer.newLine();
                    }
                }
                writer.close();
            } else {
                VectorStoreUtils.saveSpace(new File(outputfile.getAbsolutePath() + ".ri"), elementalSpace, VectorType.REAL, dimension, seed);
            }
        }
    }

}
