package com.cgzz.mapbox.jturf.util.coordinatemutation;

import com.cgzz.mapbox.jturf.JTurfBooleans;
import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.JTurfTransformation;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.FeatureCollection;
import com.cgzz.mapbox.jturf.shape.impl.Point;

import java.util.Collections;
import java.util.List;

public final class RewindHelper {

    private RewindHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * Rewind Line、MultiLine、Polygon、MultiPolygon、GeometryCollection true顺时针和false逆时针
     *
     * @param geometry 图形组件
     * @param reverse  true顺时针和false逆时针
     * @param mutate   是否影响原图形坐标
     * @return 返回处理后的图形组件
     */
    @SuppressWarnings("unchecked")
    public static <T extends Geometry> T rewind(T geometry, boolean reverse, boolean mutate) {
        T newGeometry;

        // 确保我们不会直接修改传入的geometry对象。
        if (!mutate) {
            newGeometry = JTurfTransformation.clone(geometry);
        } else {
            newGeometry = geometry;
        }

        switch (newGeometry.geometryType()) {
            case GEOMETRY_COLLECTION: {
                JTurfMeta.geomEach(newGeometry, (geo, featureIndex, featureProperties, featureId) -> {
                    rewindGeo(geo, reverse);
                    return true;
                });

                return newGeometry;
            }
            case FEATURE_COLLECTION: {
                JTurfMeta.featureEach(FeatureCollection.featureCollection(newGeometry), (feature, featureIndex) -> {
                    rewindGeo(feature, reverse);
                    return true;
                });

                return newGeometry;
            }
            default:
                return (T)rewindGeo(newGeometry, reverse);
        }
    }

    /**
     * Rewind Line、MultiLine、Polygon、MultiPolygon、GeometryCollection true顺时针和false逆时针
     *
     * @param geom     图形组件
     * @param reverse  true顺时针和false逆时针
     * @return 返回处理后的图形组件
     */
    private static <T extends Geometry> Geometry rewindGeo(T geom, boolean reverse) {
        Geometry geometry = JTurfMeta.getGeom(geom);

        GeometryType type = geometry.geometryType();

        // Support all GeoJSON Geometry Objects
        switch (type) {
            case GEOMETRY_COLLECTION:
                JTurfMeta.geomEach(geometry, (geo, featureIndex, featureProperties, featureId) -> {
                    rewindGeo(geo, reverse);
                    return true;
                });
                return geometry;
            case LINE_STRING:
            case POLYGON:
            case MULTI_LINE_STRING:
            case MULTI_POLYGON:
                JTurfMeta.coordsEach(geometry, (geo, pointList, multiIndex, geomIndex) -> {
                    rewindCoords(pointList, reverse);
                    return true;
                });
                return geometry;
            case POINT:
            case MULTI_POINT:
                return geometry;
        }
        return null;
    }

    /**
     * Rewind - true顺时针和false逆时针
     *
     * @param coords  点集合
     * @param reverse true顺时针和false逆时针
     */
    private static void rewindCoords(List<Point> coords, boolean reverse) {
        if (JTurfBooleans.booleanClockwise(coords) != reverse) {
            Collections.reverse(coords);
        }
    }

}
