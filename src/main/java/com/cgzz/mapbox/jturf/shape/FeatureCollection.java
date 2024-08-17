package com.cgzz.mapbox.jturf.shape;

import com.cgzz.mapbox.jturf.geojson.GeoJsonUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class FeatureCollection implements Geometry {

    private final List<Feature> features;

    FeatureCollection(List<Feature> features) {
        this.features = features;
    }

    public static FeatureCollection fromFeatures(Feature[] features) {
        return new FeatureCollection(Arrays.asList(features));
    }

    public static FeatureCollection fromFeatures(List<Feature> features) {
        return new FeatureCollection(features);
    }

    public static FeatureCollection fromFeature(Feature feature) {
        List<Feature> featureList = new ArrayList<>(1);
        featureList.add(feature);

        return new FeatureCollection(featureList);
    }

    public static FeatureCollection fromJson(String json) {
        return GeoJsonUtils.getGson().fromJson(json, FeatureCollection.class);
    }

    @Override
    public GeometryType geometryType() {
        return GeometryType.FEATURE_COLLECTION;
    }

    public List<Feature> features() {
        return features;
    }

    @Override
    public String toViewCoordsString() {
        StringBuilder buf = new StringBuilder();
        buf.append("├───── ").append(geometryType()).append("─────┤").append("\n");
        for (Feature feature : features) {
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof FeatureCollection) {
            FeatureCollection that = (FeatureCollection) obj;
            return (this.features == null)
                    ? (that.features() == null) : this.features.equals(that.features());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode *= 1000003;
        hashCode ^= (features == null) ? 0 : features.hashCode();
        return hashCode;
    }

}
