/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Paola Masuzzo
 */
@XmlType(namespace = "http://maf.ugent.be/beans/cellmissy")
public enum ExperimentStatus {

    IN_PROGRESS, PERFORMED;
}
