/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.limsdesktop.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author niels
 */
public class ApplicationContextProvider {

    private ApplicationContext applicationContext;
    private static final ApplicationContextProvider provider = new ApplicationContextProvider();

    private ApplicationContextProvider() throws ExceptionInInitializerError {
        try {
            this.applicationContext = new ClassPathXmlApplicationContext("mySpringXMLConfig.xml");
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
