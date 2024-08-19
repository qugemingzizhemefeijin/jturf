package com.cgzz.mapbox.jturf.geojson.adapter.impl;

import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.GeometryCollection;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.List;

public final class GeometryCollectionGsonTypeAdapter extends TypeAdapter<GeometryCollection> {

    private volatile TypeAdapter<String> stringTypeAdapter;
    private volatile TypeAdapter<List<Geometry>> listGeometryAdapter;
    private final Gson gson;

    public GeometryCollectionGsonTypeAdapter(Gson gson) {
        this.gson = gson;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void write(JsonWriter jsonWriter, GeometryCollection object) throws IOException {
        if (object == null) {
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.beginObject();
        jsonWriter.name("type");
        if (object.geometryType() == null) {
            jsonWriter.nullValue();
        } else {
            TypeAdapter<String> stringTypeAdapter = this.stringTypeAdapter;
            if (stringTypeAdapter == null) {
                stringTypeAdapter = gson.getAdapter(String.class);
                this.stringTypeAdapter = stringTypeAdapter;
            }
            stringTypeAdapter.write(jsonWriter, object.geometryType().getName());
        }
        jsonWriter.name("geometries");
        if (object.geometries() == null) {
            jsonWriter.nullValue();
        } else {
            TypeAdapter<List<Geometry>> listGeometryAdapter = this.listGeometryAdapter;
            if (listGeometryAdapter == null) {
                TypeToken typeToken = TypeToken.getParameterized(List.class, Geometry.class);
                listGeometryAdapter = (TypeAdapter<List<Geometry>>) gson.getAdapter(typeToken);
                this.listGeometryAdapter = listGeometryAdapter;
            }
            listGeometryAdapter.write(jsonWriter, object.geometries());
        }
        jsonWriter.endObject();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public GeometryCollection read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }
        jsonReader.beginObject();
        List<Geometry> geometries = null;
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                continue;
            }
            switch (name) {

                case "geometries":
                    TypeAdapter<List<Geometry>> listGeometryAdapter = this.listGeometryAdapter;
                    if (listGeometryAdapter == null) {
                        TypeToken typeToken = TypeToken.getParameterized(List.class, Geometry.class);
                        listGeometryAdapter = (TypeAdapter<List<Geometry>>) gson.getAdapter(typeToken);
                        this.listGeometryAdapter = listGeometryAdapter;
                    }
                    geometries = listGeometryAdapter.read(jsonReader);
                    break;

                default:
                    jsonReader.skipValue();

            }
        }
        jsonReader.endObject();

        return GeometryCollection.fromGeometries(geometries);
    }

}
