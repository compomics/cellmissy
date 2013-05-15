/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository;

import be.ugent.maf.cellmissy.entity.PlateFormat;

/**
 *
 * @author Paola
 */
public interface PlateFormatRepository extends GenericRepository<PlateFormat, Long> {

    PlateFormat findByFormat(int format);
}
