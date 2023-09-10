package com.cgzz.mapbox.jturf.util.polygonclipping;

public class Vector {

    // Cross Product of two vectors with first point at origin
    public static double crossProduct(Location a, Location b) {
        return a.x * b.y - a.y * b.x;
    }

    // Dot Product of two vectors with first point at origin
    public static double dotProduct(Location a, Location b) {
        return a.x * b.x + a.y * b.y;
    }

    // Comparator for two vectors with same starting point
    public static int compareVectorAngles(Location basePt, Location endPt1, Location endPt2) {
        Location v1 = Location.location(endPt1.x - basePt.x, endPt1.y - basePt.y);
        Location v2 = Location.location(endPt2.x - basePt.x, endPt2.y - basePt.y);
        double kross = crossProduct(v1, v2);

        return Flp.cmp(kross, 0);
    }

    public static double length(Location v) {
        return Math.sqrt(dotProduct(v, v));
    }

    // Get the sine of the angle from pShared -> pAngle to pShaed -> pBase
    public static double sineOfAngle(Location pShared, Location pBase, Location pAngle) {
        Location vBase = Location.location(pBase.x - pShared.x, pBase.y - pShared.y);
        Location vAngle = Location.location(pAngle.x - pShared.x, pAngle.y - pShared.y);
        return crossProduct(vAngle, vBase) / length(vAngle) / length(vBase);
    }

    // Get the cosine of the angle from pShared -> pAngle to pShaed -> pBase
    public static double cosineOfAngle(Location pShared, Location pBase, Location pAngle) {
        Location vBase = Location.location(pBase.x - pShared.x, pBase.y - pShared.y);
        Location vAngle = Location.location(pAngle.x - pShared.x, pAngle.y - pShared.y);
        return dotProduct(vAngle, vBase) / length(vAngle) / length(vBase);
    }

    // Get the closest point on an line (defined by two points) to another point.
    public static Location closestPoint(Location ptA1, Location ptA2, Location ptB) {
        if (ptA1.x == ptA2.x) { // vertical vector
            return Location.location(ptA1.x, ptB.y);
        }
        if (ptA1.y == ptA2.y) { // horizontal vector
            return Location.location(ptB.x, ptA1.y);
        }
        // determinne which point is further away
        // we use the further point as our base in the calculation, so that the
        // vectors are more parallel, providing more accurate dot product
        Location v1 = Location.location(ptB.x - ptA1.x, ptB.y - ptA1.y);
        Location v2 = Location.location(ptB.x - ptA2.x, ptB.y - ptA2.y);
        Location vFar, vA, farPt;
        if (dotProduct(v1, v1) > dotProduct(v2, v2)) {
            vFar = v1;
            vA = Location.location(ptA2.x - ptA1.x, ptA2.y - ptA1.y);
            farPt = ptA1;
        } else {
            vFar = v2;
            vA = Location.location(ptA1.x - ptA2.x, ptA1.y - ptA2.y);
            farPt = ptA2;
        }

        // manually test if the current point can be considered to be on the line
        // If the X coordinate was on the line, would the Y coordinate be as well?
        double xDist = (ptB.x - farPt.x) / vA.x;
        if (ptB.y == farPt.y + xDist * vA.y) {
            return ptB;
        }

        // If the Y coordinate was on the line, would the X coordinate be as well?
        double yDist = (ptB.y - farPt.y) / vA.y;
        if (ptB.x == farPt.x + yDist * vA.x) {
            return ptB;
        }

        // current point isn't exactly on line, so return closest point
        double dist = dotProduct(vA, vFar) / dotProduct(vA, vA);
        return Location.location(farPt.x + dist * vA.x, farPt.y + dist * vA.y);
    }

    /* Get the x coordinate where the given line (defined by a point and vector)
     * crosses the horizontal line with the given y coordiante.
     * In the case of parrallel lines (including overlapping ones) returns null. */
    public static Location horizontalIntersection(Location pt, Location v, double y) {
        if (v.y == 0) {
            return null;
        }
        return Location.location(pt.x + (v.x / v.y) * (y - pt.y), y);
    }

    /* Get the y coordinate where the given line (defined by a point and vector)
     * crosses the vertical line with the given x coordiante.
     * In the case of parrallel lines (including overlapping ones) returns null. */
    public static Location verticalIntersection(Location pt, Location v, double x) {
        if (v.x == 0) {
            return null;
        }
        return Location.location(x, pt.y + (v.y / v.x) * (x - pt.x));
    }

    /* Get the intersection of two lines, each defined by a base point and a vector.
     * In the case of parrallel lines (including overlapping ones) returns null. */
    public static Location intersection(Location pt1, Location v1, Location pt2, Location v2) {
        // take some shortcuts for vertical and horizontal lines
        // this also ensures we don't calculate an intersection and then discover
        // it's actually outside the bounding box of the line
        if (v1.x == 0) {
            return verticalIntersection(pt2, v2, pt1.x);
        }
        if (v2.x == 0) {
            return verticalIntersection(pt1, v1, pt2.x);
        }
        if (v1.y == 0) {
            return horizontalIntersection(pt2, v2, pt1.y);
        }
        if (v2.y == 0) {
            return horizontalIntersection(pt1, v1, pt2.y);
        }

        // General case for non-overlapping segments.
        // This algorithm is based on Schneider and Eberly.
        // http://www.cimec.org.ar/~ncalvo/Schneider_Eberly.pdf - pg 244

        double cross = crossProduct(v1, v2);
        if (cross == 0) {
            return null;
        }

        Location ve = Location.location(pt2.x - pt1.x, pt2.y - pt1.y);
        double d1 = crossProduct(ve, v1) / cross;
        double d2 = crossProduct(ve, v2) / cross;

        // take the average of the two calculations to minimize rounding error
        double x1 = pt1.x + d2 * v1.x, x2 = pt2.x + d1 * v2.x;
        double y1 = pt1.y + d2 * v1.y, y2 = pt2.y + d1 * v2.y;
        double x = (x1 + x2) / 2;
        double y = (y1 + y2) / 2;
        return Location.location(x, y);
    }

    // Given a vector, return one that is perpendicular
    public static Location perpendicular(Location v) {
        return Location.location(-v.y, v.x);
    }

}
