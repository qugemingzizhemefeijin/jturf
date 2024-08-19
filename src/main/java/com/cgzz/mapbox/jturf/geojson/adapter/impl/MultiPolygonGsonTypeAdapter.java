package com.cgzz.mapbox.jturf.geojson.adapter.impl;

import com.cgzz.mapbox.jturf.geojson.adapter.BaseGeometryTypeAdapter;
import com.cgzz.mapbox.jturf.geojson.adapter.ListofListofListOfPointCoordinatesTypeAdapter;
import com.cgzz.mapbox.jturf.shape.CoordinateContainer;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.MultiPolygon;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.List;

public final class MultiPolygonGsonTypeAdapter extends BaseGeometryTypeAdapter<MultiPolygon, List<List<List<Point>>>> {

    public MultiPolygonGsonTypeAdapter(Gson gson) {
        super(gson, new ListofListofListOfPointCoordinatesTypeAdapter());
    }

    @Override
    public void write(JsonWriter jsonWriter, MultiPolygon object) throws IOException {
        writeCoordinateContainer(jsonWriter, object);
    }

    @Override
    public MultiPolygon read(JsonReader jsonReader) throws IOException {
        return (MultiPolygon) readCoordinateContainer(jsonReader);
    }

    @Override
    public CoordinateContainer<List<List<List<Point>>>> createCoordinateContainer(GeometryType type,
                                                                           List<List<List<Point>>> coords) {
        return MultiPolygon.fromLngLats(coords);
    }

}
