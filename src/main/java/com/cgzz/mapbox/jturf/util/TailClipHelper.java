package com.cgzz.mapbox.jturf.util;

import com.cgzz.mapbox.jturf.shape.BoundingBox;
import com.cgzz.mapbox.jturf.shape.Point;

import java.util.ArrayList;
import java.util.List;

public final class TailClipHelper {

    private TailClipHelper() {
        throw new AssertionError("No Instances.");
    }

    /**
     * Cohen-Sutherland line clipping algorithm, adapted to efficiently handle polylines rather than just segments
     */
    public static List<List<Point>> lineclip(List<Point> points, BoundingBox bbox, List<List<Point>> result) {
        if (result == null) {
            result = new ArrayList<>();
        }
        int len = points.size(), codeA = bitCode(points.get(0), bbox);
        int codeB, lastCode;
        List<Point> part = new ArrayList<>();

        for (int i = 1; i < len; i++) {
            Point a = points.get(i - 1);
            Point b = points.get(i);

            codeB = lastCode = bitCode(b, bbox);

            while (true) {
                if ((codeA | codeB) == 0) {
                    part.add(a);

                    if (codeB != lastCode) {
                        // segment went outside
                        part.add(b);

                        if (i < len - 1) {
                            // start a new line
                            result.add(part);
                            part = new ArrayList<>();
                        }
                    } else if (i == len - 1) {
                        part.add(b);
                    }
                    break;
                } else if ((codeA & codeB) > 0) {
                    // trivial reject
                    break;
                } else if (codeA > 0) {
                    // a outside, intersect with clip edge
                    Point intersectPoint = intersect(a, b, codeA, bbox);
                    if (intersectPoint != null) {
                        a = intersectPoint;
                    }
                    codeA = bitCode(a, bbox);
                } else {
                    // b outside
                    Point intersectPoint = intersect(a, b, codeB, bbox);
                    if (intersectPoint != null) {
                        b = intersectPoint;
                    }
                    codeB = bitCode(b, bbox);
                }
            }

            codeA = lastCode;
        }

        if (part.size() > 0) {
            result.add(part);
        }

        return result;
    }

    /**
     * Sutherland-Hodgeman polygon clipping algorithm
     */
    public static List<Point> polygonclip(List<Point> points, BoundingBox bbox) {
        // clip against each side of the clip rectangle
        List<Point> result = null;
        for (int edge = 1; edge <= 8; edge *= 2) {
            result = new ArrayList<>();
            int size = points.size();
            Point prev = points.get(size - 1);
            boolean prevInside = !((bitCode(prev, bbox) & edge) > 0);

            for (int i = 0; i < size; i++) {
                Point p = points.get(i);
                boolean inside = !((bitCode(p, bbox) & edge) > 0);

                // if segment goes through the clip window, add an intersection
                if (inside != prevInside) {
                    Point intersectPoint = intersect(prev, p, edge, bbox);
                    if (intersectPoint != null) {
                        result.add(intersectPoint);
                    }
                }

                if (inside) {
                    result.add(p); // add a point if it's inside
                }

                prev = p;
                prevInside = inside;
            }

            points = result;

            if (points.isEmpty()) {
                break;
            }
        }

        return result;
    }

    /**
     * intersect a segment against one of the 4 lines that make up the bbox
     */
    public static Point intersect(Point a, Point b, int edge, BoundingBox bbox) {
        if ((edge & 8) > 0) {
            return Point.fromLngLat(a.getLongitude() + ((b.getLongitude() - a.getLongitude()) * (bbox.north() - a.getLatitude())) / (b.getLatitude() - a.getLatitude()), bbox.north()); // top
        } else if ((edge & 4) > 0) {
            return Point.fromLngLat(a.getLongitude() + ((b.getLongitude() - a.getLongitude()) * (bbox.south() - a.getLatitude())) / (b.getLatitude() - a.getLatitude()), bbox.south()); // bottom
        } else if ((edge & 2) > 0) {
            return Point.fromLngLat(bbox.east(), a.getLatitude() + ((b.getLatitude() - a.getLatitude()) * (bbox.east() - a.getLongitude())) / (b.getLongitude() - a.getLongitude())); // right
        } else if ((edge & 1) > 0) {
            return Point.fromLngLat(bbox.west(), a.getLatitude() + ((b.getLatitude() - a.getLatitude()) * (bbox.west() - a.getLongitude())) / (b.getLongitude() - a.getLongitude())); // left
        } else {
            return null;
        }
    }

    /**
     * 位代码反映相对于 bbox 的点位置：<br>
     *          left  mid  right<br>
     *     top  1001  1000  1010<br>
     *     mid  0001  0000  0010<br>
     *  bottom  0101  0100  0110<br>
     * @param point 点
     * @param bbox  边框
     * @return int
     */
    public static int bitCode(Point point, BoundingBox bbox) {
        int code = 0;
        if (point.getLongitude() < bbox.west()) { // left
            code |= 1;
        } else if (point.getLongitude() > bbox.east()) { // right
            code |= 2;
        }

        if (point.getLatitude() < bbox.south()) { // bottom
            code |= 4;
        } else if (point.getLatitude() > bbox.north()) { // top
            code |= 8;
        }

        return code;
    }

    /**
     * 裁剪多边形
     * @param ring 多边形点集合
     * @param bbox 边框
     * @return 返回裁剪后的多边形点集合
     */
    public static List<Point> clipPolygon(List<Point> ring, BoundingBox bbox) {
        List<Point> clipped = polygonclip(ring, bbox);
        if (clipped != null && !clipped.isEmpty()) {
            if (!JTurfHelper.validatePolygonEndToEnd(clipped)) {
                clipped.add(clipped.get(0));
            }
        }
        return clipped;
    }

}
