/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

/**
 * This interface takes care of XML Binding through the JAXB API, providing
 * methods for the marshal and the unmarshal of XML documents.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public interface XMLParser {

    /**
     * This method does the actual marshaling of the content tree. We specify an
     * object that contains the root of the content tree, and the output target,
     * simply a File in the XML extension.
     *
     * @param file
     * @throws JAXBException
     * @throws FileNotFoundException
     */
    <T> void marshal(Class<T> clazz, T t, File file) throws JAXBException, FileNotFoundException;

    /**
     * This method creates a tree of content objects that represents the content
     * and organization of the XML document. This method is doing exactly the
     * opposite of the marshal method in this interface.
     *
     * @param xmlFile
     * @return the Object
     * @throws JAXBException
     * @throws SAXException
     * @throws IOException
     */
    <T> T unmarshal(Class<T> clazz, File xmlFile) throws JAXBException, SAXException, IOException;

    /**
     * Get a list of error messages (if any) that are produced by the
     * ValidationEventHandler is the XML validation was not successful.
     *
     * @return
     */
    List<String> getValidationErrorMesage();
}
