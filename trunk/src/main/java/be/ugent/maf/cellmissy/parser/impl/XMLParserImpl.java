/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.parser.impl;

import be.ugent.maf.cellmissy.parser.XMLParser;
import be.ugent.maf.cellmissy.xml.XmlValidator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

/**
 * This class implements the XMLParser interface and takes care of
 * marshaling/unmarshaling XML documents. This class in annotated as a spring
 * bean: service.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Service("xMLParser")
public class XMLParserImpl implements XMLParser {

    private XmlValidator xmlValidator;

    @Override
    public <T> void marshal(Class<T> clazz, T t, File file) throws JAXBException, FileNotFoundException {
        // we create a new JAXBContext object
        JAXBContext jAXBContext = JAXBContext.newInstance(clazz);
        // we then create a Marshaller object
        Marshaller marshaller = jAXBContext.createMarshaller();
        // we want the XML document to be nicely formatted
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        // Marshal object into the output target: the file
        marshaller.marshal(t, new FileOutputStream(file));
    }

    @Override
    public <T> T unmarshal(Class<T> clazz, File xmlFile) throws JAXBException, SAXException, IOException {
        // we need to validate against our schema
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema cellmissySchema = factory.newSchema(new ClassPathResource("schema/cellmissySchema.xsd").getFile());
        // we create a new JAXBContext object
        JAXBContext jAXBContext = JAXBContext.newInstance(clazz);
        // we then create an Unmarshaller object
        Unmarshaller unmarshaller = jAXBContext.createUnmarshaller();
        unmarshaller.setSchema(cellmissySchema);
        // we set the event handler for the XML validation
        // this will take care of eventual validation errors and warnings
        xmlValidator = new XmlValidator();
        unmarshaller.setEventHandler(xmlValidator);
        // finally unmarshal the XML file to the Object
        T t = (T) unmarshaller.unmarshal(xmlFile);
        return t;
    }

    @Override
    public List<String> getValidationErrorMesage() {
        List<String> validationErrorMessages = new ArrayList<>();
        // get the validation events from the xml validator
        List<ValidationEvent> validationEvents = xmlValidator.getValidationEvents();
        // from each event, we create a specific message with the line number where the error occurred
        for (ValidationEvent validationEvent : validationEvents) {
            int lineNumber = validationEvent.getLocator().getLineNumber();
            String message = validationEvent.getMessage() + "\nCheck line number " + lineNumber + " of xml file.";
            validationErrorMessages.add(message);
        }
        return validationErrorMessages;
    }
}
