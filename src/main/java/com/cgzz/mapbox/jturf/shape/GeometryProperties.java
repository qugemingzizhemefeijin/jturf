package com.cgzz.mapbox.jturf.shape;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class GeometryProperties implements Geometry {

    protected JsonObject properties;

    @Override
    public JsonObject properties() {
        return properties;
    }

    @Override
    public boolean hasProperty(String property) {
        if (properties == null) {
            properties = new JsonObject();
        }
        return properties.has(property);
    }

    @Override
    public void addProperty(String property, Number value) {
        if (properties == null) {
            properties = new JsonObject();
        }
        properties.addProperty(property, value);
    }

    @Override
    public void addProperty(String property, String value) {
        if (properties == null) {
            properties = new JsonObject();
        }
        properties.addProperty(property, value);
    }

    @Override
    public void addProperty(String property, JsonElement value) {
        if (properties == null) {
            properties = new JsonObject();
        }
        properties.add(property, value);
    }

    @Override
    public Number getAsNumber(String property) {
        if (properties == null) {
            return null;
        }
        JsonElement element = properties.get(property);
        return element == null || element.isJsonNull() ? null : element.getAsNumber();
    }

    @Override
    public String getAsString(String property) {
        if (properties == null) {
            return null;
        }
        JsonElement element = properties.get(property);
        return element == null || element.isJsonNull() ? null : element.getAsString();
    }

    @Override
    public JsonObject getAsJsonObject(String property) {
        if (properties == null) {
            return null;
        }
        JsonElement element = properties.get(property);
        return element == null || element.isJsonNull() ? null : element.getAsJsonObject();
    }

    @Override
    public JsonArray getAsJsonArray(String property) {
        if (properties == null) {
            return null;
        }
        JsonElement element = properties.get(property);
        return element == null || element.isJsonNull() ? null : element.getAsJsonArray();
    }

    @Override
    public JsonElement getProperty(String property) {
        if (properties == null) {
            return null;
        }
        return properties.get(property);
    }

    protected JsonObject cloneProperties() {
        if (properties == null) {
            return null;
        }
        return properties.deepCopy();
    }

}
