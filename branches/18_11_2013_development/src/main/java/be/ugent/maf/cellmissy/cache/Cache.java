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
    public void putInCache(K key, V value);

    /**
     * Get a Value from Cache, returns null if no value was found
     * @param key
     * @return the found value
     */
    public V getFromCache(K key);

    /**
     * Get the cache size
     * @return the cache size value
     */
    public int getCacheSize();

    /**
     * Clear the cache
     */
    public void clearCache();
}
