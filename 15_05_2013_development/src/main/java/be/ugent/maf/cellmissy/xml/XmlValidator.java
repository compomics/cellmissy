/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

/**
 * This class implements the ValidationEventhandler interface. We make the
 * handle event return true, so if an error occurs, we collect the events and we
 * can get back the error information. If a fatal error occurs, e.g. a
 * SAXParseException is thrown because a wrong syntax in the xsd schema, this
 * returns false and the unmarshaling stops.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class XmlValidator implements ValidationEventHandler {

    private List<ValidationEvent> validationEvents = new ArrayList<>();

    public List<ValidationEvent> getValidationEvents() {
        return validationEvents;
    }

    @Override
    public boolean handleEvent(ValidationEvent event) {
        validationEvents.add(event);
        return true;
    }
}
