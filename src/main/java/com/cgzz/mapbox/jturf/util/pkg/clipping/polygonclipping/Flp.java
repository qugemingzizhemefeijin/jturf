package com.cgzz.mapbox.jturf.util.pkg.clipping.polygonclipping;

public final class Flp {

    private static final double EPSILON = Math.pow(2, -52);
    private static final double EPSILON_SQ = EPSILON * EPSILON;

    // FLP comparator
    public static int cmp(double a, double b) {
        // check if they're both 0
        if (-EPSILON < a && a < EPSILON) {
            if (-EPSILON < b && b < EPSILON) {
                return 0;
            }
        }

        // check if they're flp equal
        double ab = a - b;
        if (ab * ab < EPSILON_SQ * a * b) {
            return 0;
        }

        // normal comparison
        return a < b ? -1 : 1;
    }

    private Flp() {
        throw new AssertionError("No Instances.");
    }

}
