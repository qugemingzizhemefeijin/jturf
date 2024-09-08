package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.callback.CoordEachCallback;
import com.cgzz.mapbox.jturf.callback.CoordsEachCallback;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.util.metahelper.CoordEachHelper;
import com.cgzz.mapbox.jturf.util.metahelper.CoordsEachHelper;

public final class JTurfMeta {

    private JTurfMeta() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 循环处理组件点信息
     *
     * @param geometry 图形组件
     * @param callback 处理函数
     * @return 是否所有的点均处理成功
     */
    public static <T extends Geometry> boolean coordEach(T geometry, CoordEachCallback callback) {
        return coordEach(geometry, callback, false);
    }

    /**
     * 循环处理组件点信息
     *
     * @param geometry         图形组件
     * @param callback         处理函数
     * @param excludeWrapCoord 如果是 POLYGON || MULTI_POLYGON 是否处理最后一个闭合点
     * @return 是否所有的点均处理成功
     */
    public static <T extends Geometry> boolean coordEach(T geometry, CoordEachCallback callback, boolean excludeWrapCoord) {
        if (geometry == null) {
            return false;
        }

        return CoordEachHelper.coordEach(geometry, callback, excludeWrapCoord);
    }

    /**
     * 循环处理组件点集合信息（注意：此函数不能处理Point类型）
     *
     * @param geometry 图形组件
     * @param callback 处理函数
     * @return 是否所有的点均处理成功
     */
    public static <T extends Geometry> boolean coordsEach(T geometry, CoordsEachCallback callback) {
        if (geometry == null) {
            return false;
        }

        return CoordsEachHelper.coordsEach(geometry, callback);
    }

}
