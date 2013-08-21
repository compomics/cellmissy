/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.factory;

import be.ugent.maf.cellmissy.analysis.DistanceMetricOperator;
import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
import java.util.Map;
import java.util.Set;
import org.springframework.context.ApplicationContext;

/**
 * Factory for the distance metric between two vectors.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class DistanceMetricFactory {

    /**
     * This is mapping the bean names with the respective implementations for
     * the distance metric.
     */
    private Map<String, DistanceMetricOperator> distanceMetrics;
    private static final DistanceMetricFactory distanceMetricFactory = new DistanceMetricFactory();

    /**
     * Private constructor
     */
    private DistanceMetricFactory() {
        ApplicationContext context = ApplicationContextProvider.getInstance().getApplicationContext();
        distanceMetrics = context.getBeansOfType(DistanceMetricOperator.class);
    }

    /**
     * Get an instance.
     *
     * @return
     */
    public static DistanceMetricFactory getInstance() {
        return distanceMetricFactory;
    }

    /**
     * Get the distance metric operator according to the bean name.
     *
     * @param beanName
     * @return
     */
    public DistanceMetricOperator getDistanceMetricOperator(String beanName) {
        return distanceMetrics.get(beanName);
    }

    /**
     * Get the all set of strings for the correction beans names.
     *
     * @return a set of strings from the map.
     */
    public Set<String> getDistanceMetricsBeanNames() {
        return distanceMetrics.keySet();
    }
}
