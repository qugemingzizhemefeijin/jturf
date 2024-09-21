package com.cgzz.mapbox.jturf.util.meta;

import com.cgzz.mapbox.jturf.util.meta.func.CoordListEachFunc;
import com.cgzz.mapbox.jturf.shape.CollectionContainer;
import com.cgzz.mapbox.jturf.shape.CoordinateContainer;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.Point;

import java.util.List;

public final class CoordListEachHelper {

    private CoordListEachHelper() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 循环处理组件点集合信息（注意：此函数不能处理Point类型）
     *
     * @param geometry 图形组件
     * @param func     处理函数
     * @return 是否所有的点均处理成功
     */
    @SuppressWarnings("unchecked")
    public static <T extends Geometry> boolean coordListEach(T geometry, CoordListEachFunc func) {
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
                    return func.accept(g, ((CoordinateContainer<List<Point>>) g).coordinates(), 0, geomIndex);
                case MULTI_LINE_STRING:
                case POLYGON:
                    boolean multiIdx = gType == GeometryType.MULTI_LINE_STRING;
                    List<List<Point>> coordinates = ((CoordinateContainer<List<List<Point>>>) g).coordinates();
                    for (int i = 0, isize = coordinates.size(); i < isize; i++) {
                        List<Point> coordinate = coordinates.get(i);
                        if (!func.accept(g, coordinate, multiIdx ? i : 0, geomIndex)) {
                            return false;
                        }
                    }
                    break;
                case MULTI_POLYGON:
                    List<List<List<Point>>> coordinatesList = ((CoordinateContainer<List<List<List<Point>>>>) g).coordinates();
                    for (int i = 0, isize = coordinatesList.size(); i < isize; i++) {
                        for (List<Point> coordinate : coordinatesList.get(i)) {
                            if (!func.accept(g, coordinate, i, geomIndex)) {
                                return false;
                            }
                        }
                    }
                    break;
                case GEOMETRY_COLLECTION:
                case FEATURE_COLLECTION:
                    CollectionContainer<Geometry> newGeometryCollection = (CollectionContainer<Geometry>) g;
                    for (Geometry newGeometry : newGeometryCollection.geometries()) {
                        if (!coordListEach(newGeometry, func)) {
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
