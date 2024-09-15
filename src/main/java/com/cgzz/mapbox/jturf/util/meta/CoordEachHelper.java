package com.cgzz.mapbox.jturf.util.meta;

import com.cgzz.mapbox.jturf.callback.CoordEachCallback;
import com.cgzz.mapbox.jturf.shape.CoordinateContainer;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.FeatureCollection;
import com.cgzz.mapbox.jturf.shape.impl.GeometryCollection;
import com.cgzz.mapbox.jturf.shape.impl.Point;

import java.util.List;

public final class CoordEachHelper {

    private CoordEachHelper() {
        throw new AssertionError("No Instances.");
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
     * @param geojson          图形组件
     * @param callback         处理函数
     * @param excludeWrapCoord 如果是 POLYGON || MULTI_POLYGON 是否排除处理最后一个闭合点
     * @return 是否所有的点均处理成功
     */
    public static <T extends Geometry> boolean coordEach(T geojson, CoordEachCallback callback, boolean excludeWrapCoord) {
        int coordIndex = 0;

        FeatureCollection featureCollection = geojson instanceof FeatureCollection ? FeatureCollection.featureCollection(geojson) : null;
        boolean isFeature = geojson.geometryType() == GeometryType.FEATURE;
        int stop = featureCollection != null ? featureCollection.geometries().size() : 1;

        for (int featureIndex = 0; featureIndex < stop; featureIndex++) {
            Geometry geometryMaybeCollection = featureCollection != null ? featureCollection.geometries().get(featureIndex) : (isFeature ? Feature.feature(geojson).geometry() : geojson);
            GeometryCollection geometryCollection = geometryMaybeCollection.geometryType() == GeometryType.GEOMETRY_COLLECTION ? GeometryCollection.geometryCollection(geometryMaybeCollection) : null;
            int stopG = geometryCollection != null ? geometryCollection.geometries().size() : 1;

            for (int geomIndex = 0; geomIndex < stopG; geomIndex++) {
                int multiFeatureIndex = 0;
                int geometryIndex = 0;
                Geometry geometry = geometryCollection != null ? geometryCollection.geometries().get(geomIndex) : geometryMaybeCollection;

                // Handles null Geometry -- Skips this geometry
                if (geometry == null) {
                    continue;
                }

                GeometryType geomType = geometry.geometryType();
                int wrapShrink = excludeWrapCoord && (geomType == GeometryType.POLYGON || geomType == GeometryType.MULTI_POLYGON) ? 1 : 0;

                switch (geomType) {
                    case POINT: {
                        if (!callback.accept(Point.point(geometry), coordIndex, featureIndex, multiFeatureIndex, geometryIndex)) {
                            return false;
                        }
                        coordIndex++;
                        break;
                    }
                    case LINE_STRING:
                    case MULTI_POINT: {
                        List<Point> coords = ((CoordinateContainer<List<Point>>) geometry).coordinates();
                        for (Point coord : coords) {
                            if (!callback.accept(coord, coordIndex, featureIndex, multiFeatureIndex, geometryIndex)) {
                                return false;
                            }
                            coordIndex++;
                            if (geomType == GeometryType.MULTI_POINT) {
                                multiFeatureIndex++;
                            }
                        }
                        break;
                    }
                    case POLYGON:
                    case MULTI_LINE_STRING: {
                        List<List<Point>> coords = ((CoordinateContainer<List<List<Point>>>) geometry).coordinates();
                        for (List<Point> c : coords) {
                            for (int k = 0, s2 = c.size(); k < s2 - wrapShrink; k++) {
                                if (!callback.accept(c.get(k), coordIndex, featureIndex, multiFeatureIndex, geometryIndex)) {
                                    return false;
                                }
                                coordIndex++;
                            }
                            if (geomType == GeometryType.MULTI_LINE_STRING) {
                                multiFeatureIndex++;
                            }
                            if (geomType == GeometryType.POLYGON) {
                                geometryIndex++;
                            }
                        }
                        break;
                    }
                    case MULTI_POLYGON: {
                        List<List<List<Point>>> coordinateses = ((CoordinateContainer<List<List<List<Point>>>>) geometry).coordinates();
                        for (List<List<Point>> coordinatese : coordinateses) {
                            geometryIndex = 0;
                            for (List<Point> c : coordinatese) {
                                for (int l = 0, ls = c.size(); l < ls - wrapShrink; l++) {
                                    if (!callback.accept(c.get(l), coordIndex, featureIndex, multiFeatureIndex, geometryIndex)) {
                                        return false;
                                    }
                                    coordIndex++;
                                }
                                geometryIndex++;
                            }
                            multiFeatureIndex++;
                        }
                        break;
                    }
                    case GEOMETRY_COLLECTION: {
                        List<Geometry> geometries = GeometryCollection.geometryCollection(geometry).geometries();
                        for (Geometry value : geometries) {
                            if (!coordEach(value, callback, excludeWrapCoord)) {
                                return false;
                            }
                        }
                        break;
                    }
                    default:
                        throw new IllegalArgumentException("Not Support Geometry Type");
                }
            }
        }

        return true;
    }

}
