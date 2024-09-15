package com.cgzz.mapbox.jturf.util.meta;

import com.cgzz.mapbox.jturf.callback.CoordsEachCallback;
import com.cgzz.mapbox.jturf.shape.CollectionContainer;
import com.cgzz.mapbox.jturf.shape.CoordinateContainer;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.Point;

import java.util.List;

@SuppressWarnings("all")
public final class CoordsEachHelper {

    private CoordsEachHelper() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 循环处理组件点集合信息（注意：此函数不能处理Point类型）
     *
     * @param geometry 图形组件
     * @param callback 处理函数
     * @return 是否所有的点均处理成功
     */
    public static <T extends Geometry> boolean coordsEach(T geometry, CoordsEachCallback callback) {
        CollectionContainer<Geometry> geometryCollection = geometry instanceof CollectionContainer ? ((CollectionContainer) geometry) : null;
        int stop = geometryCollection != null ? geometryCollection.geometries().size() : 1;

        for (int geomIndex = 0; geomIndex < stop; geomIndex++) {
            Geometry g = geometryCollection != null ? geometryCollection.geometries().get(geomIndex) : geometry;
            if (g instanceof Feature) {
                g = Feature.feature(g).geometry();
            }
            GeometryType gType = g.geometryType();

            switch (gType) {
                case LINE_STRING:
                case MULTI_POINT:
                    return callback.accept(g, ((CoordinateContainer<List<Point>>) geometry).coordinates(), 0, geomIndex);
                case MULTI_LINE_STRING:
                case POLYGON:
                    boolean multiIdx = gType == GeometryType.MULTI_LINE_STRING;
                    List<List<Point>> coordinates = ((CoordinateContainer<List<List<Point>>>) geometry).coordinates();
                    for (int i = 0, isize = coordinates.size(); i < isize; i++) {
                        List<Point> coordinate = coordinates.get(i);
                        if (!callback.accept(g, coordinate, multiIdx ? i : 0, geomIndex)) {
                            return false;
                        }
                    }
                    break;
                case MULTI_POLYGON:
                    List<List<List<Point>>> coordinatesList = ((CoordinateContainer<List<List<List<Point>>>>) geometry).coordinates();
                    for (int i = 0, isize = coordinatesList.size(); i < isize; i++) {
                        for (List<Point> coordinate : coordinatesList.get(i)) {
                            if (!callback.accept(g, coordinate, i, geomIndex)) {
                                return false;
                            }
                        }
                    }
                    break;
                case GEOMETRY_COLLECTION:
                case FEATURE_COLLECTION:
                    CollectionContainer<Geometry> newGeometryCollection = (CollectionContainer<Geometry>) g;
                    for (Geometry newGeometry : newGeometryCollection.geometries()) {
                        if (!coordsEach(newGeometry, callback)) {
                            return false;
                        }
                    }
                default:
                    throw new IllegalArgumentException("Not Support Geometry Type");
            }
        }

        return true;
    }

}
