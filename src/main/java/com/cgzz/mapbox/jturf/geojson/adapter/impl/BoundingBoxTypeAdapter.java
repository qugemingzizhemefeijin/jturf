package com.cgzz.mapbox.jturf.geojson.adapter.impl;

import com.cgzz.mapbox.jturf.exception.GeoJsonException;
import com.cgzz.mapbox.jturf.geojson.GeoJsonUtils;
import com.cgzz.mapbox.jturf.shape.impl.BoundingBox;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class BoundingBoxTypeAdapter extends TypeAdapter<BoundingBox> {

    @Override
    public void write(JsonWriter out, BoundingBox value) throws IOException {

        if (value == null) {
            out.nullValue();
            return;
        }

        out.beginArray();

        // Southwest
        Point point = value.southwest();

        out.value(GeoJsonUtils.trim(point.longitude()));
        out.value(GeoJsonUtils.trim(point.latitude()));
        if (point.hasAltitude()) {
            out.value(point.altitude());
        }

        // Northeast
        point = value.northeast();
        out.value(GeoJsonUtils.trim(point.longitude()));
        out.value(GeoJsonUtils.trim(point.latitude()));
        if (point.hasAltitude()) {
            out.value(point.altitude());
        }

        out.endArray();
    }

    @Override
    public BoundingBox read(JsonReader in) throws IOException {
        List<Double> rawCoordinates = new ArrayList<>();
        in.beginArray();
        while (in.hasNext()) {
            rawCoordinates.add(in.nextDouble());
        }
        in.endArray();

        if (rawCoordinates.size() == 6) {
            return BoundingBox.fromLngLats(
                    rawCoordinates.get(0),
                    rawCoordinates.get(1),
                    rawCoordinates.get(2),
                    rawCoordinates.get(3),
                    rawCoordinates.get(4),
                    rawCoordinates.get(5));
        }
        if (rawCoordinates.size() == 4) {
            return BoundingBox.fromLngLats(
                    rawCoordinates.get(0),
                    rawCoordinates.get(1),
                    rawCoordinates.get(2),
                    rawCoordinates.get(3));
        } else {
            throw new GeoJsonException("The value of the bbox member MUST be an array of length 2*n where"
                    + " n is the number of dimensions represented in the contained geometries,"
                    + "with all axes of the most southwesterly point followed "
                    + " by all axes of the more northeasterly point. The "
                    + "axes order of a bbox follows the axes order of geometries.");
        }
    }

}
