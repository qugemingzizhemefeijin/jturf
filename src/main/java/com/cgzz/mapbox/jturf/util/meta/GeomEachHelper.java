package com.cgzz.mapbox.jturf.util.meta;

import com.cgzz.mapbox.jturf.callback.GeometryEachCallback;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.FeatureCollection;
import com.cgzz.mapbox.jturf.shape.impl.GeometryCollection;
import com.google.gson.JsonObject;

import java.util.List;

public final class GeomEachHelper {

    private GeomEachHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 循环处理Geometry对象
     *
     * @param geojson  图形组件
     * @param callback 处理函数
     * @return 是否所有的对象均处理成功
     */
    public static <T extends Geometry> boolean geomEach(T geojson, GeometryEachCallback callback) {
        FeatureCollection<Geometry> featureCollection = geojson instanceof FeatureCollection ? FeatureCollection.featureCollection(geojson) : null;
        boolean isFeature = geojson.geometryType() == GeometryType.FEATURE;
        int stop = featureCollection != null ? featureCollection.geometries().size() : 1;

        int featureIndex = 0;

        for (int i = 0; i < stop; i++) {
            String featureId = null;
            JsonObject featurePropertie = null;
            Geometry geometryMaybeCollection;

            if (featureCollection != null) {
                Feature<Geometry> temp = featureCollection.geometries().get(i);

                geometryMaybeCollection = temp;
                featurePropertie = temp.properties();
                featureId = temp.id();
            } else if (isFeature) {
                Feature<Geometry> temp = Feature.feature(geojson);

                geometryMaybeCollection = temp.geometry();
                featurePropertie = temp.properties();
                featureId = temp.id();
            } else {
                geometryMaybeCollection = geojson;
            }

            GeometryCollection geometryCollection = geometryMaybeCollection.geometryType() == GeometryType.GEOMETRY_COLLECTION ? GeometryCollection.geometryCollection(geometryMaybeCollection) : null;
            int stopG = geometryCollection != null ? geometryCollection.geometries().size() : 1;

            for (int g = 0; g < stopG; g++) {
                Geometry geometry = geometryCollection != null ? geometryCollection.geometries().get(g) : geometryMaybeCollection;

                // Handle null Geometry
                if (geometry == null) {
                    if (!callback.accept(null, featureIndex, featurePropertie, featureId)) {
                        return false;
                    }
                    continue;
                }

                GeometryType type = geometry.geometryType();
                switch (type) {
                    case POINT:
                    case LINE_STRING:
                    case MULTI_POINT:
                    case POLYGON:
                    case MULTI_LINE_STRING:
                    case MULTI_POLYGON:
                        if (!callback.accept(geometry, featureIndex, featurePropertie, featureId)) {
                            return false;
                        }
                        break;
                    case GEOMETRY_COLLECTION: {
                        List<Geometry> geometries = GeometryCollection.geometryCollection(geometry).geometries();
                        for (Geometry value : geometries) {
                            if (!callback.accept(value, featureIndex, featurePropertie, featureId)) {
                                return false;
                            }
                        }
                        break;
                    }
                    default:
                        throw new JTurfException("Unknown Geometry Type");
                }
            }

            // Only increase `featureIndex` per each feature
            featureIndex++;
        }

        return true;
    }

}
