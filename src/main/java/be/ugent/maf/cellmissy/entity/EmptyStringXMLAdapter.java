/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import javax.xml.bind.UnmarshalException;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class EmptyStringXMLAdapter extends XmlAdapter<String, String> {

    @Override
    public String unmarshal(String v) throws Exception {
        if (!v.isEmpty()) {
            return v;
        } else {
            throw new UnmarshalException("Error: string cannot be left empty.");
        }
    }

    @Override
    public String marshal(String v) throws Exception {
        if (null == v) {
            return "";
        }
        return v;
    }
}
