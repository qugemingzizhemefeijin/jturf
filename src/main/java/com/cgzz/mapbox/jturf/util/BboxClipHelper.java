package com.cgzz.mapbox.jturf.util;

import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class BboxClipHelper {

    private BboxClipHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 边界裁剪<br>
     * 使用lineclip将该图形裁切到bbox。在裁剪多边形时可能导致退化边缘。<br>
     * 图形支持：POLYGON、MULTI_POLYGON、LINE、MULTI_LINE
     *
     * @param geometry 图形组件
     * @param bbox     裁剪区域
     * @return 返回裁剪后的图形，这里如果是LINE类型的话有可能会返回MultiLine，需要自己判断。
     */
    public static Geometry bboxClip(Geometry geometry, BoundingBox bbox) {
        geometry = JTurfMeta.getGeom(geometry);
        GeometryType type = geometry.geometryType();

        switch (type) {
            case LINE_STRING:
            case MULTI_LINE_STRING: {
                List<List<Point>> lines = new ArrayList<>();
                List<List<Point>> coords;
                if (type == GeometryType.LINE_STRING) {
                    coords = new ArrayList<>(1);
                    coords.add(((LineString) geometry).coordinates());
                } else {
                    coords = ((MultiLineString) geometry).coordinates();
                }

                for (List<Point> line : coords) {
                    TailClipHelper.lineclip(line, bbox, lines);
                }

                if (lines.size() == 1) {
                    return LineString.fromLngLats(lines.get(0));
                } else {
                    return MultiLineString.fromLngLats(lines);
                }
            }
            case POLYGON:
                return Polygon.fromLngLats(TailClipHelper.clipPolygon(((Polygon) geometry).coordinates(), bbox));
            case MULTI_POLYGON: {
                List<List<List<Point>>> coords = ((MultiPolygon) geometry).coordinates();
                return MultiPolygon.fromLngLats(coords.stream().map(poly -> TailClipHelper.clipPolygon(poly, bbox)).collect(Collectors.toList()));
            }
            default:
                throw new JTurfException("geometry " + type + " not supported");
        }
    }

}
