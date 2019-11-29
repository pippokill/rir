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
package di.uniba.it.rir.cli;

import di.uniba.it.rir.RandomIndexing;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author pierpaolo
 */
public class RI {

    private static final Logger LOG = Logger.getLogger(RI.class.getName());

    static Options options;

    static CommandLineParser cmdParser = new DefaultParser();

    static {
        options = new Options();
        options.addOption("i", true, "Input corpus (compressed GZIP files are supported)")
                .addOption("o", true, "Output file")
                .addOption("txt", false, "Enable textual output format")
                .addOption("sw", true, "Stop word file")
                .addOption("w", true, "Windows size (optional, default 5)")
                .addOption("lf", true, "Enable letter filter")
                .addOption("ri", false, "Save random vector")
                .addOption("rseed", true, "Seed for random inizialization")
                .addOption("dim", true, "The vector dimension (optional, default 300)")
                .addOption("seed", true, "The number of seeds (optional, default 10)")
                .addOption("c", true, "This will discard words that appear less than <n> times; default is 5")
                .addOption("t", true, "Threshold for downsampling frequent words (optinal, default 0.001)")
                .addOption("ns", true, "Number of negative samples (optinal, default 0)");
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            CommandLine cmd = cmdParser.parse(options, args);
            if (cmd.hasOption("i") && cmd.hasOption("o")) {
                try {
                    RandomIndexing ri = new RandomIndexing();
                    ri.setDimension(Integer.parseInt(cmd.getOptionValue("d", "300")));
                    ri.setSeed(Integer.parseInt(cmd.getOptionValue("s", "10")));
                    ri.setMinCount(Integer.parseInt(cmd.getOptionValue("c", "5")));
                    ri.setT(Double.parseDouble(cmd.getOptionValue("t", "0.001")));
                    ri.setNs(Integer.parseInt(cmd.getOptionValue("ns", "0")));
                    ri.setLetterFilter(cmd.hasOption("lf"));
                    ri.setSaveRandom(cmd.hasOption("ri"));
                    ri.setTxtFormat(cmd.hasOption("txt"));
                    ri.setWindowSize(Integer.parseInt(cmd.getOptionValue("w", "5")));
                    if (cmd.hasOption("rseed")) {
                        ri.setRandomSeed(Long.parseLong(cmd.getOptionValue("rseed")));
                    }
                    ri.run(new File(cmd.getOptionValue("i")), new File(cmd.getOptionValue("o")));
                } catch (IOException | NumberFormatException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            } else {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp("Execute Random Indexing", options, true);
            }
        } catch (ParseException ex) {
            Logger.getLogger(RI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
