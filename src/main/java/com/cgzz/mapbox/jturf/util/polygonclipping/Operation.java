package com.cgzz.mapbox.jturf.util.polygonclipping;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Point;
import com.cgzz.mapbox.jturf.util.splaytree.Node;
import com.cgzz.mapbox.jturf.util.splaytree.SplayTree;

import java.util.ArrayList;
import java.util.List;

// https://github.com/mfogel/polygon-clipping/tree/v0.15.3
public final class Operation {

    // 限制迭代过程以防止无限循环 - 通常由浮点数学舍入误差引起。
    public static final int POLYGON_CLIPPING_MAX_QUEUE_SIZE = 1000000;
    public static final int POLYGON_CLIPPING_MAX_SWEEPLINE_SEGMENTS = 1000000;

    int numMultiPolys;
    PolygonClippingType type;
    PtRounder rounder;

    @SafeVarargs
    public static List<List<List<Point>>> run(PolygonClippingType type, List<List<Point>> geom, List<List<Point>>... moreGeoms) {
        Operation operation = new Operation();
        PtRounder rounder = new PtRounder();

        operation.type = type;
        operation.rounder = rounder;

        /* Convert inputs to MultiPoly objects */
        List<MultiPolyIn> multipolys = new ArrayList<>();
        multipolys.add(new MultiPolyIn(geom, true, rounder, operation));

        if (moreGeoms != null) {
            for (List<List<Point>> moreGeom : moreGeoms) {
                multipolys.add(new MultiPolyIn(moreGeom, false, rounder, operation));
            }
        }

        operation.numMultiPolys = multipolys.size();

        /* BBox optimization for difference operation
         * If the bbox of a multipolygon that's part of the clipping doesn't
         * intersect the bbox of the subject at all, we can just drop that
         * multiploygon. */
        if (type == PolygonClippingType.DIFFERENCE) {
            // in place removal
            MultiPolyIn subject = multipolys.get(0);
            int i = 1;
            while (i < multipolys.size()) {
                if (Bbox.getBboxOverlap(multipolys.get(i).getBbox(), subject.getBbox()) != null) {
                    i++;
                } else {
                    multipolys.remove(i);
                }
            }
        }

        /* BBox optimization for intersection operation
         * If we can find any pair of multipolygons whose bbox does not overlap,
         * then the result will be empty. */
        if (type == PolygonClippingType.INTERSECT) {
            // TODO: this is O(n^2) in number of polygons. By sorting the bboxes,
            //       it could be optimized to O(n * ln(n))
            for (int i = 0, iMax = multipolys.size(); i < iMax; i++) {
                MultiPolyIn mpA = multipolys.get(i);
                for (int j = i + 1; j < iMax; j++) {
                    if (Bbox.getBboxOverlap(mpA.getBbox(), multipolys.get(j).getBbox()) == null) {
                        return null;
                    }
                }
            }
        }

        /* Put segment endpoints in a priority queue */
        SplayTree<SweepEvent, Object> queue = new SplayTree<>(SweepEvent::compare);
        for (MultiPolyIn multipoly : multipolys) {
            List<SweepEvent> sweepEvents = multipoly.getSweepEvents();
            for (SweepEvent sweepEvent : sweepEvents) {
                queue.insert(sweepEvent);

                if (queue.size() > POLYGON_CLIPPING_MAX_QUEUE_SIZE) {
                    // prevents an infinite loop, an otherwise common manifestation of bugs
                    throw new JTurfException("Infinite loop when putting segment endpoints in a priority queue " +
                            "(queue size too big).");
                }
            }
        }

        /* Pass the sweep line over those endpoints */
        SweepLine sweepLine = new SweepLine(queue, Segment::compare, operation);
        int prevQueueSize = queue.size();
        Node<SweepEvent, Object> node = queue.pop();

        while (node != null) {
            SweepEvent evt = node.getKey();
            if (queue.size() == prevQueueSize) {
                // prevents an infinite loop, an otherwise common manifestation of bugs
                Segment seg = evt.segment;
                throw new JTurfException(
                        "Unable to pop() " + (evt.isLeft ? "left" : "right") + " SweepEvent " +
                                "[" + evt.getPoint().x + ", " + evt.getPoint().y + "] from segment #" + seg.id + " " +
                                "[" + seg.leftSE.getPoint().x + ", " + seg.leftSE.getPoint().y + "] -> " +
                                "[" + seg.rightSE.getPoint().x + ", " + seg.rightSE.getPoint().y + "] from queue."
                );
            }

            if (queue.size() > POLYGON_CLIPPING_MAX_QUEUE_SIZE) {
                // prevents an infinite loop, an otherwise common manifestation of bugs
                throw new JTurfException("Infinite loop when passing sweep line over endpoints (queue size too big).");
            }

            if (sweepLine.segments.size() > POLYGON_CLIPPING_MAX_SWEEPLINE_SEGMENTS) {
                // prevents an infinite loop, an otherwise common manifestation of bugs
                throw new JTurfException("Infinite loop when passing sweep line over endpoints (too many sweep line segments).");
            }

            List<SweepEvent> newEvents = sweepLine.process(evt);
            for (SweepEvent se : newEvents) {
                if (se.consumedBy == null) {
                    queue.insert(se);
                }
            }
            prevQueueSize = queue.size();
            node = queue.pop();
        }

        // free some memory we don't need anymore
        // rounder.reset();

        /* Collect and compile segments we're keeping into a multipolygon */
        List<RingOut> ringsOut = RingOut.factory(sweepLine.segments);
        MultiPolyOut result = new MultiPolyOut(ringsOut);
        return result.getGeom();
    }
}
