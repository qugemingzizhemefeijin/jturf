package com.cgzz.mapbox.jturf.geojson.adapter;

import com.cgzz.mapbox.jturf.exception.GeoJsonException;
import com.cgzz.mapbox.jturf.shape.Point;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListOfPointCoordinatesTypeAdapter extends BaseCoordinatesTypeAdapter<List<Point>> {

    @Override
    public void write(JsonWriter out, List<Point> points) throws IOException {

        if (points == null) {
            out.nullValue();
            return;
        }

        out.beginArray();

        for (Point point : points) {
            writePoint(out, point);
        }

        out.endArray();
    }

    @Override
    public List<Point> read(JsonReader in) throws IOException {

        if (in.peek() == JsonToken.NULL) {
            throw new NullPointerException();
        }

        if (in.peek() == JsonToken.BEGIN_ARRAY) {
            List<Point> points = new ArrayList<>();
            in.beginArray();

            while (in.peek() == JsonToken.BEGIN_ARRAY) {
                points.add(readPoint(in));
            }
            in.endArray();

            return points;
        }

        throw new GeoJsonException("coordinates should be non-null array of array of double");
    }

}
