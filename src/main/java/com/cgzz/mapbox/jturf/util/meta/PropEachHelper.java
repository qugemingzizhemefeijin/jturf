package com.cgzz.mapbox.jturf.util.meta;

import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.FeatureCollection;
import com.cgzz.mapbox.jturf.util.meta.func.PropEachFunc;

public final class PropEachHelper {

    private PropEachHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 迭代 FeatureCollection 对象中的属性，类似于 Array.forEach()
     *
     * @param featureCollection 要迭代的元素
     * @param func              调处理函数
     */
    public static <T extends Geometry> void propEach(FeatureCollection<T> featureCollection, PropEachFunc func) {
        for (int i = 0, size = featureCollection.size(); i < size; i++) {
            if (!func.accept(featureCollection.get(i).properties(), i)) {
                break;
            }
        }
    }

    /**
     * 迭代任何 Feature 对象中的属性，类似于 Array.forEach()
     *
     * @param feature 要迭代的元素
     * @param func    回调处理函数
     */
    public static <T extends Geometry> void propEach(Feature<T> feature, PropEachFunc func) {
        func.accept(feature.properties(), 0);
    }

}
