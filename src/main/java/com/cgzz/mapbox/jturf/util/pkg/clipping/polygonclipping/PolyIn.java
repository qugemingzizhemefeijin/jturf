package com.cgzz.mapbox.jturf.util.pkg.clipping.polygonclipping;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.impl.Point;

import java.util.ArrayList;
import java.util.List;

public class PolyIn {

    private final RingIn exteriorRing;
    private final Bbox bbox;
    private final List<RingIn> interiorRings;
    private final MultiPolyIn multiPoly;

    public PolyIn(List<List<Point>> geomPoly, MultiPolyIn multiPoly, PtRounder rounder, Operation operation) {
        if (geomPoly == null || geomPoly.isEmpty()) {
            throw new JTurfException("Input geometry is not a valid Polygon or MultiPolygon");
        }

        this.exteriorRing = new RingIn(geomPoly.get(0), this, true, rounder, operation);
        // copy by value
        this.bbox = new Bbox(
                Location.location(this.exteriorRing.getBbox().ll.x, this.exteriorRing.getBbox().ll.y),
                Location.location(this.exteriorRing.getBbox().ur.x, this.exteriorRing.getBbox().ur.y)
        );

        this.interiorRings = new ArrayList<>();
        for (int i = 1, iMax = geomPoly.size(); i < iMax; i++) {
            RingIn ring = new RingIn(geomPoly.get(i), this, false, rounder, operation);
            if (ring.getBbox().ll.x < this.bbox.ll.x) {
                this.bbox.ll.x = ring.getBbox().ll.x;
            }
            if (ring.getBbox().ll.y < this.bbox.ll.y) {
                this.bbox.ll.y = ring.getBbox().ll.y;
            }
            if (ring.getBbox().ur.x > this.bbox.ur.x) {
                this.bbox.ur.x = ring.getBbox().ur.x;
            }
            if (ring.getBbox().ur.y > this.bbox.ur.y) {
                this.bbox.ur.y = ring.getBbox().ur.y;
            }
            this.interiorRings.add(ring);
        }
        this.multiPoly = multiPoly;
    }

    public List<SweepEvent> getSweepEvents() {
        List<SweepEvent> sweepEvents = this.exteriorRing.getSweepEvents();
        for (RingIn interiorRing : this.interiorRings) {
            sweepEvents.addAll(interiorRing.getSweepEvents());
        }
        return sweepEvents;
    }

    public Bbox getBbox() {
        return bbox;
    }

    public MultiPolyIn getMultiPoly() {
        return multiPoly;
    }
}
