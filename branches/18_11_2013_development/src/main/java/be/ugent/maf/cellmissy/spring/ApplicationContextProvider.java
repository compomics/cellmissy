/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 *
 * @author Paola Masuzzo
 */
public class ApplicationContextProvider {

    private ApplicationContext applicationContext;
    private static final ApplicationContextProvider provider = new ApplicationContextProvider();

    /**
     * Private constructor.
     *
     * @throws ExceptionInInitializerError
     */
    private ApplicationContextProvider() throws ExceptionInInitializerError {
        try {
            Resource springXmlConfigResource = new FileSystemResource("mySpringXMLConfig.xml");
            if (!springXmlConfigResource.exists()) {
                this.applicationContext = new ClassPathXmlApplicationContext("mySpringXMLConfig.xml");
            } else {
                this.applicationContext = new FileSystemXmlApplicationContext("mySpringXMLConfig.xml");
            }
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public synchronized static ApplicationContextProvider getInstance() throws ExceptionInInitializerError {
        return provider;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
