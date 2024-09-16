package com.cgzz.mapbox.jturf.geojson.adapter.impl;

import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public final class FeatureGsonTypeAdapter extends TypeAdapter<Feature<Geometry>> {

    private volatile TypeAdapter<String> stringTypeAdapter;
    private volatile TypeAdapter<Geometry> geometryTypeAdapter;
    private volatile TypeAdapter<JsonObject> jsonObjectTypeAdapter;
    private final Gson gson;

    public FeatureGsonTypeAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter jsonWriter, Feature object) throws IOException {
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
        jsonWriter.name("id");
        if (object.id() == null) {
            jsonWriter.nullValue();
        } else {
            TypeAdapter<String> stringTypeAdapter = this.stringTypeAdapter;
            if (stringTypeAdapter == null) {
                stringTypeAdapter = gson.getAdapter(String.class);
                this.stringTypeAdapter = stringTypeAdapter;
            }
            stringTypeAdapter.write(jsonWriter, object.id());
        }
        jsonWriter.name("geometry");
        if (object.geometry() == null) {
            jsonWriter.nullValue();
        } else {
            TypeAdapter<Geometry> geometryTypeAdapter = this.geometryTypeAdapter;
            if (geometryTypeAdapter == null) {
                geometryTypeAdapter = gson.getAdapter(Geometry.class);
                this.geometryTypeAdapter = geometryTypeAdapter;
            }
            geometryTypeAdapter.write(jsonWriter, object.geometry());
        }
        jsonWriter.name("properties");
        if (object.properties() == null) {
            jsonWriter.nullValue();
        } else {
            TypeAdapter<JsonObject> jsonObjectTypeAdapter = this.jsonObjectTypeAdapter;
            if (jsonObjectTypeAdapter == null) {
                jsonObjectTypeAdapter = gson.getAdapter(JsonObject.class);
                this.jsonObjectTypeAdapter = jsonObjectTypeAdapter;
            }
            jsonObjectTypeAdapter.write(jsonWriter, object.properties());
        }
        jsonWriter.endObject();
    }

    @Override
    public Feature<Geometry> read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }
        jsonReader.beginObject();
        String id = null;
        Geometry geometry = null;
        JsonObject properties = null;
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                continue;
            }
            switch (name) {
                case "id":
                    TypeAdapter<String> strTypeAdapter = this.stringTypeAdapter;
                    if (strTypeAdapter == null) {
                        strTypeAdapter = gson.getAdapter(String.class);
                        this.stringTypeAdapter = strTypeAdapter;
                    }
                    id = strTypeAdapter.read(jsonReader);
                    break;

                case "geometry":
                    TypeAdapter<Geometry> geometryTypeAdapter = this.geometryTypeAdapter;
                    if (geometryTypeAdapter == null) {
                        geometryTypeAdapter = gson.getAdapter(Geometry.class);
                        this.geometryTypeAdapter = geometryTypeAdapter;
                    }
                    geometry = geometryTypeAdapter.read(jsonReader);
                    break;

                case "properties":
                    TypeAdapter<JsonObject> jsonObjectTypeAdapter = this.jsonObjectTypeAdapter;
                    if (jsonObjectTypeAdapter == null) {
                        jsonObjectTypeAdapter = gson.getAdapter(JsonObject.class);
                        this.jsonObjectTypeAdapter = jsonObjectTypeAdapter;
                    }
                    properties = jsonObjectTypeAdapter.read(jsonReader);
                    break;

                default:
                    jsonReader.skipValue();

            }
        }
        jsonReader.endObject();
        return Feature.fromGeometry(geometry, properties, id);
    }

}
