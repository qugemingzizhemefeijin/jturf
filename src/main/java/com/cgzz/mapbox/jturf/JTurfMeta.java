package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.callback.*;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.*;
import com.cgzz.mapbox.jturf.util.ObjectHolder;
import org.omg.CORBA.IntHolder;

import java.util.ArrayList;
import java.util.List;

public final class JTurfMeta {

    private JTurfMeta() {
        throw new AssertionError("No Instances.");
    }

    public static List<Point> coordAll(Line line) {
        return coordAll(new ArrayList<>(), line);
    }

    private static List<Point> coordAll(List<Point> coords, Line line) {
        coords.addAll(line.coordinates());
        return coords;
    }

    public static List<Point> coordAll(Polygon polygon, boolean excludeWrapCoord) {
        return coordAll(new ArrayList<>(), polygon, excludeWrapCoord);
    }

    private static List<Point> coordAll(List<Point> coords,
                                        Polygon polygon,
                                        boolean excludeWrapCoord) {
        int wrapShrink = excludeWrapCoord ? 1 : 0;
        List<Point> coordinates = polygon.coordinates();

        for (int i = 0, size = coordinates.size(); i < size - wrapShrink; i++) {
            coords.add(coordinates.get(i));
        }
        return coords;
    }

    public static List<Point> coordAll(MultiPolygon multiPolygon,
                                       boolean excludeWrapCoord) {
        return coordAll(new ArrayList<>(), multiPolygon, excludeWrapCoord);
    }

    private static List<Point> coordAll(List<Point> coords,
                                        MultiPolygon multiPolygon,
                                        boolean excludeWrapCoord) {
        int wrapShrink = excludeWrapCoord ? 1 : 0;

        for (List<Point> coordinate : multiPolygon.coordinates()) {
            for (int i = 0, size = coordinate.size(); i < size - wrapShrink; i++) {
                coords.add(coordinate.get(i));
            }
        }
        return coords;
    }

    public static List<Point> coordAll(MultiLine multiLine) {
        return coordAll(new ArrayList<>(), multiLine);
    }

    private static List<Point> coordAll(List<Point> coords, MultiLine multiLine) {
        for (List<Point> coordinate : multiLine.coordinates()) {
            coords.addAll(coordinate);
        }
        return coords;
    }

    public static List<Point> coordAll(MultiPoint multiPoint) {
        return coordAll(new ArrayList<>(), multiPoint);
    }

    private static List<Point> coordAll(List<Point> coords, MultiPoint multiPoint) {
        coords.addAll(multiPoint.coordinates());
        return coords;
    }

    public static List<Point> coordAll(GeometryCollection geometryCollection, boolean excludeWrapCoord) {
        return coordAllFromGeometry(new ArrayList<>(), geometryCollection, excludeWrapCoord);
    }

    private static List<Point> coordAllFromGeometry(List<Point> pointList,
                                                    Geometry geometry,
                                                    boolean excludeWrapCoord) {
        switch (geometry.type()) {
            case POINT:
                pointList.add((Point) geometry);
                break;
            case LINE:
                pointList.addAll(((Line) geometry).coordinates());
                break;
            case POLYGON:
                coordAll(pointList, (Polygon) geometry, excludeWrapCoord);
                break;
            case MULTI_LINE:
                coordAll(pointList, (MultiLine) geometry);
                break;
            case MULTI_POLYGON:
                coordAll(pointList, (MultiPolygon) geometry, excludeWrapCoord);
                break;
            case MULTI_POINT:
                coordAll(pointList, (MultiPoint) geometry);
                break;
            case GEOMETRY_COLLECTION:
                for (Geometry singleGeometry : ((GeometryCollection) geometry).geometries()) {
                    coordAllFromGeometry(pointList, singleGeometry, excludeWrapCoord);
                }
                break;
        }
        return pointList;
    }

    /**
     * 循环处理组件点信息
     *
     * @param geometry 图形组件
     * @param callback 处理函数
     * @return 是否所有的点均处理成功
     */
    public static <T extends Geometry> boolean coordEach(T geometry, CoordEachCallback callback) {
        return coordEach(geometry, callback, false);
    }

