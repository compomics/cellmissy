    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Assay;
import be.ugent.maf.cellmissy.entity.CellLine;
import be.ugent.maf.cellmissy.entity.CellLineType;
import be.ugent.maf.cellmissy.entity.EcmComposition;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
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
        ApplicationContext context = ApplicationContextProvider.getInstance().getApplicationContext();
        ExperimentService experimentService = (ExperimentService) context.getBean("experimentService");
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
        List<Experiment> experiments = experimentService.findExperimentsByProjectId(1L);
        Experiment experiment = experiments.get(0);

        
        JAXBContext jc = JAXBContext.newInstance(Experiment.class);
        //Create marshaller
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        Unmarshaller un = jc.createUnmarshaller();
//        CellLine cellLine = new CellLine("0 h", 12000, "medium", 0.5, new CellLineType(), "serum");

        //Marshal object into file.
//
        File file = new File("C:\\users\\paola\\desktop\\test.xml");
        m.marshal(experiment, new FileOutputStream(file));
//        CellLine unmarshal = (CellLine) un.unmarshal(file);

        System.out.println("test");
    }
}
