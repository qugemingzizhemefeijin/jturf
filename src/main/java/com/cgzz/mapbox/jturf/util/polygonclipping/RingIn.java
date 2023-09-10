package com.cgzz.mapbox.jturf.util.polygonclipping;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Point;

import java.util.ArrayList;
import java.util.List;

public class RingIn {

    private final PolyIn poly;
    private final boolean isExterior;
    private final List<Segment> segments;

    private final Bbox bbox;

    public RingIn(List<Point> geomRing, PolyIn poly, boolean isExterior, PtRounder rounder, Operation operation) {
        if (geomRing == null || geomRing.isEmpty()) {
            throw new JTurfException("Input geometry is not a valid Polygon or MultiPolygon");
        }

        this.poly = poly;
        this.isExterior = isExterior;
        this.segments = new ArrayList<>();

        Location firstPoint = rounder.round(geomRing.get(0).getX(), geomRing.get(0).getY());
        this.bbox = new Bbox(Location.location(firstPoint.x, firstPoint.y), Location.location(firstPoint.x, firstPoint.y));

        Location prevPoint = firstPoint;
        for (int i = 1, iMax = geomRing.size(); i < iMax; i++) {
            Location point = rounder.round(geomRing.get(i).getX(), geomRing.get(i).getY());
            // skip repeated points
            if (point.x == prevPoint.x && point.y == prevPoint.y) {
                continue;
            }
            this.segments.add(Segment.fromRing(prevPoint, point, this, operation));
            if (point.x < this.bbox.ll.x) {
                this.bbox.ll.x = point.x;
            }
            if (point.y < this.bbox.ll.y) {
                this.bbox.ll.y = point.y;
            }
            if (point.x > this.bbox.ur.x) {
                this.bbox.ur.x = point.x;
            }
            if (point.y > this.bbox.ur.y) {
                this.bbox.ur.y = point.y;
            }
            prevPoint = point;
        }

        // add segment from last to first if last is not the same as first
        if (firstPoint.x != prevPoint.x || firstPoint.y != prevPoint.y) {
            this.segments.add(Segment.fromRing(prevPoint, firstPoint, this, operation));
        }
    }

    public List<SweepEvent> getSweepEvents() {
        int iMax = this.segments.size();
        List<SweepEvent> sweepEvents = new ArrayList<>(iMax);
        for (Segment segment : this.segments) {
            sweepEvents.add(segment.leftSE);
            sweepEvents.add(segment.rightSE);
        }
        return sweepEvents;
    }

    public PolyIn getPoly() {
        return poly;
    }

    public boolean isExterior() {
        return isExterior;
    }

    public Bbox getBbox() {
        return bbox;
    }
}
