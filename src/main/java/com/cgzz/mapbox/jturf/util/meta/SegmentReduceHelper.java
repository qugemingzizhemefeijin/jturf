package com.cgzz.mapbox.jturf.util.meta;

import com.cgzz.mapbox.jturf.util.meta.func.SegmentReduceFunc;
import com.cgzz.mapbox.jturf.models.ObjectHolder;
import com.cgzz.mapbox.jturf.shape.Geometry;

public final class SegmentReduceHelper {

    private SegmentReduceHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 循环处理Segment类型的对象
     *
     * @param geometry     图形组件
     * @param func         处理函数
     * @param initialValue 初始值
     * @return 是否所有的对象均处理成功
     */
    public static <T extends Geometry, R> R segmentReduce(T geometry, SegmentReduceFunc<R> func, R initialValue) {
        ObjectHolder<R> previousValue = new ObjectHolder<>(initialValue);

        SegmentEachHelper.segmentEach(geometry, ((currentSegment, featureIndex, multiFeatureIndex, geometryIndex, segmentIndex) -> {
            previousValue.value = func.accept(previousValue.value, currentSegment, featureIndex, multiFeatureIndex, geometryIndex, segmentIndex);

            return false;
        }));

        return previousValue.value;
    }

}
