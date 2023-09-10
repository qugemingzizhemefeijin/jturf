package com.cgzz.mapbox.jturf.util.d3geo.util;

public final class MathUtil {

    public MathUtil() {
        throw new AssertionError("No Instances.");
    }

    public static final double EPSILON = 1e-6;

    public static final double EPSILON2 = 1e-12;

    public static final double PI = Math.PI;

    public static final double HALF_PI = PI / 2;

    public static final double QUARTER_PI = PI / 4;

    public static final double TAU = PI * 2;

    public static final double DEGREES = 180 / PI;

    public static final double RADIANS = PI / 180;

    public static int sign(double x) {
        return x > 0 ? 1 : x < 0 ? -1 : 0;
    }

    public static double acos(double x) {
        return x > 1 ? 0 : x < -1 ? PI : Math.acos(x);
    }

    public static double asin(double x) {
        return x > 1 ? HALF_PI : x < -1 ? -HALF_PI : Math.asin(x);
    }

    public static double haversin(double x) {
        return (x = Math.sin(x / 2)) * x;
    }

}
