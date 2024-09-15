package com.cgzz.mapbox.jturf.util.featureconversion;

import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CombineHelper {

    private CombineHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 结合<br><br>
     * <p>
     * 将包含点、线和多边形 合并为 MultiPoint、MultiString 或 MultiPolygon。
     *
     * @param featureCollection 多边形集合
     * @return 返回只包含 MultiPoint、MultiString、MultiPolygon的集合，可能为空
     */
    public static FeatureCollection combine(FeatureCollection featureCollection) {
        if (featureCollection == null) {
            return null;
        }

        return combine(featureCollection.geometries().stream().map(Feature::geometry).collect(Collectors.toList()));
    }

    /**
     * 结合<br><br>
     * <p>
     * 将包含点、线和多边形 合并为 MultiPoint、MultiString 或 MultiPolygon。
     *
     * @param geometryList 多边形集合
     * @return 返回只包含 MultiPoint、MultiString、MultiPolygon的集合，可能为空
     */
    public static FeatureCollection combine(List<Geometry> geometryList) {
        if (geometryList == null || geometryList.isEmpty()) {
            return null;
        }

        List<Point> multiPointList = new ArrayList<>();
        List<List<Point>> linePointList = new ArrayList<>();
        List<List<List<Point>>> polygonPointList = new ArrayList<>();

        for (Geometry geometry : geometryList) {
            switch (geometry.geometryType()) {
                case POINT:
                    multiPointList.add(Point.point(geometry));
                    break;
                case MULTI_POINT:
                    multiPointList.addAll(MultiPoint.multiPoint(geometry).coordinates());
                    break;
                case LINE_STRING:
                    linePointList.add(LineString.lineString(geometry).coordinates());
                    break;
                case MULTI_LINE_STRING:
                    linePointList.addAll(MultiLineString.multiLineString(geometry).coordinates());
                    break;
                case POLYGON:
                    polygonPointList.add(Polygon.polygon(geometry).coordinates());
                    break;
                case MULTI_POLYGON:
                    polygonPointList.addAll(MultiPolygon.multiPolygon(geometry).coordinates());
                    break;
                default: // other type not support
                    break;
            }
        }

        List<Feature> combineList = new ArrayList<>(3);
        if (!multiPointList.isEmpty()) {
            combineList.add(Feature.fromGeometry(MultiPoint.fromLngLats(multiPointList)));
        }
        if (!linePointList.isEmpty()) {
            combineList.add(Feature.fromGeometry(MultiLineString.fromLngLats(linePointList)));
        }
        if (!polygonPointList.isEmpty()) {
            combineList.add(Feature.fromGeometry(MultiPolygon.fromLngLats(polygonPointList)));
        }

        return FeatureCollection.fromFeatures(combineList);
    }

}
