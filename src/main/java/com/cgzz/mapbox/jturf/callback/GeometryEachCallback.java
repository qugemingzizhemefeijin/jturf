package com.cgzz.mapbox.jturf.callback;

import com.cgzz.mapbox.jturf.shape.Geometry;

@FunctionalInterface
public interface GeometryEachCallback {

    /**
     * 循环处理组件的Points信息
     * @param geometry   当前的图形组件
     * @param parent     所属的父图形组件
     * @param geomIndex  GEOMETRY_COLLECTION的位置
     * @return 每个Point处理是否成功，当返回false时，循环即被中断
     */
    boolean accept(Geometry geometry, Geometry parent, int geomIndex);

}
