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
 * Factory for the statistics test.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class StatisticsTestFactory {

    /**
     * This is mapping the bean names with the respective implementations for
     * the statistical tests.
     */
    private Map<String, StatisticsCalculator> statisticsCalculators;
    private static final StatisticsTestFactory statisticsTestFactory = new StatisticsTestFactory();

    /**
     * Private Constructor
     */
    private StatisticsTestFactory() {
        ApplicationContext context = ApplicationContextProvider.getInstance().getApplicationContext();
        statisticsCalculators = context.getBeansOfType(StatisticsCalculator.class);
    }

    /**
     * Get an instance.
     *
     * @return
     */
    public static StatisticsTestFactory getInstance() {
        return statisticsTestFactory;
    }

    /**
     * Get the statistical calculator according to the bean name
     *
     * @param beanName
     * @return
     */
    public StatisticsCalculator getStatisticsCalculator(String beanName) {
        return statisticsCalculators.get(beanName);
    }

    /**
     * Get the all set of strings for the statistical tests beans names.
     *
     * @return a set of strings from the map.
     */
    public Set<String> getStatisticsCalculatorBeanNames() {
        return statisticsCalculators.keySet();
    }
}
