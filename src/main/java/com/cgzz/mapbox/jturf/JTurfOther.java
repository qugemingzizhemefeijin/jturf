package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.models.BooleanHolder;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.util.other.BooleanValidHelper;

public final class JTurfOther {

    private JTurfOther() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 判断两个图形是否有交集（就是循环迭代图形，并对 booleanDisjoint 取反）
     *
     * @param geometry1 图形1
     * @param geometry2 图形2
     * @return 如果两个图形有交集则返回true
     */
    public static boolean booleanIntersects(Geometry geometry1, Geometry geometry2) {
        BooleanHolder bool = new BooleanHolder();

        JTurfMeta.flattenEach(geometry1, (f1, featureIndex1, multiFeatureIndex1) -> {
            JTurfMeta.flattenEach(geometry2, (f2, featureIndex2, multiFeatureIndex2) -> {
                boolean b = !JTurfBooleans.booleanDisjoint(f1, f2);
                if (b) {
                    bool.value = true;
                    return false; // 只要有相交，则跳出循环
                }
                return true;
            });

            return !bool.value; // 只要有相交，则跳出循环
        });

        return bool.value;
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
