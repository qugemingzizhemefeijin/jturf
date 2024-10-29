package com.cgzz.mapbox.jturf.util.pkg.skmeans;

import com.cgzz.mapbox.jturf.shape.impl.Point;

public final class SkmeansPrs {

    private int i;

    private double[] v;

    private double pr;

    private double cs;

    public SkmeansPrs(int i, double[] v, double pr, double cs) {
        this.i = i;
        this.v = v;
        this.pr = pr;
        this.cs = cs;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public double[] getV() {
        return v;
    }

    public void setV(double[] v) {
        this.v = v;
    }

    public double getPr() {
        return pr;
    }

    public void setPr(double pr) {
        this.pr = pr;
    }

    public double getCs() {
        return cs;
    }

    public void setCs(double cs) {
        this.cs = cs;
    }
}
