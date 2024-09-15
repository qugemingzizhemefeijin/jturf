package com.cgzz.mapbox.jturf.util.pkg.d3geo;

public class Compose implements Algorithm {

    private final Algorithm a;
    private final Algorithm b;

    public Compose(Algorithm a, Algorithm b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public double[] def(double x, double y) {
        double[] t = a.def(x, y);
        return b.def(t[0], t[1]);
    }

    @Override
    public double[] invert(double x, double y) {
        double[] t = b.invert(x, y);

        return a.invert(t[0], t[1]);
    }

}
