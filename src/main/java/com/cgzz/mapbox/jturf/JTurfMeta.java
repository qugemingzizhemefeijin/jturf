package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.callback.*;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.util.meta.*;

public final class JTurfMeta {

    private JTurfMeta() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 循环处理组件点信息
     *
     * @param geometry 图形组件
     * @param callback 处理函数
     * @return 是否所有的点均处理成功
     */
    public static <T extends Geometry> boolean coordEach(T geometry, CoordEachCallback callback) {
        return coordEach(geometry, callback, false);
    }

    /**
     * 循环处理组件点信息
     *
     * @param geometry         图形组件
     * @param callback         处理函数
     * @param excludeWrapCoord 如果是 POLYGON || MULTI_POLYGON 是否处理最后一个闭合点
     * @return 是否所有的点均处理成功
     */
    public static <T extends Geometry> boolean coordEach(T geometry, CoordEachCallback callback, boolean excludeWrapCoord) {
        if (geometry == null) {
            return false;
        }

        return CoordEachHelper.coordEach(geometry, callback, excludeWrapCoord);
    }

    /**
     * 循环处理组件点集合信息（注意：此函数不能处理Point类型）
     *
     * @param geometry 图形组件
     * @param callback 处理函数
     * @return 是否所有的点均处理成功
     */
    public static <T extends Geometry> boolean coordsEach(T geometry, CoordsEachCallback callback) {
        if (geometry == null) {
            return false;
        }

        return CoordsEachHelper.coordsEach(geometry, callback);
    }

    /**
     * 如果传入的 Geometry 为一个 Feature类型，则获取其内部的对象
     *
     * @param geometry GeoJSON Feature or Geometry Object
     * @return Geometry
     */
    public static Geometry getGeom(Geometry geometry) {
        if (geometry.geometryType() == GeometryType.FEATURE) {
            return Feature.feature(geometry).geometry();
        }
        return geometry;
    }

    /**
     * 迭代任何对象中的扁平特性，类似于Array.forEach，会将Multi(Point|Line|Polygon)拆分成单个图形
     *
     * @param geometry 要迭代的元素
     * @param callback 回调处理函数
     */
    public static void flattenEach(Geometry geometry, FlattenEachCallback callback) {
        FlattenEachHelper.flattenEach(geometry, callback);
    }

    /**
     * 迭代对象的线段
     *
     * @param geometry 要迭代的图形
     * @param callback 回调函数
     */
    public static void segmentEach(Geometry geometry, SegmentEachCallback callback) {
        SegmentEachHelper.segmentEach(geometry, callback);
    }

    /**
     * 循环处理Geometry对象
     *
     * @param geometry 图形组件
     * @param callback 处理函数
     * @return 是否所有的对象均处理成功
     */
    public static <T extends Geometry> boolean geomEach(T geometry, GeometryEachCallback callback) {
        return GeomEachHelper.geomEach(geometry, callback);
    }

}
