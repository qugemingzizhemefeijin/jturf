package com.cgzz.mapbox.jturf.callback;

import com.cgzz.mapbox.jturf.shape.Line;

@FunctionalInterface
public interface SegmentEachCallback {

    /**
     * 循环迭代组件内部的相邻线段
     *
     * @param currentSegment 当前的线段
     * @param geometryIndex  GEOMETRY_COLLECTION的位置
     * @param multiIndex     组合点循环位置，MULTI_LINE和MULTI_POLYGON的上层位置
     * @param segmentIndex   当前线段的位置
     * @return
     */
    boolean accept(Line currentSegment, int geometryIndex, int multiIndex, int segmentIndex);

}
