/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.limsdesktop.repository.impl;

import be.ugent.maf.limsdesktop.entity.PlateFormat;
import be.ugent.maf.limsdesktop.repository.PlateFormatRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola
 */
@Repository("plateFormatRepository")
public class PlateFormatJpaRepository extends GenericJpaRepository<PlateFormat, Long> implements PlateFormatRepository {
    
}

