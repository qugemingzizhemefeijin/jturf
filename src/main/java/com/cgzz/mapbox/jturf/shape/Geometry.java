package com.cgzz.mapbox.jturf.shape;

import com.cgzz.mapbox.jturf.geojson.GeoJson;

public interface Geometry extends GeoJson {

    /**
     * 图形类型枚举
     *
     * @return GeometryType
     */
    GeometryType geometryType();

    /**
     * 输出可视化的数据
     *
     * @return String
     */
    String toViewCoordsString();

}
