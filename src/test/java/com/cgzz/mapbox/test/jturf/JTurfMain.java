package com.cgzz.mapbox.test.jturf;

import com.cgzz.mapbox.jturf.JTurfFeatureConversion;
import com.cgzz.mapbox.jturf.shape.Polygon;

public class JTurfMain {

    public static void main(String[] args) {
        // x, n, e, n
        // 0, 1, 2, 3
        // [0, 1], [2, 1], [e, n], [x, n]

        Polygon polygon = Polygon.fromLngLats(new double[]{-81, 41, -88, 36, -84, 31, -80, 33, -77, 39, -81, 41});
        System.out.println(JTurfFeatureConversion.explode(polygon));

//        MultiPolygon multiPolygon = MultiPolygon.fromLngLats(new double[][][]{
//                new double[][] {
//                        new double[]{102.0, 2.0}, new double[]{103.0, 2.0}, new double[]{103.0, 3.0}, new double[]{102.0, 3.0}, new double[]{102.0, 2.0}
//                },
//                new double[][] {
//                        new double[]{100.0, 0.0}, new double[]{101.0, 0.0}, new double[]{101.0, 1.0}, new double[]{100.0, 1.0}, new double[]{100.0, 0.0}
//                }
//        });
//
//        List<Geometry> geometryList = JTurfFeatureConversion.flatten(multiPolygon);
//        for (Geometry g : geometryList) {
//            System.out.println(g);
//        }

//        Point point = Point.fromLngLat(-90.548630, 14.616599);
//        Geometry g = JTurfTransformation.buffer(point, 500, Units.MILES);
//        System.out.println(g.toViewCoordsString());

//        Polygon poly1 = Polygon.fromLngLats(new double[]{-122.801742, 45.48565, -122.801742, 45.60491, -122.584762, 45.60491, -122.584762, 45.48565, -122.801742, 45.48565});
//        Polygon poly2 = Polygon.fromLngLats(new double[]{-122.520217, 45.535693, -122.64038, 45.553967, -122.720031, 45.526554, -122.669906, 45.507309, -122.723464, 45.446643, -122.532577, 45.408574, -122.487258, 45.477466, -122.520217, 45.535693});
//        Geometry intersect = TaleTransformation.intersect(poly1, poly2);
//        System.out.println(intersect.toViewCoordsString());

//        Polygon poly1 = Polygon.fromLngLats(new double[]{128, -26,141, -26,141, -21,128, -21,128, -26});
//        Polygon poly2 = Polygon.fromLngLats(new double[]{126, -28,140, -28,140, -20,126, -20,126, -28});
//        Geometry difference = TaleTransformation.difference(poly1, poly2);
//        System.out.println(difference.toViewCoordsString());

//        Polygon poly1 = Polygon.fromLngLats(new double[]{-82.574787, 35.594087,-82.574787, 35.615581,-82.545261, 35.615581,-82.545261, 35.594087,-82.574787, 35.594087});
//        Polygon poly2 = Polygon.fromLngLats(new double[]{-82.560024, 35.585153,-82.560024, 35.602602,-82.52964, 35.602602,-82.52964, 35.585153,-82.560024, 35.585153});
//
//        Geometry union = TaleTransformation.union(poly1, poly2);
//        System.out.println(union.toViewCoordsString());

//        Polygon polygon = Polygon.fromLngLats(new double[]{-70.603637, -33.399918,-70.614624, -33.395332,-70.639343, -33.392466,-70.659942, -33.394759,-70.683975, -33.404504,-70.697021, -33.419406,-70.701141, -33.434306,-70.700454, -33.446339,-70.694274, -33.458369,-70.682601, -33.465816,-70.668869, -33.472117,-70.646209, -33.473835,-70.624923, -33.472117,-70.609817, -33.468107,-70.595397, -33.458369,-70.587158, -33.442901,-70.587158, -33.426283,-70.590591, -33.414248,-70.594711, -33.406224,-70.603637, -33.399918});
//        Polygon newPolygon = TaleTransformation.simplify(polygon, 0.01);
//        System.out.println(newPolygon.toViewCoordsString());

//        Polygon polygon = Polygon.fromLngLats(new double[]{11, 0, 22, 4, 31, 0, 31, 11, 21, 15, 11, 11, 11, 0});
//        List<Polygon> ss = TaleTransformation.tesselate(polygon);
//        System.out.println("ss == " + ss.size());
//        for (Polygon s : ss) {
//            for (Point p : s.coordinates()) {
//                System.out.println("[" + p.getX()+","+p.getY() + "]");
//            }
//            System.out.println();
//        }
//
//        Line line = Line.fromLngLats(new double[]{-83, 30, -84, 36, -78, 41});
//        System.out.println(TaleTransformation.lineOffset(line, 2, Units.MILES));

//        Polygon polygon = Polygon.fromLngLats(new double[]{0, 29, 3.5, 29, 2.5, 32, 0, 29});
//        System.out.println(TaleTransformation.transformRotate(polygon, 10, Point.fromLngLat(0, 25)));;

//        Polygon polygon = Polygon.fromLngLats(new double[]{0, 29, 3.5, 29, 2.5, 32, 0, 29});
//        System.out.println(TaleMeasurement.centroid(polygon));
//        System.out.println(TaleTransformation.transformScale(polygon, 3));


//        Point start = Point.fromLngLat(-122, 48);
//        Point end = Point.fromLngLat(-77, 39);
//
//        Geometry g = TaleMeasurement.greatCircle(start, end);
//        for (Point p : Line.line(g).coordinates()) {
//            System.out.println(p.getX()+","+p.getY());
//        }

//        Polygon polygon = Polygon.fromLngLats(new double[]{116.363162,39.952888,116.439913,39.955543,116.44365,39.908183,116.451124,39.904862,116.451412,39.876961,116.357126,39.87386,116.355401,39.903755,116.364312,39.905526,116.363162,39.952888,116.388387,39.928824,116.412533,39.930318,116.413324,39.907353,116.390543,39.906689,116.388387,39.928824});
//        System.out.println(TaleMeasurement.centroid(polygon));
//
//        Point centroid = Point.fromLngLat(116.39637320000001D, 39.9192908);
//        Point rightPoint = TaleMeasurement.destination(centroid, 5, -90);
//        System.out.println(rightPoint);
//
//        Line line = Line.fromLngLats(Point.fromLngLat(116.337743482095, 39.9192760365798), Point.fromLngLat(116.45500291790505, 39.9192760365798));
//

//        Point from = Point.fromLngLat(-75.343, 39.984);
//        Point to = Point.fromLngLat(-75.534, 39.123);
//
//        System.out.println(TaleMeasurement.rhumbDistance(from, to, Units.MILES));

//        Polygon polygon = TaleRandom.randomPolygon(new double[]{116.293885D, 39.839408D, 116.464635D, 39.982417D});
//        System.out.println(polygon);
//
//        int i = 0;
//        for (Point p : polygon.coordinates()) {
//            if (i > 0) {
//                System.out.print(",");
//            }
//            System.out.print("new BMap.Point("+p.getX()+","+p.getY()+")");
//            i++;
//        }

        //System.out.println(TaleRandom.randomLine());

//        List<Point> points = TaleRandom.randomPoint(100, new double[]{-80, 30, -60, 60});
//        List<Point> sample = TaleData.sample(points, 5);
//        System.out.println(sample);

//        Point point = Point.fromLngLat(1, 2);
//        Line line = Line.fromLngLats(new double[]{1, 1, 1, 2, 1, 3, 1, 4});
//        System.out.println(TaleBooleans.booleanIntersects(line, point));

//        Line line = Line.fromLngLats(new double[]{1, 1, 1, 2, 1, 3, 1, 4});
//        System.out.println(TaleBooleans.booleanValid(line));

//        Line line = Line.fromLngLats(new double[]{1, 1, 1, 2, 1, 3, 1, 4});
//        Point point = Point.fromLngLat(1, 2);
//        System.out.println(TaleBooleans.booleanContains(line, point));

//        Point point = Point.fromLngLat(2, 2);
//        Line line = Line.fromLngLats(new double[]{1, 1, 1, 2, 1, 3, 1, 4});
//
//        System.out.println(TaleBooleans.booleanDisjoint(point, line));

//        Line line1 = Line.fromLngLats(new double[]{-2, 2, 4, 2});
//        Line line2 = Line.fromLngLats(new double[]{1, 1, 1, 2, 1, 3, 1, 4});
//        System.out.println(TaleBooleans.booleanCrosses(line1, line2));

//        Polygon polygon = Polygon.fromLngLats(new double[]{125, -30, 145, -30, 145, -20, 125, -20, 125, -30});
//        System.out.println(TaleFeatureConversion.polygonToLine(polygon));

//        Line line = Line.fromLngLats(new double[]{1, 1, 1, 2, 2, 3, 1, 4});
//        Point point = Point.fromLngLat(1, 2);
//        Line line2 = Line.fromLngLats(new double[]{1, 1, 1, 4});
//
//        System.out.println(TaleBooleans.booleanWithin(point, line));
//        System.out.println(TaleBooleans.booleanWithin(line2, line));

//        Polygon poly1 = Polygon.fromLngLats(new double[]{0, 0, 0, 5, 5, 5, 5, 0, 0, 0});
//        Polygon poly2 = Polygon.fromLngLats(new double[]{1, 1, 1, 6, 6, 6, 6, 1, 1, 1});
//        Polygon poly3 = Polygon.fromLngLats(new double[]{10, 10, 10, 15, 15, 15, 15, 10, 10, 10});
//
//        System.out.println(TaleBooleans.booleanOverlap(poly1, poly2));
//        System.out.println(TaleBooleans.booleanOverlap(poly2, poly3));

//        Polygon polygon = Polygon.fromLngLats(new double[][]{new double[]{115, -35},
//                new double[]{125, -30},
//                new double[]{135, -30},
//                new double[]{145, -35},
//                new double[]{115, -35}});
//
//        Line line2 = Line.fromLngLats(new double[][]{new double[]{115, -25},
//                new double[]{125, -30},
//                new double[]{135, -30},
//                new double[]{145, -25}});
//
//        System.out.println(TaleMisc.lineOverlap(polygon, line2, null));

//        MultiLine line = MultiLine.fromLngLats(new double[][]{new double[]{-77.031669, 38.878605},
//                new double[]{-77.029609, 38.881946},
//                new double[]{-77.020339, 38.884084},
//                new double[]{-77.025661, 38.885821},
//                new double[]{-77.021884, 38.889563},
//                new double[]{-77.019824, 38.892368}});
//        Point pt = Point.fromLngLat(-77.037076, 38.884017);
//
//        System.out.println(TaleMisc.nearestPointOnLine(line, pt));

//        Polygon polygon = Polygon.fromLngLats(new double[][]{new double[]{-50, 5},
//                new double[]{-40, -10},
//                new double[]{-50, -10},
//                new double[]{-40, 5},
//                new double[]{-50, 5}});
//
//        IntHolder total = new IntHolder();
//        TaleMeta.segmentEach(polygon, ((currentSegment, geometryIndex, segmentIndex) -> {
//            total.value++;
//            return true;
//        }));
//
//        System.out.println(total.value);

//        Polygon polygon1 = Polygon.fromLngLats(new double[][]{new double[]{116.288136,40.008065},
//                new double[]{116.484182,39.978437},
//                new double[]{116.49913,39.844283},
//                new double[]{116.290435,39.846056},
//                new double[]{116.288136,40.008065}});
//        Polygon polygon2 = Polygon.fromLngLats(new double[][]{new double[]{116.349939,40.019228},
//                new double[]{116.418929,39.997564},
//                new double[]{116.431002,39.800061},
//                new double[]{116.375236,39.823118},
//                new double[]{116.349939,40.019228}});
//        System.out.println(TaleMisc.lineIntersect(polygon1, polygon2));

        // 创建R树时，可以指定最小、最大孩子结点数，splitter
//        SpatialSearch<Rect2d> rtree = SpatialSearches.rTree(new Rect2d.Builder(), 2, 9, RTree.Split.AXIAL);
//        rtree.add(new Rect2d(116.288136, 39.978437, 116.484182, 40.008065));
//        rtree.add(new Rect2d(116.484182, 39.844283, 116.49913, 39.978437));
//        rtree.add(new Rect2d(116.290435, 39.844283, 116.49913, 39.846056));
//        rtree.add(new Rect2d(116.288136, 39.846056, 116.290435, 40.008065));
//
//        final Rect2d searchRect = new Rect2d(116.388136, 39.978437, 116.484182, 39.988437);
//        Rect2d[] results = new Rect2d[20];
//
//        final int foundCount = rtree.search(searchRect, results);
//        System.out.println(results.length);
//        System.out.println(foundCount);;

//        Line line1 = Line.fromLngLats(126, -11, 129, -21);
//        Line line2 = Line.fromLngLats(123, -18, 131, -14);
//
//        List<Point> intersects = TaleMisc.lineIntersect(line1, line2);
//        System.out.println(intersects); // [127.43478260869566, -15.782608695652174]

//        Point pt = Point.fromLngLat(-1, -1);
//        Line line = Line.fromLngLats(new double[][]{new double[]{-1, -1}, new double[]{1, 1}, new double[]{1.5, 2.2}});
//        System.out.println(TaleBooleans.booleanPointOnLine(pt, line));

//        Point pt = Point.fromLngLat(-81, 47);
//        Polygon poly = Polygon.fromLngLats(new double[][]{new double[]{-81, 41}, new double[]{-81, 47}, new double[]{-72, 47}, new double[]{-72, 41}, new double[]{-81, 41}});
//        System.out.println(TaleBooleans.booleanPointInPolygon(pt, poly, true));

//        Line line1 = Line.fromLngLats(new double[][]{new double[]{0, 0}, new double[]{0, 1}});
//        Line line2 = Line.fromLngLats(new double[][]{new double[]{1, 0}, new double[]{1, 1}});
//
//        System.out.println(TaleBooleans.booleanParallel(line1, line2));

//        Polygon polygon = Polygon.fromLngLats(new double[][]{new double[]{-50, 5}, new double[]{-40, -10}, new double[]{-50, -10}, new double[]{-40, 5}, new double[]{-50, 5}});
//        List<Line> lines = TaleMisc.lineSegment(polygon);
//        System.out.println(lines);

//        Point pt1 = Point.fromLngLat(0, 0);
//        Point pt2 = Point.fromLngLat(0, 0);
//        Point pt3 = Point.fromLngLat(1, 1);
//
//        System.out.println(TaleBooleans.booleanEqual(pt1, pt2));
//        System.out.println(TaleBooleans.booleanEqual(pt2, pt3));

//        Line line = Line.fromLngLats(new double[][]{new double[]{0, 0}, new double[]{1, 1}, new double[]{1, 0}, new double[]{0, 0}});
//        System.out.println(TaleBooleans.booleanClockwise(line));
//
//        Line line2 = Line.fromLngLats(new double[][]{new double[]{0, 0}, new double[]{1, 0}, new double[]{1, 1}, new double[]{0, 0}});
//        System.out.println(TaleBooleans.booleanClockwise(line2));
//
//        Polygon polygon = Polygon.fromLngLats(new double[][]{new double[]{121, -29}, new double[]{138, -29}, new double[]{138, -18}, new double[]{121, -18}, new double[]{121, -29}});
//        TaleCoordinateMutation.rewind(polygon, true);
//        System.out.println(polygon);

//        Line line = Line.fromLngLats(new double[][]{new double[]{-76.091308, 18.427501},new double[]{-76.695556, 18.729501},new double[]{-76.552734, 19.40443},new double[]{-74.61914, 19.134789},new double[]{-73.652343, 20.07657},new double[]{-73.157958, 20.210656}});
//        Line bezierLine = TaleTransformation.bezierSpline(line);
//        System.out.println(bezierLine);
//        System.out.println(bezierLine.coordinates().size());

//        BoundingBox bbox = BoundingBox.fromLngLats(0, 0, 10, 10);
//        Polygon polygon = Polygon.fromLngLats(new double[][]{new double[]{2, 2}, new double[]{8, 4}, new double[]{12, 8}, new double[]{3, 7}, new double[]{2, 2}});
//        Polygon newPolygon = (Polygon) TaleTransformation.bboxClip(polygon, bbox);
//        System.out.println(newPolygon);
//
//        Line line = Line.fromLngLats(new double[][]{new double[]{2, 2}, new double[]{8, 4}, new double[]{12, 8}, new double[]{3, 7}});
//        Geometry geometry = TaleTransformation.bboxClip(line, bbox);
//        System.out.println(geometry);

//        Line line = Line.fromLngLats(new double[][]{new double[]{0, 0}, new double[]{0, 2}, new double[]{0, 5}, new double[]{0, 8}, new double[]{0, 8}, new double[]{0, 10}});
//        MultiPoint multiPoint = MultiPoint.fromLngLats(new double[][]{new double[]{0, 0}, new double[]{0, 0}, new double[]{2, 2}});
//
//        TaleCoordinateMutation.cleanCoords(line, true);
//        TaleCoordinateMutation.cleanCoords(multiPoint, true);
//
//        System.out.println(line);
//        System.out.println(multiPoint);

//        Point targetPoint = Point.fromLngLat(112.015826, 36.074031);
//        MultiPoint multiPoint = MultiPoint.fromLngLats(new double[][]{new double[]{105.142483000001, 35.83472500002}, new double[]{104.772949, 30.963027}, new double[]{110.907223, 33.09316}});

//        MultiPoint multiPoint1 = TaleTransformation.clone(multiPoint);
//        multiPoint1.coordinates().remove(0);
//
//        System.out.println(multiPoint.coordinates().size());
//        System.out.println(multiPoint1.coordinates().size());

//        TaleCoordinateMutation.truncate(multiPoint);
//        System.out.println(multiPoint);
//
//        System.out.println(TaleHelper.round(120.4321, 2));


//        Point nearest = TaleClassification.nearestPoint(targetPoint, multiPoint);
//        log.info("nearest {}", nearest);
//
//        System.out.println(Arrays.deepToString(TaleMeta.unwrapCoords(multiPoint)));

//        Point center = Point.fromLngLat(-75.343, 39.984);
//        Polygon circle = TaleTransformation.circle(center, 5, 10, Units.KILOMETERS);
//
//        System.out.println(circle);
//
//        TaleMeta.coordEach(circle, (p, index, multiIndex, geomIndex) -> {
//            double latitude = p.getLatitude(), longitude = p.getLongitude();
//
//            // 翻转
//            p.setLongitude(latitude);
//            p.setLatitude(longitude);
//
//            return true;
//        });
//
//        System.out.println(circle);

//        Point center = Point.fromLngLat(-75.343, 39.984);
//        Polygon circle = TaleTransformation.circle(center, 5, 10, Units.KILOMETERS);
//        log.info("circle:{}", circle);

//        Line line = Line.fromLngLats(new double[][]{new double[]{109.502991, 29.68718}, new double[]{108.837829, 32.969237}, new double[]{113.567871, 37.200787}});
//        Point point = TaleMeasurement.along(line, 300, Units.KILOMETERS);
//        log.info("目标点：{}", point);

//        BoundingBox bbox = BoundingBox.fromLngLats(-20, -20, -15, 0);
//        BoundingBox newBbox = TaleMeasurement.square(bbox);
//        log.info("正方形：{}", newBbox);


//        Line line = Line.fromLngLats(new double[]{-74, 40, -78, 42, -82, 35});
//        BoundingBox bbox = TaleMeasurement.bbox(line);
//        System.out.println(bbox.west());
//        System.out.println(bbox.south());
//        System.out.println(bbox.east());
//        System.out.println(bbox.north());
//        Polygon bboxPolygon = TaleMeasurement.bboxPolygon(bbox);
//        log.info("多边形：{}", bboxPolygon.coordinates());
//
//        Point p = Point.fromLngLat(1,2);
//        System.out.println(TaleMeasurement.bbox(p));

//        Polygon polygon = Polygon.fromLngLats(new double[][]{new double[]{-97.522259, 35.4691},new double[]{-97.502754, 35.463455}, new double[]{-97.508269, 35.463245}});
//        Point center = TaleMeasurement.center(polygon);
//        log.info("多边中心：{}", center);
//        Polygon polygon = Polygon.fromLngLats(new double[][]{new double[]{-81, 41}, new double[]{-88, 36}, new double[]{-84, 31}, new double[]{-80, 33}, new double[]{-77, 39}, new double[]{-81, 41}});
//        Point point = TaleMeasurement.centroid(polygon);
//
//        log.info("多边形质心：{}", point);

//        Line line = Line.fromLngLats(new double[][]{new double[]{115, -32}, new double[]{131, -22}, new double[]{143, -25}, new double[]{150, -34}});
//        double length = TaleMeasurement.length(line, Units.KILOMETERS);
//        log.info("多线段总周长：{}公里", length);

//        Point p1 = Point.fromLngLat(-75.343, 39.984);
//        Point p2 = Point.fromLngLat(-75.534, 39.123);
//        double rhumbBearing = TaleMeasurement.rhumbBearing(p1, p2);
//        log.info("计算两点恒向线夹角：{}", rhumbBearing);

//        Point p = Point.fromLngLat(-75.343, 39.984);
//        double distance = 50;
//        double bearing = 90;
//
//        Point destination = TaleMeasurement.destination(p, distance, bearing, Units.MILES);
//        log.info("计算的指定点：{}", destination);

//        Point p1 = Point.fromLngLat(144.834823, -37.771257);
//        Point p2 = Point.fromLngLat(145.14244, -37.830937);
//        Point midPoint = TaleMeasurement.midpoint(p1, p2);
//        log.info("计算两点中间点：{}", midPoint);

//        double[][] coordinates = new double[][] {new double[]{116.54849,39.94737},new double[]{116.57106,39.94676},new double[]{116.57602,39.946262},new double[]{116.59377,39.94571},new double[]{116.59241,39.953728},new double[]{116.59075,39.96269},new double[]{116.58996,39.97148},new double[]{116.580475,39.970818},new double[]{116.5776,39.982704},new double[]{116.55151,39.98088},new double[]{116.54735,39.986187},new double[]{116.53484,39.988842},new double[]{116.532486,39.987984},new double[]{116.53153,39.987846},new double[]{116.530655,39.987362},new double[]{116.53071,39.986824},new double[]{116.52999,39.986862},new double[]{116.52931,39.986824},new double[]{116.528465,39.986145},new double[]{116.525986,39.986794},new double[]{116.52528,39.986755},new double[]{116.524796,39.9867},new double[]{116.52327,39.98612},new double[]{116.52645,39.98193},new double[]{116.528534,39.979084},new double[]{116.52084,39.976414},new double[]{116.51824,39.975544},new double[]{116.515526,39.975323},new double[]{116.51567,39.9687},new double[]{116.51567,39.968716},new double[]{116.515656,39.963795},new double[]{116.51571,39.96179},new double[]{116.51562,39.953354},new double[]{116.51842,39.953922},new double[]{116.51794,39.95619},new double[]{116.52595,39.956146},new double[]{116.54088,39.956535},new double[]{116.5471,39.949787},new double[]{116.54805,39.947}, new double[]{116.54849,39.94737}};
//        Line line = Line.fromLngLats(coordinates);
//        List<Point> points = TaleMisc.kinks(line);
//        System.out.println(JSONUtil.toJsonStr(points));

//        Point ggPoint = Point.fromLngLat(116.396939, 39.921382);
//        Point xdscPoint = Point.fromLngLat(116.3812, 39.918919);
//
//        log.info("两点距离：{} 公里", TaleMeasurement.distance(ggPoint, xdscPoint, Units.KILOMETERS));

//        double[][] coordinates = new double[][]{new double[]{108.09876, 37.200787}, new double[]{106.398901, 33.648651}, new double[]{114.972103, 33.340483}, new double[]{113.715685, 37.845557}, new double[]{108.09876, 37.200787}};
//        Polygon polygon = Polygon.fromLngLats(coordinates);
//
//        log.info("面积：{} 平方公里", TaleMeasurement.area(polygon, Units.KILOMETERS));

//        Point p1 = Point.fromLngLat(109.104262, 37.831315);
//        Point p2 = Point.fromLngLat(102.865569, 34.089941);
//
//        double bearing = TaleMeasurement.bearing(p1, p2);
//        log.info("计算两点之间的角度：{}", bearing);
    }

}
