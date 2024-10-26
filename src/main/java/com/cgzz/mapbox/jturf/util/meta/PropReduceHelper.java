package com.cgzz.mapbox.jturf.util.meta;

import com.cgzz.mapbox.jturf.models.ObjectHolder;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.FeatureCollection;
import com.cgzz.mapbox.jturf.util.meta.func.PropReduceFunc;
import com.google.gson.JsonObject;

public final class PropReduceHelper {

    private PropReduceHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 将任何 GeoJSON 对象中的属性缩减为单个值，类似于 Array.reduce 的工作方式。但是，在这种情况下，我们延迟运行 reduce，因此不需要所有属性的数组。
     *
     * @param featureCollection 要迭代的元素
     * @param func              调处理函数
     * @return 返回归并后的属性
     */
    public static <T extends Geometry> JsonObject propReduce(FeatureCollection<T> featureCollection, PropReduceFunc func, JsonObject initialValue) {
        ObjectHolder<JsonObject> previousValue = new ObjectHolder<>(initialValue);
        PropEachHelper.propEach(featureCollection, (currentProperties, featureIndex) -> {
            if (featureIndex == 0 && previousValue.value == null) {
                previousValue.value = currentProperties.deepCopy();
            } else {
                previousValue.value = func.accept(previousValue.value, currentProperties, featureIndex);
            }

            return true;
        });

        return previousValue.value;
    }

    /**
     * 将任何 GeoJSON 对象中的属性缩减为单个值，类似于 Array.reduce 的工作方式。但是，在这种情况下，我们延迟运行 reduce，因此不需要所有属性的数组。
     *
     * @param feature 要迭代的元素
     * @param func    调处理函数
     * @return 返回归并后的属性
     */
    public static <T extends Geometry> JsonObject propReduce(Feature<T> feature, PropReduceFunc func, JsonObject initialValue) {
        ObjectHolder<JsonObject> previousValue = new ObjectHolder<>(initialValue);
        PropEachHelper.propEach(feature, (currentProperties, featureIndex) -> {
            if (featureIndex == 0 && previousValue.value == null) {
                previousValue.value = currentProperties.deepCopy();
            } else {
                previousValue.value = func.accept(previousValue.value, currentProperties, featureIndex);
            }

            return true;
        });

        return previousValue.value;
    }

}
