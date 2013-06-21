/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.parser;

import java.io.File;
import java.io.FileNotFoundException;
import javax.xml.bind.JAXBException;

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
     * @param object
     * @param file
     * @throws JAXBException
     * @throws FileNotFoundException
     */
    public <T> void marshal(Class<T> clazz, T t, File file) throws JAXBException, FileNotFoundException;

    /**
     * This method creates a tree of content objects that represents the content
     * and organization of the XML document. This method is doing exactly the
     * opposite of the marshal method in this interface.
     *
     * @param xmlFile
     * @return the Object
     * @throws JAXBException
     */
    public <T> T unmarshal(Class<T> clazz, File xmlFile) throws JAXBException;
}
