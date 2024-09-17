package com.cgzz.mapbox.jturf.util.meta.func;

import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.Feature;

@FunctionalInterface
public interface FeatureEachFunc<T extends Geometry> {

    /**
     * 迭代任何 GeoJSON 对象中的特征，类似于 Array.forEach
     *
     * @param currentFeature 当前循环的Feature
     * @param featureIndex   当前循环的Feature在FeatureCollection中的索引
     * @return 处理是否成功，当返回false时，循环即被中断
     */
    boolean accept(Feature<T> currentFeature, int featureIndex);

}
