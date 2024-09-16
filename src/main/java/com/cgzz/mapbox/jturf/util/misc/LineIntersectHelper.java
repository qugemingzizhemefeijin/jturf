package com.cgzz.mapbox.jturf.util.misc;

import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.JTurfMisc;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.FeatureCollection;
import com.cgzz.mapbox.jturf.shape.impl.LineString;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import com.cgzz.mapbox.jturf.util.pkg.RTreeHelper;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Rectangle;
import rx.Observable;

import java.util.*;

public final class LineIntersectHelper {

    private LineIntersectHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 计算两个图形相交点
     *
     * @param geometry1 图形1，支持 Line、Polygon
     * @param geometry2 图形2，支持 Line、Polygon
     * @return 返回相交点集合
     */
    public static FeatureCollection<Point> lineIntersect(Geometry geometry1, Geometry geometry2) {
        if (geometry1 == null) {
            throw new JTurfException("geometry1 is required");
        }
        if (geometry2 == null) {
            throw new JTurfException("geometry2 is required");
        }

        geometry1 = JTurfMeta.getGeom(geometry1);
        geometry2 = JTurfMeta.getGeom(geometry2);

        GeometryType t1 = geometry1.geometryType(), t2 = geometry2.geometryType();
        if (t1 != GeometryType.LINE_STRING
                && t1 != GeometryType.POLYGON
                && t1 != GeometryType.MULTI_LINE_STRING
                && t1 != GeometryType.MULTI_POLYGON) {
            throw new JTurfException("geometry1 type must LineString、Polygon、MultiLineString、MultiPolygon");
        }
        if (t2 != GeometryType.LINE_STRING
                && t2 != GeometryType.POLYGON
                && t2 != GeometryType.MULTI_LINE_STRING
                && t2 != GeometryType.MULTI_POLYGON) {
            throw new JTurfException("geometry2 type must LineString、Polygon、MultiLineString、MultiPolygon");
        }

        // 如果两个都是一条线段的话，不需要展开处理
        if (t1 == GeometryType.LINE_STRING && t2 == GeometryType.LINE_STRING) {
            LineString l1 = LineString.lineString(geometry1), l2 = LineString.lineString(geometry2);
            if (l1.coordinates().size() == 2 && l2.coordinates().size() == 2) {
                Point intersect = intersects(LineString.lineString(geometry1), LineString.lineString(geometry2));

                return intersect != null ? FeatureCollection.fromFeatures(Collections.singletonList(Feature.fromGeometry(intersect))) : null;
            }
        }

        // 处理复杂的几何图形
        List<Feature<Point>> results = new ArrayList<>();
        Set<String> unique = new HashSet<>();

        RTree<LineString, Rectangle> rtree = RTreeHelper.initRTree(geometry2);
        for (Feature<LineString> feature : JTurfMisc.lineSegment(geometry1).geometries()) {
            LineString segment = LineString.lineString(feature.geometry());

            Observable<Entry<LineString, Rectangle>> entries = rtree.search(RTreeHelper.createRectangle(segment));

            for (Entry<LineString, Rectangle> p : entries.toBlocking().toIterable()) {
                Point intersect = intersects(segment, p.value());
                if (intersect != null) {
                    // 防止重复点
                    if (unique.add(intersect.getX() + "," + intersect.getY())) {
                        results.add(Feature.fromGeometry(intersect));
                    }
                }
            }
        }

        return FeatureCollection.fromFeatures(results);
    }

    /**
     * 查找与线串相交的点，每个点有两个坐标
     *
     * @param line1 必须是只有两个点的线段
     * @param line2 必须是只有两个点的线段
     * @return 返回相交点
     */
    private static Point intersects(LineString line1, LineString line2) {
        List<Point> coords1 = line1.coordinates(), coords2 = line2.coordinates();

        if (coords1.size() != 2) {
            throw new JTurfException("<intersects> line1 must only contain 2 coordinates");
        }
        if (coords2.size() != 2) {
            throw new JTurfException("<intersects> line2 must only contain 2 coordinates");
        }

        double x1 = coords1.get(0).getX(), y1 = coords1.get(0).getY();
        double x2 = coords1.get(1).getX(), y2 = coords1.get(1).getY();
        double x3 = coords2.get(0).getX(), y3 = coords2.get(0).getY();
        double x4 = coords2.get(1).getX(), y4 = coords2.get(1).getY();

        double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        double numeA = (x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3);
        double numeB = (x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3);

        if (denom == 0) {
            if (numeA == 0 && numeB == 0) {
                return null;
            }
            return null;
        }

        double uA = numeA / denom;
        double uB = numeB / denom;

        if (uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1) {
            double x = x1 + uA * (x2 - x1);
            double y = y1 + uA * (y2 - y1);
            return Point.fromLngLat(x, y);
        }
        return null;
    }

}
