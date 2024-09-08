package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.enums.Units;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.BoundingBox;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import com.cgzz.mapbox.jturf.shape.impl.Polygon;
import com.cgzz.mapbox.jturf.util.AreaHelper;
import com.cgzz.mapbox.jturf.util.LengthHelper;
import org.omg.CORBA.DoubleHolder;
import org.omg.CORBA.IntHolder;

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
     * @param from 起始点
     * @param to   目标点
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

    /**
     * 获取两个点，找出它们之间的地理方位，即从正北算起的角度(0度)
     *
     * @param p1 点1
     * @param p2 点1
     * @return 介于 -180 和 180 度之间（顺时针正方向）
     */
    public static double bearing(Point p1, Point p2) {
        return bearing(p1, p2, false);
    }

    /**
     * 获取两个点，找出它们之间的地理方位，即从正北算起的角度(0度)
     *
     * @param p1           点1
     * @param p2           点1
     * @param finalBearing 计算最终方位角（如果为真）
     * @return 介于 -180 和 180 度之间（顺时针正方向）
     */
    public static double bearing(Point p1, Point p2, boolean finalBearing) {
        if (finalBearing) {
            return calculateFinalBearing(p1, p2);
        }

        double lon1 = JTurfHelper.degreesToRadians(p1.longitude());
        double lon2 = JTurfHelper.degreesToRadians(p2.longitude());
        double lat1 = JTurfHelper.degreesToRadians(p1.latitude());
        double lat2 = JTurfHelper.degreesToRadians(p2.latitude());
        double value1 = Math.sin(lon2 - lon1) * Math.cos(lat2);
        double value2 = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(lon2 - lon1);

        return JTurfHelper.radiansToDegrees(Math.atan2(value1, value2));
    }

    /**
     * 计算最终方位角
     *
     * @param p1 点1
     * @param p2 点2
     * @return 介于 -180 和 180 度之间（顺时针正方向）
     */
    private static double calculateFinalBearing(Point p1, Point p2) {
        // Swap start & end
        double bear = bearing(p2, p1, false);
        bear = (bear + 180) % 360;
        return bear;
    }

    /**
     * 获取一个点并计算给定距离(以度、弧度、英里或公里为单位)的目标点的位置。
     *
     * @param p        开始点
     * @param distance 与开始点的相距公里数
     * @param bearing  角度，范围从 -180 到 180
     * @return Point
     */
    public static Point destination(Point p, double distance, double bearing) {
        return destination(p, distance, bearing, null);
    }

    /**
     * 获取一个点并计算给定距离(以度、弧度、英里或公里为单位)的目标点的位置。
     *
     * @param p        开始点
     * @param distance 与开始点的距离
     * @param bearing  角度，范围从 -180 到 180
     * @param units    距离单位
     * @return Point
     */
    public static Point destination(Point p, double distance, double bearing, Units units) {
        double longitude1 = JTurfHelper.degreesToRadians(p.longitude());
        double latitude1 = JTurfHelper.degreesToRadians(p.latitude());
        double bearingRad = JTurfHelper.degreesToRadians(bearing);
        double radians = JTurfHelper.lengthToRadians(distance, units);

        // Main
        double latitude2 = Math.asin(Math.sin(latitude1)
                * Math.cos(radians)
                + Math.cos(latitude1)
                * Math.sin(radians)
                * Math.cos(bearingRad));

        double longitude2 = longitude1 + Math.atan2(
                Math.sin(bearingRad) * Math.sin(radians) * Math.cos(latitude1),
                Math.cos(radians) - Math.sin(latitude1) * Math.sin(latitude2));

        double lng = JTurfHelper.radiansToDegrees(longitude2);
        double lat = JTurfHelper.radiansToDegrees(latitude2);

        return Point.fromLngLat(lng, lat);
    }

    /**
     * 获取两个点并返回中间的一个点。中点是测地线计算的，这意味着地球的曲率也被考虑在内
     *
     * @param p1 点1
     * @param p2 点2
     * @return 两点的中间点
     */
    public static Point midpoint(Point p1, Point p2) {
        double dist = distance(p1, p2);
        double heading = bearing(p1, p2);

        return destination(p1, dist / 2, heading, null);
    }

    /**
     * 两个点，找出它们之间沿恒向线的夹角，即从北线开始(0度)测量的角度。默认情况不计算最终方位角
     *
     * @param start 开始点
     * @param end   目标点
     * @return 从北线计算的方位，介于 -180 和 180 度之间（顺时针正方向）
     */
    public static double rhumbBearing(Point start, Point end) {
        return rhumbBearing(start, end, false);
    }

    /**
     * 两个点，找出它们之间沿恒向线的夹角，即从北线开始(0度)测量的角度。
     *
     * @param start        开始点
     * @param end          目标点
     * @param finalBearing 是否计算最终方位角
     * @return 从北线计算的方位，介于 -180 和 180 度之间（顺时针正方向）
     */
    public static double rhumbBearing(Point start, Point end, boolean finalBearing) {
        double bear360;
        if (finalBearing) {
            bear360 = calculateRhumbBearing(end, start);
        } else {
            bear360 = calculateRhumbBearing(start, end);
        }

        return bear360 > 180 ? -(360 - bear360) : bear360;
    }

    /**
     * 沿恒向线从开始点返回到目标点的方位角。
     *
     * @param start 开始点
     * @param end   目标点
     * @return 返回从北开始的度
     */
    private static double calculateRhumbBearing(Point start, Point end) {
        double phi1 = JTurfHelper.degreesToRadians(start.latitude());
        double phi2 = JTurfHelper.degreesToRadians(end.latitude());
        double deltaLambda = JTurfHelper.degreesToRadians(end.longitude() - start.longitude());
        // if deltaLambdaon over 180° take shorter rhumb line across the anti-meridian:
        if (deltaLambda > Math.PI) {
            deltaLambda -= 2 * Math.PI;
        }
        if (deltaLambda < -Math.PI) {
            deltaLambda += 2 * Math.PI;
        }

        double deltaPsi = Math.log(Math.tan(phi2 / 2 + Math.PI / 4) / Math.tan(phi1 / 2 + Math.PI / 4));
        double theta = Math.atan2(deltaLambda, deltaPsi);

        return (JTurfHelper.radiansToDegrees(theta) + 360) % 360;
    }

    /**
     * 获取多边形并以公里单位测量其周长
     *
     * @param geometry 支持 LINE、POLYGON、MULTI_LINE、MULTI_POLYGON
     * @return 以公里表示的输入面的总周长
     */
    public static double length(Geometry geometry) {
        return LengthHelper.length(geometry, Units.KILOMETERS);
    }

    /**
     * 获取多边形并以指定单位测量其周长。
     *
     * @param geometry 支持 LINE、POLYGON、MULTI_LINE、MULTI_POLYGON
     * @param units    距离单位
     * @return 以指定单位表示的输入面的总周长
     */
    public static double length(Geometry geometry, Units units) {
        return LengthHelper.length(geometry, units);
    }

    /**
     * 获取一个图形计算并返回一个边界框(西南东北)
     *
     * @param geometry 支持Line、MultiLine、Polygon、MultiPolygon
     * @return minX, minY, maxX, maxY
     */
    public static BoundingBox bbox(Geometry geometry) {
        double[] bbox = new double[]{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY};

        JTurfMeta.coordEach(geometry, (point, coordIndex, featureIndex, multiFeatureIndex, geometryIndex) -> {
            if (bbox[0] > point.longitude()) { // minX
                bbox[0] = point.longitude();
            }
            if (bbox[1] > point.latitude()) { // minY
                bbox[1] = point.latitude();
            }
            if (bbox[2] < point.longitude()) { // maxX
                bbox[2] = point.longitude();
            }
            if (bbox[3] < point.latitude()) { // maxY
                bbox[3] = point.latitude();
            }
            return true;
        });

        return BoundingBox.fromLngLats(bbox);
    }

    /**
     * 使用多边形并计算绝对中心点
     *
     * @param geometry 多边形组件
     * @return 位于所有输入要素绝对中心点
     */
    public static Point center(Geometry geometry) {
        BoundingBox bbox = bbox(geometry);
        double x = (bbox.west() + bbox.east()) / 2;
        double y = (bbox.south() + bbox.north()) / 2;
        return Point.fromLngLat(x, y);
    }

    /**
     * 根据bbox并返回一个等价的多边形
     *
     * @param bbox 左下右上经纬度(西南，东北)
     * @return 边界框的多边形
     */
    public static Polygon bboxPolygon(BoundingBox bbox) {
        return Polygon.fromLngLats(
                bbox.southwest(), // 西南点
                bbox.southeast(), // 东南点
                bbox.northeast(), // 东北点
                bbox.northwest(), // 西北点
                bbox.southwest()); // 需要闭合
    }

    /**
     * 使用多边形所有顶点的平均值计算质心。
     *
     * @param geometry 多边形组件
     * @return 多边形质心
     */
    public static Point centroid(Geometry geometry) {
        DoubleHolder sumX = new DoubleHolder(), sumY = new DoubleHolder();
        IntHolder len = new IntHolder();

        JTurfMeta.coordEach(geometry, (point, coordIndex, featureIndex, multiFeatureIndex, geometryIndex) -> {
            sumX.value += point.longitude();
            sumY.value += point.latitude();
            len.value++;

            return true;
        }, true);

        return Point.fromLngLat(sumX.value / len.value, sumY.value / len.value);
    }

}
