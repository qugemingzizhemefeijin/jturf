package com.cgzz.mapbox.jturf.shape;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public interface Geometry {

    GeometryType type();

    /**
     * 坐标点的数量，仅适合Point、Line、Polygon、MultiPoint
     *
     * @return int
     */
    default int coordsSize() {
        throw new JTurfException("geometry not support coordsSize");
    }

    JsonObject properties();

    boolean hasProperty(String property);

    void addProperty(String property, Number value);

    void addProperty(String property, String value);

    void addProperty(String property, JsonElement value);

    Number getAsNumber(String property);

    String getAsString(String property);

    JsonObject getAsJsonObject(String property);

    JsonArray getAsJsonArray(String property);

    JsonElement getProperty(String property);

    /**
     * 输出被读取的点阵字符
     *
     * @return String
     */
    String toViewCoordsString();

}
