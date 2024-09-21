package com.cgzz.mapbox.jturf.shape.impl;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.geojson.GeoJsonUtils;
import com.cgzz.mapbox.jturf.shape.AbstractGeometryProperties;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.google.gson.JsonObject;

public final class Feature<T extends Geometry> extends AbstractGeometryProperties implements Geometry {

    private final String id;

    private final T geometry;

    Feature(String id, T geometry, JsonObject properties) {
        super(properties);

        if (geometry == null) {
            throw new NullPointerException("Null geometry");
        }

        this.id = id;
        this.geometry = geometry;
    }

    public static <T extends Geometry> Feature<T> fromGeometry(T geometry) {
        return fromGeometry(geometry, new JsonObject(), null);
    }

    public static <T extends Geometry> Feature<T> fromGeometry(T geometry, String id) {
        return fromGeometry(geometry, new JsonObject(), id);
    }

    public static <T extends Geometry> Feature<T> fromGeometry(T geometry, JsonObject properties) {
        return fromGeometry(geometry, properties == null ? new JsonObject() : properties, null);
    }

    public static <T extends Geometry> Feature<T> fromGeometry(T geometry, JsonObject properties, String id) {
        if (geometry instanceof Feature) {
            throw new JTurfException("feature can not save feature object");
        }

        return new Feature<>(id, geometry, properties == null ? new JsonObject() : properties);
    }

    public static Feature<Geometry> fromJson(String json) {
        return fromJson(json, Geometry.class);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Geometry> Feature<T> fromJson(String json, Class<T> geometryClass) {
        Feature<T> feature = GeoJsonUtils.getGson().fromJson(json, Feature.class);

        if (feature.geometry() instanceof Feature) {
            throw new JTurfException("feature can not save feature object");
        }

        if (feature.properties != null) {
            return feature;
        }

        return new Feature<>(feature.id(), feature.geometry(), new JsonObject());
    }

    @SuppressWarnings("unchecked")
    public static Feature<Geometry> feature(Geometry g) {
        return (Feature<Geometry>) g;
    }

    public String id() {
        return id;
    }

    public T geometry() {
        return geometry;
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
        return GeoJsonUtils.getGson().toJson(this);
    }

    @Override
    public String toString() {
        return "Feature{"
                + "id=" + id + ", "
                + "geometry=" + geometry + ", "
                + "properties=" + properties
                + "}";
    }

    @SuppressWarnings("rawtypes")
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
