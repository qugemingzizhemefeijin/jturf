package com.cgzz.mapbox.jturf.util.meta.func;

import com.cgzz.mapbox.jturf.shape.impl.LineString;

@FunctionalInterface
public interface SegmentReduceFunc<R> {

    /**
     * 迭代计算组件的数值
     *
     * @param previousValue     上一次的值
     * @param currentSegment    当前线段
     * @param featureIndex      当前组件在父组件中的索引
     * @param multiFeatureIndex 当前组件在FEATURE_COLLECTION的位置
     * @param geometryIndex     当前组件在GEOMETRY_COLLECTION的位置
     * @param segmentIndex      当前线段在组建中的位置
     * @return 返回与 previousValue 相同类型的对象
     */
    R accept(R previousValue, LineString currentSegment, int featureIndex, int multiFeatureIndex, int geometryIndex, int segmentIndex);


}
