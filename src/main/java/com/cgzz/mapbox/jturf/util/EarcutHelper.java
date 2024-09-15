package com.cgzz.mapbox.jturf.util;

import java.util.ArrayList;
import java.util.List;

public final class EarcutHelper {

    private EarcutHelper() {
        throw new AssertionError("No Instances.");
    }

    private static class Node {
        // vertex index in coordinates array
        int i;
        // vertex coordinates
        double x;
        double y;

        // previous and next vertex nodes in a polygon ring
        Node prev;
        Node next;

        // z-order curve value
        int z = 0;

        // previous and next nodes in z-order
        Node prevZ;
        Node nextZ;

        // indicates whether this is a steiner point
        boolean steiner = false;

        Node(int i, double x, double y) {
            this.i = i;
            this.x = x;
            this.y = y;
        }
    }

    public static List<Integer> earcut(List<Double> vertices, List<Integer> holeIndices, int dim) {
        double[] data = toDoubleArray(vertices);

        boolean hasHoles = holeIndices != null && holeIndices.size() > 0;
        int outerLen = hasHoles ? holeIndices.get(0) * dim : data.length;
        Node outerNode = linkedList(data, 0, outerLen, dim, true);
        List<Integer> triangles = new ArrayList<>();

        if (outerNode == null || outerNode.next == outerNode.prev) {
            return triangles;
        }

        double minX = 0, minY = 0, maxX, maxY, x, y;
        int invSize = 0;

        if (hasHoles) outerNode = eliminateHoles(data, holeIndices, outerNode, dim);

        // if the shape is not too simple, we'll use z-order curve hash later; calculate polygon bbox
        if (data.length > 80 * dim) {
            minX = maxX = data[0];
            minY = maxY = data[1];

            for (int i = dim; i < outerLen; i += dim) {
                x = data[i];
                y = data[i + 1];
                if (x < minX) minX = x;
                if (y < minY) minY = y;
                if (x > maxX) maxX = x;
                if (y > maxY) maxY = y;
            }

            // minX, minY and invSize are later used to transform coords into integers for z-order calculation
            invSize = (int)Math.max(maxX - minX, maxY - minY);
            invSize = invSize != 0 ? 32767 / invSize : 0;
        }

        earcutLinked(outerNode, triangles, dim, minX, minY, invSize, 0);

        return triangles;
    }

    // create a circular doubly linked list from polygon points in the specified winding order
    private static Node linkedList(double[] data, int start, int end, int dim, boolean clockwise) {
        Node last = null;

        if (clockwise == (signedArea(data, start, end, dim) > 0)) {
            for (int i = start; i < end; i += dim) {
                last = insertNode(i, data[i], data[i + 1], last);
            }
        } else {
            for (int i = end - dim; i >= start; i -= dim) {
                last = insertNode(i, data[i], data[i + 1], last);
            }
        }

        if (last != null && equals(last, last.next)) {
            removeNode(last);
            last = last.next;
        }

        return last;
    }

    // eliminate colinear or duplicate points
    private static Node filterPoints(Node start, Node end) {
        if (start == null) return null;
        if (end == null) end = start;

        Node p = start;
        boolean again;
        do {
            again = false;

            if (!p.steiner && (equals(p, p.next) || area(p.prev, p, p.next) == 0)) {
                removeNode(p);
                p = end = p.prev;
                if (p == p.next) break;
                again = true;

            } else {
                p = p.next;
            }
        } while (again || p != end);

        return end;
    }

    // main ear slicing loop which triangulates a polygon (given as a linked list)
    private static void earcutLinked(Node ear, List<Integer> triangles, int dim, double minX, double minY, int invSize, int pass) {
        if (ear == null) return;

        // interlink polygon nodes in z-order
        if (pass == 0 && invSize != 0) indexCurve(ear, minX, minY, invSize);

        Node stop = ear, prev, next;

        // iterate through ears, slicing them one by one
        while (ear.prev != ear.next) {
            prev = ear.prev;
            next = ear.next;

            if (invSize != 0 ? isEarHashed(ear, minX, minY, invSize) : isEar(ear)) {
                // cut off the triangle
                triangles.add(prev.i / dim);
                triangles.add(ear.i / dim);
                triangles.add(next.i / dim);

                removeNode(ear);

                // skipping the next vertex leads to less sliver triangles
                ear = next.next;
                stop = next.next;

                continue;
            }

            ear = next;

            // if we looped through the whole remaining polygon and can't find any more ears
            if (ear == stop) {
                // try filtering points and slicing again
                if (pass == 0) {
                    earcutLinked(filterPoints(ear, null), triangles, dim, minX, minY, invSize, 1);

                    // if this didn't work, try curing all small self-intersections locally
                } else if (pass == 1) {
                    ear = cureLocalIntersections(filterPoints(ear, null), triangles, dim);
                    earcutLinked(ear, triangles, dim, minX, minY, invSize, 2);

                    // as a last resort, try splitting the remaining polygon into two
                } else if (pass == 2) {
                    splitEarcut(ear, triangles, dim, minX, minY, invSize);
                }

                break;
            }
        }
    }

