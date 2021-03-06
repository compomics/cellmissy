/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.analysis.KernelDensityEstimator;
import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;
import be.ugent.maf.cellmissy.exception.TwoOrMoreObservationsException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import umontreal.iro.lecuyer.gof.KernelDensity;
import umontreal.iro.lecuyer.probdist.EmpiricalDist;
import umontreal.iro.lecuyer.probdist.NormalDist;
import umontreal.iro.lecuyer.randvar.KernelDensityGen;
import umontreal.iro.lecuyer.randvar.NormalGen;
import umontreal.iro.lecuyer.rng.MRG31k3p;
import umontreal.iro.lecuyer.rng.RandomStream;

/**
 * This class makes use of "SSJ: Stochastic Simulation in Java" library from
 * iro.umontreal.ca to estimate probability density function of an array of
 * double. It first generates independent and identically distributed random
 * variables from the dataset, at which the density needs to be computed and
 * then generates the vector of density estimates at the corresponding
 * variables.
 *
 * The KernelDensityGen class from the same library is used: the class
 * implements random variate generators for distributions obtained via kernel
 * density estimation methods from a set of n individual observations x1,...,
 * xn. The basic idea is to center a copy of the same symmetric density at each
 * observation and take an equally weighted mixture of the n copies as an
 * estimator of the density from which the observations come. The resulting
 * kernel density has the general form: fn(x) = (1/nh)∑i=1nk((x - xi)/h). K is
 * the kernel (here a Gaussian is chosen) and h is the bandwidth (smoothing
 * factor).
 *
 * @author Paola Masuzzo
 */
public class NormalKernelDensityEstimator implements KernelDensityEstimator {

    private EmpiricalDist empiricalDist;
    private KernelDensityGen kernelDensityGen;

    /**
     * This method initialize the KDE, i.e. sort values in ascending order,
     * compute an empirical distribution out of it, makes use of a NormalGen to
     * generate random variates from the normal distribution, and then use these
     * variates to generate a kernel density generator of the empirical
     * distribution
     *
     * @param data
     */
    private void init(double[] data) throws TwoOrMoreObservationsException {
        Arrays.sort(data);
        if (data.length < 2) {
            throw new TwoOrMoreObservationsException("KDE needs at least 2 obs!");
        }
        empiricalDist = new EmpiricalDist(data);
        //new Stream to randomly generate numbers
        //combined multiple recursive generator (CMRG)
        RandomStream stream = new MRG31k3p();
        NormalGen normalKernelDensityGen = new NormalGen(stream);
        kernelDensityGen = new KernelDensityGen(stream, empiricalDist, normalKernelDensityGen);
    }

    @Override
    public List estimateDensityFunction(Double[] data) throws TwoOrMoreObservationsException {
        // Number Of Density Points to be used
        // This is a measurements of the estimation precision
        // usually, this is set to a default of 512, as in most KDE algorithms default values, i.e. R "density"function, OmicSoft, Matlab algorithms.
        int numberOfDensityPoints = PropertiesConfigurationHolder.getInstance().getInt("numberOfDensityPoints");
        List<double[]> densityFunction = new ArrayList<>();
        //init the KDE with a normal generator
        init(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(data)));
        //compute x values
        //array for random samples
        double[] randomSamples = new double[numberOfDensityPoints];
        for (int i = 0; i < numberOfDensityPoints; i++) {
            double nextDouble = kernelDensityGen.nextDouble();
            randomSamples[i] = nextDouble;
        }
        Arrays.sort(randomSamples);
        densityFunction.add(randomSamples);

        //compute y values
        //use normal default kernel
        NormalDist kern = new NormalDist();
        double datasetSize = (double) data.length;
        //calculate optimal bandwidth with the (ROBUST) Silverman's ‘rule of thumb’ (Scott Variation uses factor = 1.06)
        double bandWidth = 0.99 * Math.min(empiricalDist.getSampleStandardDeviation(), (empiricalDist.getInterQuartileRange() / 1.34)) / Math.pow(datasetSize, 0.2);
        //estimate density and store values in a vector
        double[] estimatedDensityValues = KernelDensity.computeDensity(empiricalDist, kern, bandWidth, randomSamples);
        densityFunction.add(estimatedDensityValues);

        return densityFunction;
    }
}
