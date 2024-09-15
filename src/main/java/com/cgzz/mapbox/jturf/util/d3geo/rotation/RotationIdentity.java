package com.cgzz.mapbox.jturf.util.d3geo.rotation;

import com.cgzz.mapbox.jturf.util.d3geo.Algorithm;
import com.cgzz.mapbox.jturf.util.d3geo.util.MathUtil;

public class RotationIdentity implements Algorithm {

    @Override
    public double[] def(double lambda, double phi) {
        return new double[]{lambda > MathUtil.PI ? lambda - MathUtil.TAU : lambda < -MathUtil.PI ? lambda + MathUtil.TAU : lambda, phi};
    }

    @Override
    public double[] invert(double lambda, double phi) {
        return this.def(lambda, phi);
    }

}
