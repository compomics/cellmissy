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
