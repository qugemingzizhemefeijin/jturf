package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.*;

import java.util.ArrayList;
import java.util.List;

public final class JTurfFeatureConversion {

    public JTurfFeatureConversion() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 多边形转换成多线<br><br>
     * <p>
     * 将多边形转换为(多)线段或将多面体转换为包含(多)线段
     *
     * @param polygon 多边形
     * @return Line
     */
    public static Line polygonToLine(Polygon polygon) {
        if (polygon == null) {
            throw new JTurfException("polygon is required");
        }
        return Line.fromLngLats(polygon.coordinates());
    }

    /**
     * 多边形转换成多线<br><br>
     * <p>
     * 将多边形转换为(多)线段或将多面体转换为包含(多)线段
     *
     * @param multiPolygon 组合多边形
     * @return List<Line>
     */
    public static List<Line> polygonToLine(MultiPolygon multiPolygon) {
        if (multiPolygon == null) {
            throw new JTurfException("polygon is required");
        }

        List<List<Point>> allPoint = multiPolygon.coordinates();
        List<Line> lines = new ArrayList<>(allPoint.size());
        for (List<Point> ps : allPoint) {
            lines.add(Line.fromLngLats(ps));
        }

        return lines;
    }

    /**
     * 减少嵌套层级<br><br>
     * <p>
     * 扁平化任何 Geometry 为 List
     *
     * @param geometry 多边形
     * @return 所有多几何对象都被展平为单一要素
     */
    public static List<Geometry> flatten(Geometry geometry) {
        List<Geometry> geometryList = new ArrayList<>();

        JTurfMeta.flattenEach(geometry, (g, multiIndex) -> {
            geometryList.add(g);

            return true;
        });

        return geometryList;
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

        JTurfMeta.coordEach(geometry, (g, point, index, multiIndex, geomIndex) -> {
            points.add(point);
            return true;
        });

        return points;
    }

}
