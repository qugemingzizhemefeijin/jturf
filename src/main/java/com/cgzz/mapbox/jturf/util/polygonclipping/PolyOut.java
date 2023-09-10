package com.cgzz.mapbox.jturf.util.polygonclipping;

import com.cgzz.mapbox.jturf.shape.Point;

import java.util.ArrayList;
import java.util.List;

public class PolyOut {

    RingOut exteriorRing;
    private List<RingOut> interiorRings;

    public PolyOut(RingOut exteriorRing) {
        this.exteriorRing = exteriorRing;
        exteriorRing.poly = this;
        this.interiorRings = new ArrayList<>();
    }

    public void addInterior(RingOut ring) {
        this.interiorRings.add(ring);
        ring.poly = this;
    }

    public List<List<Point>> getGeom() {
        List<List<Point>> geom = new ArrayList<>();
        geom.add(this.exteriorRing.getGeom());

        // exterior ring was all (within rounding error of angle calc) colinear points
        if (geom.get(0) == null) {
            return null;
        }
        for (RingOut interiorRing : this.interiorRings) {
            List<Point> ringGeom = interiorRing.getGeom();
            // interior ring was all (within rounding error of angle calc) colinear points
            if (ringGeom == null) {
                continue;
            }
            geom.add(ringGeom);
        }
        return geom;
    }

}