    /**
     * 循环处理组件点信息
     *
     * @param geometry         图形组件
     * @param callback         处理函数
     * @param excludeWrapCoord 如果是 POLYGON || MULTI_POLYGON 是否处理最后一个闭合点
     * @return 是否所有的点均处理成功
     */
    @SuppressWarnings({"all"})
    public static <T extends Geometry> boolean coordEach(T geometry, CoordEachCallback callback, boolean excludeWrapCoord) {
        if (geometry == null) {
            return false;
        }

        GeometryType geometryType = geometry.type();
        GeometryCollection geometryCollection = geometryType == GeometryType.GEOMETRY_COLLECTION ? ((GeometryCollection) geometry) : null;
        int stop = geometryCollection != null ? geometryCollection.geometries().size() : 1;

        for (int geomIndex = 0; geomIndex < stop; geomIndex++) {
            int multiIndex = 0;
            Geometry g = geometryCollection != null ? geometryCollection.geometries().get(geomIndex) : geometry;
            GeometryType gType = g.type();
            int wrapShrink = excludeWrapCoord && (gType == GeometryType.POLYGON || gType == GeometryType.MULTI_POLYGON) ? 1 : 0;

            if (gType == GeometryType.POINT) {
                if (!callback.accept(g, (Point) geometry, 0, 0, geomIndex)) {
                    return false;
                }
            } else if (gType == GeometryType.LINE || gType == GeometryType.POLYGON || gType == GeometryType.MULTI_POINT) {
                List<Point> coordinate = ((CoordinateContainer<List<Point>, ?>) geometry).coordinates();
                for (int i = 0, size = coordinate.size() - wrapShrink; i < size; i++) {
                    if (!callback.accept(g, coordinate.get(i), i, 0, geomIndex)) {
                        return false;
                    }
                }
            } else if (gType == GeometryType.MULTI_LINE || gType == GeometryType.MULTI_POLYGON) {
                List<List<Point>> coordinates = ((CoordinateContainer<List<List<Point>>, ?>) geometry).coordinates();
                for (int i = 0, isize = coordinates.size(); i < isize; i++) {
                    List<Point> coordinate = coordinates.get(i);
                    for (int j = 0, jsize = coordinate.size() - wrapShrink; j < jsize; j++) {
                        if (!callback.accept(g, coordinate.get(j), j, i, geomIndex)) {
                            return false;
                        }
                    }
                }
            } else if (gType == GeometryType.GEOMETRY_COLLECTION) {
                GeometryCollection newGeometryCollection = (GeometryCollection) g;
                for (Geometry newGeometry : newGeometryCollection.geometries()) {
                    if (!coordEach(newGeometry, callback, excludeWrapCoord)) {
                        return false;
                    }
                }
            } else {
                throw new IllegalArgumentException("Not Support Geometry Type");
            }
        }

        return true;
    }

    /**
     * 循环处理组件点集合信息（注意：此函数不能处理Point类型）
     *
     * @param geometry 图形组件
     * @param callback 处理函数
     * @return 是否所有的点均处理成功
     */
    @SuppressWarnings({"all"})
    public static <T extends Geometry> boolean coordsEach(T geometry, CoordsEachCallback callback) {
        if (geometry == null) {
            return false;
        }

        GeometryType geometryType = geometry.type();
        GeometryCollection geometryCollection = geometryType == GeometryType.GEOMETRY_COLLECTION ? ((GeometryCollection) geometry) : null;
        int stop = geometryCollection != null ? geometryCollection.geometries().size() : 1;

        for (int geomIndex = 0; geomIndex < stop; geomIndex++) {
            int multiIndex = 0;
            Geometry g = geometryCollection != null ? geometryCollection.geometries().get(geomIndex) : geometry;
            GeometryType gType = g.type();

            if (gType == GeometryType.LINE || gType == GeometryType.POLYGON || gType == GeometryType.MULTI_POINT) {
                List<Point> coordinate = ((CoordinateContainer<List<Point>, ?>) geometry).coordinates();
                return callback.accept(g, coordinate, 0, geomIndex);
            } else if (gType == GeometryType.MULTI_LINE || gType == GeometryType.MULTI_POLYGON) {
                List<List<Point>> coordinates = ((CoordinateContainer<List<List<Point>>, ?>) geometry).coordinates();
                for (int i = 0, isize = coordinates.size(); i < isize; i++) {
                    List<Point> coordinate = coordinates.get(i);
                    if (!callback.accept(g, coordinate, i, geomIndex)) {
                        return false;
                    }
                }
            } else if (gType == GeometryType.GEOMETRY_COLLECTION) {
                GeometryCollection newGeometryCollection = (GeometryCollection) g;
                for (Geometry newGeometry : newGeometryCollection.geometries()) {
                    if (!coordsEach(newGeometry, callback)) {
                        return false;
                    }
                }
            } else {
                throw new IllegalArgumentException("Not Support Geometry Type");
            }
        }

        return true;
    }

