package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.util.CleanCoordsHelper;

public final class JTurfCoordinateMutation {

    private JTurfCoordinateMutation() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 清除重复坐标点，默认情况不影响原图形
     *
     * @param geometry 图形组件
     * @return 返回处理后的图形组件
     */
    public static <T extends Geometry> T cleanCoords(T geometry) {
        return cleanCoords(geometry, false);
    }

    /**
     * 清除重复坐标点
     *
     * @param geometry 图形组件
     * @param mutate   是否影响原图形坐标
     * @return 返回处理后的图形组件
     */
    public static <T extends Geometry> T cleanCoords(T geometry, boolean mutate) {
        return CleanCoordsHelper.cleanCoords(geometry, mutate);
    }

}
