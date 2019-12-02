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
package di.uniba.it.rir.tools;

import di.uniba.it.vectors.FileVectorReader;
import di.uniba.it.vectors.ObjectVector;
import di.uniba.it.vectors.RealVector;
import di.uniba.it.vectors.VectorReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pierpaolo
 */
public class VectorReader2Txt {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length==2) {
            try {
                BufferedWriter writer=new BufferedWriter(new FileWriter(args[1]));
                VectorReader vr=new FileVectorReader(new File(args[0]));
                vr.init();
                Iterator<ObjectVector> allVectors = vr.getAllVectors();
                while (allVectors.hasNext()) {
                    ObjectVector next = allVectors.next();
                    writer.append(next.getKey());
                    float[] coordinates = ((RealVector) next.getVector()).getCoordinates();
                    for (float v:coordinates) {
                        writer.append(" ").append(String.valueOf(v));
                    }
                    writer.newLine();
                }
                vr.close();
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(VectorReader2Txt.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.err.println("This script needs two parameters: <vectors file> <output file>");
        }
    }
    
}
