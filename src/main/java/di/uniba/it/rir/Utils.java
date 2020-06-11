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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.CharArraySet;

/**
 *
 * @author pierpaolo
 */
public class Utils {

    /**
     *
     * @param inputfile
     * @return
     * @throws IOException
     */
    public static Set<String> loadWordset(File inputfile) throws IOException {
        Set<String> set = new HashSet<>();
        BufferedReader reader = new BufferedReader(new FileReader(inputfile));
        while (reader.ready()) {
            set.add(reader.readLine().trim().toLowerCase());
        }
        return set;
    }

    /**
     *
     * @param reader
     * @return
     * @throws IOException
     */
    public static List<String> getTokens(Reader reader) throws IOException {
        List<String> tokens = new ArrayList<>();
        Analyzer analyzer = new StandardAnalyzer(CharArraySet.EMPTY_SET);
        TokenStream tokenStream = analyzer.tokenStream("text", reader);
        tokenStream.reset();
        CharTermAttribute cattr = tokenStream.addAttribute(CharTermAttribute.class);
        while (tokenStream.incrementToken()) {
            String token = cattr.toString();
            tokens.add(token);
        }
        tokenStream.end();
        return tokens;
    }

    /**
     *
     * @param text
     * @return
     * @throws IOException
     */
    public static List<String> getTokens(String text) throws IOException {
        return getTokens(new StringReader(text));
    }

    /**
     *
     * @param reader
     * @param analyzer
     * @return
     * @throws IOException
     */
    public static List<String> getTokens(Reader reader, Analyzer analyzer) throws IOException {
        List<String> tokens = new ArrayList<>();
        TokenStream tokenStream = analyzer.tokenStream("text", reader);
        tokenStream.reset();
        CharTermAttribute cattr = tokenStream.addAttribute(CharTermAttribute.class);
        while (tokenStream.incrementToken()) {
            String token = cattr.toString();
            tokens.add(token);
        }
        tokenStream.end();
        return tokens;
    }

    /**
     *
     * @param text
     * @param analyzer
     * @return
     * @throws IOException
     */
    public static List<String> getTokens(String text, Analyzer analyzer) throws IOException {
        return getTokens(new StringReader(text), analyzer);
    }

    /**
     *
     * @param tokens
     */
    public static void letterfilter(List<String> tokens) {
        for (int i = tokens.size() - 1; i >= 0; i--) {
            boolean remove = false;
            for (int j = 0; j < tokens.get(i).length() && !remove; j++) {
                remove = !Character.isLetter(tokens.get(i).charAt(j));
            }
            if (remove) {
                tokens.remove(i);
            }
        }
    }

    /**
     *
     * @param tokens
     * @param set
     */
    public static void stopwordFilter(List<String> tokens, Set<String> set) {
        for (int i = tokens.size() - 1; i >= 0; i--) {
            if (set.contains(tokens.get(i))) {
                tokens.remove(i);
            }
        }
    }

}
