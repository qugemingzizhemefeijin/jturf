package com.cgzz.mapbox.jturf.geojson;

import com.cgzz.mapbox.jturf.geojson.adapter.factory.GeoJsonAdapterFactory;
import com.cgzz.mapbox.jturf.geojson.adapter.factory.GeometryAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class GeoJsonUtils {

    private static final double ROUND_PRECISION = 10000000.0;
    private static final long MAX_DOUBLE_TO_ROUND = (long) (Long.MAX_VALUE / ROUND_PRECISION);

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapterFactory(GeoJsonAdapterFactory.create())
            .registerTypeAdapterFactory(GeometryAdapterFactory.create())
            .create();

    private GeoJsonUtils() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 将双精度值调整为只有 7 位数字。
     *
     * @param value 待调整的数值
     * @return 调整后的数值
     */
    public static double trim(double value) {
        if (value > MAX_DOUBLE_TO_ROUND || value < -MAX_DOUBLE_TO_ROUND) {
            return value;
        }
        return Math.round(value * ROUND_PRECISION) / ROUND_PRECISION;
    }

    /**
     * 返回Gson对象，用于GeoJson序列化和反序列化
     *
     * @return Gson
     */
    public static Gson getGson() {
        return GSON;
    }

}
