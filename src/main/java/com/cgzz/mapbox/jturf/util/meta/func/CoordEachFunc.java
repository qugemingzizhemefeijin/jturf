package com.cgzz.mapbox.jturf.util.meta.func;

import com.cgzz.mapbox.jturf.shape.impl.Point;

@FunctionalInterface
public interface CoordEachFunc {

    /**
     * 循环处理组件的Point信息
     * @param point              当前的点
     * @param coordIndex         点位置
     * @param featureIndex       组件的索引位置
     * @param multiFeatureIndex  组合点循环位置，MULTI_LINE和MULTI_POLYGON的上层位置
     * @param geometryIndex      GEOMETRY_COLLECTION或FEATURE_COLLECTION的位置
     * @return 每个Point处理是否成功，当返回false时，循环即被中断
     */
    boolean accept(Point point, int coordIndex, int featureIndex, int multiFeatureIndex, int geometryIndex);

}
