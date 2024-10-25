package com.cgzz.mapbox.jturf.util.booleans;

import com.cgzz.mapbox.jturf.JTurfBooleans;
import com.cgzz.mapbox.jturf.JTurfHelper;
import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.*;

import java.util.List;

public final class BooleanTouchesHelper {

    private BooleanTouchesHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 判断是否接触，不支持{@link com.cgzz.mapbox.jturf.shape.GeometryType#GEOMETRY_COLLECTION}、{@link com.cgzz.mapbox.jturf.shape.GeometryType#FEATURE_COLLECTION}
     *
     * @param geometry1 图形1
     * @param geometry2 图形2
     * @return 如果两个几何图形的公共点都不与两个几何图形的内部相交，返回 true。
     */
    public static boolean booleanTouches(Geometry geometry1, Geometry geometry2) {
        geometry1 = JTurfMeta.getGeom(geometry1);
        geometry2 = JTurfMeta.getGeom(geometry2);
        GeometryType type1 = geometry1.geometryType();

        switch (type1) {
            case POINT:
                return booleanPointTouches(Point.point(geometry1), geometry2);
            case MULTI_POINT:
                return booleanMultiPointTouches(MultiPoint.multiPoint(geometry1), geometry2);
            case LINE_STRING:
                return booleanLineStringTouches(LineString.lineString(geometry1), geometry2);
            case MULTI_LINE_STRING:
                return booleanMultiLineStringTouches(MultiLineString.multiLineString(geometry1), geometry2);
            case POLYGON:
                return booleanPolygonTouches(Polygon.polygon(geometry1), geometry2);
            case MULTI_POLYGON:
                return booleanMultiPolygonTouches(MultiPolygon.multiPolygon(geometry1), geometry2);
            default:
                throw new JTurfException("geometry1 " + type1 + " geometry not supported");
        }
    }

    /**
     * 当第一个图形为点的时候，判断是否与第二个图形有接触
     *
     * @param geom1     Point对象
     * @param geometry2 图形2
     * @return 如果两个几何图形的公共点都不与两个几何图形的内部相交，返回 true。
     */
    private static boolean booleanPointTouches(Point geom1, Geometry geometry2) {
        GeometryType type2 = geometry2.geometryType();

        switch (type2) {
            case LINE_STRING:
                return isPointOnCoordinatesEnd(geom1, LineString.lineString(geometry2).coordinates());
            case MULTI_LINE_STRING: {
                MultiLineString geom2 = MultiLineString.multiLineString(geometry2);
                for (List<Point> coordinates : geom2.coordinates()) {
                    if (isPointOnCoordinatesEnd(geom1, coordinates)) {
                        return true;
                    }
                }
                return false;
            }
            case POLYGON: {
                Polygon geom2 = Polygon.polygon(geometry2);
                for (List<Point> coordinates : geom2.coordinates()) {
                    if (JTurfBooleans.booleanPointOnLine(geom1, LineString.fromLngLats(coordinates))) {
                        return true;
                    }
                }
                return false;
            }
            case MULTI_POLYGON: {
                MultiPolygon geom2 = MultiPolygon.multiPolygon(geometry2);
                for (List<List<Point>> coordinates : geom2.coordinates()) {
                    for (List<Point> coords : coordinates) {
                        if (JTurfBooleans.booleanPointOnLine(geom1, LineString.fromLngLats(coords))) {
                            return true;
                        }
                    }
                }
                return false;
            }
            default:
                throw new JTurfException("geometry2 " + type2 + " geometry not supported");
        }
    }

    /**
     * 当第一个图形为点集合的时候，判断是否与第二个图形有接触
     *
     * @param mp        MultiPoint对象
     * @param geometry2 图形2
     * @return 如果两个几何图形的公共点都不与两个几何图形的内部相交，返回 true。
     */
    private static boolean booleanMultiPointTouches(MultiPoint mp, Geometry geometry2) {
        GeometryType type2 = geometry2.geometryType();

        switch (type2) {
            case LINE_STRING: {
                LineString geom2 = LineString.lineString(geometry2);
                boolean foundTouchingPoint = false;
                for (Point geom1 : mp.coordinates()) {
                    if (!foundTouchingPoint) {
                        if (isPointOnCoordinatesEnd(geom1, geom2.coordinates())) {
                            foundTouchingPoint = true;
                        }
                    }
                    if (JTurfBooleans.booleanPointOnLine(geom1, geom2, true)) {
                        return false;
                    }
                }

                return foundTouchingPoint;
            }
            case MULTI_LINE_STRING: {
                boolean foundTouchingPoint = false;
                MultiLineString geom2 = MultiLineString.multiLineString(geometry2);

                for (Point geom1 : mp.coordinates()) {
                    for (List<Point> coords : geom2.coordinates()) {
                        if (!foundTouchingPoint) {
                            if (isPointOnCoordinatesEnd(geom1, coords)) {
                                foundTouchingPoint = true;
                            }
                        }
                        if (JTurfBooleans.booleanPointOnLine(geom1, LineString.fromLngLats(coords), true)) {
                            return false;
                        }
                    }
                }

                return foundTouchingPoint;
            }
            case POLYGON: {
                boolean foundTouchingPoint = false;
                Polygon geom2 = Polygon.polygon(geometry2);

                for (Point geom1 : mp.coordinates()) {
                    if (!foundTouchingPoint) {
                        if (isPointOnCoordinatesEnd(geom1, geom2.coordinates().get(0))) {
                            foundTouchingPoint = true;
                        }
                    }
                    if (JTurfBooleans.booleanPointInPolygon(geom1, geom2, true)) {
                        return false;
                    }
                }

                return foundTouchingPoint;
            }
            case MULTI_POLYGON: {
                boolean foundTouchingPoint = false;
                MultiPolygon geom2 = MultiPolygon.multiPolygon(geometry2);

                for (Point geom1 : mp.coordinates()) {
                    for (List<List<Point>> coordinates : geom2.coordinates()) {
                        if (!foundTouchingPoint) {
                            if (isPointOnCoordinatesEnd(geom1, coordinates.get(0))) {
                                foundTouchingPoint = true;
                            }
                        }
                        if (JTurfBooleans.booleanPointInPolygon(geom1, Polygon.fromLngLats(coordinates), true)) {
                            return false;
                        }
                    }
                }

                return foundTouchingPoint;
            }
            default:
                throw new JTurfException("geometry2 " + type2 + " geometry not supported");
        }
    }

    /**
     * 当第一个图形为线的时候，判断是否与第二个图形有接触
     *
     * @param lineString LineString对象
     * @param geometry2  图形2
     * @return 如果两个几何图形的公共点都不与两个几何图形的内部相交，返回 true。
     */
    private static boolean booleanLineStringTouches(LineString lineString, Geometry geometry2) {
        GeometryType type2 = geometry2.geometryType();

        switch (type2) {
            case POINT:
                return isPointOnCoordinatesEnd(Point.point(geometry2), lineString.coordinates());
            case MULTI_POINT: {
                boolean foundTouchingPoint = false;
                MultiPoint geom2 = MultiPoint.multiPoint(geometry2);

                for (Point p : geom2.coordinates()) {
                    if (!foundTouchingPoint) {
                        if (isPointOnCoordinatesEnd(p, lineString.coordinates())) {
                            foundTouchingPoint = true;
                        }
                    }
                    if (JTurfBooleans.booleanPointOnLine(p, lineString, true)) {
                        return false;
                    }
                }

                return foundTouchingPoint;
            }
            case LINE_STRING: {
                boolean endMatch = false;
                LineString geom2 = LineString.lineString(geometry2);

                if (isPointOnCoordinatesEnd(lineString.coordinates().get(0), geom2.coordinates())) {
                    endMatch = true;
                } else if (isPointOnCoordinatesEnd(lineString.coordinates().get(lineString.coordinates().size() - 1), geom2.coordinates())) {
                    endMatch = true;
                }

                if (!endMatch) {
                    return false;
                }

                for (Point p : lineString.coordinates()) {
                    if (JTurfBooleans.booleanPointOnLine(p, geom2, true)) {
                        return false;
                    }
                }

                return endMatch;
            }
            case MULTI_LINE_STRING: {
                boolean endMatch = false;
                MultiLineString geom2 = MultiLineString.multiLineString(geometry2);
                List<Point> geom1Coords = lineString.coordinates();

                for (List<Point> coords : geom2.coordinates()) {
                    if (isPointOnCoordinatesEnd(geom1Coords.get(0), coords)) {
                        endMatch = true;
                    } else if (isPointOnCoordinatesEnd(geom1Coords.get(geom1Coords.size() - 1), coords)) {
                        endMatch = true;
                    }

                    for (Point p : geom1Coords) {
                        if (JTurfBooleans.booleanPointOnLine(p, LineString.fromLngLats(coords), true)) {
                            return false;
                        }
                    }
                }

                return endMatch;
            }
            case POLYGON: {
                boolean foundTouchingPoint = false;
                Polygon geom2 = Polygon.polygon(geometry2);

                for (Point p : lineString.coordinates()) {
                    if (!foundTouchingPoint) {
                        if (JTurfBooleans.booleanPointOnLine(p, LineString.fromLngLats(geom2.coordinates().get(0)))) {
                            foundTouchingPoint = true;
                        }
                    }
                    if (JTurfBooleans.booleanPointInPolygon(p, geom2, true)) {
                        return false;
                    }
                }

                return foundTouchingPoint;
            }
            case MULTI_POLYGON: {
                boolean foundTouchingPoint = false;
                MultiPolygon geom2 = MultiPolygon.multiPolygon(geometry2);

                for (Point p : lineString.coordinates()) {
                    for (List<List<Point>> coords : geom2.coordinates()) {
                        if (!foundTouchingPoint) {
                            if (JTurfBooleans.booleanPointOnLine(p, LineString.fromLngLats(coords.get(0)))) {
                                foundTouchingPoint = true;
                            }
                        }
                    }

                    if (JTurfBooleans.booleanPointInPolygon(p, geom2, true)) {
                        return false;
                    }
                }

                return foundTouchingPoint;
            }
            default:
                throw new JTurfException("geometry2 " + type2 + " geometry not supported");
        }
    }

    /**
     * 当第一个图形为组合线的时候，判断是否与第二个图形有接触
     *
     * @param multiLineString MultiLineString对象
     * @param geometry2       图形2
     * @return 如果两个几何图形的公共点都不与两个几何图形的内部相交，返回 true。
     */
    private static boolean booleanMultiLineStringTouches(MultiLineString multiLineString, Geometry geometry2) {
        GeometryType type2 = geometry2.geometryType();

        switch (type2) {
            case POINT: {
                for (List<Point> pointList : multiLineString.coordinates()) {
                    if (isPointOnCoordinatesEnd(Point.point(geometry2), pointList)) {
                        return true;
                    }
                }
                return false;
            }
            case MULTI_POINT: {
                boolean foundTouchingPoint = false;
                MultiPoint geom2 = MultiPoint.multiPoint(geometry2);

                for (List<Point> pointList : multiLineString.coordinates()) {
                    for (Point p : geom2.coordinates()) {
                        if (!foundTouchingPoint) {
                            if (isPointOnCoordinatesEnd(p, pointList)) {
                                foundTouchingPoint = true;
                            }
                        }

                        if (JTurfBooleans.booleanPointOnLine(p, LineString.fromLngLats(pointList), true)) {
                            return false;
                        }
                    }
                }

                return foundTouchingPoint;
            }
            case LINE_STRING: {
                boolean endMatch = false;
                LineString geom2 = LineString.lineString(geometry2);
                List<Point> geom2Coords = geom2.coordinates();

                for (List<Point> pointList : multiLineString.coordinates()) {
                    if (isPointOnCoordinatesEnd(pointList.get(0), geom2Coords)) {
                        endMatch = true;
                    } else if (isPointOnCoordinatesEnd(pointList.get(pointList.size() - 1), geom2Coords)) {
                        endMatch = true;
                    }

                    for (Point p : geom2Coords) {
                        if (JTurfBooleans.booleanPointOnLine(p, LineString.fromLngLats(pointList), true)) {
                            return false;
                        }
                    }
                }

                return endMatch;
            }
            case MULTI_LINE_STRING: {
                boolean endMatch = false;
                MultiLineString geom2 = MultiLineString.multiLineString(geometry2);

                for (List<Point> p1 : multiLineString.coordinates()) {
                    for (List<Point> p2 : geom2.coordinates()) {
                        if (isPointOnCoordinatesEnd(p1.get(0), p2)) {
                            endMatch = true;
                        } else if (isPointOnCoordinatesEnd(p1.get(p1.size() - 1), p2)) {
                            endMatch = true;
                        }

                        for (Point p : p1) {
                            if (JTurfBooleans.booleanPointOnLine(p, LineString.fromLngLats(p2), true)) {
                                return false;
                            }
                        }
                    }
                }

                return endMatch;
            }
            case POLYGON: {
                boolean foundTouchingPoint = false;
                Polygon geom2 = Polygon.polygon(geometry2);

                for (List<Point> p1 : multiLineString.coordinates()) {
                    for (Point p2 : p1) {
                        if (!foundTouchingPoint) {
                            if (JTurfBooleans.booleanPointOnLine(p2, LineString.fromLngLats(geom2.coordinates().get(0)))) {
                                foundTouchingPoint = true;
                            }
                        }
                        if (JTurfBooleans.booleanPointInPolygon(p2, geom2, true)) {
                            return false;
                        }
                    }
                }

                return foundTouchingPoint;
            }
            case MULTI_POLYGON: {
                boolean foundTouchingPoint = false;
                MultiPolygon geom2 = MultiPolygon.multiPolygon(geometry2);

                for (List<Point> points : geom2.coordinates().get(0)) {
                    for (List<Point> p1 : multiLineString.coordinates()) {
                        for (Point p2 : p1) {
                            if (!foundTouchingPoint) {
                                if (JTurfBooleans.booleanPointOnLine(p2, LineString.fromLngLats(points))) {
                                    foundTouchingPoint = true;
                                }
                            }
                            if (JTurfBooleans.booleanPointInPolygon(p2, Polygon.fromOuterInner(points), true)) {
                                return false;
                            }
                        }
                    }
                }

                return foundTouchingPoint;
            }
            default:
                throw new JTurfException("geometry2 " + type2 + " geometry not supported");
        }
    }

    /**
     * 当第一个图形为多边形的时候，判断是否与第二个图形有接触
     *
     * @param polygon    Polygon对象
     * @param geometry2  图形2
     * @return 如果两个几何图形的公共点都不与两个几何图形的内部相交，返回 true。
     */
    private static boolean booleanPolygonTouches(Polygon polygon, Geometry geometry2) {
        GeometryType type2 = geometry2.geometryType();

        switch (type2) {
            case POINT: {
                Point geom2 = Point.point(geometry2);

                for (List<Point> pointList : polygon.coordinates()) {
                    if (JTurfBooleans.booleanPointOnLine(geom2, LineString.fromLngLats(pointList))) {
                        return true;
                    }
                }

                return false;
            }
            case MULTI_POINT: {
                boolean foundTouchingPoint = false;
                MultiPoint geom2 = MultiPoint.multiPoint(geometry2);

                for (Point p : geom2.coordinates()) {
                    if (!foundTouchingPoint) {
                        if (JTurfBooleans.booleanPointOnLine(p, LineString.fromLngLats(polygon.coordinates().get(0)))) {
                            foundTouchingPoint = true;
                        }
                    }
                    if (JTurfBooleans.booleanPointInPolygon(p, polygon, true)) {
                        return false;
                    }
                }

                return foundTouchingPoint;
            }
            case LINE_STRING: {
                boolean foundTouchingPoint = false;
                LineString geom2 = LineString.lineString(geometry2);

                for (Point p : geom2.coordinates()) {
                    if (!foundTouchingPoint) {
                        if (JTurfBooleans.booleanPointOnLine(p, LineString.fromLngLats(polygon.coordinates().get(0)))) {
                            foundTouchingPoint = true;
                        }
                    }
                    if (JTurfBooleans.booleanPointInPolygon(p, polygon, true)) {
                        return false;
                    }
                }

                return foundTouchingPoint;
            }
            case MULTI_LINE_STRING: {
                boolean foundTouchingPoint = false;
                MultiLineString geom2 = MultiLineString.multiLineString(geometry2);

                for (List<Point> p1 : geom2.coordinates()) {
                    for (Point p2 : p1) {
                        if (!foundTouchingPoint) {
                            if (JTurfBooleans.booleanPointOnLine(p2, LineString.fromLngLats(polygon.coordinates().get(0)))) {
                                foundTouchingPoint = true;
                            }
                        }
                        if (JTurfBooleans.booleanPointInPolygon(p2, polygon, true)) {
                            return false;
                        }
                    }
                }

                return foundTouchingPoint;
            }
            case POLYGON: {
                boolean foundTouchingPoint = false;
                Polygon geom2 = Polygon.polygon(geometry2);

                for (Point p : polygon.coordinates().get(0)) {
                    if (!foundTouchingPoint) {
                        if (JTurfBooleans.booleanPointOnLine(p, LineString.fromLngLats(geom2.coordinates().get(0)))) {
                            foundTouchingPoint = true;
                        }
                    }
                    if (JTurfBooleans.booleanPointInPolygon(p, geom2, true)) {
                        return false;
                    }
                }

                return foundTouchingPoint;
            }
            case MULTI_POLYGON: {
                boolean foundTouchingPoint = false;
                MultiPolygon geom2 = MultiPolygon.multiPolygon(geometry2);

                for (List<Point> pointList : geom2.coordinates().get(0)) {
                    for (Point p : polygon.coordinates().get(0)) {
                        if (!foundTouchingPoint) {
                            if (JTurfBooleans.booleanPointOnLine(p, LineString.fromLngLats(pointList))) {
                                foundTouchingPoint = true;
                            }
                        }
                        if (JTurfBooleans.booleanPointInPolygon(p, Polygon.fromOuterInner(pointList), true)) {
                            return false;
                        }
                    }
                }

                return foundTouchingPoint;
            }
            default:
                throw new JTurfException("geometry2 " + type2 + " geometry not supported");
        }
    }

    /**
     * 当第一个图形为多边形集合的时候，判断是否与第二个图形有接触
     *
     * @param multiPolygon  MultiPolygon对象
     * @param geometry2     图形2
     * @return 如果两个几何图形的公共点都不与两个几何图形的内部相交，返回 true。
     */
    private static boolean booleanMultiPolygonTouches(MultiPolygon multiPolygon, Geometry geometry2) {
        GeometryType type2 = geometry2.geometryType();

        switch (type2) {
            case POINT: {
                Point geom2 = Point.point(geometry2);

                for (List<Point> pointList : multiPolygon.coordinates().get(0)) {
                    if (JTurfBooleans.booleanPointOnLine(geom2, LineString.fromLngLats(pointList))) {
                        return true;
                    }
                }

                return false;
            }
            case MULTI_POINT: {
                boolean foundTouchingPoint = false;
                MultiPoint geom2 = MultiPoint.multiPoint(geometry2);

                for (List<Point> pointList : multiPolygon.coordinates().get(0)) {
                    for (Point p : geom2.coordinates()) {
                        if (!foundTouchingPoint) {
                            if (JTurfBooleans.booleanPointOnLine(p, LineString.fromLngLats(pointList))) {
                                foundTouchingPoint = true;
                            }
                        }
                        if (JTurfBooleans.booleanPointInPolygon(p, Polygon.fromOuterInner(pointList), true)) {
                            return false;
                        }
                    }
                }

                return foundTouchingPoint;
            }
            case LINE_STRING: {
                boolean foundTouchingPoint = false;
                LineString geom2 = LineString.lineString(geometry2);

                for (List<Point> pointList : multiPolygon.coordinates().get(0)) {
                    for (Point p : geom2.coordinates()) {
                        if (!foundTouchingPoint) {
                            if (JTurfBooleans.booleanPointOnLine(p, LineString.fromLngLats(pointList))) {
                                foundTouchingPoint = true;
                            }
                        }
                        if (JTurfBooleans.booleanPointInPolygon(p, Polygon.fromOuterInner(pointList), true)) {
                            return false;
                        }
                    }
                }

                return foundTouchingPoint;
            }
            case MULTI_LINE_STRING: {
                boolean foundTouchingPoint = false;
                MultiLineString geom2 = MultiLineString.multiLineString(geometry2);

                for (List<List<Point>> coordinates : multiPolygon.coordinates()) {
                    for (List<Point> p1 : geom2.coordinates()) {
                        for (Point p2 : p1) {
                            if (!foundTouchingPoint) {
                                if (JTurfBooleans.booleanPointOnLine(p2, LineString.fromLngLats(coordinates.get(0)))) {
                                    foundTouchingPoint = true;
                                }
                            }
                            if (JTurfBooleans.booleanPointInPolygon(p2, Polygon.fromOuterInner(coordinates.get(0)), true)) {
                                return false;
                            }
                        }
                    }
                }

                return foundTouchingPoint;
            }
            case POLYGON: {
                boolean foundTouchingPoint = false;
                Polygon geom2 = Polygon.polygon(geometry2);

                for (List<Point> pointList : multiPolygon.coordinates().get(0)) {
                    for (Point p : pointList) {
                        if (!foundTouchingPoint) {
                            if (JTurfBooleans.booleanPointOnLine(p, LineString.fromLngLats(geom2.coordinates().get(0)))) {
                                foundTouchingPoint = true;
                            }
                        }
                        if (JTurfBooleans.booleanPointInPolygon(p, geom2, true)) {
                            return false;
                        }
                    }
                }

                return foundTouchingPoint;
            }
            case MULTI_POLYGON: {
                boolean foundTouchingPoint = false;
                MultiPolygon geom2 = MultiPolygon.multiPolygon(geometry2);

                for (List<Point> pointList : multiPolygon.coordinates().get(0)) {
                    for (List<Point> p2List : geom2.coordinates().get(0)) {
                        for (Point p : pointList) {
                            if (!foundTouchingPoint) {
                                if (JTurfBooleans.booleanPointOnLine(p, LineString.fromLngLats(p2List))) {
                                    foundTouchingPoint = true;
                                }
                            }
                            if (JTurfBooleans.booleanPointInPolygon(p, Polygon.fromOuterInner(p2List), true)) {
                                return false;
                            }
                        }
                    }
                }

                return foundTouchingPoint;
            }
            default:
                throw new JTurfException("geometry2 " + type2 + " geometry not supported");
        }
    }

    /**
     * 判断传入的点是否与传入的点集合的第一个点或最后一个点一致
     *
     * @param point       Point对象
     * @param coordinates Point集合
     * @return 如果point于coordinates的收尾点任意一个一致则返回true
     */
    private static boolean isPointOnCoordinatesEnd(Point point, List<Point> coordinates) {
        if (compareCoords(coordinates.get(0), point)) {
            return true;
        }
        return compareCoords(coordinates.get(coordinates.size() - 1), point);
    }

    /**
     * 判断坐标点是否一致
     *
     * @param pair1 Point对象
     * @param pair2 Point对象
     * @return 如果坐标对匹配返回true
     */
    private static boolean compareCoords(Point pair1, Point pair2) {
        return JTurfHelper.equalsD2(pair1, pair2);
    }

}
