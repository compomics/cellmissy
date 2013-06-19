/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
import java.util.Map;
import java.util.Set;
import org.springframework.context.ApplicationContext;

/**
 * Factory for outliers detection and correction algorithms.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class OutliersHandlerFactory {

    /**
     * This is mapping the bean names with the respective implementations for
     * the OutliersHandler.
     */
    private Map<String, OutliersHandler> outliersHandlers;
    private static final OutliersHandlerFactory outliersHandlerFactory = new OutliersHandlerFactory();

    /**
     * Private constructor.
     */
    private OutliersHandlerFactory() {
        ApplicationContext context = ApplicationContextProvider.getInstance().getApplicationContext();
        outliersHandlers = context.getBeansOfType(OutliersHandler.class);
    }

    /**
     * Get an instance.
     */
    public static OutliersHandlerFactory getInstance() {
        return outliersHandlerFactory;
    }

    /**
     * Get the outlier handler according to the bean name.
     *
     * @param beanName
     * @return
     */
    public OutliersHandler getOutliersHandler(String beanName) {
        return outliersHandlers.get(beanName);
    }

    /**
     * Get the all set of strings for the outliers beans names.
     *
     * @return
     */
    public Set<String> getOutliersHandlersBeanNames() {
        return outliersHandlers.keySet();
    }
}
