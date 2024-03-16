package com.cgzz.mapbox.jturf.util.polygonclipping;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RingOut {

    List<SweepEvent> events;
    PolyOut poly;

    private Boolean _isExteriorRing;
    private RingOut _enclosingRing;

    public RingOut(List<SweepEvent> events) {
        this.events = events;
        if (events != null) {
            for (SweepEvent event : events) {
                event.segment.ringOut = this;
            }
        }
        this.poly = null;
    }

    public List<Point> getGeom() {
        // Remove superfluous points (ie extra points along a straight line),
        Location prevPt = this.events.get(0).getPoint();
        List<Location> points = new ArrayList<>();
        points.add(prevPt);

        for (int i = 1, iMax = this.events.size() - 1; i < iMax; i++) {
            Location pt = this.events.get(i).getPoint();
            Location nextPt = this.events.get(i + 1).getPoint();
            if (Vector.compareVectorAngles(pt, prevPt, nextPt) == 0) {
                continue;
            }
            points.add(pt);
            prevPt = pt;
        }

        // ring was all (within rounding error of angle calc) colinear points
        if (points.size() == 1) {
            return null;
        }

        // check if the starting point is necessary
        Location pt = points.get(0);
        Location nextPt = points.get(1);
        if (Vector.compareVectorAngles(pt, prevPt, nextPt) == 0) {
            points.remove(0);
        }

        points.add(points.get(0));
        int step = this.isExteriorRing() ? 1 : -1;
        int iStart = this.isExteriorRing() ? 0 : points.size() - 1;
        int iEnd = this.isExteriorRing() ? points.size() : -1;
        List<Point> orderedPoints = new ArrayList<>();
        for (int i = iStart; i != iEnd; i += step) {
            Location p = points.get(i);

            orderedPoints.add(Point.fromLngLat(p.x, p.y));
        }
        return orderedPoints;
    }

    public boolean isExteriorRing() {
        if (this._isExteriorRing == null) {
            RingOut enclosing = this.enclosingRing();
            this._isExteriorRing = enclosing == null || !enclosing.isExteriorRing();
        }
        return this._isExteriorRing;
    }

    public RingOut enclosingRing() {
        if (this._enclosingRing == null) {
            this._enclosingRing = this._calcEnclosingRing();
        }
        return this._enclosingRing;
    }

    /* Returns the ring that encloses this one, if any */
    private RingOut _calcEnclosingRing() {
        // start with the ealier sweep line event so that the prevSeg
        // chain doesn't lead us inside of a loop of ours
        SweepEvent leftMostEvt = this.events.get(0);
        for (int i = 1, iMax = this.events.size(); i < iMax; i++) {
            SweepEvent evt = this.events.get(i);
            if (SweepEvent.compare(leftMostEvt, evt) > 0) {
                leftMostEvt = evt;
            }
        }

        Segment prevSeg = leftMostEvt.segment.prevInResult();
        Segment prevPrevSeg = prevSeg != null ? prevSeg.prevInResult() : null;

        while (true) {
            // no segment found, thus no ring can enclose us
            if (prevSeg == null) {
                return null;
            }

            // no segments below prev segment found, thus the ring of the prev
            // segment must loop back around and enclose us
            if (prevPrevSeg == null) {
                return prevSeg.ringOut;
            }

            // if the two segments are of different rings, the ring of the prev
            // segment must either loop around us or the ring of the prev prev
            // seg, which would make us and the ring of the prev peers
            if (prevPrevSeg.ringOut != prevSeg.ringOut) {
                if (prevPrevSeg.ringOut.enclosingRing() != prevSeg.ringOut) {
                    return prevSeg.ringOut;
                } else {
                    return prevSeg.ringOut.enclosingRing();
                }
            }

            // two segments are from the same ring, so this was a penisula
            // of that ring. iterate downward, keep searching
            prevSeg = prevPrevSeg.prevInResult();
            prevPrevSeg = prevSeg != null ? prevSeg.prevInResult() : null;
        }
    }

    /* Given the segments from the sweep line pass, compute & return a series
     * of closed rings from all the segments marked to be part of the result */
    public static List<RingOut> factory(List<Segment> allSegments) {
        List<RingOut> ringsOut = new ArrayList<>();

        for (Segment segment : allSegments) {
            if (!segment.isInResult() || segment.ringOut != null) {
                continue;
            }

            SweepEvent prevEvent = null;
            SweepEvent event = segment.leftSE;
            SweepEvent nextEvent = segment.rightSE;
            List<SweepEvent> events = new ArrayList<>();
            events.add(event);

            Location startingPoint = event.getPoint();
            List<IntersectionPoint> intersectionLEs = new ArrayList<>();

            /* Walk the chain of linked events to form a closed ring */
            while (true) {
                prevEvent = event;
                event = nextEvent;
                events.add(event);

                /* Is the ring complete? */
                if (event.getPoint() == startingPoint) {
                    break;
                }

                while (true) {
                    List<SweepEvent> availableLEs = event.getAvailableLinkedEvents();

                    /* Did we hit a dead end? This shouldn't happen.
                     * Indicates some earlier part of the algorithm malfunctioned. */
                    if (availableLEs.size() == 0) {
                        Location firstPt = events.get(0).getPoint();
                        Location lastPt = events.get(events.size() - 1).getPoint();
                        throw new JTurfException(
                                "Unable to complete output ring starting at [" + firstPt.x + "," +
                                        " " + firstPt.y + "]. Last matching segment found ends at" +
                                        " [" + lastPt.x + ", " + lastPt.y + "].");
                    }

                    /* Only one way to go, so cotinue on the path */
                    if (availableLEs.size() == 1) {
                        nextEvent = availableLEs.get(0).otherSE;
                        break;
                    }

                    /* We must have an intersection. Check for a completed loop */
                    Integer indexLE = null;
                    for (int j = 0, jMax = intersectionLEs.size(); j < jMax; j++) {
                        if (intersectionLEs.get(j).point == event.getPoint()) {
                            indexLE = j;
                            break;
                        }
                    }
                    /* Found a completed loop. Cut that off and make a ring */
                    if (indexLE != null) {
                        IntersectionPoint intersectionLE = intersectionLEs.remove(indexLE.intValue());
                        List<SweepEvent> ringEvents = new ArrayList<>(events.subList(0, intersectionLE.index));
                        ringEvents.add(0, ringEvents.get(0).otherSE);

                        Collections.reverse(ringEvents);
                        ringsOut.add(new RingOut(ringEvents));
                        continue;
                    }
                    /* register the intersection */
                    intersectionLEs.add(new IntersectionPoint(events.size(), event.getPoint()));
                    /* Choose the left-most option to continue the walk */
                    Comparator<SweepEvent> comparator = event.getLeftmostComparator(prevEvent);
                    availableLEs.sort(comparator);
                    nextEvent = availableLEs.get(0).otherSE;
                    break;
                }
            }

            ringsOut.add(new RingOut(events));
        }
        return ringsOut;
    }

    private static class IntersectionPoint {
        private final int index;
        private final Location point;

        IntersectionPoint(int index, Location point) {
            this.index = index;
            this.point = point;
        }
    }

}
