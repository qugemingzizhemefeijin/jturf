package com.cgzz.mapbox.jturf.util.pkg.clipping.polygonclipping;

public class Bbox {

    public final Location ll;

    public final Location ur;

    public Bbox(Location ll, Location ur) {
        this.ll = ll;
        this.ur = ur;
    }

    public static boolean isInBbox(Bbox bbox, Location point) {
        return bbox.ll.x <= point.x
                && point.x <= bbox.ur.x
                && bbox.ll.y <= point.y
                && point.y <= bbox.ur.y;
    }

    /*
     * Returns either null, or a bbox (aka an ordered pair of points)
     * If there is only one point of overlap, a bbox with identical points
     * will be returned
     */
    public static Bbox getBboxOverlap(Bbox b1, Bbox b2) {
        // check if the bboxes overlap at all
        if (b2.ur.x < b1.ll.x
                || b1.ur.x < b2.ll.x
                || b2.ur.y < b1.ll.y
                || b1.ur.y < b2.ll.y) {
            return null;
        }

        // find the middle two X values
        double lowerX = Math.max(b1.ll.x, b2.ll.x);
        double upperX = Math.min(b1.ur.x, b2.ur.x);

        // find the middle two Y values
        double lowerY = Math.max(b1.ll.y, b2.ll.y);
        double upperY = Math.min(b1.ur.y, b2.ur.y);

        // put those middle values together to get the overlap
        return new Bbox(new Location(lowerX, lowerY), new Location(upperX, upperY));
    }

}
