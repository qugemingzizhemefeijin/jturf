package com.cgzz.mapbox.jturf.util.clipping.polygonclipping;

import com.cgzz.mapbox.jturf.shape.impl.Point;

import java.util.ArrayList;
import java.util.List;

public class MultiPolyOut {

    List<RingOut> rings;
    List<PolyOut> polys;

    public MultiPolyOut(List<RingOut> rings) {
        this.rings = rings;
        this.polys = this._composePolys(rings);
    }

    public List<List<List<Point>>> getGeom() {
        List<List<List<Point>>> geom = new ArrayList<>();
        for (PolyOut poly : this.polys) {
            List<List<Point>> polyGeom = poly.getGeom();
            // exterior ring was all (within rounding error of angle calc) colinear points
            if (polyGeom == null) {
                continue;
            }
            geom.add(polyGeom);
        }
        return geom;
    }

    private List<PolyOut> _composePolys(List<RingOut> rings) {
        List<PolyOut> polys = new ArrayList<>();
        for (RingOut ring : rings) {
            if (ring.poly != null) {
                continue;
            }
            if (ring.isExteriorRing()) {
                polys.add(new PolyOut(ring));
            } else {
                RingOut enclosingRing = ring.enclosingRing();
                if (enclosingRing != null) {
                    if (enclosingRing.poly == null) {
                        polys.add(new PolyOut(enclosingRing));
                    }
                    enclosingRing.poly.addInterior(ring);
                }
            }
        }
        return polys;
    }

}