    /**
     * 从单个坐标展开坐标并返回数组
     *
     * @param point 点
     * @return double数组，0=经度，1=纬度
     */
    public static double[] unwrapCoord(Point point) {
        return new double[]{point.getLongitude(), point.getLatitude()};
    }

    /**
     * 从线中展开坐标并返回数组
     *
     * @param line 线
     * @return 坐标点展开数组
     */
    public static double[][] unwrapCoords(Line line) {
        return unwrapCoordinates(line.coordinates());
    }

    /**
     * 从点集合中展开坐标并返回数组
     *
     * @param multiPoint 点集合
     * @return 坐标点展开数组
     */
    public static double[][] unwrapCoords(MultiPoint multiPoint) {
        return unwrapCoordinates(multiPoint.coordinates());
    }

    /**
     * 从面中展开坐标并返回数组
     *
     * @param polygon 面
     * @return 坐标点展开数组
     */
    public static double[][] unwrapCoords(Polygon polygon) {
        return unwrapCoordinates(polygon.coordinates());
    }

    /**
     * 从线组合中展开坐标并返回数组
     *
     * @param multiLine 线组合
     * @return 坐标点展开数组
     */
    public static double[][][] unwrapCoords(MultiLine multiLine) {
        return unwrapHighCoordinates(multiLine.coordinates());
    }

    /**
     * 从面组合中展开坐标并返回数组
     *
     * @param multiPolygon 面组合
     * @return 坐标点展开数组
     */
    public static double[][][] unwrapCoords(MultiPolygon multiPolygon) {
        return unwrapHighCoordinates(multiPolygon.coordinates());
    }

    /**
     * 展开坐标信息
     *
     * @param pointList 坐标点集合
     * @return 坐标点展开数组，二维
     */
    private static double[][] unwrapCoordinates(List<Point> pointList) {
        int size = pointList.size();
        double[][] coords = new double[size][2];
        for (int i = 0; i < size; i++) {
            Point p = pointList.get(i);
            coords[i] = new double[]{p.getLongitude(), p.getLatitude()};
        }

        return coords;
    }

    /**
     * 展开坐标信息
     *
     * @param pointList 坐标点集合
     * @return 坐标点展开数组，三维
     */
    public static double[][][] unwrapHighCoordinates(List<List<Point>> pointList) {
        int isize = pointList.size();
        double[][][] highCoords = new double[isize][][];

        for (int i = 0; i < isize; i++) {
            List<Point> plist = pointList.get(i);
            int jsize = plist.size();
            double[][] coords = new double[jsize][2];
            for (int j = 0; j < jsize; j++) {
                Point p = plist.get(j);
                coords[j] = new double[]{p.getLongitude(), p.getLatitude()};
            }

            highCoords[i] = coords;
        }

        return highCoords;
    }

