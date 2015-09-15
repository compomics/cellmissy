/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.cache;

/**
 * Interface cache 
 * @param <K> 
 * @param <V> 
 * @author Paola Masuzzo
 */
public interface Cache<K, V> {

    /**
     * Put a new Entry in Cache
     * @param key the key
     * @param value the value
     */
    void putInCache(K key, V value);

    /**
     * Get a Value from Cache, returns null if no value was found
     * @param key
     * @return the found value
     */
    V getFromCache(K key);

    /**
     * Get the cache size
     * @return the cache size value
     */
    int getCacheSize();

    /**
     * Clear the cache
     */
    void clearCache();
}
