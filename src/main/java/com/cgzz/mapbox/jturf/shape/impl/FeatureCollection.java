package com.cgzz.mapbox.jturf.shape.impl;

import com.cgzz.mapbox.jturf.geojson.GeoJsonUtils;
import com.cgzz.mapbox.jturf.shape.CollectionContainer;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class FeatureCollection<T extends Geometry> implements CollectionContainer<Feature<T>> {

    private final List<Feature<T>> features;

    FeatureCollection(List<Feature<T>> features) {
        if (features == null) {
            throw new NullPointerException("Null features");
        }
        this.features = features;
    }

    public static <T extends Geometry> FeatureCollection<T> fromFeatures(Feature<T>[] features) {
        return new FeatureCollection<>(Arrays.asList(features));
    }

    public static <T extends Geometry> FeatureCollection<T> fromFeatures(List<Feature<T>> features) {
        return new FeatureCollection<>(features);
    }

    public static <T extends Geometry> FeatureCollection<T> fromFeature(Feature<T> feature) {
        List<Feature<T>> featureList = new ArrayList<>(1);
        featureList.add(feature);

        return new FeatureCollection<>(featureList);
    }

    @SuppressWarnings("unchecked")
    public static FeatureCollection<Geometry> fromJson(String json) {
        return GeoJsonUtils.getGson().fromJson(json, FeatureCollection.class);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Geometry> FeatureCollection<T> fromJson(String json, Class<T> geometryClass) {
        return GeoJsonUtils.getGson().fromJson(json, FeatureCollection.class);
    }

    @SuppressWarnings("unchecked")
    public static FeatureCollection<Geometry> featureCollection(Geometry geometry) {
        return (FeatureCollection<Geometry>) geometry;
    }

    @Override
    public GeometryType geometryType() {
        return GeometryType.FEATURE_COLLECTION;
    }

    @Override
    public List<Feature<T>> geometries() {
        return features;
    }

    @Override
    public int size() {
        return features.size();
    }

    @Override
    public Iterator<Feature<T>> iterator() {
        return features.iterator();
    }

    @Override
    public Feature<T> get(int index) {
        return features.get(index);
    }

    @Override
    public boolean isEmpty() {
        return features.isEmpty();
    }

    public List<T> toItemList() {
        if (isEmpty()) {
            return null;
        }

        List<T> toList = new ArrayList<>();
        for(Feature<T> feature : features) {
            toList.add(feature.geometry());
        }

        return toList;
    }

    @Override
    public String toViewCoordsString() {
        StringBuilder buf = new StringBuilder();
        buf.append("├───── ").append(geometryType()).append("─────┤").append("\n");
        for (Feature<T> feature : features) {
            buf.append(feature.toViewCoordsString());
        }
        return buf.toString();
    }

    @Override
    public String toJson() {
        return GeoJsonUtils.getGson().toJson(this);
    }

    @Override
    public String toString() {
        return "FeatureCollection{"
                + "features=" + features
                + "}";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof FeatureCollection) {
            FeatureCollection that = (FeatureCollection) obj;
            return this.features.equals(that.geometries());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode *= 1000003;
        hashCode ^= features.hashCode();
        return hashCode;
    }

}
