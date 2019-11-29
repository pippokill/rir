/**
   Copyright (c) 2011, the SemanticVectors AUTHORS.

   All rights reserved.

   Redistribution and use in source and binary forms, with or without
   modification, are permitted provided that the following conditions are
   met:

   * Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

   * Redistributions in binary form must reproduce the above
   copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided
   with the distribution.

   * Neither the name of the University of Pittsburgh nor the names
   of its contributors may be used to endorse or promote products
   derived from this software without specific prior written
   permission.

   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
   "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
   LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
   A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
   CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
   EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
   PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
   PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
   LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
   NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
   SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
**/

package di.uniba.it.vectors;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Random;


/**
 * Base representation of a vector over a particular ground field and vector operations.
 * Designed to enable real, complex, and binary implementations. 
 * 
 * @author Dominic Widdows
 */
public interface Vector {

  /**
   * Returns a copy of this vector.
     * @return 
   */
  public abstract Vector copy();
  
  /**
   * Creates a sparse vector with appropriate entries from the random seed.
   * 
   * Ideally this would be a static method of the implementing class, but it's not easy to
   * express this with either an abstract class or an interface.  Client implementations should
   * use the {@link VectorFactory} class, hence the protected status.
   * 
   * @param dimension number of dimension of vector returned
   * @param numEntries number of non-zero entries, usually a combination of +/-1 entries 
   *        as determined by implementation
   * @param random random seed for generation
     * @return 
   */
  public abstract Vector generateRandomVector(int dimension, int numEntries, Random random);
  
  /** Returns the dimension of the vector.
     * @return  */
  public abstract int getDimension();
  
  /** Returns the {@link VectorType} of the vector.
     * @return  */
  public abstract VectorType getVectorType(); 
  
  /** Returns true if this is a representation of a zero vector.
     * @return  */
  public abstract boolean isZeroVector();
  
  /**
   * Returns a canonical overlap measure (usually between 0 and 1) between this and other.
     * @param other
     * @return 
   */
  public abstract double measureOverlap(Vector other);
  
  /**
   * Superposes other vector onto this one with given weight, usually by vector addition.
   * Coordinates of other vector may be permuted and reweighted in the process.
   * 
   * Permuted coordinates supports encoding of order information, as begun by Sahlgren, Holst and 
   * Kanerva 2008.
   * 
   * @param other vector to be added
   * @param weight multiple of {@code other} vector added (TODO widdows support complex weights)
   * @param permutation represents the permutation to be applied while adding, so that the ith
   *        coordinate of {@code other} is added to the coordinate given by the ith entry in this
   *        list. Can be NULL in which case no permutation is applied. 
   */
  public abstract void superpose(Vector other, double weight, int[] permutation);

  /**
   * Binds the other vector to this one.
     * @param other
   */
  public abstract void bind(Vector other);
 
  /**
   * Inverse of the binding operator. May be an approximate inverse.
     * @param other
   */
  public abstract void release(Vector other);
  
  /**
   * Transforms vector to a normalized representation.  A normalized representation should
   * satisfy the property that {@code measureOverlap} with itself is equal to 1.0, to within
   * precision tolerances.
   */
  public abstract void normalize();
  
  /**
   * Writes vector to Lucene output stream.  Writes exactly {@link #getDimension} coordinates.
     * @param outputStream
   */
  public abstract void writeToStream(DataOutputStream outputStream);

  /**
   * Reads vector from Lucene input stream.  Reads exactly {@link #getDimension} coordinates.
     * @param inputStream
   */
  public abstract void readFromStream(DataInputStream inputStream);
  
  /**
   * Writes vector to text representation.  Writes exactly {@link #getDimension} coordinates.
     * @return 
   */
  public abstract String writeToString();

  /**
   * Reads vector from text representation.  Reads exactly {@link #getDimension} coordinates.
     * @param input
   */
  public abstract void readFromString(String input);
 
    /**
     *
     * @return
     */
    @Override
 /**
  * Subclasses must implement a string representation for debugging. This may be different and
  * more explicit than the {@code writeToString} method.
  */
  public abstract String toString();
}
