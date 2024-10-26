package com.cgzz.mapbox.jturf.util.meta.func;

import com.google.gson.JsonObject;

@FunctionalInterface
public interface PropReduceFunc {

    /**
     * 将任何 GeoJSON 对象中的属性缩减为单个值，类似于 Array.reduce 的工作原理。但是，在本例中，我们延迟运行归约，因此不需要所有属性的数组。
     *
     * @param previousValue     上一个属性值
     * @param currentProperties 当前循环到的组建的属性值
     * @param featureIndex      当前循环的Feature在FeatureCollection中的索引
     * @return 返回新的属性值
     */
    JsonObject accept(JsonObject previousValue, JsonObject currentProperties, int featureIndex);

}
