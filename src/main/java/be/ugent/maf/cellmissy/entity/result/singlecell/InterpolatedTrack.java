package be.ugent.maf.cellmissy.entity.result.singlecell;

import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.util.FastMath;

/**
 * This class holds the results of applying an interpolation to a single cell
 * track.
 * <p>
 * Created by Paola on 2/1/2016.
 */
public class InterpolatedTrack {

    // the interpolant time
    private double[] interpolantTime;
    // the interpolated x coordinates
    private double[] interpolatedX;
    // the interpolated y coordinates
    private double[] interpolatedY;
    // the polynomial function for the x interpolation
    private PolynomialFunction polynomialFunctionX;
    // the polynomial function for the y interpolation
    private PolynomialFunction polynomialFunctionY;

    /**
     * Empty constructor.
     */
    public InterpolatedTrack() {
    }

    /**
     * Constructor.
     *
     * @param interpolantTime
     * @param interpolatedX
     * @param interpolatedY
     * @param polynomialFunctionX
     * @param polynomialFunctionY
     */
    public InterpolatedTrack(double[] interpolantTime, double[] interpolatedX, double[] interpolatedY, PolynomialFunction polynomialFunctionX, PolynomialFunction polynomialFunctionY) {
        this.interpolantTime = interpolantTime;
        this.interpolatedX = interpolatedX;
        this.interpolatedY = interpolatedY;
        this.polynomialFunctionX = polynomialFunctionX;
        this.polynomialFunctionY = polynomialFunctionY;
    }

    public double[] getInterpolantTime() {
        return interpolantTime;
    }

    public void setInterpolantTime(double[] interpolantTime) {
        this.interpolantTime = interpolantTime;
    }

    public double[] getInterpolatedX() {
        return interpolatedX;
    }

    public void setInterpolatedX(double[] interpolatedX) {
        this.interpolatedX = interpolatedX;
    }

    public double[] getInterpolatedY() {
        return interpolatedY;
    }

    public void setInterpolatedY(double[] interpolatedY) {
        this.interpolatedY = interpolatedY;
    }

    public PolynomialFunction getPolynomialFunctionX() {
        return polynomialFunctionX;
    }

    public void setPolynomialFunctionX(PolynomialFunction polynomialFunctionX) {
        this.polynomialFunctionX = polynomialFunctionX;
    }

    public PolynomialFunction getPolynomialFunctionY() {
        return polynomialFunctionY;
    }

    public void setPolynomialFunctionY(PolynomialFunction polynomialFunctionY) {
        this.polynomialFunctionY = polynomialFunctionY;
    }

    @Override
    public String toString() {
        double[] xCoeff = polynomialFunctionX.getCoefficients();
        double[] yCoeff = polynomialFunctionY.getCoefficients();

        StringBuilder s1 = new StringBuilder();
        if (xCoeff[0] == 0.0) {
            if (xCoeff.length == 1) {
                return "0";
            }
        } else {
            s1.append(toString(xCoeff[0]));
        }

        for (int i = 1; i < xCoeff.length; ++i) {
            if (xCoeff[i] != 0) {
                if (s1.length() > 0) {
                    if (xCoeff[i] < 0) {
                        s1.append(" - ");
                    } else {
                        s1.append(" + ");
                    }
                } else {
                    if (xCoeff[i] < 0) {
                        s1.append("-");
                    }
                }

                double absAi = FastMath.abs(xCoeff[i]);
                if ((absAi - 1) != 0) {
                    s1.append(toString(absAi));
                    s1.append(' ');
                }

                s1.append("x");
                if (i > 1) {
                    s1.append('^');
                    s1.append(Integer.toString(i));
                }
            }
        }

        StringBuilder s2 = new StringBuilder();
        if (yCoeff[0] == 0.0) {
            if (yCoeff.length == 1) {
                return "0";
            }
        } else {
            s2.append(toString(yCoeff[0]));
        }

        for (int i = 1; i < yCoeff.length; ++i) {
            if (yCoeff[i] != 0) {
                if (s2.length() > 0) {
                    if (yCoeff[i] < 0) {
                        s2.append(" - ");
                    } else {
                        s2.append(" + ");
                    }
                } else {
                    if (yCoeff[i] < 0) {
                        s2.append("-");
                    }
                }

                double absAi = FastMath.abs(yCoeff[i]);
                if ((absAi - 1) != 0) {
                    s2.append(toString(absAi));
                    s2.append(' ');
                }

                s1.append("y");
                if (i > 1) {
                    s1.append('^');
                    s1.append(Integer.toString(i));
                }
            }
        }
        String tot = s1.toString() + "\n" + s2.toString();
        return tot;
    }

    /**
     *
     * @param coeff
     * @return
     */
    private static String toString(double coeff) {
        coeff = AnalysisUtils.roundThreeDecimals(coeff);
        final String c = Double.toString(coeff);
        if (c.endsWith(".0")) {
            return c.substring(0, c.length() - 2);
        } else {
            return c;
        }
    }

}
