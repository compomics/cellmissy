/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import javax.xml.bind.UnmarshalException;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * This class extends the abstract class XMLAdapter: for certain XML attributes
 * in our entity classes, we need to unmarshal an empty string as a not
 * acceptable value, that means if an empty string is returned, we throw a new
 * UnmarshalException. The error is then reported to the user through our
 * ValidationEventHandler (XmlValidator class).
 *
 * In some entity classes, we make use of this adapter with the annotation:
 *
 * @XmlJavaTypeAdapter(EmptyStringXMLAdapter.class)
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
