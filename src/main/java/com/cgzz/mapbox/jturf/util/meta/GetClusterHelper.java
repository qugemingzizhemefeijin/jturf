package com.cgzz.mapbox.jturf.util.meta;

import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.FeatureCollection;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class GetClusterHelper {

    private GetClusterHelper() {
        throw new AssertionError("No Instances.");
    }

    /**
     * Get Cluster
     *
     * @param geojson 一组坐标点
     * @param filter  用于 GeoJSON 属性的筛选器以获取 Cluster
     * @return 按 GeoJSON 属性筛选的单个集群
     */
    public static FeatureCollection<Point> getCluster(FeatureCollection<Point> geojson, String filter) {
        if (filter == null) {
            throw new JTurfException("filter is required");
        }

        // Filter Features
        List<Feature<Point>> features = filterFeatures(geojson, (properties) -> applyFilter(properties, filter));

        return FeatureCollection.fromFeatures(features);
    }

    /**
     * Get Cluster
     *
     * @param geojson   一组坐标点
     * @param filter    用于 GeoJSON 属性的筛选器以获取 Cluster
     * @param filterVal 筛选的属性值
     * @return 按 GeoJSON 属性筛选的单个集群
     */
    public static FeatureCollection<Point> getCluster(FeatureCollection<Point> geojson, String filter, Object filterVal) {
        if (filter == null) {
            throw new JTurfException("filter is required");
        }

        // Filter Features
        List<Feature<Point>> features = filterFeatures(geojson, (properties) -> applyFilter(properties, filter, filterVal));

        return FeatureCollection.fromFeatures(features);
    }

    /**
     * Get Cluster
     *
     * @param geojson 一组坐标点
     * @param filter  用于 GeoJSON 属性的筛选器以获取 Cluster
     * @return 按 GeoJSON 属性筛选的单个集群
     */
    public static FeatureCollection<Point> getCluster(FeatureCollection<Point> geojson, String[] filter) {
        if (filter == null) {
            throw new JTurfException("filter is required");
        }

        // Filter Features
        List<Feature<Point>> features = filterFeatures(geojson, (properties) -> applyFilter(properties, filter));

        return FeatureCollection.fromFeatures(features);
    }

    /**
     * Get Cluster
     *
     * @param geojson 一组坐标点
     * @param filter  用于 GeoJSON 属性的筛选器以获取 Cluster
     * @return 按 GeoJSON 属性筛选的单个集群
     */
    public static FeatureCollection<Point> getCluster(FeatureCollection<Point> geojson, Map<String, Object> filter) {
        if (filter == null) {
            throw new JTurfException("filter is required");
        }

        // Filter Features
        List<Feature<Point>> features = filterFeatures(geojson, (properties) -> applyFilter(properties, filter));

        return FeatureCollection.fromFeatures(features);
    }

    /**
     * Get Cluster 的循环比对
     *
     * @param geojson 属性信息
     * @param func    执行比较方法的函数
     * @return 返回符合过滤的点集合
     */
    private static List<Feature<Point>> filterFeatures(FeatureCollection<Point> geojson, Function<JsonObject, Boolean> func) {
        if (geojson == null) {
            throw new JTurfException("geojson is required");
        }

        List<Feature<Point>> features = new ArrayList<>();
        JTurfMeta.featureEach(geojson, (currentFeature, featureIndex) -> {
            if (func.apply(currentFeature.properties())) {
                features.add(currentFeature);
            }

            return true;
        });

        return features;
    }

    /**
     * applyFilter
     *
     * @param properties 属性信息
     * @param filter     过滤条件
     * @return 返回是否符合过滤的条件
     */
    private static boolean applyFilter(JsonObject properties, String filter) {
        if (properties == null || properties.size() == 0) {
            return false;
        }

        JsonElement val = properties.get(filter);
        if (val == null) {
            return false;
        }

        return !val.isJsonNull();
    }

    /**
     * applyFilter
     *
     * @param properties 属性信息
     * @param filter     过滤条件
     * @param filterVal  过滤属性值
     * @return 返回是否符合过滤的条件
     */
    private static boolean applyFilter(JsonObject properties, String filter, Object filterVal) {
        if (properties == null || properties.size() == 0) {
            return false;
        }

        JsonElement val = properties.get(filter);
        if (val == null) {
            return false;
        }

        return applyFilter(val, filterVal);
    }

    /**
     * applyFilter
     *
     * @param properties 属性信息
     * @param filter     过滤条件
     * @return 返回是否符合过滤的条件
     */
    private static boolean applyFilter(JsonObject properties, String[] filter) {
        if (properties == null || properties.size() == 0) {
            return false;
        }

        for (String s : filter) {
            if (!applyFilter(properties, s)) {
                return false;
            }
        }

        return true;
    }

    /**
     * applyFilter
     *
     * @param properties 属性信息
     * @param filter     过滤条件
     * @return 返回是否符合过滤的条件
     */
    private static boolean applyFilter(JsonObject properties, Map<String, Object> filter) {
        if (properties == null || properties.size() == 0) {
            return false;
        }

        for (Map.Entry<String, Object> me : filter.entrySet()) {
            String key = me.getKey();
            Object val = me.getValue();

            JsonElement element = properties.get(key);
            if (element == null) {
                return false;
            }

            if (!applyFilter(element, val)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断指定的属性存储的值是否与传入的一致
     *
     * @param element 要判断的Json元素
     * @param val     要判断的值
     * @return 返回true则表示一致
     */
    private static boolean applyFilter(JsonElement element, Object val) {
        if (val == null) {
            return element.isJsonNull();
        } else {
            if (element instanceof JsonPrimitive) {
                JsonPrimitive e = (JsonPrimitive)element;

                if (val instanceof Number) {
                    return e.isNumber() && Objects.equals(((Number) val).doubleValue(), element.getAsDouble());
                } else if (val instanceof String) {
                    return Objects.equals(val, element.getAsString());
                }
            } else {
                return false;
            }
        }

        return true;
    }

}
