package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.util.coordinatemutation.CleanCoordsHelper;
import com.cgzz.mapbox.jturf.util.coordinatemutation.FlipHelper;
import com.cgzz.mapbox.jturf.util.coordinatemutation.RewindHelper;
import com.cgzz.mapbox.jturf.util.coordinatemutation.TruncateHelper;

public final class JTurfCoordinateMutation {

    private JTurfCoordinateMutation() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 获取输入feature并将它们的所有坐标从[x, y]翻转为[y, x]，默认会生成新的 geometry 图形组件
     *
     * @param geometry 图形组件
     * @return 返回处理后的图形组件
     */
    public static <T extends Geometry> T flip(T geometry) {
        return flip(geometry, false);
    }

    /**
     * 获取输入feature并将它们的所有坐标从[x, y]翻转为[y, x]
     *
     * @param geometry 图形组件
     * @param mutate   是否影响原图形坐标
     * @return 返回处理后的图形组件
     */
    public static <T extends Geometry> T flip(T geometry, boolean mutate) {
        return FlipHelper.flip(geometry, mutate);
    }

    /**
     * 坐标小数处理，默认情况保留6位小数
     *
     * @param geometry 图形组件
     * @return 返回处理后的图形组件
     */
    public static <T extends Geometry> T truncate(T geometry) {
        return truncate(geometry, 6, 3, false);
    }

    /**
     * 坐标小数处理
     *
     * @param geometry  图形组件
     * @param precision 保留的小数位数，如果传入空则保留6位小数
     * @return 返回处理后的图形组件
     */
    public static <T extends Geometry> T truncate(T geometry, Integer precision) {
        return truncate(geometry, 6, 3, false);
    }

    /**
     * 坐标小数处理
     *
     * @param geometry    图形组件
     * @param precision   保留的小数位数，如果传入空则保留6位小数
     * @param coordinates 最大坐标数 (主要用于删除 Z 坐标)（默认3）
     * @return 返回处理后的图形组件
     */
    public static <T extends Geometry> T truncate(T geometry, Integer precision, Integer coordinates) {
        return truncate(geometry, 6, coordinates, false);
    }

    /**
     * 坐标小数处理
     *
     * @param geometry    图形组件
     * @param precision   保留的小数位数，如果传入空则保留6位小数
     * @param coordinates 最大坐标数 (主要用于删除 Z 坐标)（默认3）
     * @param mutate      是否影响原图形坐标
     * @return 返回处理后的图形组件
     */
    public static <T extends Geometry> T truncate(T geometry, Integer precision, Integer coordinates, boolean mutate) {
        return TruncateHelper.truncate(geometry, precision, coordinates, mutate);
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

    /**
     * Rewind Line、MultiLine、Polygon、MultiPolygon、GeometryCollection 默认按照顺时针返回
     *
     * @param geometry 图形组件
     * @return 返回处理后的图形组件
     */
    public static <T extends Geometry> T rewind(T geometry) {
        return rewind(geometry, true, false);
    }

    /**
     * Rewind Line、MultiLine、Polygon、MultiPolygon、GeometryCollection 默认按照顺时针返回
     *
     * @param geometry 图形组件
     * @param mutate   是否影响原图形坐标
     * @return 返回处理后的图形组件
     */
    public static <T extends Geometry> T rewind(T geometry, boolean mutate) {
        return rewind(geometry, true, mutate);
    }

    /**
     * Rewind Line、MultiLine、Polygon、MultiPolygon、GeometryCollection true顺时针和false逆时针
     *
     * @param geometry 图形组件
     * @param reverse  true顺时针和false逆时针
     * @param mutate   是否影响原图形坐标
     * @return 返回处理后的图形组件
     */
    public static <T extends Geometry> T rewind(T geometry, boolean reverse, boolean mutate) {
        return RewindHelper.rewind(geometry, reverse, mutate);
    }

}
