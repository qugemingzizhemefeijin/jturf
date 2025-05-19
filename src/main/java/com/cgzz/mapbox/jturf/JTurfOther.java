package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.util.other.BooleanValidHelper;

public final class JTurfOther {

    private JTurfOther() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 判断有效性<br>
     * booleanValid 检查几何体是否符合 OGC 简单特征规范的有效性。
     *
     * @param feature Feature图形组件
     * @return 有效的对象则返回true
     */
    public static <T extends Geometry> boolean booleanValid(Feature<T> feature) {
        return BooleanValidHelper.booleanValid(feature);
    }

    /**
     * 判断有效性<br>
     * booleanValid 检查几何体是否符合 OGC 简单特征规范的有效性。
     *
     * @param geometry 图形组件
     * @return 有效的对象则返回true
     */
    public static boolean booleanValid(Geometry geometry) {
        return BooleanValidHelper.booleanValid(geometry);
    }

}
