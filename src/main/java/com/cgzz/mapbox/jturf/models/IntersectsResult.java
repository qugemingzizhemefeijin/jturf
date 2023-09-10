package com.cgzz.mapbox.jturf.models;

public class IntersectsResult {

    private Double x;
    private Double y;
    private boolean onLine1;
    private boolean onLine2;

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public boolean isOnLine1() {
        return onLine1;
    }

    public void setOnLine1(boolean onLine1) {
        this.onLine1 = onLine1;
    }

    public boolean isOnLine2() {
        return onLine2;
    }

    public void setOnLine2(boolean onLine2) {
        this.onLine2 = onLine2;
    }

    @Override
    public String toString() {
        return "IntersectsResult{" +
                "x=" + x +
                ", y=" + y +
                ", onLine1=" + onLine1 +
                ", onLine2=" + onLine2 +
                '}';
    }

}
