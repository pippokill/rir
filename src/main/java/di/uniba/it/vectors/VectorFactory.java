/**
 * Copyright (c) 2011, the SemanticVectors AUTHORS.
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
 * Neither the name of the University of Pittsburgh nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
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
 */
package di.uniba.it.vectors;

import java.util.Random;

/**
 * Class for building vectors, designed to be used externally.
 *
 * @author Dominic Widdows
 */
public class VectorFactory {

    //private static final BinaryVector binaryInstance = new BinaryVector(0);

    private static final RealVector realInstance = new RealVector(0);

    /**
     *
     * @param type
     * @param dimension
     * @return
     */
    public static Vector createZeroVector(VectorType type, int dimension) {
        switch (type) {
            case BINARY:
            //return new BinaryVector(dimension);
            case REAL:
                return new RealVector(dimension);
            default:
                throw new IllegalArgumentException("Unrecognized VectorType: " + type);
        }
    }

    /**
     * Generates an appropriate random vector.
     *
     * @param type one of the recognized vector types
     * @param dimension number of dimensions in the generated vector
     * @param numEntries total number of non-zero entries; must be no greater
     * than half of dimension
     * @param random random number generator; passed in to enable deterministic
     * testing
     * @return vector generated with appropriate type, dimension and number of
     * nonzero entries
     */
    public static Vector generateRandomVector(
            VectorType type, int dimension, int numEntries, Random random) {

        if (2 * numEntries > dimension && !type.equals(VectorType.COMPLEX) && !(numEntries == dimension)) {
            throw new RuntimeException("Requested " + numEntries + " to be filled in sparse "
                    + "vector of dimension " + dimension + ". This is not sparse and may cause problems.");
        }
        switch (type) {
            case BINARY:
            //return binaryInstance.generateRandomVector(dimension, numEntries, random);
            case REAL:
                return realInstance.generateRandomVector(dimension, numEntries, random);
            default:
                throw new IllegalArgumentException("Unrecognized VectorType: " + type);
        }
    }

    /**
     * Returns the size in bytes expected to be taken up by the serialization of
     * this vector in Lucene format.
     * @param vectorType
     * @param dimension
     * @return 
     */
    public static int getByteSize(VectorType vectorType, int dimension) {
        switch (vectorType) {
            case BINARY:
                return 8 * ((dimension / 64));
            case REAL:
                return 4 * dimension;
            default:
                throw new IllegalArgumentException("Unrecognized VectorType: " + vectorType);
        }
    }
}
