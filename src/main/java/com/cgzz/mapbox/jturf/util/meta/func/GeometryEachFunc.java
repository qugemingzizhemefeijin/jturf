package com.cgzz.mapbox.jturf.util.meta.func;

import com.cgzz.mapbox.jturf.shape.Geometry;
import com.google.gson.JsonObject;

@FunctionalInterface
public interface GeometryEachFunc {

    /**
     * 循环处理组件的Points信息
     * @param currentGeometry   当前的图形组件
     * @param featureIndex      当前组件在父组件中的索引
     * @param featureProperties Feature中记录的属性信息
     * @param featureId         Feature的ID
     * @return 每个Point处理是否成功，当返回false时，循环即被中断
     */
    boolean accept(Geometry currentGeometry, int featureIndex, JsonObject featureProperties, String featureId);

}
