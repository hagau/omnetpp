package org.omnetpp.scave.writers;

import java.io.IOException;

/**
 * An output vector. Output vectors should not be instantiated directly;
 * instances should be obtained from an IOutputVectorManager.
 *  
 * @author Andras
 */
public interface IOutputVector {
    /**
     * Flush this output vector, i.e. force any potentially buffered data to
     * be written out.
     * @throws IOException 
     */
    void flush() throws IOException;

    /**
     * Close this output vector. This may free resources in the output
     * vector manager, but otherwise it is optional. (The output vector
     * manager's close method closes all open vectors as well.)
     * @throws IOException 
     */
    void close() throws IOException;

    /**
     * Record a value with the given time stamp into the output vector. The 
     * return value is true if the data was actually recorded, and false 
     * if it was not recorded (because of filtering, etc.)
     * @throws IOException 
     */
    boolean record(Number time, double value) throws IOException;
}
