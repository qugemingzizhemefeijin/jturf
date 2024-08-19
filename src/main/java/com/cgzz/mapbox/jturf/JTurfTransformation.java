package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.util.DeepCloneHelper;

public final class JTurfTransformation {

    private static final int DEFAULT_STEPS = 64;

    private JTurfTransformation() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 深度克隆一个 geometry 对象
     *
     * @param geometry 图形组件
     * @return 返回克隆后的图形组件
     */
    @SuppressWarnings("unchecked")
    public static <T extends Geometry> T clone(T geometry) {
        if (geometry == null) {
            return null;
        }

        return (T)DeepCloneHelper.deepClone(geometry);
    }

}
