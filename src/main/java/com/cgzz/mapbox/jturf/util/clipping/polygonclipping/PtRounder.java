package com.cgzz.mapbox.jturf.util.clipping.polygonclipping;

public class PtRounder {

    private final CoordRounder xRounder = new CoordRounder();
    private final CoordRounder yRounder = new CoordRounder();

    public Location round(double x, double y) {
        return new Location(this.xRounder.round(x), this.yRounder.round(y));
    }

}
