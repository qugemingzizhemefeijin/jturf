package com.cgzz.mapbox.jturf.util.misc;

import com.cgzz.mapbox.jturf.JTurfMisc;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.LineString;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public final class LineSliceHelper {

    private LineSliceHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 根据点截取多线段<br>
     * <p>
     * 获取一条线、起点和终点，并返回这些点之间的线段。起止点不需要正好落在直线上
     *
     * @param startPt     起点
     * @param stopPt      终点
     * @param line        被截取的线段
     * @param properties  属性信息，一般继承自line
     * @return 切片线
     */
    public static Feature lineSlice(Point startPt, Point stopPt, LineString line, JsonObject properties) {
        List<Point> coords = line.coordinates();

        Feature startVertex = JTurfMisc.nearestPointOnLine(line, startPt);
        Feature stopVertex = JTurfMisc.nearestPointOnLine(line, stopPt);

        Feature[] ends;
        if (startVertex.getPropertyAsNumber("index").intValue() <= stopVertex.getPropertyAsNumber("index").intValue()) {
            ends = new Feature[]{startVertex, stopVertex};
        } else {
            ends = new Feature[]{stopVertex, startVertex};
        }

        List<Point> clipCoords = new ArrayList<>();
        clipCoords.add((Point)ends[0].geometry());
        for (int i = ends[0].getPropertyAsNumber("index").intValue() + 1; i < ends[1].getPropertyAsNumber("index").intValue(); i++) {
            clipCoords.add(coords.get(i));
        }
        clipCoords.add((Point)ends[1].geometry());

        return Feature.fromGeometry(LineString.fromLngLats(clipCoords), properties);
    }

}
