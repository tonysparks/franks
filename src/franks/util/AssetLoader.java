/*
 * see license.txt 
 */
package franks.util;

import java.io.IOException;

/**
 * Loads an Asset
 * 
 * @author Tony
 */
public interface AssetLoader<T> {

    /**
     * Loads an Asset
     * 
     * @param filename
     * @return the Asset
     * @throws IOException
     */
    public T loadAsset(String filename) throws IOException;
}