    // check whether a polygon node forms a valid ear with adjacent nodes
    private static boolean isEar(Node ear) {
        Node a = ear.prev, b = ear, c = ear.next;

        if (area(a, b, c) >= 0) return false; // reflex, can't be an ear

        // now make sure we don't have other points inside the potential ear
        double ax = a.x, bx = b.x, cx = c.x, ay = a.y, by = b.y, cy = c.y;

        // triangle bbox; min & max are calculated like this for speed
        double x0 = ax < bx ? (Math.min(ax, cx)) : (Math.min(bx, cx)),
                y0 = ay < by ? (Math.min(ay, cy)) : (Math.min(by, cy)),
                x1 = ax > bx ? (Math.max(ax, cx)) : (Math.max(bx, cx)),
                y1 = ay > by ? (Math.max(ay, cy)) : (Math.max(by, cy));

        Node p = c.next;
        while (p != a) {
            if (p.x >= x0 && p.x <= x1 && p.y >= y0 && p.y <= y1 &&
                    pointInTriangle(ax, ay, bx, by, cx, cy, p.x, p.y) &&
                    area(p.prev, p, p.next) >= 0) return false;
            p = p.next;
        }

        return true;
    }

    private static boolean isEarHashed(Node ear, double minX, double minY, int invSize) {
        Node a = ear.prev,
                b = ear,
                c = ear.next;

        if (area(a, b, c) >= 0) return false; // reflex, can't be an ear

        double ax = a.x, bx = b.x, cx = c.x, ay = a.y, by = b.y, cy = c.y;

        // triangle bbox; min & max are calculated like this for speed
        double x0 = ax < bx ? (Math.min(ax, cx)) : (Math.min(bx, cx)),
                y0 = ay < by ? (Math.min(ay, cy)) : (Math.min(by, cy)),
                x1 = ax > bx ? (Math.max(ax, cx)) : (Math.max(bx, cx)),
                y1 = ay > by ? (Math.max(ay, cy)) : (Math.max(by, cy));

        // z-order range for the current triangle bbox;
        int minZ = zOrder((int)x0, (int)y0, (int)minX, (int)minY, invSize), maxZ = zOrder((int)x1, (int)y1, (int)minX, (int)minY, invSize);

        Node p = ear.prevZ, n = ear.nextZ;

        // look for points inside the triangle in both directions
        while (p != null && p.z >= minZ && n != null && n.z <= maxZ) {
            if (p.x >= x0 && p.x <= x1 && p.y >= y0 && p.y <= y1 && p != a && p != c &&
                    pointInTriangle(ax, ay, bx, by, cx, cy, p.x, p.y) && area(p.prev, p, p.next) >= 0) return false;
            p = p.prevZ;

            if (n.x >= x0 && n.x <= x1 && n.y >= y0 && n.y <= y1 && n != a && n != c &&
                    pointInTriangle(ax, ay, bx, by, cx, cy, n.x, n.y) && area(n.prev, n, n.next) >= 0) return false;
            n = n.nextZ;
        }

        // look for remaining points in decreasing z-order
        while (p != null && p.z >= minZ) {
            if (p.x >= x0 && p.x <= x1 && p.y >= y0 && p.y <= y1 && p != a && p != c &&
                    pointInTriangle(ax, ay, bx, by, cx, cy, p.x, p.y) && area(p.prev, p, p.next) >= 0) return false;
            p = p.prevZ;
        }

        // look for remaining points in increasing z-order
        while (n != null && n.z <= maxZ) {
            if (n.x >= x0 && n.x <= x1 && n.y >= y0 && n.y <= y1 && n != a && n != c &&
                    pointInTriangle(ax, ay, bx, by, cx, cy, n.x, n.y) && area(n.prev, n, n.next) >= 0) return false;
            n = n.nextZ;
        }

        return true;
    }

