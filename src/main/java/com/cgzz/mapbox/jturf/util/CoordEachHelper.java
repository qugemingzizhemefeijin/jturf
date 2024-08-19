package com.cgzz.mapbox.jturf.util;

import com.cgzz.mapbox.jturf.callback.CoordEachCallback;
import com.cgzz.mapbox.jturf.shape.CollectionContainer;
import com.cgzz.mapbox.jturf.shape.CoordinateContainer;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.Point;

import java.util.List;

@SuppressWarnings("all")
public final class CoordEachHelper {

    private CoordEachHelper() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 循环处理组件点信息
     *
     * @param geometry         图形组件
     * @param callback         处理函数
     * @param excludeWrapCoord 如果是 POLYGON || MULTI_POLYGON 是否处理最后一个闭合点
     * @return 是否所有的点均处理成功
     */
    public static <T extends Geometry> boolean coordEach(T geometry, CoordEachCallback callback, boolean excludeWrapCoord) {
        CollectionContainer<Geometry> geometryCollection = geometry instanceof CollectionContainer ? ((CollectionContainer) geometry) : null;
        int stop = geometryCollection != null ? geometryCollection.geometries().size() : 1;

        for (int geomIndex = 0; geomIndex < stop; geomIndex++) {
            Geometry g = geometryCollection != null ? geometryCollection.geometries().get(geomIndex) : geometry;
            if (g instanceof Feature) {
                g = Feature.feature(g).geometry();
            }

            GeometryType gType = g.geometryType();
            int wrapShrink = excludeWrapCoord && (gType == GeometryType.POLYGON || gType == GeometryType.MULTI_POLYGON) ? 1 : 0;

            switch (gType) {
                case POINT:
                    if (!callback.accept(g, Point.point(geometry), 0, 0, geomIndex)) {
                        return false;
                    }
                    break;
                case LINE_STRING:
                case MULTI_POINT:
                    if (!coordEachCoordinates(callback, g, ((CoordinateContainer<List<Point>>) geometry).coordinates(), wrapShrink, 0, geomIndex)) {
                        return false;
                    }
                    break;
                case MULTI_LINE_STRING:
                    if (!coordEachCoordinatesList(callback, g, ((CoordinateContainer<List<List<Point>>>) geometry).coordinates(), wrapShrink, geomIndex, true)) {
                        return false;
                    }
                    break;
                case POLYGON:
                    if (!coordEachCoordinatesList(callback, g, ((CoordinateContainer<List<List<Point>>>) geometry).coordinates(), wrapShrink, geomIndex, false)) {
                        return false;
                    }
                    break;
                case MULTI_POLYGON:
                    List<List<List<Point>>> coordinateses = ((CoordinateContainer<List<List<List<Point>>>>) geometry).coordinates();
                    for (int i = 0, isize = coordinateses.size(); i < isize; i++) {
                        if (!coordEachCoordinatesList(callback, g, coordinateses.get(i), wrapShrink, geomIndex, i)) {
                            return false;
                        }
                    }
                    break;
                case GEOMETRY_COLLECTION:
                case FEATURE_COLLECTION:
                    CollectionContainer<Geometry> newGeometryCollection = (CollectionContainer<Geometry>) g;
                    for (Geometry newGeometry : newGeometryCollection.geometries()) {
                        if (!coordEach(newGeometry, callback, excludeWrapCoord)) {
                            return false;
                        }
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Not Support Geometry Type");
            }
        }

        return true;
    }

    /**
     * 循环处理组件点信息
     *
     * @param callback        回到函数
     * @param g               当前执行的组件
     * @param coordinatesList 组件的点集合
     * @param wrapShrink      最后一个点是否处理，0处理，1不处理
     * @param geomIndex       循环的组件索引值multiIndex
     * @param multiIndex      multiIndex值
     * @return 当处理成功返回true
     */
    private static boolean coordEachCoordinatesList(CoordEachCallback callback, Geometry g, List<List<Point>> coordinatesList, int wrapShrink, int geomIndex, int multiIndex) {
        for (List<Point> points : coordinatesList) {
            if (!coordEachCoordinates(callback, g, points, wrapShrink, multiIndex, geomIndex)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 循环处理组件点信息
     *
     * @param callback        回到函数
     * @param g               当前执行的组件
     * @param coordinatesList 组件的点集合
     * @param wrapShrink      最后一个点是否处理，0处理，1不处理
     * @param geomIndex       循环的组件索引值multiIndex
     * @param multiIdx        是否需要记录
     * @return 当处理成功返回true
     */
    private static boolean coordEachCoordinatesList(CoordEachCallback callback, Geometry g, List<List<Point>> coordinatesList, int wrapShrink, int geomIndex, boolean multiIdx) {
        for (int i = 0, size = coordinatesList.size(); i < size; i++) {
            if (!coordEachCoordinates(callback, g, coordinatesList.get(i), wrapShrink, multiIdx ? i : 0, geomIndex)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 循环处理组件点信息
     *
     * @param callback    回到函数
     * @param g           当前执行的组件
     * @param coordinates 组件的点集合
     * @param wrapShrink  最后一个点是否处理，0处理，1不处理
     * @param multiIndex  组合点循环位置，MULTI_LINE和MULTI_POLYGON的上层位
     * @param geomIndex   循环的组件索引值
     * @return 当处理成功返回true
     */
    private static boolean coordEachCoordinates(CoordEachCallback callback, Geometry g, List<Point> coordinates, int wrapShrink, int multiIndex, int geomIndex) {
        for (int i = 0, size = coordinates.size() - wrapShrink; i < size; i++) {
            if (!callback.accept(g, coordinates.get(i), i, multiIndex, geomIndex)) {
                return false;
            }
        }
        return true;
    }

}
