package com.cgzz.mapbox.jturf.callback;

import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.Point;

@FunctionalInterface
public interface CoordEachCallback {

    /**
     * 循环处理组件的Point信息
     * @param geometry   当前的图形组件
     * @param point      当前的点
     * @param index      循环位置
     * @param multiIndex 组合点循环位置，MULTI_LINE和MULTI_POLYGON的上层位置
     * @param geomIndex  GEOMETRY_COLLECTION或FEATURE_COLLECTION的位置
     * @return 每个Point处理是否成功，当返回false时，循环即被中断
     */
    boolean accept(Geometry geometry, Point point, int index, int multiIndex, int geomIndex);

}
