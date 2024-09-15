package com.cgzz.mapbox.jturf.util.meta;

import com.cgzz.mapbox.jturf.callback.SegmentEachCallback;
import com.cgzz.mapbox.jturf.models.IntHolder;
import com.cgzz.mapbox.jturf.models.ObjectHolder;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.LineString;
import com.cgzz.mapbox.jturf.shape.impl.Point;

public final class SegmentEachHelper {

    private SegmentEachHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 迭代任何 GeoJSON 对象中的 2 顶点线段，似于Array.forEach，点几何图形不包含线段，因此在此操作期间将忽略它们。
     *
     * @param geometry 要迭代的元素
     * @param callback 回调处理函数
     */
    public static void segmentEach(Geometry geometry, SegmentEachCallback callback) {
        FlattenEachHelper.flattenEach(geometry, (feature, featureIndex, multiFeatureIndex) -> {
            // Exclude null Geometries
            if (feature.geometry() == null) {
                return true;
            }

            // (Multi)Point geometries do not contain segments therefore they are ignored during this operation.
            GeometryType type = feature.geometry().geometryType();
            if (type == GeometryType.POINT || type == GeometryType.MULTI_POINT) {
                return true;
            }

            IntHolder segmentIndex = IntHolder.newObjectHolder();

            // Generate 2-vertex line segments
            ObjectHolder<Point> previousCoords = ObjectHolder.newObjectHolder();
            IntHolder previousFeatureIndex = IntHolder.newObjectHolder();
            IntHolder previousMultiIndex = IntHolder.newObjectHolder();
            IntHolder prevGeomIndex = IntHolder.newObjectHolder();

            if (!CoordEachHelper.coordEach(feature, (currentCoord, coordIndex, featureIndexCoord, multiPartIndexCoord, geometryIndex) -> {
                // Simulating a meta.coordReduce() since `reduce` operations cannot be stopped by returning `false`
                if (previousCoords.value == null || featureIndex > previousFeatureIndex.value || multiPartIndexCoord > previousMultiIndex.value || geometryIndex > prevGeomIndex.value) {
                    previousCoords.value = currentCoord;
                    previousFeatureIndex.value = featureIndex;
                    previousMultiIndex.value = multiPartIndexCoord;
                    prevGeomIndex.value = geometryIndex;
                    segmentIndex.value = 0;
                    return true;
                }

                LineString currentSegment = LineString.fromLngLats(previousCoords.value, currentCoord);
                if (!callback.accept(currentSegment, featureIndex, multiFeatureIndex, geometryIndex, segmentIndex.value)) {
                    return false;
                }

                segmentIndex.value++;
                previousCoords.value = currentCoord;
                return true;
            })) {
                return false;
            };

            return true;
        });
    }

}
