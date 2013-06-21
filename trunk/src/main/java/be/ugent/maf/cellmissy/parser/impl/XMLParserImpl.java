/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.parser.impl;

import be.ugent.maf.cellmissy.parser.XMLParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.springframework.stereotype.Service;

/**
 * This class implements the XMLParser interface and takes care of
 * marshaling/unmarshaling XML documents. This class in annotated as a spring
 * bean: service.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Service("xMLParser")
public class XMLParserImpl implements XMLParser {

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
    public <T> T unmarshal(Class<T> clazz, File xmlFile) throws JAXBException {
        // we create a new JAXBContext object
        JAXBContext jAXBContext = JAXBContext.newInstance(clazz);
        // we then create an Unmarshaller object
        Unmarshaller unmarshaller = jAXBContext.createUnmarshaller();
        // unmarshal the XML file to the Object
        T t = (T)unmarshaller.unmarshal(xmlFile);
        return t;
    }
}
