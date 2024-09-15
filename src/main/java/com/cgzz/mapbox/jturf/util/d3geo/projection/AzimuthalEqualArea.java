package com.cgzz.mapbox.jturf.util.d3geo.projection;

import com.cgzz.mapbox.jturf.util.d3geo.Algorithm;
import com.cgzz.mapbox.jturf.util.d3geo.util.MathUtil;

public class AzimuthalEqualArea implements Algorithm {

    @Override
    public double[] def(double x, double y) {
        return Azimuthal.azimuthalRaw(x, y, cxcy -> Math.sqrt(2 / (1 + cxcy)));
    }

    public double[] invert(double x, double y) {
        return Azimuthal.azimuthalInvert(x, y, z -> 2 * MathUtil.asin(z / 2));
    }

    public static Projection geoAzimuthalEqualArea() {
        return new Projection(new AzimuthalEqualArea()).scale(124.75).clipAngle(180 - 1e-3);
    }

}
