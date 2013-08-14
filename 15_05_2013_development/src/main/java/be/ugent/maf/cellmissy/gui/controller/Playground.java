 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.WellService;
import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
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

    public static void main(String[] args) {
        // get the application context
        ApplicationContext context = ApplicationContextProvider.getInstance().getApplicationContext();

//        ExperimentService experimentService = (ExperimentService) context.getBean("experimentService");
//        WellService wellService = (WellService) context.getBean("wellService");
//        Experiment experiment = experimentService.findById(1L);
//        ExperimentStatus experimentStatus = experiment.getExperimentStatus();
//        System.out.println("" + experimentStatus);
//
//        for (PlateCondition plateCondition : experiment.getPlateConditionList()) {
//            List<Well> wells = new ArrayList<>();
//            for (Well well : plateCondition.getWellList()) {
//                Well fetchedWell = wellService.fetchMigrationData(well.getWellid());
//                wells.add(fetchedWell);
//            }
//            plateCondition.setWellList(wells);
//        }
//
//
//        try {
//            experimentService.exportExperimentToXMLFile(experiment, new File("C:\\Users\\paola\\Desktop\\test.xml"));
//        } catch (JAXBException | FileNotFoundException ex) {
//            Logger.getLogger(Playground.class.getName()).log(Level.SEVERE, null, ex);
//        }


        LocalContainerEntityManagerFactoryBean fb = (LocalContainerEntityManagerFactoryBean) context.getBean("&entityManagerFactory");
        Ejb3Configuration cfg = new Ejb3Configuration();
        Ejb3Configuration configured = cfg.configure(fb.getPersistenceUnitInfo(), fb.getJpaPropertyMap());
        // export the database schema
        SchemaExport schemaExport = new SchemaExport(configured.getHibernateConfiguration());

        schemaExport.setOutputFile("C:\\Users\\paola\\Desktop\\testing_schema.sql");
        schemaExport.setFormat(true);
        schemaExport.execute(true, false, false, true);
        schemaExport.execute(true, false, false, true);

    }
}