    // go through all polygon nodes and cure small local self-intersections
    private static Node cureLocalIntersections(Node start, List<Integer> triangles, int dim) {
        Node p = start;
        do {
            Node a = p.prev, b = p.next.next;

            if (!equals(a, b) && intersects(a, p, p.next, b) && locallyInside(a, b) && locallyInside(b, a)) {

                triangles.add(a.i / dim);
                triangles.add(p.i / dim);
                triangles.add(b.i / dim);

                // remove two nodes involved
                removeNode(p);
                removeNode(p.next);

                p = start = b;
            }
            p = p.next;
        } while (p != start);

        return filterPoints(p, null);
    }

    // try splitting polygon into two and triangulate them independently
    private static void splitEarcut(Node start, List<Integer> triangles, int dim, double minX, double minY, int invSize) {
        // look for a valid diagonal that divides the polygon into two
        Node a = start;
        do {
            Node b = a.next.next;
            while (b != a.prev) {
                if (a.i != b.i && isValidDiagonal(a, b)) {
                    // split the polygon in two by the diagonal
                    Node c = splitPolygon(a, b);

                    // filter colinear points around the cuts
                    a = filterPoints(a, a.next);
                    c = filterPoints(c, c.next);

                    // run earcut on each half
                    earcutLinked(a, triangles, dim, minX, minY, invSize, 0);
                    earcutLinked(c, triangles, dim, minX, minY, invSize, 0);
                    return;
                }
                b = b.next;
            }
            a = a.next;
        } while (a != start);
    }

    // link every hole into the outer loop, producing a single-ring polygon without holes
    private static Node eliminateHoles(double[] data, List<Integer> holeIndices, Node outerNode, int dim) {
        List<Node> queue = new ArrayList<>();
        Node list;

        for (int i = 0, len = holeIndices.size(); i < len; i++) {
            int start = holeIndices.get(i) * dim;
            int end = i < len - 1 ? holeIndices.get(i + 1) * dim : data.length;
            list = linkedList(data, start, end, dim, false);
            if (list == list.next) list.steiner = true;
            queue.add(getLeftmost(list));
        }

        queue.sort(EarcutHelper::compareX);

        // process holes from left to right
        for (Node node : queue) {
            outerNode = eliminateHole(node, outerNode);
        }

        return outerNode;
    }

    private static int compareX(Node a, Node b) {
        double c = a.x - b.x;
        return c > 0 ? 1 : c == 0 ? 0 : -1;
    }

    // find a bridge between vertices that connects hole with an outer ring and and link it
    private static Node eliminateHole(Node hole, Node outerNode) {
        Node bridge = findHoleBridge(hole, outerNode);
        if (bridge == null) {
            return outerNode;
        }

        Node bridgeReverse = splitPolygon(bridge, hole);

        // filter collinear points around the cuts
        filterPoints(bridgeReverse, bridgeReverse.next);
        return filterPoints(bridge, bridge.next);
    }

    // David Eberly's algorithm for finding a bridge between hole and outer polygon
    private static Node findHoleBridge(Node hole, Node outerNode) {
        Node p = outerNode, m = null;
        double hx = hole.x,
                hy = hole.y,
                qx = Double.NEGATIVE_INFINITY;

        // find a segment intersected by a ray from the hole's leftmost point to the left;
        // segment's endpoint with lesser x will be potential connection point
        do {
            if (hy <= p.y && hy >= p.next.y && p.next.y != p.y) {
                double x = p.x + (hy - p.y) * (p.next.x - p.x) / (p.next.y - p.y);
                if (x <= hx && x > qx) {
                    qx = x;
                    m = p.x < p.next.x ? p : p.next;
                    if (x == hx) return m; // hole touches outer segment; pick leftmost endpoint
                }
            }
            p = p.next;
        } while (p != outerNode);

        if (m == null) return null;

        // look for points inside the triangle of hole point, segment intersection and endpoint;
        // if there are no points found, we have a valid connection;
        // otherwise choose the point of the minimum angle with the ray as connection point

        Node stop = m;
        double mx = m.x,
                my = m.y,
                tanMin = Double.POSITIVE_INFINITY,
                tan;

        p = m;

        do {
            if (hx >= p.x && p.x >= mx && hx != p.x &&
                    pointInTriangle(hy < my ? hx : qx, hy, mx, my, hy < my ? qx : hx, hy, p.x, p.y)) {

                tan = Math.abs(hy - p.y) / (hx - p.x); // tangential

                if (locallyInside(p, hole) &&
                        (tan < tanMin || (tan == tanMin && (p.x > m.x || (p.x == m.x && sectorContainsSector(m, p)))))) {
                    m = p;
                    tanMin = tan;
                }
            }

            p = p.next;
        } while (p != stop);

        return m;
    }

