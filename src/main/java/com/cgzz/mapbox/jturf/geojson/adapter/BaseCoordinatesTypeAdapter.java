package com.cgzz.mapbox.jturf.geojson.adapter;

import com.cgzz.mapbox.jturf.JTurfHelper;
import com.cgzz.mapbox.jturf.exception.GeoJsonException;
import com.cgzz.mapbox.jturf.geojson.GeoJsonUtils;
import com.cgzz.mapbox.jturf.shape.Point;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseCoordinatesTypeAdapter<T> extends TypeAdapter<T> {

    protected void writePoint(JsonWriter out, Point point) throws IOException {
        if (point == null) {
            return;
        }
        writePointList(out, point);
    }

    protected Point readPoint(JsonReader in) throws IOException {
        List<Double> coordinates = readPointList(in);
        if (coordinates != null && coordinates.size() > 1) {
            return Point.fromLngLat(coordinates);
        }
        throw new GeoJsonException(" Point coordinates should be non-null double array");
    }


    protected void writePointList(JsonWriter out, Point point) throws IOException {
        out.beginArray();

        out.value(GeoJsonUtils.trim(point.longitude()));
        out.value(GeoJsonUtils.trim(point.latitude()));

        // Includes altitude
        if (point.hasAltitude()) {
            out.value(point.altitude());
        }
        out.endArray();
    }

    protected List<Double> readPointList(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            throw new NullPointerException();
        }

        List<Double> coordinates = new ArrayList<>(3);
        in.beginArray();
        while (in.hasNext()) {
            coordinates.add(in.nextDouble());
        }
        in.endArray();

        if (coordinates.size() > 2) {
            return JTurfHelper.lonLatAltToList(coordinates.get(0), coordinates.get(1), coordinates.get(2));
        }
        return JTurfHelper.lonLatToList(coordinates.get(0), coordinates.get(1));
    }

}
