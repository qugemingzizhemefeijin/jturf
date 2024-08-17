package com.cgzz.mapbox.jturf.geojson.adapter.factory;

import com.cgzz.mapbox.jturf.geojson.adapter.impl.*;
import com.cgzz.mapbox.jturf.shape.*;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

public final class GeoJsonAdapterFactoryImpl extends GeoJsonAdapterFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<?> rawType = type.getRawType();
        if (BoundingBox.class.isAssignableFrom(rawType)) {
            return (TypeAdapter<T>) new BoundingBoxTypeAdapter();
        } else if (Feature.class.isAssignableFrom(rawType)) {
            return (TypeAdapter<T>) new FeatureGsonTypeAdapter(gson);
        } else if (FeatureCollection.class.isAssignableFrom(rawType)) {
            return (TypeAdapter<T>) new FeatureCollectionGsonTypeAdapter(gson);
        } else if (GeometryCollection.class.isAssignableFrom(rawType)) {
            return (TypeAdapter<T>) new GeometryCollectionGsonTypeAdapter(gson);
        } else if (LineString.class.isAssignableFrom(rawType)) {
            return (TypeAdapter<T>) new LineStringGsonTypeAdapter(gson);
        } else if (MultiLineString.class.isAssignableFrom(rawType)) {
            return (TypeAdapter<T>) new MultiLineStringGsonTypeAdapter(gson);
        } else if (MultiPoint.class.isAssignableFrom(rawType)) {
            return (TypeAdapter<T>) new MultiPointGsonTypeAdapter(gson);
        } else if (MultiPolygon.class.isAssignableFrom(rawType)) {
            return (TypeAdapter<T>) new MultiPolygonGsonTypeAdapter(gson);
        } else if (Polygon.class.isAssignableFrom(rawType)) {
            return (TypeAdapter<T>) new PolygonGsonTypeAdapter(gson);
        } else if (Point.class.isAssignableFrom(rawType)) {
            return (TypeAdapter<T>) new PointGsonTypeAdapter(gson);
        }
        return null;
    }

}
