package com.cgzz.mapbox.jturf.util.meta.func;

import com.cgzz.mapbox.jturf.shape.impl.LineString;

@FunctionalInterface
public interface SegmentEachFunc {

    /**
     * 循环迭代组件内部的相邻线段
     *
     * @param currentSegment     当前的线段
     * @param featureIndex       组件的索引位置
     * @param multiFeatureIndex  组件在组合Feature组件中的位置
     * @param geometryIndex      组件在组合Geometry组件中的位置
     * @param segmentIndex       当前线段的位置
     * @return 处理是否成功，当返回false时，循环即被中断
     */
    boolean accept(LineString currentSegment, int featureIndex, int multiFeatureIndex, int geometryIndex, int segmentIndex);

}
