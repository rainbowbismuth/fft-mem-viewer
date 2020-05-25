package rainbowbismuth.fft;


/**
 * Read a block of bytes from the PS's memory
 */
public interface PSMemory {
    byte[] read(long gameAddress, int size) throws PSMemoryReadException;

    void write(long gameAddress, byte[] memory) throws PSMemoryWriteException;
}
