package com.cgzz.mapbox.jturf.geojson.adapter;

import com.cgzz.mapbox.jturf.exception.GeoJsonException;
import com.cgzz.mapbox.jturf.shape.CoordinateContainer;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public abstract class BaseGeometryTypeAdapter<G, T> extends TypeAdapter<G> {

    private volatile TypeAdapter<String> stringAdapter;
    private volatile TypeAdapter<T> coordinatesAdapter;

    private final Gson gson;

    public BaseGeometryTypeAdapter(Gson gson, TypeAdapter<T> coordinatesAdapter) {
        this.gson = gson;
        this.coordinatesAdapter = coordinatesAdapter;
    }

    public void writeCoordinateContainer(JsonWriter jsonWriter, CoordinateContainer<T> object)
            throws IOException {
        if (object == null) {
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
        stringAdapter.write(jsonWriter, object.geometryType().name);

        jsonWriter.name("coordinates");
        if (object.coordinates() == null) {
            jsonWriter.nullValue();
        } else {
            TypeAdapter<T> coordinatesAdapter = this.coordinatesAdapter;
            if (coordinatesAdapter == null) {
                throw new GeoJsonException("Coordinates type adapter is null");
            }
            coordinatesAdapter.write(jsonWriter, object.coordinates());
        }
        jsonWriter.endObject();
    }

    public CoordinateContainer<T> readCoordinateContainer(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }

        jsonReader.beginObject();
        GeometryType type = null;
        T coordinates = null;

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                continue;
            }
            switch (name) {
                case "type":
                    TypeAdapter<String> stringAdapter = this.stringAdapter;
                    if (stringAdapter == null) {
                        stringAdapter = gson.getAdapter(String.class);
                        this.stringAdapter = stringAdapter;
                    }
                    type = GeometryType.getByName(stringAdapter.read(jsonReader));
                    break;

                case "coordinates":
                    TypeAdapter<T> coordinatesAdapter = this.coordinatesAdapter;
                    if (coordinatesAdapter == null) {
                        throw new GeoJsonException("Coordinates type adapter is null");
                    }
                    coordinates = coordinatesAdapter.read(jsonReader);
                    break;

                default:
                    jsonReader.skipValue();

            }
        }
        jsonReader.endObject();

        return createCoordinateContainer(type, coordinates);
    }

    public abstract CoordinateContainer<T> createCoordinateContainer(GeometryType type, T coordinates);

}
