/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository;

import be.ugent.maf.cellmissy.entity.Ecm;
import java.util.List;

/**
 *
 * @author Paola
 */
public interface EcmRepository extends GenericRepository<Ecm, Long> {

    List<String> findAllPolimerysationPh();
}
