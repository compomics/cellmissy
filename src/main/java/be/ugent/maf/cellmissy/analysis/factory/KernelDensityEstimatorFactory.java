/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.factory;

import be.ugent.maf.cellmissy.analysis.KernelDensityEstimator;
import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
import java.util.Map;
import java.util.Set;
import org.springframework.context.ApplicationContext;

/**
 * Factory for kernel density estimators.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class KernelDensityEstimatorFactory {

    /**
     * This is mapping the bean names with the respective implementations for
     * the kernel density estimation.
     */
    private Map<String, KernelDensityEstimator> kernelDensityEstimators;
    private static final KernelDensityEstimatorFactory kernelDensityEstimatorFactory = new KernelDensityEstimatorFactory();

    /**
     * Private constructor
     */
    private KernelDensityEstimatorFactory() {
        ApplicationContext context = ApplicationContextProvider.getInstance().getApplicationContext();
        kernelDensityEstimators = context.getBeansOfType(KernelDensityEstimator.class);
    }

    /**
     * Get an instance
     *
     * @return
     */
    public static KernelDensityEstimatorFactory getInstance() {
        return kernelDensityEstimatorFactory;
    }

    /**
     * Get the estimator according to the bean name
     *
     * @param beanName
     * @return
     */
    public KernelDensityEstimator getKernelDensityEstimator(String beanName) {
        return kernelDensityEstimators.get(beanName);
    }

    /**
     * Get the all set of strings for the correction beans names.
     *
     * @return a set of strings from the map.
     */
    public Set<String> getKernelDensityEstimatorsBeanNames() {
        return kernelDensityEstimators.keySet();
    }
}
