package com.cgzz.mapbox.jturf.geojson.adapter.impl;

import com.cgzz.mapbox.jturf.geojson.adapter.BaseGeometryTypeAdapter;
import com.cgzz.mapbox.jturf.geojson.adapter.ListOfPointCoordinatesTypeAdapter;
import com.cgzz.mapbox.jturf.shape.CoordinateContainer;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.MultiPoint;
import com.cgzz.mapbox.jturf.shape.Point;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.List;

public final class MultiPointGsonTypeAdapter extends BaseGeometryTypeAdapter<MultiPoint, List<Point>> {

    public MultiPointGsonTypeAdapter(Gson gson) {
        super(gson, new ListOfPointCoordinatesTypeAdapter());
    }

    @Override
    public void write(JsonWriter jsonWriter, MultiPoint object) throws IOException {
        writeCoordinateContainer(jsonWriter, object);
    }

    @Override
    public MultiPoint read(JsonReader jsonReader) throws IOException {
        return (MultiPoint) readCoordinateContainer(jsonReader);
    }

    @Override
    public CoordinateContainer<List<Point>> createCoordinateContainer(GeometryType type,
                                                               List<Point> coordinates) {
        return MultiPoint.fromLngLats(coordinates);
    }

}
