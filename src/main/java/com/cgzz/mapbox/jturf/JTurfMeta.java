package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.FeatureCollection;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import com.cgzz.mapbox.jturf.util.meta.*;
import com.cgzz.mapbox.jturf.util.meta.func.*;
import com.cgzz.mapbox.jturf.util.pkg.skmeans.Skmeans;
import com.cgzz.mapbox.jturf.util.pkg.skmeans.SkmeansRes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JTurfMeta {

    private JTurfMeta() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 循环处理组件点信息
     *
     * @param geometry 图形组件
     * @param func     处理函数
     * @return 是否所有的点均处理成功
     */
    public static <T extends Geometry> boolean coordEach(T geometry, CoordEachFunc func) {
        return coordEach(geometry, func, false);
    }

    /**
     * 循环处理组件点信息
     *
     * @param geometry         图形组件
     * @param func             处理函数
     * @param excludeWrapCoord 如果是 POLYGON || MULTI_POLYGON 是否处理最后一个闭合点
     * @return 是否所有的点均处理成功
     */
    public static <T extends Geometry> boolean coordEach(T geometry, CoordEachFunc func, boolean excludeWrapCoord) {
        if (geometry == null) {
            return false;
        }

        return CoordEachHelper.coordEach(geometry, func, excludeWrapCoord);
    }

    /**
     * 循环处理组件点集合信息（注意：此函数不能处理Point类型）
     *
     * @param geometry 图形组件
     * @param func     处理函数
     * @return 是否所有的点均处理成功
     */
    public static <T extends Geometry> boolean coordListEach(T geometry, CoordListEachFunc func) {
        if (geometry == null) {
            return false;
        }

        return CoordListEachHelper.coordListEach(geometry, func);
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
     * @param func     回调处理函数
     */
    public static void flattenEach(Geometry geometry, FlattenEachFunc func) {
        FlattenEachHelper.flattenEach(geometry, func);
    }

    /**
     * 迭代对象的线段
     *
     * @param geometry 要迭代的图形
     * @param func     回调函数
     */
    public static void segmentEach(Geometry geometry, SegmentEachFunc func) {
        SegmentEachHelper.segmentEach(geometry, func);
    }

    /**
     * 循环处理Geometry对象
     *
     * @param geometry 图形组件
     * @param func     处理函数
     * @return 是否所有的对象均处理成功
     */
    public static <T extends Geometry> boolean geomEach(T geometry, GeometryEachFunc func) {
        return GeomEachHelper.geomEach(geometry, func);
    }

    /**
     * 循环处理Feature对象
     *
     * @param feature Feature图形组件
     * @param func    处理函数
     * @return 如果全部处理成功，则返回true,否则false
     */
    public static <T extends Geometry> boolean featureEach(Feature<T> feature, FeatureEachFunc<T> func) {
        return FeatureEachHelper.featureEach(feature, func);
    }

    /**
     * 循环处理FeatureCollection对象
     *
     * @param featureCollection Feature图形组件
     * @param func              处理函数
     * @return 如果全部处理成功，则返回true,否则false
     */
    public static <T extends Geometry> boolean featureEach(FeatureCollection<T> featureCollection, FeatureEachFunc<T> func) {
        return FeatureEachHelper.featureEach(featureCollection, func);
    }

    /**
     * 迭代 FeatureCollection 对象中的属性，类似于 Array.forEach()
     *
     * @param featureCollection 要迭代的元素
     * @param func              调处理函数
     */
    public static <T extends Geometry> void propEach(FeatureCollection<T> featureCollection, PropEachFunc func) {
        PropEachHelper.propEach(featureCollection, func);
    }

    /**
     * 迭代任何 Feature 对象中的属性，类似于 Array.forEach()
     *
     * @param feature 要迭代的元素
     * @param func    回调处理函数
     */
    public static <T extends Geometry> void propEach(Feature<T> feature, PropEachFunc func) {
        PropEachHelper.propEach(feature, func);
    }

    /**
     * 将任何 GeoJSON 对象中的属性缩减为单个值，类似于 Array.reduce 的工作方式。但是，在这种情况下，我们延迟运行 reduce，因此不需要所有属性的数组。
     *
     * @param featureCollection 要迭代的元素
     * @param func              调处理函数
     * @return 返回归并后的属性
     */
    public static <T extends Geometry> JsonObject propReduce(FeatureCollection<T> featureCollection, PropReduceFunc func) {
        return PropReduceHelper.propReduce(featureCollection, func, null);
    }

    /**
     * 将任何 GeoJSON 对象中的属性缩减为单个值，类似于 Array.reduce 的工作方式。但是，在这种情况下，我们延迟运行 reduce，因此不需要所有属性的数组。
     *
     * @param featureCollection 要迭代的元素
     * @param func              调处理函数
     * @return 返回归并后的属性
     */
    public static <T extends Geometry> JsonObject propReduce(FeatureCollection<T> featureCollection, PropReduceFunc func, JsonObject initialValue) {
        return PropReduceHelper.propReduce(featureCollection, func, initialValue);
    }

    /**
     * 将任何 GeoJSON 对象中的属性缩减为单个值，类似于 Array.reduce 的工作方式。但是，在这种情况下，我们延迟运行 reduce，因此不需要所有属性的数组。
     *
     * @param feature 要迭代的元素
     * @return 返回归并后的属性
     */
    public static <T extends Geometry> JsonObject propReduce(Feature<T> feature, PropReduceFunc func) {
        return PropReduceHelper.propReduce(feature, func, null);
    }

    /**
     * 将任何 GeoJSON 对象中的属性缩减为单个值，类似于 Array.reduce 的工作方式。但是，在这种情况下，我们延迟运行 reduce，因此不需要所有属性的数组。
     *
     * @param feature 要迭代的元素
     * @param func    调处理函数
     * @return 返回归并后的属性
     */
    public static <T extends Geometry> JsonObject propReduce(Feature<T> feature, PropReduceFunc func, JsonObject initialValue) {
        return PropReduceHelper.propReduce(feature, func, initialValue);
    }

    /**
     * 从任何 GeoJSON 对象获取所有坐标。
     *
     * @param geometry 要迭代的元素
     * @return 返回坐标集合
     */
    public static List<Point> coordAll(Geometry geometry) {
        List<Point> coords = new ArrayList<>();
        coordEach(geometry, (coord, coordIndex, featureIndex, multiFeatureIndex, geometryIndex) -> {
            coords.add(coord);
            return true;
        });
        return coords;
    }

    /**
     * 从任何 GeoJSON 对象获取所有坐标。
     *
     * @param geometry 要迭代的元素
     * @return 返回坐标集合
     */
    public static List<double[]> coordAllToArray(Geometry geometry) {
        List<double[]> coords = new ArrayList<>();
        coordEach(geometry, (coord, coordIndex, featureIndex, multiFeatureIndex, geometryIndex) -> {
            coords.add(coord.getCoords());
            return true;
        });
        return coords;
    }

}
