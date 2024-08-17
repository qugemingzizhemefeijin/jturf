package com.cgzz.mapbox.jturf.geojson.adapter;

import com.cgzz.mapbox.jturf.exception.GeoJsonException;
import com.cgzz.mapbox.jturf.shape.Point;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListOfListOfPointCoordinatesTypeAdapter extends BaseCoordinatesTypeAdapter<List<List<Point>>> {

    @Override
    public void write(JsonWriter out, List<List<Point>> points) throws IOException {

        if (points == null) {
            out.nullValue();
            return;
        }

        out.beginArray();

        for (List<Point> listOfPoints : points) {

            out.beginArray();

            for (Point point : listOfPoints) {
                writePoint(out, point);
            }
            out.endArray();
        }

        out.endArray();
    }

    @Override
    public List<List<Point>> read(JsonReader in) throws IOException {

        if (in.peek() == JsonToken.NULL) {
            throw new NullPointerException();
        }

        if (in.peek() == JsonToken.BEGIN_ARRAY) {

            in.beginArray();
            List<List<Point>> points = new ArrayList<>();

            while (in.peek() == JsonToken.BEGIN_ARRAY) {

                in.beginArray();
                List<Point> listOfPoints = new ArrayList<>();

                while (in.peek() == JsonToken.BEGIN_ARRAY) {
                    listOfPoints.add(readPoint(in));
                }
                in.endArray();
                points.add(listOfPoints);
            }
            in.endArray();

            return points;
        }

        throw new GeoJsonException("coordinates should be array of array of array of double");
    }

}
