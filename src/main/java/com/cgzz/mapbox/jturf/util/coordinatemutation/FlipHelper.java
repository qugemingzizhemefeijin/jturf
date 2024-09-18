package com.cgzz.mapbox.jturf.util.coordinatemutation;

import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.JTurfTransformation;
import com.cgzz.mapbox.jturf.shape.Geometry;

public class FlipHelper {

    private FlipHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 获取输入feature并将它们的所有坐标从[x, y]翻转为[y, x]
     *
     * @param geometry 图形组件
     * @param mutate   是否影响原图形坐标
     * @return 返回处理后的图形组件
     */
    public static <T extends Geometry> T flip(T geometry, boolean mutate) {
        T newGeometry;

        // 确保我们不会直接修改传入的geometry对象。
        if (!mutate) {
            newGeometry = JTurfTransformation.clone(geometry);
        } else {
            newGeometry = geometry;
        }

        JTurfMeta.coordEach(newGeometry, (p, coordIndex, featureIndex, multiFeatureIndex, geometryIndex) -> {
            // 翻转
            p.setCoords(p.latitude(), p.longitude());

            return true;
        });

        return newGeometry;
    }

}
