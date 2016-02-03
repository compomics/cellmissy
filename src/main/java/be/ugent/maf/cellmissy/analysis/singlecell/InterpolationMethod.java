package be.ugent.maf.cellmissy.analysis.singlecell;

/**
 * An ENUM class for interpolation methods.
 * <p>
 * Created by Paola on 2/1/2016.
 */
public enum InterpolationMethod {

    LINEAR(1), SPLINE(2), LOESS(3);
    private final int type;

    /**
     * Constructor.
     *
     * @param type
     */
    InterpolationMethod(int type) {
        this.type = type;
    }

    /**
     * Decide how to show the type of the method.
     *
     * @return
     */
    public String getStringForType() {
        String string = "";
        switch (type) {
            case 1:
                string = "linear_interpolator";
                break;
            case 2:
                string = "spline_interpolator";
                break;
            case 3:
                string = "loess_interpolator";
                break;
        }
        return string;
    }
}
