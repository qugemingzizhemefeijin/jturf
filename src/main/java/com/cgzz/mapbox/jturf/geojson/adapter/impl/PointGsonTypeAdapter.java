package com.cgzz.mapbox.jturf.geojson.adapter.impl;

import com.cgzz.mapbox.jturf.geojson.GeoJsonUtils;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class PointGsonTypeAdapter extends TypeAdapter<Point> {

    private volatile TypeAdapter<String> stringAdapter;

    private final Gson gson;

    public PointGsonTypeAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter jsonWriter, Point point) throws IOException {
        if (point == null) {
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.beginObject();
        jsonWriter.name("type");

        TypeAdapter<String> stringAdapter = this.stringAdapter;
        if (stringAdapter == null) {
            stringAdapter = gson.getAdapter(String.class);
            this.stringAdapter = stringAdapter;
        }
        stringAdapter.write(jsonWriter, point.geometryType().getName());

        jsonWriter.name("coordinates");
        jsonWriter.beginArray();
        jsonWriter.value(GeoJsonUtils.trim(point.longitude()));
        jsonWriter.value(GeoJsonUtils.trim(point.latitude()));

        // Includes altitude
        if (point.hasAltitude()) {
            jsonWriter.value(point.altitude());
        }
        jsonWriter.endArray();

        jsonWriter.endObject();
    }

    @Override
    public Point read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }

        jsonReader.beginObject();
        Point point = null;

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                continue;
            }
            switch (name) {

                case "coordinates":
                    point = readPointList(jsonReader);
                    break;

                default:
                    jsonReader.skipValue();

            }
        }
        jsonReader.endObject();

        return point;
    }

    protected Point readPointList(JsonReader in) throws IOException {
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
            return Point.fromLngLat(coordinates.get(0), coordinates.get(1), coordinates.get(2));
        }
        return Point.fromLngLat(coordinates.get(0), coordinates.get(1));
    }


}
