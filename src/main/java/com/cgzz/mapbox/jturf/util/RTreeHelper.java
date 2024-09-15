package com.cgzz.mapbox.jturf.util;

import com.cgzz.mapbox.jturf.JTurfMeasurement;
import com.cgzz.mapbox.jturf.JTurfMisc;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.BoundingBox;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.LineString;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Rectangle;

public final class RTreeHelper {

    private RTreeHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 创建 RTree 需要使用到的 Rectangle 对象
     *
     * @param line 线段
     * @return Rectangle
     */
    public static Rectangle createRectangle(LineString line) {
        BoundingBox bbox = JTurfMeasurement.bbox(line);

        return Geometries.rectangle(bbox.west(), bbox.south(), bbox.east(), bbox.north());
    }

    /**
     * 构建一棵R树
     *
     * @param geometry 图形
     * @return RTree<LineString, Rectangle>
     */
    public static RTree<LineString, Rectangle> initRTree(Geometry geometry) {
        // 创建R树时，可以指定最小、最大孩子结点数
        RTree<LineString, Rectangle> rtree = RTree.minChildren(2).maxChildren(9).create();
        for (Feature feature : JTurfMisc.lineSegment(geometry).geometries()) {
            LineString line = LineString.lineString(feature.geometry());

            rtree = rtree.add(line, createRectangle(line));
        }

        return rtree;
    }

}
