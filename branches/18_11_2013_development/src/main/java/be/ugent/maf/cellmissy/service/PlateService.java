/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.PlateFormat;

/**
 *
 * @author Paola
 */
public interface PlateService extends GenericService<PlateFormat, Long> {

    PlateFormat findByFormat(int format);

    public PlateFormat fetchExperiments(PlateFormat plateFormat);
}
