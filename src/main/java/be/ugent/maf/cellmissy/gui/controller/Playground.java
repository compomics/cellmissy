    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * Playground class
 *
 * @author Paola Masuzzo
 */
public class Playground {

    public static void main(String[] args) throws JAXBException, FileNotFoundException {
        // get the application context
//        ApplicationContext context = ApplicationContextProvider.getInstance().getApplicationContext();
//
//        LocalContainerEntityManagerFactoryBean fb = (LocalContainerEntityManagerFactoryBean) context.getBean("&entityManagerFactory");
//
//        Ejb3Configuration cfg = new Ejb3Configuration();
//        Ejb3Configuration configured = cfg.configure(fb.getPersistenceUnitInfo(), fb.getJpaPropertyMap());
//        // export the database schema
//        SchemaExport schemaExport = new SchemaExport(configured.getHibernateConfiguration());
//
//        schemaExport.setOutputFile("C:\\Users\\paola\\Desktop\\testing_schema.txt");
//        schemaExport.setFormat(true);
//        schemaExport.setDelimiter(";");
//        schemaExport.execute(true, false, false, true);

        JAXBContext jc = JAXBContext.newInstance("be.ugent.maf.cellmissy.entity");
        //Create marshaller
        Marshaller m = jc.createMarshaller();
        //Marshal object into file.
        m.marshal(null, new FileOutputStream(new File("C:\\users\\paola\\desktop")));
    }
}