    // whether sector in vertex m contains sector in vertex p in the same coordinates
    private static boolean sectorContainsSector(Node m, Node p) {
        return area(m.prev, m, p.prev) < 0 && area(p.next, m, m.next) < 0;
    }

    // interlink polygon nodes in z-order
    private static void indexCurve(Node start, double minX, double minY, int invSize) {
        Node p = start;
        do {
            if (p.z == 0) p.z = zOrder((int)p.x, (int)p.y, (int)minX, (int)minY, invSize);
            p.prevZ = p.prev;
            p.nextZ = p.next;
            p = p.next;
        } while (p != start);

        p.prevZ.nextZ = null;
        p.prevZ = null;

        sortLinked(p);
    }

    // Simon Tatham's linked list merge sort algorithm
    // http://www.chiark.greenend.org.uk/~sgtatham/algorithms/listsort.html
    private static Node sortLinked(Node list) {
        Node p, q, e, tail;
        int i, inSize = 1, pSize, qSize, numMerges;

        do {
            p = list;
            list = null;
            tail = null;
            numMerges = 0;

            while (p != null) {
                numMerges++;
                q = p;
                pSize = 0;
                for (i = 0; i < inSize; i++) {
                    pSize++;
                    q = q.nextZ;
                    if (q == null) break;
                }
                qSize = inSize;

                while (pSize > 0 || (qSize > 0 && q != null)) {

                    if (pSize != 0 && (qSize == 0 || q == null || p.z <= q.z)) {
                        e = p;
                        p = p.nextZ;
                        pSize--;
                    } else {
                        e = q;
                        q = q.nextZ;
                        qSize--;
                    }

                    if (tail != null) tail.nextZ = e;
                    else list = e;

                    e.prevZ = tail;
                    tail = e;
                }

                p = q;
            }

            tail.nextZ = null;
            inSize *= 2;

        } while (numMerges > 1);

        return list;
    }

    private static int zOrder(int x, int y, int minX, int minY, int invSize) {
        // coords are transformed into non-negative 15-bit integer range
        x = (x - minX) * invSize;
        y = (y - minY) * invSize;

        x = (x | (x << 8)) & 0x00FF00FF;
        x = (x | (x << 4)) & 0x0F0F0F0F;
        x = (x | (x << 2)) & 0x33333333;
        x = (x | (x << 1)) & 0x55555555;

        y = (y | (y << 8)) & 0x00FF00FF;
        y = (y | (y << 4)) & 0x0F0F0F0F;
        y = (y | (y << 2)) & 0x33333333;
        y = (y | (y << 1)) & 0x55555555;

        return x | (y << 1);
    }

    // find the leftmost node of a polygon ring
    private static Node getLeftmost(Node start) {
        Node p = start, leftmost = start;
        do {
            if (p.x < leftmost.x || (p.x == leftmost.x && p.y < leftmost.y)) leftmost = p;
            p = p.next;
        } while (p != start);

        return leftmost;
    }

    // check if a point lies within a convex triangle
    private static boolean pointInTriangle(double ax, double ay, double bx, double by, double cx, double cy, double px, double py) {
        return (cx - px) * (ay - py) >= (ax - px) * (cy - py) &&
                (ax - px) * (by - py) >= (bx - px) * (ay - py) &&
                (bx - px) * (cy - py) >= (cx - px) * (by - py);
    }

    // check if a diagonal between two polygon nodes is valid (lies in polygon interior)
    private static boolean isValidDiagonal(Node a, Node b) {
        return a.next.i != b.i && a.prev.i != b.i && !intersectsPolygon(a, b) && // dones't intersect other edges
                (locallyInside(a, b) && locallyInside(b, a) && middleInside(a, b) && // locally visible
                        (area(a.prev, a, b.prev) != 0 || area(a, b.prev, b) != 0) || // does not create opposite-facing sectors
                        equals(a, b) && area(a.prev, a, a.next) > 0 && area(b.prev, b, b.next) > 0); // special zero-length case
    }

