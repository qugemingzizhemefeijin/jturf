package com.cgzz.mapbox.jturf.util.meta;

import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.FeatureCollection;
import com.cgzz.mapbox.jturf.util.meta.func.FeatureEachFunc;

public final class FeatureEachHelper {

    private FeatureEachHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 迭代任何对象中的扁平特性，类似于Array.forEach，会将Multi(Point|Line|Polygon)拆分成单个图形
     *
     * @param feature 要迭代的元素
     * @param func    回调处理函数
     * @return 如果全部处理成功，则返回true,否则false
     */
    public static <T extends Geometry> boolean featureEach(Feature<T> feature, FeatureEachFunc<T> func) {
        return func.accept(feature, 0);
    }

    /**
     * 迭代任何对象中的扁平特性，类似于Array.forEach，会将Multi(Point|Line|Polygon)拆分成单个图形
     *
     * @param featureCollection 要迭代的元素
     * @param func              回调处理函数
     * @return 如果全部处理成功，则返回true,否则false
     */
    public static <T extends Geometry> boolean featureEach(FeatureCollection<T> featureCollection, FeatureEachFunc<T> func) {
        for (int i = 0, size = featureCollection.size(); i < size; i++) {
            if (func.accept(featureCollection.get(i), 0)) {
                return false;
            }
        }
        return true;
    }

}
