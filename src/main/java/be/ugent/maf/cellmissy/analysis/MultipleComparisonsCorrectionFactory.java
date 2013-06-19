/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.context.ApplicationContext;

/**
 * Factory for multiple test correction algorithms.
 *
 * @author Paola Masuzzo
 */
public class MultipleComparisonsCorrectionFactory {

    /**
     * This is mapping the bean names with the respective implementations for
     * the MultipleComparisonsCorrector.
     */
    private Map<String, MultipleComparisonsCorrector> correctors;
    private static final MultipleComparisonsCorrectionFactory multipleComparisonsCorrectionFactory = new MultipleComparisonsCorrectionFactory();

    /**
     * Private constructor.
     */
    private MultipleComparisonsCorrectionFactory() {
        ApplicationContext context = ApplicationContextProvider.getInstance().getApplicationContext();
        correctors = context.getBeansOfType(MultipleComparisonsCorrector.class);
    }

    /**
     * Get an instance.
     */
    public static MultipleComparisonsCorrectionFactory getInstance() {
        return multipleComparisonsCorrectionFactory;
    }

    /**
     * Get the corrector according to the correction bean name.
     *
     * @param String: the name of the bean for the corrector
     * @return
     */
    public MultipleComparisonsCorrector getCorrector(String beanName) {
        return correctors.get(beanName);
    }

    /**
     * Get the all set of strings for the correction beans names.
     *
     * @return a set of strings from the map.
     */
    public Set<String> getCorrectionBeanNames() {
        return correctors.keySet();
    }
}