    /**
     * 循环处理Geometry对象
     *
     * @param geometry 图形组件
     * @param callback 处理函数
     * @return 是否所有的对象均处理成功
     */
    @SuppressWarnings({"all"})
    public static <T extends Geometry> boolean geomEach(T geometry, GeometryEachCallback callback) {
        if (geometry == null) {
            return false;
        }

        GeometryType geometryType = geometry.type();
        GeometryCollection geometryCollection = geometryType == GeometryType.GEOMETRY_COLLECTION ? ((GeometryCollection) geometry) : null;
        int stop = geometryCollection != null ? geometryCollection.geometries().size() : 1;

        for (int geomIndex = 0; geomIndex < stop; geomIndex++) {
            int multiIndex = 0;
            Geometry g = geometryCollection != null ? geometryCollection.geometries().get(geomIndex) : geometry;
            GeometryType gType = g.type();

            // Handle null Geometry
            if (g == null) {
                if (!callback.accept(null, geometryCollection != null ? geometryCollection : null, geomIndex)) {
                    return false;
                }
                continue;
            }

            switch (gType) {
                case POINT:
                case LINE:
                case MULTI_POINT:
                case POLYGON:
                case MULTI_LINE:
                case MULTI_POLYGON:
                    if (!callback.accept(g, geometryCollection != null ? geometryCollection : null, geomIndex)) {
                        return false;
                    }
                    break;
                case GEOMETRY_COLLECTION:
                    GeometryCollection newGeometryCollection = (GeometryCollection) g;
                    for (Geometry newGeometry : newGeometryCollection.geometries()) {
                        if (!geomEach(newGeometry, callback)) {
                            return false;
                        }
                    }
                    break;
                default:
                    throw new JTurfException("Unknown Geometry Type");
            }
        }

        return true;
    }

    /**
     * 迭代任何对象中的扁平特性，类似于Array.forEach，会将Multi(Point|Line|Polygon)拆分成单个图形
     *
     * @param geometry 要迭代的元素
     * @param callback 回调处理函数
     */
    public static void flattenEach(Geometry geometry, FlattenEachCallback callback) {
        geomEach(geometry, (g, parent, geomIndex) -> {
            // Callback for single geometry
            if (g == null) {
                return callback.accept(null, 0);
            }

            GeometryType type = g.type();
            switch (type) {
                case POINT:
                case LINE:
                case POLYGON:
                    return callback.accept(g, 0);
            }

            switch (type) {
                case MULTI_POINT:
                    MultiPoint multiPoint = (MultiPoint) g;
                    for (int i = 0, size = multiPoint.coordinates().size(); i < size; i++) {
                        if (!callback.accept(multiPoint.coordinates().get(i), i)) {
                            return false;
                        }
                    }
                    break;
                case MULTI_LINE:
                    MultiLine multiLine = (MultiLine) g;
                    for (int i = 0, size = multiLine.coordinates().size(); i < size; i++) {
                        if (!callback.accept(Line.fromLngLats(multiLine.coordinates().get(i)), i)) {
                            return false;
                        }
                    }
                    break;
                case MULTI_POLYGON:
                    MultiPolygon multiPolygon = (MultiPolygon) g;
                    for (int i = 0, size = multiPolygon.coordinates().size(); i < size; i++) {
                        if (!callback.accept(Polygon.fromLngLats(multiPolygon.coordinates().get(i)), i)) {
                            return false;
                        }
                    }
                    break;
            }

            return true;
        });
    }

    /**
     * 迭代对象的线段
     *
     * @param geometry 要迭代的图形
     * @param callback 回调函数
     */
    public static void segmentEach(Geometry geometry, SegmentEachCallback callback) {
        flattenEach(geometry, (g1, multiIndex) -> {
            GeometryType type = g1.type();
            // （多）点几何不包含线段，因此在此操作期间将忽略它们
            if (type == GeometryType.POINT || type == GeometryType.MULTI_POINT) {
                return true;
            }

            IntHolder segmentIndex = new IntHolder();
            ObjectHolder<Point> previousCoords = new ObjectHolder<>();
            IntHolder prevGeomIndex = new IntHolder();

            return coordEach(g1, (g2, currentCoord, index, m, geomIndex) -> {
                if (previousCoords.value == null || geomIndex > prevGeomIndex.value) {
                    previousCoords.value = currentCoord;
                    prevGeomIndex.value = geomIndex;
                    segmentIndex.value = 0;

                    return true;
                }

                Line currentSegment = Line.fromLngLats(previousCoords.value, currentCoord);
                if (!callback.accept(currentSegment, geomIndex, m, segmentIndex.value)) {
                    return false;
                }

                segmentIndex.value++;
                previousCoords.value = currentCoord;

                return true;
            });
        });
    }

}
