package com.cgzz.mapbox.jturf.util.metahelper;

import com.cgzz.mapbox.jturf.callback.GeometryReduceCallback;
import com.cgzz.mapbox.jturf.models.ObjectHolder;
import com.cgzz.mapbox.jturf.shape.Geometry;

public final class GeomReduceHelper {

    private GeomReduceHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 循环处理Geometry对象
     *
     * @param geometry      图形组件
     * @param callback      处理函数
     * @param initialValue  初始值
     * @return 是否所有的对象均处理成功
     */
    public static <R> R geomReduce(Geometry geometry, GeometryReduceCallback<R> callback, R initialValue) {
        ObjectHolder<R> previousValue = new ObjectHolder<>(initialValue);

        GeomEachHelper.geomEach(geometry, (currentGeometry, featureIndex, featureProperties, featureId) -> {
            previousValue.value = callback.accept(previousValue.value, currentGeometry, featureIndex, featureProperties, featureId);

            return true;
        });

        return previousValue.value;
    }

}
