package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.*;
import com.cgzz.mapbox.jturf.util.featureconversion.CombineHelper;
import com.cgzz.mapbox.jturf.util.featureconversion.PolygonToLineHelper;
import com.cgzz.mapbox.jturf.util.meta.FlattenEachHelper;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class JTurfFeatureConversion {

    private JTurfFeatureConversion() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 多边形转换成多线<br><br>
     * <p>
     * 将多边形转换为(多)线段或将多面体转换为包含(多)线段
     *
     * @param polygon 多边形
     * @return Feature 根据传入的多边形的环数 内部可能是 LineString 或 MultiLineString
     */
    public static Feature<Geometry> polygonToLine(Polygon polygon) {
        return polygonToLine(polygon, null);
    }

    /**
     * 多边形转换成多线<br><br>
     * <p>
     * 将多边形转换为(多)线段或将多面体转换为包含(多)线段
     *
     * @param polygon    多边形
     * @param properties 附加属性
     * @return Feature 根据传入的多边形的环数 内部可能是 LineString 或 MultiLineString
     */
    public static Feature<Geometry> polygonToLine(Polygon polygon, JsonObject properties) {
        if (polygon == null) {
            throw new JTurfException("polygon is required");
        }
        return PolygonToLineHelper.polygonToLine(polygon, null);
    }

    /**
     * 多边形转换成多线<br><br>
     * <p>
     * 将多边形转换为(多)线段或将多面体转换为包含(多)线段
     *
     * @param multiPolygon 组合多边形
     * @return FeatureCollection 根据传入的多边形的环数内部可能是 LineString 或 MultiLineString
     */
    public static FeatureCollection<Geometry> polygonToLine(MultiPolygon multiPolygon) {
        return polygonToLine(multiPolygon, null);
    }

    /**
     * 多边形转换成多线<br><br>
     * <p>
     * 将多边形转换为(多)线段或将多面体转换为包含(多)线段
     *
     * @param multiPolygon 组合多边形
     * @param properties   附加属性
     * @return FeatureCollection 根据传入的多边形的环数内部可能是 LineString 或 MultiLineString
     */
    public static FeatureCollection<Geometry> polygonToLine(MultiPolygon multiPolygon, JsonObject properties) {
        if (multiPolygon == null) {
            throw new JTurfException("polygon is required");
        }

        return PolygonToLineHelper.polygonToLine(multiPolygon, null);
    }

    /**
     * 减少嵌套层级<br><br>
     * <p>
     * 扁平化任何 Geometry 为 List
     *
     * @param geometry 多边形
     * @return 所有多几何对象都被展平为单一要素
     */
    public static FeatureCollection<Geometry> flatten(Geometry geometry) {
        List<Feature<Geometry>> results = new ArrayList<>();

        FlattenEachHelper.flattenEach(geometry, (feature, featureIndex, multiFeatureIndex) -> {
            results.add(feature);

            return true;
        });

        return FeatureCollection.fromFeatures(results);
    }

    /**
     * 拆分多边形为点<br><br>
     * <p>
     * 从图形组件返回所有位置作为点。
     *
     * @param geometry 图形组件
     * @return 返回分解的点
     */
    public static List<Point> explode(Geometry geometry) {
        List<Point> points = new ArrayList<>();

        JTurfMeta.coordEach(geometry, (point, coordIndex, featureIndex, multiFeatureIndex, geometryIndex) -> {
            points.add(point);
            return true;
        });

        return points;
    }

    /**
     * 结合<br><br>
     * <p>
     * 将包含点、线和多边形 合并为 MultiPoint、MultiString 或 MultiPolygon。
     *
     * @param geometryList 多边形集合
     * @return 返回只包含 MultiPoint、MultiString、MultiPolygon的集合，可能为空
     */
    public static FeatureCollection<Geometry> combine(List<Geometry> geometryList) {
        return CombineHelper.combine(geometryList);
    }

    /**
     * 结合<br><br>
     * <p>
     * 将包含点、线和多边形 合并为 MultiPoint、MultiString 或 MultiPolygon。
     *
     * @param featureCollection 多边形集合
     * @return 返回只包含 MultiPoint、MultiString、MultiPolygon的集合，可能为空
     */
    public static FeatureCollection<Geometry> combine(FeatureCollection<Geometry> featureCollection) {
        return CombineHelper.combine(featureCollection);
    }

}
