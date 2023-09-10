package com.cgzz.mapbox.jturf.util;

import com.cgzz.mapbox.jturf.shape.Point;
import com.cgzz.mapbox.jturf.shape.Polygon;

import java.util.List;

public class JTurfPointInPolygonHelper {

    private JTurfPointInPolygonHelper() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 判断点是否在多边形中
     *
     * @param point   要判断的点
     * @param polygon 多边形
     * @return 在多边形边界中则返回0，在多边形中则返回1，不在则返回2
     */
    public static int pointInPolygon(Point point, Polygon polygon) {
        List<Point> coords = polygon.coordinates();
        Point currentP = coords.get(0);
        int contourLen = coords.size() - 1;
        int k = 0;

        double x = point.getLongitude(), y = point.getLatitude();

        double u1 = currentP.getX() - x, v1 = currentP.getY() - y;
        for (int i = 0; i < contourLen; i++) {
            Point nextP = coords.get(i + 1);
            double v2 = nextP.getY() - y;

            if ((v1 < 0 && v2 < 0) || (v1 > 0 && v2 > 0)) {
                currentP = nextP;
                v1 = v2;
                u1 = currentP.getX() - x;
                continue;
            }

            double u2 = nextP.getX() - x;

            if (v2 > 0 && v1 <= 0) {
                double f = (u1 * v2) - (u2 * v1);
                if (f > 0) {
                    k = k + 1;
                } else if (f == 0) {
                    return 0;
                }
            } else if (v1 > 0 && v2 <= 0) {
                double f = (u1 * v2) - (u2 * v1);
                if (f < 0) {
                    k = k + 1;
                } else if (f == 0) {
                    return 0;
                }
            } else if (v2 == 0 && v1 < 0) {
                double f = (u1 * v2) - (u2 * v1);
                if (f == 0) {
                    return 0;
                }
            } else if (v1 == 0 && v2 < 0) {
                double f = u1 * v2 - u2 * v1;
                if (f == 0) {
                    return 0;
                }
            } else if (v1 == 0 && v2 == 0) {
                if (u2 <= 0 && u1 >= 0) {
                    return 0;
                } else if (u1 <= 0 && u2 >= 0) {
                    return 0;
                }
            }
            currentP = nextP;
            v1 = v2;
            u1 = u2;
        }

        if (k % 2 == 0) {
            return 2;
        } else {
            return 1;
        }
    }

}
