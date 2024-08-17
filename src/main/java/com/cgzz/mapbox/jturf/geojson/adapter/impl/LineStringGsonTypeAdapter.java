package com.cgzz.mapbox.jturf.geojson.adapter.impl;

import com.cgzz.mapbox.jturf.geojson.adapter.BaseGeometryTypeAdapter;
import com.cgzz.mapbox.jturf.geojson.adapter.ListOfPointCoordinatesTypeAdapter;
import com.cgzz.mapbox.jturf.shape.CoordinateContainer;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.LineString;
import com.cgzz.mapbox.jturf.shape.Point;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.List;

public final class LineStringGsonTypeAdapter extends BaseGeometryTypeAdapter<LineString, List<Point>> {

    public LineStringGsonTypeAdapter(Gson gson) {
        super(gson, new ListOfPointCoordinatesTypeAdapter());
    }

    @Override
    public void write(JsonWriter jsonWriter, LineString object) throws IOException {
        writeCoordinateContainer(jsonWriter, object);
    }

    @Override
    public LineString read(JsonReader jsonReader) throws IOException {
        return (LineString) readCoordinateContainer(jsonReader);
    }

    @Override
    public CoordinateContainer<List<Point>> createCoordinateContainer(GeometryType type,
                                                               List<Point> coordinates) {
        return LineString.fromLngLats(coordinates);
    }

}
