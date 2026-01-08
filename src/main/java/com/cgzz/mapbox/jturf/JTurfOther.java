package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.models.BooleanHolder;
import com.cgzz.mapbox.jturf.models.DoubleHolder;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import com.cgzz.mapbox.jturf.util.other.BooleanValidHelper;
import com.google.gson.JsonObject;

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

    /**
     * 均值中心<br>
     * 接受一个 几何体 或 几何体集合，并返回均值中心。可以进行加权。
     *
     * @param geometry 图形组件
     * @return 一个位于所有输入要素平均中心点的点要素
     */
    public static Feature<Point> centerMean(Geometry geometry) {
        return centerMean(geometry, null);
    }

    /**
     * 均值中心<br>
     * 接受一个 几何体 或 几何体集合，并返回均值中心。可以进行加权。
     *
     * @param geometry 图形组件
     * @param options  属性信息，支持ID，properties病赋予返回的Feature<Point>
     * @return 一个位于所有输入要素平均中心点的点要素
     */
    public static Feature<Point> centerMean(Geometry geometry, JsonObject options) {
        DoubleHolder sumXs = new DoubleHolder(), sumYs = new DoubleHolder(), sumNs = new DoubleHolder();
        String weightName = options.has("weight") ? options.get("weight").getAsString() : null;
        JTurfMeta.geomEach(geometry, (geom, featureIndex, properties, featureId) -> {
            String weight = weightName != null ? properties.get(weightName).getAsString() : null;
            if (weight == null) {
                weight = "1";
            }
            int w;
            try {
                w = Integer.parseInt(weight);
            } catch (Exception e) {
                throw new JTurfException("weight value must be a number for feature index " + featureIndex, e);
            }
            if (w > 0) {
                JTurfMeta.coordEach(geom, (coord, coordIndex, fIdx, multiFeatureIndex, geometryIndex) -> {
                    sumXs.value += coord.getX() * w;
                    sumYs.value += coord.getY() * w;
                    sumNs.value += w;

                    return true;
                });
            }

            return true;
        });

        JsonObject properties = options != null ? options.getAsJsonObject("properties") : null;
        String id = options != null && options.has("id") ? options.get("id").getAsString() : null;

        return Feature.fromGeometry(Point.fromLngLat(sumXs.value / sumNs.value, sumYs.value / sumNs.value), properties, id);
    }

}
