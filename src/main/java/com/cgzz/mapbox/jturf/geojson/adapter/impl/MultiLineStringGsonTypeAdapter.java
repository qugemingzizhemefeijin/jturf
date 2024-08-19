package com.cgzz.mapbox.jturf.geojson.adapter.impl;

import com.cgzz.mapbox.jturf.geojson.adapter.BaseGeometryTypeAdapter;
import com.cgzz.mapbox.jturf.geojson.adapter.ListOfListOfPointCoordinatesTypeAdapter;
import com.cgzz.mapbox.jturf.shape.CoordinateContainer;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.MultiLineString;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.List;

public final class MultiLineStringGsonTypeAdapter extends BaseGeometryTypeAdapter<MultiLineString, List<List<Point>>> {

    public MultiLineStringGsonTypeAdapter(Gson gson) {
        super(gson, new ListOfListOfPointCoordinatesTypeAdapter());
    }

    @Override
    public void write(JsonWriter jsonWriter, MultiLineString object) throws IOException {
        writeCoordinateContainer(jsonWriter, object);
    }

    @Override
    public MultiLineString read(JsonReader jsonReader) throws IOException {
        return (MultiLineString) readCoordinateContainer(jsonReader);
    }

    @Override
    public CoordinateContainer<List<List<Point>>> createCoordinateContainer(GeometryType type,
                                                                     List<List<Point>> coords) {
        return MultiLineString.fromLngLats(coords);
    }

}
