package com.cgzz.mapbox.jturf.util.d3geo.projection;

import com.cgzz.mapbox.jturf.util.d3geo.util.MathUtil;

import java.util.function.DoubleFunction;

public final class Azimuthal {

    public Azimuthal() {
        throw new AssertionError("No Instances.");
    }

    public static double[] azimuthalRaw(double x, double y, DoubleFunction<Double> scale) {
        double cx = Math.cos(x), cy = Math.cos(y), k = scale.apply(cx * cy);
        return new double[]{k * cy * Math.sin(x), k * Math.sin(y)};
    }

    public static double[] azimuthalInvert(double x, double y, DoubleFunction<Double> angle) {
        double z = Math.sqrt(x * x + y * y), c = angle.apply(z), sc = Math.sin(c), cc = Math.cos(c);
        return new double[]{Math.atan2(x * sc, z * cc), MathUtil.asin(z == 0 ? 0 : (y * sc / z))};
    }

}
