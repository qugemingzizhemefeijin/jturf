package com.cgzz.mapbox.jturf.geojson;

import java.io.Serializable;

public interface GeoJson extends Serializable {

    /**
     * 转换为 GeoJson 字符串。
     *
     * @return 表示此功能的 JSON 字符串
     */
    String toJson();

}
