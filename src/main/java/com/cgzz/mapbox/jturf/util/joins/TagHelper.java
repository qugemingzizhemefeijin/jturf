package com.cgzz.mapbox.jturf.util.joins;

import com.cgzz.mapbox.jturf.JTurfBooleans;
import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.JTurfTransformation;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.FeatureCollection;
import com.cgzz.mapbox.jturf.shape.impl.Point;

public class TagHelper {

    private TagHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 接受一组 点 和一组 多边形，如果点在多边形内，则执行空间连接。
     *
     * @param points   一组点
     * @param polygons 一组多边形
     * @param field    添加到连接的 Point 上
     * @param outField 用于存储 多边形 的连接属性
     * @return 具有 outField 属性的点包含 polygons 中的 field 属性值
     */
    public static <T extends Geometry> FeatureCollection<Point> tag(FeatureCollection<Point> points, FeatureCollection<T> polygons, String field, String outField) {
        points = JTurfTransformation.clone(points);

        JTurfMeta.featureEach(points, (pt, featureIndex) -> {
            JTurfMeta.featureEach(polygons, (poly, fi) -> {
                if (poly.propertyNotEmpty()) {
                    if (pt.getProperty(outField) == null && JTurfBooleans.booleanPointInPolygon(pt.geometry(), poly)) {
                        pt.addProperty(outField, poly.getProperty(field));
                    }
                }

                return true;
            });

            return true;
        });

        return points;
    }

}
