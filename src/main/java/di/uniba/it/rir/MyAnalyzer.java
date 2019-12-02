/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.rir;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;

/**
 *
 * @author pierpaolo
 */
public class MyAnalyzer extends StopwordAnalyzerBase {

    public MyAnalyzer(CharArraySet stopwords) {
        super(stopwords);
    }

    public MyAnalyzer() {
        this(CharArraySet.EMPTY_SET);
    }

    @Override
    protected TokenStreamComponents createComponents(String arg0, Reader arg1) {
        final StandardTokenizer src = new StandardTokenizer(arg1);
        StopFilter tok = new StopFilter(src, stopwords);
        return new TokenStreamComponents(src, tok) {
            @Override
            protected void setReader(final Reader reader) throws IOException {
                super.setReader(reader);
            }
        };
    }

}
