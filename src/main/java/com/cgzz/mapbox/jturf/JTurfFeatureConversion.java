package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Line;
import com.cgzz.mapbox.jturf.shape.MultiPolygon;
import com.cgzz.mapbox.jturf.shape.Point;
import com.cgzz.mapbox.jturf.shape.Polygon;

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
     * @param multiPolygon 自核多边形
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

}
