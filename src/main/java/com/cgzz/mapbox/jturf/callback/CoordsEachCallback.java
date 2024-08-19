package com.cgzz.mapbox.jturf.callback;

import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.Point;

import java.util.List;

@FunctionalInterface
public interface CoordsEachCallback {

    /**
     * 循环处理组件的Points信息
     * @param geometry   当前的图形组件
     * @param pointList  当前的集合点
     * @param multiIndex 组合点循环位置，MULTI_LINE和MULTI_POLYGON的上层位置
     * @param geomIndex  GEOMETRY_COLLECTION的位置
     * @return 每个Point处理是否成功，当返回false时，循环即被中断
     */
    boolean accept(Geometry geometry, List<Point> pointList, int multiIndex, int geomIndex);

}
