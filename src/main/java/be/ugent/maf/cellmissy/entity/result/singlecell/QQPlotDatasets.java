/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.singlecell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author ninad
 */
public class QQPlotDatasets {
   

    HashMap<String, double[][]> datasetHashMap = new HashMap();
    
    // public HashMap<String, double[][]> getDatasetHashMap(Map<SingleCellConditionDataHolder, List<TrackDataHolder>> entireExperimentDataholdersMap){
//    public HashMap<String, double[][]> getDatasetHashMap(List<SingleCellConditionDataHolder> allConditions){
//        int i =0;
//        //Array with length of number of conditions
//        double[][] cumdistarray = new double[allConditions.size()][];
//        double[][] eucdistarray = new double[allConditions.size()][];
//        double[][] directionalityarray = new double[allConditions.size()][];  
//        double[][] speedarray = new double[allConditions.size()][];
//        //Loop over every condition in dataholder
//        for(SingleCellConditionDataHolder dataholder : allConditions){
//            
//            cumdistarray[i] = ArrayUtils.toPrimitive((dataholder.getCumulativeDistancesVector()));
//            eucdistarray[i] = ArrayUtils.toPrimitive((dataholder.getEuclideanDistancesVector()));
//            directionalityarray[i] = ArrayUtils.toPrimitive((dataholder.getMedianDirectionalityRatiosVector()));
//            speedarray[i] = ArrayUtils.toPrimitive(dataholder.getTrackSpeedsVector());
//            
//            //for every condition, add arrays into HashMap
//            datasetHashMap.put(dataholder.getPlateCondition().toString(), cumdistarray);
//            datasetHashMap.put(dataholder.getPlateCondition().toString(), eucdistarray);
//            datasetHashMap.put(dataholder.getPlateCondition().toString(), directionalityarray);
//            datasetHashMap.put(dataholder.getPlateCondition().toString(), speedarray);
//            i++;
//        }
//        
//        return datasetHashMap;
//    }
    
    public HashMap<String, List<double[]>> getDatasetHashMap(List<SingleCellConditionDataHolder> allConditions){
        HashMap<String, List<double[]>> datasetHashMap = new HashMap();
        
        for (SingleCellConditionDataHolder dataholder : allConditions){
            double[] cumdist = ArrayUtils.toPrimitive((dataholder.getCumulativeDistancesVector()));
            double[] eucdist = ArrayUtils.toPrimitive((dataholder.getEuclideanDistancesVector()));
            double[] directionality = ArrayUtils.toPrimitive((dataholder.getMedianDirectionalityRatiosVector()));
            double[] speed = ArrayUtils.toPrimitive(dataholder.getTrackSpeedsVector());
            
            if (!datasetHashMap.containsKey(dataholder.getPlateCondition().toString())) {
                List<double[]> list = new ArrayList<double[]>();
                datasetHashMap.put(dataholder.getPlateCondition().toString(), list);
                list.add(new double[] {});
                list.add(new double[] {});
                list.add(new double[] {});
                list.add(new double[] {});
            }
            
            List<double[]> list = datasetHashMap.get(dataholder.getPlateCondition().toString());
            list.set(0, ArrayUtils.addAll(list.get(0), cumdist));
            list.set(1, ArrayUtils.addAll(list.get(1), eucdist));
            list.set(2, ArrayUtils.addAll(list.get(2), directionality));
            list.set(3, ArrayUtils.addAll(list.get(3), speed));

        }
        
        return datasetHashMap;
    }    
} 

