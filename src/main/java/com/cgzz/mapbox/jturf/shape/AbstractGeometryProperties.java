package com.cgzz.mapbox.jturf.shape;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class AbstractGeometryProperties {

    protected JsonObject properties;

    public AbstractGeometryProperties(JsonObject properties) {
        this.properties = properties;
    }

    public JsonObject properties() {
        return properties;
    }

    public int propertySize() {
        if (properties == null) {
            return 0;
        }
        return properties.size();
    }

    public boolean propertyEmpty() {
        return propertySize() == 0;
    }

    public boolean propertyNotEmpty() {
        return propertySize() > 0;
    }

    private JsonObject getOrCreate() {
        if (properties == null) {
            properties = new JsonObject();
        }
        return properties;
    }

    public void addProperty(String property, Number value) {
        getOrCreate().addProperty(property, value);
    }

    public void addProperty(String property, String value) {
        getOrCreate().addProperty(property, value);
    }

    public void addProperty(String property, JsonElement value) {
        getOrCreate().add(property, value);
    }

    public JsonObject deepCopyProperties() {
        if (properties == null) {
            return null;
        }
        return properties.deepCopy();
    }

    public Number getPropertyAsNumber(String property) {
        if (properties == null) {
            return null;
        }
        JsonElement element = properties.get(property);
        return element == null || element.isJsonNull() ? null : element.getAsNumber();
    }

    public String getPropertyAsString(String property) {
        if (properties == null) {
            return null;
        }
        JsonElement element = properties.get(property);
        return element == null || element.isJsonNull() ? null : element.getAsString();
    }

    public JsonObject getPropertyAsJsonObject(String property) {
        if (properties == null) {
            return null;
        }
        JsonElement element = properties.get(property);
        return element == null || element.isJsonNull() ? null : element.getAsJsonObject();
    }

    public JsonArray getPropertyAsJsonArray(String property) {
        if (properties == null) {
            return null;
        }
        JsonElement element = properties.get(property);
        return element == null || element.isJsonNull() ? null : element.getAsJsonArray();
    }

    public JsonElement getProperty(String key) {
        if (properties == null) {
            return null;
        }
        return properties.get(key);
    }

    public JsonElement removeProperty(String key) {
        if (properties == null) {
            return null;
        }
        return properties.remove(key);
    }

    public boolean hasProperty(String key) {
        if (properties == null) {
            return false;
        }
        return properties.has(key);
    }

    public boolean hasNonNullValueForProperty(String key) {
        JsonElement element = getProperty(key);

        return element != null && !element.isJsonNull();
    }

}
