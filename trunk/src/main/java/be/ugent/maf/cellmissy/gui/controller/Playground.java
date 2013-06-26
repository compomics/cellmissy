 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.parser.XMLParser;
import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.xml.sax.SAXException;

/**
 * Playground class
 *
 * @author Paola Masuzzo
 */
public class Playground {

    public static void main(String[] args) {
        // get the application context
        ApplicationContext context = ApplicationContextProvider.getInstance().getApplicationContext();

        XMLParser xMLParser = (XMLParser) context.getBean("xMLParser");
        try {
            xMLParser.unmarshal(Experiment.class, new File("C:\\Users\\paola\\Desktop\\wrong template.xml"));
            //        LocalContainerEntityManagerFactoryBean fb = (LocalContainerEntityManagerFactoryBean) context.getBean("&entityManagerFactory");
            //        Ejb3Configuration cfg = new Ejb3Configuration();
            //        Ejb3Configuration configured = cfg.configure(fb.getPersistenceUnitInfo(), fb.getJpaPropertyMap());
            //        // export the database schema
            //        SchemaExport schemaExport = new SchemaExport(configured.getHibernateConfiguration());
            //
            //        schemaExport.setOutputFile("C:\\Users\\paola\\Desktop\\testing_schema.txt");
            //        schemaExport.setFormat(true);
            //        schemaExport.execute(true, false, false, true);
            //        schemaExport.execute(true, false, false, true);
        } catch (SAXException ex) {
            System.out.println("SAXException" + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("IOException" + ex.getMessage());
        } catch (JAXBException ex) {
            List<String> validationErrorMesage = xMLParser.getValidationErrorMesage();
            System.out.println("JAXBException" + ex.getMessage());
        }
    }
}
