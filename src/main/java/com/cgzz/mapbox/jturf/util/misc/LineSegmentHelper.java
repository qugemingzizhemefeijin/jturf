package com.cgzz.mapbox.jturf.util.misc;

import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.*;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class LineSegmentHelper {

    private LineSegmentHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 多边型顶点连线，从一个(多)Line或(多)Polygon创建一个2-vertex线段的GeometryCollection
     *
     * @param geometry 支持 Line|MultiLine|MultiPolygon|Polygon
     * @return 返回的肯定是线段集合，可能会返回空
     */
    public static FeatureCollection<LineString> lineSegment(Geometry geometry) {
        if (geometry == null) {
            throw new JTurfException("geometry is required");
        }

        List<Feature<LineString>> results = new ArrayList<>();
        JTurfMeta.flattenEach(geometry, (feature, featureIndex, multiFeatureIndex) -> {
            lineSegmentFeature(feature, results);
            return true;
        });

        return results.isEmpty() ? null : FeatureCollection.fromFeatures(results);
    }

    /**
     * 从Line中创建线段
     *
     * @param feature  支持 Line|Polygon
     * @param results  待返回的线段集合
     */
    private static void lineSegmentFeature(Feature<? extends Geometry> feature, List<Feature<LineString>> results) {
        Geometry geometry = feature.geometry();
        GeometryType type = geometry.geometryType();
        List<List<Point>> coordinates = null;
        switch (type) {
            case POLYGON:
                coordinates = ((Polygon) geometry).coordinates();
                break;
            case LINE_STRING:
                coordinates = Collections.singletonList(((LineString) geometry).coordinates());
                break;
        }

        if (coordinates == null || coordinates.isEmpty()) {
            return;
        }

        for (List<Point> coords : coordinates) {
            // 组合成N组线段
            List<Feature<LineString>> segments = createSegments(coords, feature.properties(), results.size());
            if (!segments.isEmpty()) {
                results.addAll(segments);
            }
        }
    }

    /**
     * 将传入的coords中的点两两组合成一组线段
     *
     * @param coords     传入的坐标组
     * @param properties 处理组件的属性信息
     * @param startId    ID的起始值
     * @return List<Line>
     */
    private static List<Feature<LineString>> createSegments(List<Point> coords, JsonObject properties, int startId) {
        List<Feature<LineString>> segments = new ArrayList<>();

        for (int i = 1, size = coords.size(); i < size; i++) {
            Point previousCoords = coords.get(i - 1);
            Point currentCoords = coords.get(i);

            segments.add(Feature.fromGeometry(LineString.fromLngLats(previousCoords, currentCoords), properties, String.valueOf(startId)));
            startId++;
        }

        return segments;
    }

}
