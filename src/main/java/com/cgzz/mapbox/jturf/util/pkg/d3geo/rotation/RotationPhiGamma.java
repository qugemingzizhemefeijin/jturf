package com.cgzz.mapbox.jturf.util.pkg.d3geo.rotation;

import com.cgzz.mapbox.jturf.util.pkg.d3geo.Algorithm;
import com.cgzz.mapbox.jturf.util.pkg.d3geo.util.MathUtil;

public class RotationPhiGamma implements Algorithm {

    private final double cosDeltaPhi;
    private final double sinDeltaPhi;
    private final double cosDeltaGamma;
    private final double sinDeltaGamma;

    public RotationPhiGamma(double deltaPhi, double deltaGamma) {
        cosDeltaPhi = Math.cos(deltaPhi);
        sinDeltaPhi = Math.sin(deltaPhi);
        cosDeltaGamma = Math.cos(deltaGamma);
        sinDeltaGamma = Math.sin(deltaGamma);
    }

    @Override
    public double[] def(double lambda, double phi) {
        double cosPhi = Math.cos(phi),
                x = Math.cos(lambda) * cosPhi,
                y = Math.sin(lambda) * cosPhi,
                z = Math.sin(phi),
                k = z * cosDeltaPhi + x * sinDeltaPhi;

        return new double[]{
                Math.atan2(y * cosDeltaGamma - k * sinDeltaGamma, x * cosDeltaPhi - z * sinDeltaPhi),
                MathUtil.asin(k * cosDeltaGamma + y * sinDeltaGamma)
        };
    }

    @Override
    public double[] invert(double lambda, double phi) {
        double cosPhi = Math.cos(phi),
                x = Math.cos(lambda) * cosPhi,
                y = Math.sin(lambda) * cosPhi,
                z = Math.sin(phi),
                k = z * cosDeltaGamma - y * sinDeltaGamma;

        return new double[]{
                Math.atan2(y * cosDeltaGamma + z * sinDeltaGamma, x * cosDeltaPhi + k * sinDeltaPhi),
                MathUtil.asin(k * cosDeltaPhi - x * sinDeltaPhi)
        };
    }

}
