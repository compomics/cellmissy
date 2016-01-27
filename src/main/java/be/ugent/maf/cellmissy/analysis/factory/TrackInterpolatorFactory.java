/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.factory;

import be.ugent.maf.cellmissy.analysis.singlecell.preprocessing.TrackInterpolator;
import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
import java.util.Map;
import java.util.Set;
import org.springframework.context.ApplicationContext;

/**
 * Factory for the interpolation methods.
 *
 * @author Paola
 */
public class TrackInterpolatorFactory {

    /**
     * This is mapping the bean names with with their respective implementations
     * for the track interpolations.
     */
    private final Map<String, TrackInterpolator> interpolators;
    private static final TrackInterpolatorFactory TRACK_INTERPOLATOR_FACTORY = new TrackInterpolatorFactory();

    /**
     * Private constructor.
     */
    private TrackInterpolatorFactory() {
        ApplicationContext context = ApplicationContextProvider.getInstance().getApplicationContext();
        interpolators = context.getBeansOfType(TrackInterpolator.class);
    }

    /**
     * Get an instance.
     *
     * @return
     */
    public static TrackInterpolatorFactory getInstance() {
        return TRACK_INTERPOLATOR_FACTORY;
    }

    /**
     * Get the right track interpolator according to the bean name.
     *
     * @param beanName
     * @return
     */
    public TrackInterpolator getTrackInterpolator(String beanName) {
        return interpolators.get(beanName);
    }

    /**
     * Get the all set of strings for the track interpolation techniques.
     *
     * @return
     */
    public Set<String> getTrackInterpolatorsBeanNames() {
        return interpolators.keySet();
    }
}
