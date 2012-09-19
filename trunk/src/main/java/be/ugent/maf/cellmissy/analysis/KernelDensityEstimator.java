/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import java.util.Arrays;
import umontreal.iro.lecuyer.probdist.EmpiricalDist;
import umontreal.iro.lecuyer.randvar.KernelDensityGen;
import umontreal.iro.lecuyer.randvar.NormalGen;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.rng.RandomStream;

/**
 * This class makes use of "SSJ: Stochastic Simulation in Java" library from iro.umontreal.ca to compute estimation of kernel density estimation of an array of double
 * It first generates independent and identically distributed random variables from the dataset, at which the density needs to be computed
 * and then generates the vector of density estimates at the corresponding variables.
 * 
 * The KernelDensityGen class from the same library is used: the class implements random variate generators for distributions
 * obtained via kernel density estimation methods from a set of n individual observations x1,..., xn.
 * The basic idea is to center a copy of the same symmetric density at each observation and take an equally weighted mixture of the n copies as an estimator of the density
 * from which the observations come. The resulting kernel density has the general form: fn(x) = (1/nh)∑i=1nk((x - xi)/h).
 * K is the kernel and h is the bandwidth (smoothing factor). 
 * @author Paola Masuzzo
 */
public class KernelDensityEstimator {

    //number of points to be used for kernel density estimation
    int n = 4096;
    KernelDensityGen kernelDensityGen;
    EmpiricalDist dist;
    RandomStream stream = new MRG32k3a();
    NormalGen kGen = new NormalGen(stream);

    public KernelDensityEstimator(EmpiricalDist dist) {
        this.dist = dist;
        kernelDensityGen = new KernelDensityGen(stream, dist, kGen);
    }

    /**
     * This method randomly compute points at which density function needs to be estimated
     * @return an array with double values
     */
    public double[] drawRandomSample() {
        //array for random samples (N, estimation precision, is set to a default of 512, as in most KDE algorithms default values, i.e. R function, OmicSoft)
        double[] randomSamples = new double[n];
        for (int i = 0; i < n; i++) {
            double nextDouble = kernelDensityGen.nextDouble();
            randomSamples[i] = nextDouble;
        }
        Arrays.sort(randomSamples);
        return randomSamples;
    }
}