    // signed area of a triangle
    private static double area(Node p, Node q, Node r) {
        return (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
    }

    // check if two points are equal
    private static boolean equals(Node p1, Node p2) {
        return p1.x == p2.x && p1.y == p2.y;
    }

    // check if two segments intersect
    private static boolean intersects(Node p1, Node q1, Node p2, Node q2) {
        int o1 = sign(area(p1, q1, p2));
        int o2 = sign(area(p1, q1, q2));
        int o3 = sign(area(p2, q2, p1));
        int o4 = sign(area(p2, q2, q1));

        if (o1 != o2 && o3 != o4) return true; // general case

        if (o1 == 0 && onSegment(p1, p2, q1)) return true; // p1, q1 and p2 are collinear and p2 lies on p1q1
        if (o2 == 0 && onSegment(p1, q2, q1)) return true; // p1, q1 and q2 are collinear and q2 lies on p1q1
        if (o3 == 0 && onSegment(p2, p1, q2)) return true; // p2, q2 and p1 are collinear and p1 lies on p2q2
        if (o4 == 0 && onSegment(p2, q1, q2)) return true; // p2, q2 and q1 are collinear and q1 lies on p2q2

        return false;
    }

    // for collinear points p, q, r, check if point q lies on segment pr
    private static boolean onSegment(Node p, Node q, Node r) {
        return q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) && q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y);
    }

    private static int sign(double num) {
        return num > 0 ? 1 : num < 0 ? -1 : 0;
    }

    // check if a polygon diagonal intersects any polygon segments
    private static boolean intersectsPolygon(Node a, Node b) {
        Node p = a;
        do {
            if (p.i != a.i && p.next.i != a.i && p.i != b.i && p.next.i != b.i &&
                    intersects(p, p.next, a, b)) return true;
            p = p.next;
        } while (p != a);

        return false;
    }

    // check if a polygon diagonal is locally inside the polygon
    private static boolean locallyInside(Node a, Node b) {
        return area(a.prev, a, a.next) < 0 ?
                area(a, b, a.next) >= 0 && area(a, a.prev, b) >= 0 :
                area(a, b, a.prev) < 0 || area(a, a.next, b) < 0;
    }

    // check if the middle point of a polygon diagonal is inside the polygon
    private static boolean middleInside(Node a, Node b) {
        Node p = a;
        boolean inside = false;
        double px = (a.x + b.x) / 2, py = (a.y + b.y) / 2;
        do {
            if (((p.y > py) != (p.next.y > py)) && p.next.y != p.y &&
                    (px < (p.next.x - p.x) * (py - p.y) / (p.next.y - p.y) + p.x))
                inside = !inside;
            p = p.next;
        } while (p != a);

        return inside;
    }

    // link two polygon vertices with a bridge; if the vertices belong to the same ring, it splits polygon into two;
// if one belongs to the outer ring and another to a hole, it merges it into a single ring
    private static Node splitPolygon(Node a, Node b) {
        Node a2 = new Node(a.i, a.x, a.y),
                b2 = new Node(b.i, b.x, b.y),
                an = a.next,
                bp = b.prev;

        a.next = b;
        b.prev = a;

        a2.next = an;
        an.prev = a2;

        b2.next = a2;
        a2.prev = b2;

        bp.next = b2;
        b2.prev = bp;

        return b2;
    }

    private static Node insertNode(int i, double x, double y, Node last) {
        Node p = new Node(i, x, y);

        if (last == null) {
            p.prev = p;
            p.next = p;
        } else {
            p.next = last.next;
            p.prev = last;
            last.next.prev = p;
            last.next = p;
        }
        return p;
    }

    private static void removeNode(Node p) {
        p.next.prev = p.prev;
        p.prev.next = p.next;

        if (p.prevZ != null) {
            p.prevZ.nextZ = p.nextZ;
        }
        if (p.nextZ != null) {
            p.nextZ.prevZ = p.prevZ;
        }
    }

    private static double signedArea(double[] data, int start, int end, int dim) {
        double sum = 0;
        for (int i = start, j = end - dim; i < end; i += dim) {
            sum += (data[j] - data[i]) * (data[i + 1] + data[j + 1]);
            j = i;
        }
        return sum;
    }

    private static double[] toDoubleArray(List<Double> vertices) {
        double[] data = new double[vertices.size()];
        for (int i = 0, size = vertices.size(); i < size; i++) {
            data[i] = vertices.get(i);
        }
        return data;
    }

}
