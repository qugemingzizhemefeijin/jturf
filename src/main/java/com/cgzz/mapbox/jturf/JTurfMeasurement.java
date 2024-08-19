package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.enums.Units;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import com.cgzz.mapbox.jturf.util.AreaHelper;

public final class JTurfMeasurement {

    /**
     * 地球半径（以米为单位）
     */
    public static double EARTH_RADIUS = 6378137;

    private JTurfMeasurement() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 计算两点之间的距离，默认单位公里。该方法使用哈弗赛因公式来考虑全球曲率。
     *
     * @param p1 点1
     * @param p2 点2
     * @return double 默认单位公里
     */
    public static double distance(Point p1, Point p2) {
        return distance(p1, p2, Units.KILOMETERS);
    }

    /**
     * 计算两点之间的距离。该方法使用哈弗赛因公式来考虑全球曲率。
     *
     * @param p1    点1
     * @param p2    点2
     * @param units 距离单位
     * @return double
     */
    public static double distance(Point p1, Point p2, Units units) {
        double dLat = JTurfHelper.degreesToRadians(p2.latitude() - p1.latitude());
        double dLon = JTurfHelper.degreesToRadians(p2.longitude() - p1.longitude());
        double lat1 = JTurfHelper.degreesToRadians(p1.latitude());
        double lat2 = JTurfHelper.degreesToRadians(p2.latitude());

        double value = Math.pow(Math.sin(dLat / 2), 2)
                + Math.pow(Math.sin(dLon / 2), 2)
                * Math.cos(lat1)
                * Math.cos(lat2);

        return JTurfHelper.radiansToLength(2 * Math.atan2(Math.sqrt(value), Math.sqrt(1 - value)), units);
    }

    /**
     * 计算两个点之间的大圆距离，单位公里。
     *
     * @param from  起始点
     * @param to    目标点
     * @return 返回两点之间的距离（公里）
     */
    public static double rhumbDistance(Point from, Point to) {
        return rhumbDistance(from, to, Units.KILOMETERS);
    }

    /**
     * 计算两个点之间的大圆距离(以度、弧度、英里或公里为单位)。
     *
     * @param from  起始点
     * @param to    目标点
     * @param units 距离单位，支持 DEGREES、RADIANS、MILES、KILOMETERS
     * @return 返回两点之间的距离
     */
    public static double rhumbDistance(Point from, Point to, Units units) {
        // compensate the crossing of the 180th meridian (https://macwright.org/2016/09/26/the-180th-meridian.html)
        // solution from https://github.com/mapbox/mapbox-gl-js/issues/3250#issuecomment-294887678
        double destX = to.getX() + (to.getX() - from.getX() > 180 ? -360 : from.getX() - to.getX() > 180 ? 360 : 0);
        double destY = to.getY();

        double distanceInMeters = calculateRhumbDistance(from, Point.fromLngLat(destX, destY), null);

        return JTurfUnitConversion.convertLength(distanceInMeters, Units.METERS, units);
    }

    /**
     * 返回沿恒向线从起始点点到目标点的距离。<br>
     * Adapted from Geodesy: https://github.com/chrisveness/geodesy/blob/master/latlon-spherical.js
     *
     * @param origin      起始点
     * @param destination 目标点
     * @param radius      地球半径（默认为以米为单位的半径）。
     * @return 返回两点之间的距离，单位为米
     */
    private static double calculateRhumbDistance(Point origin, Point destination, Double radius) {
        // φ => phi
        // λ => lambda
        // ψ => psi
        // Δ => Delta
        // δ => delta
        // θ => theta
        double R = radius == null ? JTurfHelper.EARTH_RADIUS : radius;
        double phi1 = (origin.getY() * Math.PI) / 180;
        double phi2 = (destination.getY() * Math.PI) / 180;
        double DeltaPhi = phi2 - phi1;
        double DeltaLambda = (Math.abs(destination.getX() - origin.getX()) * Math.PI) / 180;
        // if dLon over 180° take shorter rhumb line across the anti-meridian:
        if (DeltaLambda > Math.PI) {
            DeltaLambda -= 2 * Math.PI;
        }

        // on Mercator projection, longitude distances shrink by latitude; q is the 'stretch factor'
        // q becomes ill-conditioned along E-W line (0/0); use empirical tolerance to avoid it
        double DeltaPsi = Math.log(Math.tan(phi2 / 2 + Math.PI / 4) / Math.tan(phi1 / 2 + Math.PI / 4));
        double q = Math.abs(DeltaPsi) > 10e-12 ? DeltaPhi / DeltaPsi : Math.cos(phi1);

        // distance is pythagoras on 'stretched' Mercator projection
        double delta = Math.sqrt(DeltaPhi * DeltaPhi + q * q * DeltaLambda * DeltaLambda); // angular distance in radians

        return delta * R;
    }

    /**
     * 计算面积
     *
     * @param geometry 支持POLYGON、MULTI_POLYGON
     * @param units    支持公里、英里、米
     * @return 面积（指定单位）
     */
    public static double area(Geometry geometry, Units units) {
        double area = AreaHelper.area(geometry);
        if (units == null) {
            return area;
        }

        switch (units) {
            case KILOMETERS:
            case KILOMETRES:
                return area / 1000000;
            case MILES:
                return area / 2589988.110336D;
            default:
                return area;
        }
    }

}
