package com.cgzz.mapbox.jturf.callback;

import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.Feature;

@FunctionalInterface
public interface FlattenEachCallback {

    /**
     * 处理元素循环的回调
     * @param feature            当前正在处理的图形组件
     * @param featureIndex       当前组件在父组件中的索引
     * @param multiFeatureIndex  如果是组合组件，则为其所处在组合组件中的索引位置
     * @return 处理是否成功，当返回false时，循环即被中断
     */
    boolean accept(Feature<Geometry> feature, int featureIndex, int multiFeatureIndex);

}
