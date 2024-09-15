package com.cgzz.mapbox.jturf.util.pkg.clipping.polygonclipping;

import com.cgzz.mapbox.jturf.exception.JTurfException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Segment implements Comparable<Segment> {

    private static final AtomicInteger LAST_SEGMENT_ID = new AtomicInteger();

    final int id;

    SweepEvent leftSE;
    SweepEvent rightSE;

    private List<RingIn> rings;
    private List<Integer> windings;

    Segment consumedBy;
    Segment prev;

    RingOut ringOut;

    Segment _prevInResult;

    SegmentState _beforeState;
    SegmentState _afterState;

    private Boolean _isInResult;

    private Operation operation;

    public Segment() {
        this.id = LAST_SEGMENT_ID.incrementAndGet();
    }

    /* Warning: a reference to ringWindings input will be stored,
     *  and possibly will be later modified */
    public Segment(SweepEvent leftSE, SweepEvent rightSE, List<RingIn> rings, List<Integer> windings, Operation operation) {
        this.id = LAST_SEGMENT_ID.incrementAndGet();
        this.leftSE = leftSE;
        leftSE.segment = this;
        leftSE.otherSE = rightSE;
        this.rightSE = rightSE;
        rightSE.segment = this;
        rightSE.otherSE = leftSE;
        this.rings = rings;
        this.windings = windings;
        this.operation = operation;
        // left unset for performance, set later in algorithm
        // this.ringOut, this.consumedBy, this.prev
    }

    /* When a segment is split, the rightSE is replaced with a new sweep event */
    public void replaceRightSE(SweepEvent newRightSE) {
        this.rightSE = newRightSE;
        this.rightSE.segment = this;
        this.rightSE.otherSE = this.leftSE;
        this.leftSE.otherSE = this.rightSE;
    }

    public Bbox bbox() {
        double y1 = this.leftSE.getPoint().y;
        double y2 = this.rightSE.getPoint().y;

        return new Bbox(
                Location.location(this.leftSE.getPoint().x, Math.min(y1, y2)),
                Location.location(this.rightSE.getPoint().x, Math.max(y1, y2))
        );
    }

    /* A vector from the left point to the right */
    public Location vector() {
        return Location.location(
                this.rightSE.getPoint().x - this.leftSE.getPoint().x,
                this.rightSE.getPoint().y - this.leftSE.getPoint().y
        );
    }

    public boolean isAnEndpoint(Location pt) {
        return ((pt.x == this.leftSE.getPoint().x && pt.y == this.leftSE.getPoint().y)
                || (pt.x == this.rightSE.getPoint().x && pt.y == this.rightSE.getPoint().y)
        );
    }

    /* Compare this segment with a point.
     *
     * A point P is considered to be colinear to a segment if there
     * exists a distance D such that if we travel along the segment
     * from one * endpoint towards the other a distance D, we find
     * ourselves at point P.
     *
     * Return value indicates:
     *
     *   1: point lies above the segment (to the left of vertical)
     *   0: point is colinear to segment
     *  -1: point lies below the segment (to the right of vertical)
     */
    public int comparePoint(Location point) {
        if (this.isAnEndpoint(point)) {
            return 0;
        }

        Location lPt = this.leftSE.getPoint();
        Location rPt = this.rightSE.getPoint();
        Location v = this.vector();

        // Exactly vertical segments.
        if (lPt.x == rPt.x) {
            if (point.x == lPt.x) {
                return 0;
            }
            return point.x < lPt.x ? 1 : -1;
        }

        // Nearly vertical segments with an intersection.
        // Check to see where a point on the line with matching Y coordinate is.
        double yDist = (point.y - lPt.y) / v.y;
        double xFromYDist = lPt.x + yDist * v.x;
        if (point.x == xFromYDist) {
            return 0;
        }

        // General case.
        // Check to see where a point on the line with matching X coordinate is.
        double xDist = (point.x - lPt.x) / v.x;
        double yFromXDist = lPt.y + xDist * v.y;
        if (point.y == yFromXDist) {
            return 0;
        }
        return point.y < yFromXDist ? -1 : 1;
    }

    /**
     * Given another segment, returns the first non-trivial intersection
     * between the two segments (in terms of sweep line ordering), if it exists.
     * <p>
     * A 'non-trivial' intersection is one that will cause one or both of the
     * segments to be split(). As such, 'trivial' vs. 'non-trivial' intersection:
     * <p>
     * * endpoint of segA with endpoint of segB --> trivial
     * * endpoint of segA with point along segB --> non-trivial
     * * endpoint of segB with point along segA --> non-trivial
     * * point along segA with point along segB --> non-trivial
     * <p>
     * If no non-trivial intersection exists, return null
     * Else, return null.
     */
    public Location getIntersection(Segment other) {
        // If bboxes don't overlap, there can't be any intersections
        Bbox tBbox = this.bbox();
        Bbox oBbox = other.bbox();
        Bbox bboxOverlap = Bbox.getBboxOverlap(tBbox, oBbox);
        if (bboxOverlap == null) {
            return null;
        }

        // We first check to see if the endpoints can be considered intersections.
        // This will 'snap' intersections to endpoints if possible, and will
        // handle cases of colinearity.

        Location tlp = this.leftSE.getPoint();
        Location trp = this.rightSE.getPoint();
        Location olp = other.leftSE.getPoint();
        Location orp = other.rightSE.getPoint();

        // does each endpoint touch the other segment?
        // note that we restrict the 'touching' definition to only allow segments
        // to touch endpoints that lie forward from where we are in the sweep line pass
        boolean touchesOtherLSE = Bbox.isInBbox(tBbox, olp) && this.comparePoint(olp) == 0;
        boolean touchesThisLSE = Bbox.isInBbox(oBbox, tlp) && other.comparePoint(tlp) == 0;
        boolean touchesOtherRSE = Bbox.isInBbox(tBbox, orp) && this.comparePoint(orp) == 0;
        boolean touchesThisRSE = Bbox.isInBbox(oBbox, trp) && other.comparePoint(trp) == 0;

        // do left endpoints match?
        if (touchesThisLSE && touchesOtherLSE) {
            // these two cases are for colinear segments with matching left
            // endpoints, and one segment being longer than the other
            if (touchesThisRSE && !touchesOtherRSE) {
                return trp;
            }
            if (!touchesThisRSE && touchesOtherRSE) {
                return orp;
            }
            // either the two segments match exactly (two trival intersections)
            // or just on their left endpoint (one trivial intersection
            return null;
        }

        // does this left endpoint matches (other doesn't)
        if (touchesThisLSE) {
            // check for segments that just intersect on opposing endpoints
            if (touchesOtherRSE) {
                if (tlp.x == orp.x && tlp.y == orp.y) {
                    return null;
                }
            }
            // t-intersection on left endpoint
            return tlp;
        }

        // does other left endpoint matches (this doesn't)
        if (touchesOtherLSE) {
            // check for segments that just intersect on opposing endpoints
            if (touchesThisRSE) {
                if (trp.x == olp.x && trp.y == olp.y) {
                    return null;
                }
            }
            // t-intersection on left endpoint
            return olp;
        }

        // trivial intersection on right endpoints
        if (touchesThisRSE && touchesOtherRSE) {
            return null;
        }

        // t-intersections on just one right endpoint
        if (touchesThisRSE) {
            return trp;
        }
        if (touchesOtherRSE) {
            return orp;
        }

        // None of our endpoints intersect. Look for a general intersection between
        // infinite lines laid over the segments
        Location pt = Vector.intersection(tlp, this.vector(), olp, other.vector());

        // are the segments parrallel? Note that if they were colinear with overlap,
        // they would have an endpoint intersection and that case was already handled above
        if (pt == null) {
            return null;
        }

        // is the intersection found between the lines not on the segments?
        if (!Bbox.isInBbox(bboxOverlap, pt)) {
            return null;
        }

        // round the the computed point if needed
        return operation.rounder.round(pt.x, pt.y);
    }

    /**
     * Split the given segment into multiple segments on the given points.
     * * Each existing segment will retain its leftSE and a new rightSE will be
     * generated for it.
     * * A new segment will be generated which will adopt the original segment's
     * rightSE, and a new leftSE will be generated for it.
     * * If there are more than two points given to split on, new segments
     * in the middle will be generated with new leftSE and rightSE's.
     * * An array of the newly generated SweepEvents will be returned.
     * <p>
     * Warning: input array of points is modified
     */
    public List<SweepEvent> split(Location point) {
        List<SweepEvent> newEvents = new ArrayList<>();
        boolean alreadyLinked = point.getEvents() != null;

        SweepEvent newLeftSE = new SweepEvent(point, true);
        SweepEvent newRightSE = new SweepEvent(point, false);
        SweepEvent oldRightSE = this.rightSE;
        this.replaceRightSE(newRightSE);
        newEvents.add(newRightSE);
        newEvents.add(newLeftSE);
        Segment newSeg = new Segment(newLeftSE, oldRightSE, new ArrayList<>(this.rings), new ArrayList<>(this.windings), operation);

        // when splitting a nearly vertical downward-facing segment,
        // sometimes one of the resulting new segments is vertical, in which
        // case its left and right events may need to be swapped
        if (SweepEvent.comparePoints(newSeg.leftSE.getPoint(), newSeg.rightSE.getPoint()) > 0) {
            newSeg.swapEvents();
        }
        if (SweepEvent.comparePoints(this.leftSE.getPoint(), this.rightSE.getPoint()) > 0) {
            this.swapEvents();
        }

        // in the point we just used to create new sweep events with was already
        // linked to other events, we need to check if either of the affected
        // segments should be consumed
        if (alreadyLinked) {
            newLeftSE.checkForConsuming();
            newRightSE.checkForConsuming();
        }

        return newEvents;
    }

    /* Swap which event is left and right */
    public void swapEvents() {
        SweepEvent tmpEvt = this.rightSE;
        this.rightSE = this.leftSE;
        this.leftSE = tmpEvt;
        this.leftSE.isLeft = true;
        this.rightSE.isLeft = false;
        for (int i = 0, iMax = this.windings.size(); i < iMax; i++) {
            this.windings.set(i, this.windings.get(i) * -1);
        }
    }

    /* Consume another segment. We take their rings under our wing
     * and mark them as consumed. Use for perfectly overlapping segments */
    public void consume(Segment other) {
        Segment consumer = this;
        Segment consumee = other;
        while (consumer.consumedBy != null) {
            consumer = consumer.consumedBy;
        }
        while (consumee.consumedBy != null) {
            consumee = consumee.consumedBy;
        }

        int cmp = Segment.compare(consumer, consumee);
        if (cmp == 0) {
            return; // already consumed
        }
        // the winner of the consumption is the earlier segment
        // according to sweep line ordering
        if (cmp > 0) {
            Segment tmp = consumer;
            consumer = consumee;
            consumee = tmp;
        }

        // make sure a segment doesn't consume it's prev
        if (consumer.prev == consumee) {
            Segment tmp = consumer;
            consumer = consumee;
            consumee = tmp;
        }

        for (int i = 0, iMax = consumee.rings.size(); i < iMax; i++) {
            RingIn ring = consumee.rings.get(i);
            Integer winding = consumee.windings.get(i);
            int index = consumer.rings.indexOf(ring);
            if (index == -1) {
                consumer.rings.add(ring);
                consumer.windings.add(winding);
            } else {
                consumer.windings.set(index, consumer.windings.get(index) + winding);
            }
        }
        consumee.rings = null;
        consumee.windings = null;
        consumee.consumedBy = consumer;

        // mark sweep events consumed as to maintain ordering in sweep event queue
        consumee.leftSE.consumedBy = consumer.leftSE;
        consumee.rightSE.consumedBy = consumer.rightSE;
    }

    public Segment prevInResult() {
        if (this._prevInResult != null) {
            return this._prevInResult;
        }
        if (this.prev == null) {
            this._prevInResult = null;
        } else if (this.prev.isInResult()) {
            this._prevInResult = this.prev;
        } else {
            this._prevInResult = this.prev.prevInResult();
        }

        return this._prevInResult;
    }

    public SegmentState beforeState() {
        if (this._beforeState != null) {
            return this._beforeState;
        }
        if (this.prev == null)
            this._beforeState = new SegmentState(null, null, null);
        else {
            Segment seg = this.prev.consumedBy != null ? this.prev.consumedBy : this.prev;
            this._beforeState = seg.afterState();
        }
        return this._beforeState;
    }

    public SegmentState afterState() {
        if (this._afterState != null) {
            return this._afterState;
        }

        SegmentState beforeState = this.beforeState();
        this._afterState = new SegmentState(beforeState.rings, beforeState.windings, null);
        List<RingIn> ringsAfter = this._afterState.rings;
        List<Integer> windingsAfter = this._afterState.windings;
        List<MultiPolyIn> mpsAfter = this._afterState.multiPolys;

        // calculate ringsAfter, windingsAfter
        for (int i = 0, iMax = this.rings.size(); i < iMax; i++) {
            RingIn ring = this.rings.get(i);
            Integer winding = this.windings.get(i);
            int index = ringsAfter.indexOf(ring);
            if (index == -1) {
                ringsAfter.add(ring);
                windingsAfter.add(winding);
            } else {
                windingsAfter.set(index, windingsAfter.get(index) + winding);
            }
        }

        // calcualte polysAfter
        List<PolyIn> polysAfter = new ArrayList<>();
        List<PolyIn> polysExclude = new ArrayList<>();
        for (int i = 0, iMax = ringsAfter.size(); i < iMax; i++) {
            if (windingsAfter.get(i) == 0) {
                continue; // non-zero rule
            }
            RingIn ring = ringsAfter.get(i);
            PolyIn poly = ring.getPoly();
            if (polysExclude.contains(poly)) {
                continue;
            }
            if (ring.isExterior()) {
                polysAfter.add(poly);
            } else {
                if (!polysExclude.contains(poly)) {
                    polysExclude.add(poly);
                }
                int index = polysAfter.indexOf(ring.getPoly());
                if (index != -1) {
                    polysAfter.remove(index);
                }
            }
        }

        // calculate multiPolysAfter
        for (PolyIn polyIn : polysAfter) {
            MultiPolyIn mp = polyIn.getMultiPoly();
            if (!mpsAfter.contains(mp)) {
                mpsAfter.add(mp);
            }
        }

        return this._afterState;
    }

    /* Is this segment part of the final result? */
    public boolean isInResult() {
        // if we've been consumed, we're not in the result
        if (this.consumedBy != null) {
            return false;
        }

        if (this._isInResult != null) {
            return this._isInResult;
        }

        List<MultiPolyIn> mpsBefore = this.beforeState().multiPolys;
        List<MultiPolyIn> mpsAfter = this.afterState().multiPolys;

        switch (operation.type) {
            case UNION: {
                // UNION - included iff:
                //  * On one side of us there is 0 poly interiors AND
                //  * On the other side there is 1 or more.
                boolean noBefores = mpsBefore.size() == 0;
                boolean noAfters = mpsAfter.size() == 0;
                this._isInResult = noBefores != noAfters;
                break;
            }
            case INTERSECT: {
                // INTERSECTION - included iff:
                //  * on one side of us all multipolys are rep. with poly interiors AND
                //  * on the other side of us, not all multipolys are repsented
                //    with poly interiors
                int least;
                int most;
                if (mpsBefore.size() < mpsAfter.size()) {
                    least = mpsBefore.size();
                    most = mpsAfter.size();
                } else {
                    least = mpsAfter.size();
                    most = mpsBefore.size();
                }
                this._isInResult = most == operation.numMultiPolys && least < most;
                break;
            }
            case XOR: {
                // XOR - included iff:
                //  * the difference between the number of multipolys represented
                //    with poly interiors on our two sides is an odd number
                int diff = Math.abs(mpsBefore.size() - mpsAfter.size());
                this._isInResult = diff % 2 == 1;
                break;
            }
            case DIFFERENCE: {
                // DIFFERENCE included iff:
                //  * on exactly one side, we have just the subject
                this._isInResult = isJustSubject(mpsBefore) != isJustSubject(mpsAfter);
                break;
            }
            default:
                throw new JTurfException("Unrecognized operation type found " + operation.type);
        }

        return this._isInResult;
    }

    private boolean isJustSubject(List<MultiPolyIn> multiPolyIns) {
        return multiPolyIns.size() == 1 && multiPolyIns.get(0).isSubject();
    }

    @Override
    public int compareTo(Segment o) {
        return compare(this, o);
    }

    /* This compare() function is for ordering segments in the sweep
     * line tree, and does so according to the following criteria:
     *
     * Consider the vertical line that lies an infinestimal step to the
     * right of the right-more of the two left endpoints of the input
     * segments. Imagine slowly moving a point up from negative infinity
     * in the increasing y direction. Which of the two segments will that
     * point intersect first? That segment comes 'before' the other one.
     *
     * If neither segment would be intersected by such a line, (if one
     * or more of the segments are vertical) then the line to be considered
     * is directly on the right-more of the two left inputs.
     */
    public static int compare(Segment a, Segment b) {
        double alx = a.leftSE.getPoint().x;
        double blx = b.leftSE.getPoint().x;
        double arx = a.rightSE.getPoint().x;
        double brx = b.rightSE.getPoint().x;

        // check if they're even in the same vertical plane
        if (brx < alx) {
            return 1;
        }
        if (arx < blx) {
            return -1;
        }

        double aly = a.leftSE.getPoint().y;
        double bly = b.leftSE.getPoint().y;
        double ary = a.rightSE.getPoint().y;
        double bry = b.rightSE.getPoint().y;

        // is left endpoint of segment B the right-more?
        if (alx < blx) {
            // are the two segments in the same horizontal plane?
            if (bly < aly && bly < ary) {
                return 1;
            }
            if (bly > aly && bly > ary) {
                return -1;
            }

            // is the B left endpoint colinear to segment A?
            int aCmpBLeft = a.comparePoint(b.leftSE.getPoint());
            if (aCmpBLeft < 0) {
                return 1;
            }
            if (aCmpBLeft > 0) {
                return -1;
            }

            // is the A right endpoint colinear to segment B ?
            int bCmpARight = b.comparePoint(a.rightSE.getPoint());
            if (bCmpARight != 0) {
                return bCmpARight;
            }

            // colinear segments, consider the one with left-more
            // left endpoint to be first (arbitrary?)
            return -1;
        }

        // is left endpoint of segment A the right-more?
        if (alx > blx) {
            if (aly < bly && aly < bry) {
                return -1;
            }
            if (aly > bly && aly > bry) {
                return 1;
            }

            // is the A left endpoint colinear to segment B?
            int bCmpALeft = b.comparePoint(a.leftSE.getPoint());
            if (bCmpALeft != 0) {
                return bCmpALeft;
            }

            // is the B right endpoint colinear to segment A?
            int aCmpBRight = a.comparePoint(b.rightSE.getPoint());
            if (aCmpBRight < 0) {
                return 1;
            }
            if (aCmpBRight > 0) {
                return -1;
            }

            // colinear segments, consider the one with left-more
            // left endpoint to be first (arbitrary?)
            return 1;
        }

        // if we get here, the two left endpoints are in the same
        // vertical plane, ie alx === blx

        // consider the lower left-endpoint to come first
        if (aly < bly) {
            return -1;
        }
        if (aly > bly) {
            return 1;
        }

        // left endpoints are identical
        // check for colinearity by using the left-more right endpoint

        // is the A right endpoint more left-more?
        if (arx < brx) {
            int bCmpARight = b.comparePoint(a.rightSE.getPoint());
            if (bCmpARight != 0) {
                return bCmpARight;
            }
        }

        // is the B right endpoint more left-more?
        if (arx > brx) {
            int aCmpBRight = a.comparePoint(b.rightSE.getPoint());
            if (aCmpBRight < 0) {
                return 1;
            }
            if (aCmpBRight > 0) {
                return -1;
            }
        }

        if (arx != brx) {
            // are these two [almost] vertical segments with opposite orientation?
            // if so, the one with the lower right endpoint comes first
            double ay = ary - aly;
            double ax = arx - alx;
            double by = bry - bly;
            double bx = brx - blx;
            if (ay > ax && by < bx) {
                return 1;
            }
            if (ay < ax && by > bx) {
                return -1;
            }
        }

        // we have colinear segments with matching orientation
        // consider the one with more left-more right endpoint to be first
        if (arx > brx) {
            return 1;
        }
        if (arx < brx) {
            return -1;
        }

        // if we get here, two two right endpoints are in the same
        // vertical plane, ie arx === brx

        // consider the lower right-endpoint to come first
        if (ary < bry) {
            return -1;
        }
        if (ary > bry) {
            return 1;
        }

        // right endpoints identical as well, so the segments are idential
        // fall back on creation order as consistent tie-breaker
        if (a.id < b.id) {
            return -1;
        }
        if (a.id > b.id) {
            return 1;
        }

        // identical segment, ie a === b
        return 0;
    }

    public static Segment fromRing(Location pt1, Location pt2, RingIn ring, Operation operation) {
        Location leftPt, rightPt;
        int winding;

        // ordering the two points according to sweep line ordering
        int cmpPts = SweepEvent.comparePoints(pt1, pt2);
        if (cmpPts < 0) {
            leftPt = pt1;
            rightPt = pt2;
            winding = 1;
        } else if (cmpPts > 0) {
            leftPt = pt2;
            rightPt = pt1;
            winding = -1;
        } else {
            throw new JTurfException("Tried to create degenerate segment at [" + pt1.x + ", " + pt1.y + "]");
        }

        SweepEvent leftSE = new SweepEvent(leftPt, true);
        SweepEvent rightSE = new SweepEvent(rightPt, false);

        List<RingIn> rings = new ArrayList<>();
        List<Integer> windings = new ArrayList<>();

        rings.add(ring);
        windings.add(winding);

        return new Segment(leftSE, rightSE, rings, windings, operation);
    }

}
