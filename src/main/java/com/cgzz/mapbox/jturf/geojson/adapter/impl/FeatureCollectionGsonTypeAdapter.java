package com.cgzz.mapbox.jturf.geojson.adapter.impl;

import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.FeatureCollection;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.List;

public final class FeatureCollectionGsonTypeAdapter extends TypeAdapter<FeatureCollection> {

    private volatile TypeAdapter<String> stringAdapter;
    private volatile TypeAdapter<List<Feature>> listFeatureAdapter;
    private final Gson gson;

    public FeatureCollectionGsonTypeAdapter(Gson gson) {
        this.gson = gson;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void write(JsonWriter jsonWriter, FeatureCollection object) throws IOException {
        if (object == null) {
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.beginObject();
        jsonWriter.name("type");
        if (object.geometryType() == null) {
            jsonWriter.nullValue();
        } else {
            TypeAdapter<String> stringAdapter = this.stringAdapter;
            if (stringAdapter == null) {
                stringAdapter = gson.getAdapter(String.class);
                this.stringAdapter = stringAdapter;
            }
            stringAdapter.write(jsonWriter, object.geometryType().getName());
        }
        jsonWriter.name("features");
        if (object.geometries() == null) {
            jsonWriter.nullValue();
        } else {
            TypeAdapter<List<Feature>> listFeatureAdapter = this.listFeatureAdapter;
            if (listFeatureAdapter == null) {
                TypeToken typeToken = TypeToken.getParameterized(List.class, Feature.class);
                listFeatureAdapter = (TypeAdapter<List<Feature>>) gson.getAdapter(typeToken);
                this.listFeatureAdapter = listFeatureAdapter;
            }
            listFeatureAdapter.write(jsonWriter, object.geometries());
        }
        jsonWriter.endObject();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public FeatureCollection read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }
        jsonReader.beginObject();
        List<Feature> features = null;
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                continue;
            }
            switch (name) {

                case "features":
                    TypeAdapter<List<Feature>> listFeatureAdapter = this.listFeatureAdapter;
                    if (listFeatureAdapter == null) {
                        TypeToken typeToken = TypeToken.getParameterized(List.class, Feature.class);
                        listFeatureAdapter = (TypeAdapter<List<Feature>>) gson.getAdapter(typeToken);
                        this.listFeatureAdapter = listFeatureAdapter;
                    }
                    features = listFeatureAdapter.read(jsonReader);
                    break;

                default:
                    jsonReader.skipValue();

            }
        }
        jsonReader.endObject();

        return FeatureCollection.fromFeatures(features);
    }

}
