package com.cgzz.mapbox.jturf.geojson.adapter.impl;

import com.cgzz.mapbox.jturf.geojson.adapter.BaseGeometryTypeAdapter;
import com.cgzz.mapbox.jturf.geojson.adapter.ListOfListOfPointCoordinatesTypeAdapter;
import com.cgzz.mapbox.jturf.shape.CoordinateContainer;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.Point;
import com.cgzz.mapbox.jturf.shape.Polygon;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.List;

public final class PolygonGsonTypeAdapter extends BaseGeometryTypeAdapter<Polygon, List<List<Point>>> {

    public PolygonGsonTypeAdapter(Gson gson) {
        super(gson, new ListOfListOfPointCoordinatesTypeAdapter());
    }

    @Override
    public void write(JsonWriter jsonWriter, Polygon object) throws IOException {
        writeCoordinateContainer(jsonWriter, object);
    }

    @Override
    public Polygon read(JsonReader jsonReader) throws IOException {
        return (Polygon) readCoordinateContainer(jsonReader);
    }

    @Override
    public CoordinateContainer<List<List<Point>>> createCoordinateContainer(GeometryType type,
                                                                     List<List<Point>> coords) {
        return Polygon.fromLngLats(coords);
    }

}
