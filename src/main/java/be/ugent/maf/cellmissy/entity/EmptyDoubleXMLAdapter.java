/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import javax.xml.bind.UnmarshalException;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * This class extends the abstract class XMLAdapter: for Double values, we need
 * to catch NumberFormatException and throw an UnmarshalException; the
 * ValidationEventHandler will then take care of reporting XML validation errors
 * encountered during the unmarshaling task to the user.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
class EmptyDoubleXMLAdapter extends XmlAdapter<String, Double> {

    @Override
    public Double unmarshal(String v) throws Exception {
        try {
            return new Double(v);
        } catch (NumberFormatException e) {
            throw new UnmarshalException("Be sure you are importing valid numbers!");
        }
    }

    @Override
    public String marshal(Double v) throws Exception {
        return v.toString();
    }
}
