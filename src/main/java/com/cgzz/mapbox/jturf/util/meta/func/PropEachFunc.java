package com.cgzz.mapbox.jturf.util.meta.func;

import com.google.gson.JsonObject;

@FunctionalInterface
public interface PropEachFunc {

    /**
     * 迭代任何 GeoJSON 对象中的特征，类似于 Array.forEach
     *
     * @param currentProperties 当前循环的属性信息
     * @param featureIndex      当前循环的Feature在FeatureCollection中的索引
     * @return 处理是否成功，当返回false时，循环即被中断
     */
    boolean accept(JsonObject currentProperties, int featureIndex);

}
