package com.cgzz.mapbox.jturf.util.pkg.clipping.polygonclipping;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.impl.Point;

import java.util.ArrayList;
import java.util.List;

public class MultiPolyIn {

    private final List<PolyIn> polys;
    private final Bbox bbox;
    private final boolean isSubject;

    public MultiPolyIn(List<List<List<Point>>> geom, boolean isSubject, PtRounder rounder, Operation operation) {
        if (geom == null || geom.isEmpty()) {
            throw new JTurfException("Input geometry is not a valid Polygon or MultiPolygon");
        }

        this.polys = new ArrayList<>(geom.size());
        this.bbox = new Bbox(
                Location.location(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY),
                Location.location(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY)
        );

        for (List<List<Point>> coords : geom) {
            PolyIn poly = new PolyIn(coords, this, rounder, operation);
            if (poly.getBbox().ll.x < this.bbox.ll.x) {
                this.bbox.ll.x = poly.getBbox().ll.x;
            }
            if (poly.getBbox().ll.y < this.bbox.ll.y) {
                this.bbox.ll.y = poly.getBbox().ll.y;
            }
            if (poly.getBbox().ur.x > this.bbox.ur.x) {
                this.bbox.ur.x = poly.getBbox().ur.x;
            }
            if (poly.getBbox().ur.y > this.bbox.ur.y) {
                this.bbox.ur.y = poly.getBbox().ur.y;
            }
            this.polys.add(poly);
        }

        this.isSubject = isSubject;
    }

    public List<SweepEvent> getSweepEvents() {
        List<SweepEvent> sweepEvents = new ArrayList<>();
        for (PolyIn poly : this.polys) {
            sweepEvents.addAll(poly.getSweepEvents());
        }
        return sweepEvents;
    }

    public boolean isSubject() {
        return isSubject;
    }

    public Bbox getBbox() {
        return bbox;
    }
}
