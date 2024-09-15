package com.cgzz.mapbox.jturf.util.clipping.polygonclipping;

import com.cgzz.mapbox.jturf.exception.JTurfException;

import java.util.*;

import static com.cgzz.mapbox.jturf.util.clipping.polygonclipping.Vector.cosineOfAngle;
import static com.cgzz.mapbox.jturf.util.clipping.polygonclipping.Vector.sineOfAngle;

public class SweepEvent implements Comparable<SweepEvent> {

    private Location point;
    boolean isLeft;
    Segment segment;
    SweepEvent otherSE;

    SweepEvent consumedBy;

    public SweepEvent(Location point, boolean isLeft) {
        point.addEvent(this);
        this.point = point;
        this.isLeft = isLeft;
        // this.segment, this.otherSE set by factory
    }

    public void link(SweepEvent other) {
        if (other.point == this.point) {
            throw new JTurfException("Tried to link already linked events");
        }
        List<SweepEvent> otherEvents = other.point.getEvents();
        if (otherEvents != null) {
            for (SweepEvent evt : otherEvents) {
                this.point.addEvent(evt);
                evt.point = this.point;
            }
        }
        this.checkForConsuming();
    }

    /* Do a pass over our linked events and check to see if any pair
     * of segments match, and should be consumed. */
    public void checkForConsuming() {
        // FIXME: The loops in this method run O(n^2) => no good.
        //        Maintain little ordered sweep event trees?
        //        Can we maintaining an ordering that avoids the need
        //        for the re-sorting with getLeftmostComparator in geom-out?

        // Compare each pair of events to see if other events also match
        List<SweepEvent> events = this.point.getEvents();
        if (events == null) {
            return;
        }
        for (int i = 0, numEvents = events.size(); i < numEvents; i++) {
            SweepEvent evt1 = events.get(i);
            if (evt1.segment.consumedBy != null) {
                continue;
            }
            for (int j = i + 1; j < numEvents; j++) {
                SweepEvent evt2 = events.get(j);
                if (evt2.consumedBy != null) {
                    continue;
                }
                if (evt1.otherSE.point.getEvents() != evt2.otherSE.point.getEvents()) {
                    continue;
                }
                evt1.segment.consume(evt2.segment);
            }
        }
    }

    public List<SweepEvent> getAvailableLinkedEvents() {
        // point.events is always of length 2 or greater
        List<SweepEvent> es = this.point.getEvents();
        if (es == null) {
            return null;
        }

        List<SweepEvent> events = new ArrayList<>();
        for (SweepEvent evt : es) {
            if (evt != this && evt.segment.ringOut == null && evt.segment.isInResult()) {
                events.add(evt);
            }
        }

        return events;
    }

    /**
     * Returns a comparator function for sorting linked events that will
     * favor the event that will give us the smallest left-side angle.
     * All ring construction starts as low as possible heading to the right,
     * so by always turning left as sharp as possible we'll get polygons
     * without uncessary loops & holes.
     * <p>
     * The comparator function has a compute cache such that it avoids
     * re-computing already-computed values.
     */
    public Comparator<SweepEvent> getLeftmostComparator(SweepEvent baseEvent) {
        Map<SweepEvent, SweepEventLeftMost> cache = new HashMap<>();

        return (a, b) -> {
            if (!cache.containsKey(a)) {
                fillCache(cache, a, baseEvent);
            }
            if (!cache.containsKey(b)) {
                fillCache(cache, b, baseEvent);
            }

            SweepEventLeftMost v1 = cache.get(a), v2 = cache.get(b);

            // both on or above x-axis
            if (v1.sine >= 0 && v2.sine >= 0) {
                return Double.compare(v2.cosine, v1.cosine);
            }

            // both below x-axis
            if (v1.sine < 0 && v2.sine < 0) {
                return Double.compare(v1.cosine, v2.cosine);
            }

            // one above x-axis, one below
            return Double.compare(v2.sine, v1.sine);
        };
    }

    private void fillCache(Map<SweepEvent, SweepEventLeftMost> cache, SweepEvent linkedEvent, SweepEvent baseEvent) {
        SweepEvent nextEvent = linkedEvent.otherSE;
        cache.put(linkedEvent, SweepEventLeftMost.create(
                sineOfAngle(this.point, baseEvent.point, nextEvent.point),
                cosineOfAngle(this.point, baseEvent.point, nextEvent.point))
        );
    }

    public Location getPoint() {
        return point;
    }

    @Override
    public int compareTo(SweepEvent o) {
        return compare(this, o);
    }

    // for ordering sweep events in the sweep event queue
    public static int compare(SweepEvent a, SweepEvent b) {
        // favor event with a point that the sweep line hits first
        int ptCmp = SweepEvent.comparePoints(a.point, b.point);
        if (ptCmp != 0) {
            return ptCmp;
        }

        // the points are the same, so link them if needed
        if (a.point != b.point) {
            a.link(b);
        }

        // favor right events over left
        if (a.isLeft != b.isLeft) {
            return a.isLeft ? 1 : -1;
        }

        // we have two matching left or right endpoints
        // ordering of this case is the same as for their segments
        return Segment.compare(a.segment, b.segment);
    }

    // for ordering points in sweep line order
    public static int comparePoints(Location aPt, Location bPt) {
        if (aPt.x < bPt.x) {
            return -1;
        } else if (aPt.x > bPt.x) {
            return 1;
        }

        if (aPt.y < bPt.y) {
            return -1;
        } else if (aPt.y > bPt.y) {
            return 1;
        }

        return 0;
    }

}
