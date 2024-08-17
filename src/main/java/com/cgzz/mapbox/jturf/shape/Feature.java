package com.cgzz.mapbox.jturf.shape;

import com.cgzz.mapbox.jturf.geojson.GeoJsonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class Feature implements Geometry {

    private final String id;

    private final Geometry geometry;

    private final JsonObject properties;

    Feature(String id, Geometry geometry, JsonObject properties) {
        if (geometry == null) {
            throw new NullPointerException("Null geometry");
        }

        this.id = id;
        this.geometry = geometry;
        this.properties = properties;
    }

    public static Feature fromGeometry(Geometry geometry) {
        return new Feature(null, geometry, new JsonObject());
    }

    public static Feature fromGeometry(Geometry geometry, JsonObject properties) {
        return new Feature(null, geometry, properties == null ? new JsonObject() : properties);
    }

    public static Feature fromGeometry(Geometry geometry, JsonObject properties, String id) {
        return new Feature(id, geometry, properties == null ? new JsonObject() : properties);
    }

    public static Feature fromJson(String json) {
        Feature feature = GeoJsonUtils.getGson().fromJson(json, Feature.class);

        if (feature.properties() != null) {
            return feature;
        }

        return new Feature(feature.id(), feature.geometry(), new JsonObject());
    }

    public String id() {
        return id;
    }

    public Geometry geometry() {
        return geometry;
    }

    public JsonObject properties() {
        return properties;
    }

    public void addStringProperty(String key, String value) {
        properties().addProperty(key, value);
    }

    public void addNumberProperty(String key, Number value) {
        properties().addProperty(key, value);
    }

    public void addBooleanProperty(String key, Boolean value) {
        properties().addProperty(key, value);
    }

    public void addProperty(String key, JsonElement value) {
        properties().add(key, value);
    }

    public String getStringProperty(String key) {
        JsonElement propertyKey = properties().get(key);
        return propertyKey == null ? null : propertyKey.getAsString();
    }

    public Number getNumberProperty(String key) {
        JsonElement propertyKey = properties().get(key);
        return propertyKey == null ? null : propertyKey.getAsNumber();
    }

    public Boolean getBooleanProperty(String key) {
        JsonElement propertyKey = properties().get(key);
        return propertyKey == null ? null : propertyKey.getAsBoolean();
    }

    public JsonElement getProperty(String key) {
        return properties().get(key);
    }

    public JsonElement removeProperty(String key) {
        return properties().remove(key);
    }

    public boolean hasProperty(String key) {
        return properties().has(key);
    }

    public boolean hasNonNullValueForProperty(String key) {
        return hasProperty(key) && !getProperty(key).isJsonNull();
    }

    @Override
    public GeometryType geometryType() {
        return GeometryType.FEATURE;
    }

    @Override
    public String toViewCoordsString() {
        StringBuilder buf = new StringBuilder();
        buf.append("├───── ").append(geometryType()).append("─────┤").append("\n");
        buf.append("id{").append(id).append("}").append("\n");
        buf.append("properties{").append(properties).append("}").append("\n");
        buf.append(geometry.toViewCoordsString());
        return buf.toString();
    }

    @Override
    public String toJson() {
        return null;
    }

    @Override
    public String toString() {
        return "Feature{"
                + "id=" + id + ", "
                + "geometry=" + geometry + ", "
                + "properties=" + properties
                + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Feature) {
            Feature that = (Feature) obj;
            return (this.id == null ? that.id() == null : this.id.equals(that.id()))
                    && this.geometry.equals(that.geometry())
                    && (this.properties == null ? that.properties() == null : this.properties.equals(that.properties()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode *= 1000003;
        hashCode ^= (id == null) ? 0 : id.hashCode();
        hashCode *= 1000003;
        hashCode ^= geometry.hashCode();
        hashCode *= 1000003;
        hashCode ^= (properties == null) ? 0 : properties.hashCode();
        return hashCode;
    }

}
