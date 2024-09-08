package com.cgzz.mapbox.jturf.callback;

import com.cgzz.mapbox.jturf.shape.Geometry;
import com.google.gson.JsonObject;

@FunctionalInterface
public interface GeometryReduceCallback<R> {

    /**
     * 迭代计算组件的数值
     *
     * @param previousValue     上一次的值
     * @param currentGeometry   当前组件
     * @param featureIndex      当前组件在父组件中的索引
     * @param featureProperties Feature中记录的属性信息
     * @param featureId         Feature的ID
     * @return 返回与 previousValue 相同类型的对象
     */
    R accept(R previousValue, Geometry currentGeometry, int featureIndex, JsonObject featureProperties, String featureId);

}
