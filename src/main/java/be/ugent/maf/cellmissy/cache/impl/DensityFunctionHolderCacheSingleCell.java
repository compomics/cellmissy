/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.cache.impl;

import be.ugent.maf.cellmissy.cache.Cache;
import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.stereotype.Component;

/**
 *
 * @author Paola
 */
@Component("densityFunctionHolderCacheSingleCell")
public class DensityFunctionHolderCacheSingleCell extends LinkedHashMap<SingleCellConditionDataHolder, Map<DensityFunctionHolderCacheSingleCell.DataCategory, List<List<double[]>>>> implements Cache<SingleCellConditionDataHolder, Map<DensityFunctionHolderCacheSingleCell.DataCategory, List<List<double[]>>>> {

    /**
     * ENUM for data category: is it raw data or already corrected data?
     */
    public enum DataCategory {

        INST_DISPL, TRACK_DISPL, TRACK_SPEED
    }

    @Override
    public void putInCache(SingleCellConditionDataHolder singleCellConditionDataHolder, Map<DensityFunctionHolderCacheSingleCell.DataCategory, List<List<double[]>>> densityFunction) {
        this.put(singleCellConditionDataHolder, densityFunction);
    }

    @Override
    public Map<DensityFunctionHolderCacheSingleCell.DataCategory, List<List<double[]>>> getFromCache(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        return this.get(singleCellConditionDataHolder);
    }

    /**
     * Method from LinkedHashMap
     *
     * @param eldest
     * @return
     */
    @Override
    protected boolean removeEldestEntry(Entry<SingleCellConditionDataHolder, Map<DensityFunctionHolderCacheSingleCell.DataCategory, List<List<double[]>>>> eldest) {
        int maximumCacheSize = PropertiesConfigurationHolder.getInstance().getInt("densityFunctionCache.maximumCacheSize");
        return this.size() > maximumCacheSize;
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
