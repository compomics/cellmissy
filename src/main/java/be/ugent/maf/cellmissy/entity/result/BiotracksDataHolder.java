/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result;

import java.util.HashMap;
import java.util.List;

/**
 * This class holds information on imported CMSO biotracks data. Contains
 * tracking software used, well coordinates and maps with objects and links
 * information.
 *
 * @author Gwendolien Sergeant
 */
public class BiotracksDataHolder {

    // Have tried to put object info in new Object with id, frame, x and y info
    // but not straightforward in finding object by id using this Object
    // objectsmap inner list contains in this order: frame, x, y
    private final HashMap<Integer, List<Double>> objectsMap;

    private final HashMap<Integer, List<Integer>> linksMap;

    public BiotracksDataHolder(HashMap<Integer, List<Double>> objectsMap, HashMap<Integer, List<Integer>> linksMap) {
        this.objectsMap = objectsMap;
        this.linksMap = linksMap;
    }

    /**
     * Getters and setters
     *
     * @return
     */
    public HashMap<Integer, List<Double>> getObjectsMap() {
        return objectsMap;
    }

    public HashMap<Integer, List<Integer>> getLinksMap() {
        return linksMap;
    }

}
