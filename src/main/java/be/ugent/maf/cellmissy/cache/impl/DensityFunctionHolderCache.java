/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.cache.impl;

import be.ugent.maf.cellmissy.cache.Cache;
import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.stereotype.Component;

/**
 *
 * @author Paola Masuzzo
 */
@Component("densityFunctionHolderCache")
public class DensityFunctionHolderCache extends LinkedHashMap<PlateCondition, Map<DensityFunctionHolderCache.DataCategory, List<List<double[]>>>> implements Cache<PlateCondition, Map<DensityFunctionHolderCache.DataCategory, List<List<double[]>>>> {

    //set maximum cache size from properties file
    private static final int MAXIMUM_CACHE_SIZE = PropertiesConfigurationHolder.getInstance().getInt("densityFunctionCache.maximumCacheSize");

    /**
     * enum for data category: is it raw data or already corrected data?
     */
    public enum DataCategory {

        RAW_DATA, CORRECTED_DATA;
    }

    /**
     * Puts the given densityFunction in the cache for the given PlateCondition.
     * If the maximum size is reached, the first added element is removed and replaced
     * by the given plateCondition.
     * @param plateCondition
     * @param densityFunction 
     */
    @Override
    public void putInCache(PlateCondition plateCondition, Map<DensityFunctionHolderCache.DataCategory, List<List<double[]>>> densityFunction) {
        this.put(plateCondition, densityFunction);
    }

    /**
     * Get the density function by its key, the plateCondition
     * @param plateCondition
     * @return the DensityFunction for the given plate condition
     */
    @Override
    public Map<DensityFunctionHolderCache.DataCategory, List<List<double[]>>> getFromCache(PlateCondition plateCondition) {
        return this.get(plateCondition);
    }

    /**
     * Method from LinkedHashMap
     * @param eldest
     * @return 
     */
    @Override
    protected boolean removeEldestEntry(Entry<PlateCondition, Map<DensityFunctionHolderCache.DataCategory, List<List<double[]>>>> eldest) {
        return this.size() > MAXIMUM_CACHE_SIZE;
    }

    @Override
    public int getCacheSize() {
        return this.size();
    }

    @Override
    public void clearCache() {
        this.clear();
    }
}
