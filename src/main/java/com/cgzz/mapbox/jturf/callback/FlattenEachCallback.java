package com.cgzz.mapbox.jturf.callback;

import com.cgzz.mapbox.jturf.shape.Geometry;

@FunctionalInterface
public interface FlattenEachCallback {

    /**
     * 处理元素循环的回调
     * @param geometry   当前的图形组件
     * @param multiIndex 如果是组合组件，则为其所处在组合组件中的索引位置
     * @return 处理是否成功，当返回false时，循环即被中断
     */
    boolean accept(Geometry geometry, int multiIndex);

}
