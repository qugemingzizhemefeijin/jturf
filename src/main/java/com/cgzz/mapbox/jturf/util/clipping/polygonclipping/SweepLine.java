package com.cgzz.mapbox.jturf.util.clipping.polygonclipping;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.util.clipping.splaytree.Node;
import com.cgzz.mapbox.jturf.util.clipping.splaytree.SplayTree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SweepLine {

    SplayTree<SweepEvent, Object> queue;
    SplayTree<Segment, Object> tree;
    List<Segment> segments;
    Operation operation;

    public SweepLine(SplayTree<SweepEvent, Object> queue, Comparator<Segment> comparator, Operation operation) {
        this.queue = queue;
        this.tree = new SplayTree<>(comparator);
        this.segments = new ArrayList<>();
        this.operation = operation;
    }

    public List<SweepEvent> process(SweepEvent event) {
        Segment segment = event.segment;
        List<SweepEvent> newEvents = new ArrayList<>();

        // if we've already been consumed by another segment,
        // clean up our body parts and get out
        if (event.consumedBy != null) {
            if (event.isLeft) {
                this.queue.remove(event.otherSE);
            } else {
                this.tree.remove(segment);
            }
            return newEvents;
        }

        Node<Segment, Object> node = event.isLeft ? this.tree.add(segment) : this.tree.find(segment);

        if (node == null)
            throw new JTurfException(
                    "Unable to find segment #" + segment.id + " " +
                            "[" + segment.leftSE.getPoint().x + ", " + segment.leftSE.getPoint().y + "] -> " +
                            "[" + segment.rightSE.getPoint().x + ", " + segment.rightSE.getPoint().y + "] " +
                            "in SweepLine tree."
            );

        Node<Segment, Object> prevNode = node;
        Node<Segment, Object> nextNode = node;
        Segment sentinel = new Segment();
        Segment prevSeg = sentinel;
        Segment nextSeg = sentinel;

        // skip consumed segments still in tree
        while (prevSeg == sentinel) {
            prevNode = this.tree.prev(prevNode);
            if (prevNode == null) {
                prevSeg = null;
            } else if (prevNode.getKey().consumedBy == null) {
                prevSeg = prevNode.getKey();
            }
        }

        // skip consumed segments still in tree
        while (nextSeg == sentinel) {
            nextNode = this.tree.next(nextNode);
            if (nextNode == null) {
                nextSeg = null;
            } else if (nextNode.getKey().consumedBy == null) {
                nextSeg = nextNode.getKey();
            }
        }

        if (event.isLeft) {
            // Check for intersections against the previous segment in the sweep line
            Location prevMySplitter = null;
            if (prevSeg != null && prevSeg != sentinel) {
                Location prevInter = prevSeg.getIntersection(segment);
                if (prevInter != null) {
                    if (!segment.isAnEndpoint(prevInter)) {
                        prevMySplitter = prevInter;
                    }
                    if (!prevSeg.isAnEndpoint(prevInter)) {
                        List<SweepEvent> newEventsFromSplit = this._splitSafely(prevSeg, prevInter);
                        newEvents.addAll(newEventsFromSplit);
                    }
                }
            }

            // Check for intersections against the next segment in the sweep line
            Location nextMySplitter = null;
            if (nextSeg != null && nextSeg != sentinel) {
                Location nextInter = nextSeg.getIntersection(segment);
                if (nextInter != null) {
                    if (!segment.isAnEndpoint(nextInter)) {
                        nextMySplitter = nextInter;
                    }
                    if (!nextSeg.isAnEndpoint(nextInter)) {
                        List<SweepEvent> newEventsFromSplit = this._splitSafely(nextSeg, nextInter);
                        newEvents.addAll(newEventsFromSplit);
                    }
                }
            }

            // For simplicity, even if we find more than one intersection we only
            // spilt on the 'earliest' (sweep-line style) of the intersections.
            // The other intersection will be handled in a future process().
            if (prevMySplitter != null || nextMySplitter != null) {
                Location mySplitter = null;
                if (prevMySplitter == null) {
                    mySplitter = nextMySplitter;
                } else if (nextMySplitter == null) {
                    mySplitter = prevMySplitter;
                } else {
                    int cmpSplitters = SweepEvent.comparePoints(prevMySplitter, nextMySplitter);
                    mySplitter = cmpSplitters <= 0 ? prevMySplitter : nextMySplitter;
                }

                // Rounding errors can cause changes in ordering,
                // so remove afected segments and right sweep events before splitting
                this.queue.remove(segment.rightSE);
                newEvents.add(segment.rightSE);

                List<SweepEvent> newEventsFromSplit = segment.split(mySplitter);
                newEvents.addAll(newEventsFromSplit);
            }

            if (newEvents.size() > 0) {
                // We found some intersections, so re-do the current event to
                // make sure sweep line ordering is totally consistent for later
                // use with the segment 'prev' pointers
                this.tree.remove(segment);
                newEvents.add(event);
            } else {
                // done with left event
                this.segments.add(segment);
                segment.prev = prevSeg;
            }
        } else {
            // event.isRight

            // since we're about to be removed from the sweep line, check for
            // intersections between our previous and next segments
            if (prevSeg != null && nextSeg != null) {
                Location inter = prevSeg.getIntersection(nextSeg);
                if (inter != null) {
                    if (!prevSeg.isAnEndpoint(inter)) {
                        List<SweepEvent> newEventsFromSplit = this._splitSafely(prevSeg, inter);
                        newEvents.addAll(newEventsFromSplit);
                    }
                    if (!nextSeg.isAnEndpoint(inter)) {
                        List<SweepEvent> newEventsFromSplit = this._splitSafely(nextSeg, inter);
                        newEvents.addAll(newEventsFromSplit);
                    }
                }
            }

            this.tree.remove(segment);
        }

        return newEvents;
    }

    /* Safely split a segment that is currently in the datastructures
     * IE - a segment other than the one that is currently being processed. */
    public List<SweepEvent> _splitSafely(Segment seg, Location pt) {
        // Rounding errors can cause changes in ordering,
        // so remove afected segments and right sweep events before splitting
        // removeNode() doesn't work, so have re-find the seg
        // https://github.com/w8r/splay-tree/pull/5
        this.tree.remove(seg);
        SweepEvent rightSE = seg.rightSE;
        this.queue.remove(rightSE);
        List<SweepEvent> newEvents = seg.split(pt);
        newEvents.add(rightSE);
        // splitting can trigger consumption
        if (seg.consumedBy == null) {
            this.tree.add(seg);
        }
        return newEvents;
    }

}
