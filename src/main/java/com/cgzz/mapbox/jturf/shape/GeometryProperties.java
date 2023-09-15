package com.cgzz.mapbox.jturf.shape;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class GeometryProperties {

    protected JsonObject properties;

    public JsonObject properties() {
        return properties;
    }

    public void addProperty(String property, Number value) {
        if (properties == null) {
            properties = new JsonObject();
        }
        properties.addProperty(property, value);
    }

    public void addProperty(String property, String value) {
        if (properties == null) {
            properties = new JsonObject();
        }
        properties.addProperty(property, value);
    }

    public void addProperty(String property, JsonElement value) {
        if (properties == null) {
            properties = new JsonObject();
        }
        properties.add(property, value);
    }

    public Number getAsNumber(String property) {
        if (properties == null) {
            return null;
        }
        JsonElement element = properties.get(property);
        return element == null || element.isJsonNull() ? null : element.getAsNumber();
    }

    public String getAsString(String property) {
        if (properties == null) {
            return null;
        }
        JsonElement element = properties.get(property);
        return element == null || element.isJsonNull() ? null : element.getAsString();
    }

    public JsonObject getAsJsonObject(String property) {
        if (properties == null) {
            return null;
        }
        JsonElement element = properties.get(property);
        return element == null || element.isJsonNull() ? null : element.getAsJsonObject();
    }

    public JsonArray getAsJsonArray(String property) {
        if (properties == null) {
            return null;
        }
        JsonElement element = properties.get(property);
        return element == null || element.isJsonNull() ? null : element.getAsJsonArray();
    }

}
