package com.cgzz.mapbox.test;

import com.cgzz.mapbox.jturf.JTurfBooleans;
import com.cgzz.mapbox.jturf.JTurfMeasurement;
import com.cgzz.mapbox.jturf.enums.Units;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.*;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JTurfMeasurementTest {

    @Test
    public void distanceTest() {
        Point from = Point.fromLngLat(-75.343, 39.984);
        Point to = Point.fromLngLat(-75.534, 39.123);

        double distance = JTurfMeasurement.distance(from, to, Units.MILES);

        assertEquals(distance, 60.35329997171415D, 10);
    }

    @Test
    public void rhumbDistanceTest() {
        Point from = Point.fromLngLat(-75.343, 39.984);
        Point to = Point.fromLngLat(-75.534, 39.123);

        double distance = JTurfMeasurement.rhumbDistance(from, to, Units.MILES);

        assertEquals(distance, 60.35331130430885D, 10);
    }

    @Test
    public void areaTest() {
        Polygon polygon = Polygon.fromJson("{\"type\":\"Polygon\",\"coordinates\":[[[125,-15],[113,-22],[154,-27],[144,-15],[125,-15]]]}");

        double area = JTurfMeasurement.area(polygon);

        assertEquals(area, 3332484969239.2676, 10);
    }

    @Test
    public void bearingTest() {
        Point point1 = Point.fromLngLat(-75.343, 39.984);
        Point point2 = Point.fromLngLat(-75.534, 39.123);

        double bearing = JTurfMeasurement.bearing(point1, point2);

        assertEquals(bearing, -170.2330491349224, 13);
    }

    @Test
    public void destinationTest() {
        Point point = Point.fromLngLat(-75.343, 39.984);

        Point destination = JTurfMeasurement.destination(point, 50, 90, Units.MILES);

        Feature<Point> same = Feature.fromJson("{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[-74.39858826442095,39.98016766669771]}}", Point.class);

        assertTrue(JTurfBooleans.booleanEqual(destination, same));
    }

    @Test
    public void midpointTest() {
        Point point1 = Point.fromLngLat(144.834823, -37.771257);
        Point point2 = Point.fromLngLat(145.14244, -37.830937);

        Point midpoint = JTurfMeasurement.midpoint(point1, point2);

        Feature<Point> same = Feature.fromJson("{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[144.98856936202512,-37.801196981553204]}}", Point.class);

        assertTrue(JTurfBooleans.booleanEqual(midpoint, same));
    }

    @Test
    public void rhumbBearingTest() {
        Point point1 = Point.fromLngLat(-75.343, 39.984);
        Point point2 = Point.fromLngLat(-75.534, 39.123);

        double bearing = JTurfMeasurement.rhumbBearing(point1, point2);

        assertEquals(bearing, -170.29417535572546, 13);
    }

    @Test
    public void lengthTest() {
        LineString line = LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":[[115,-32],[131,-22],[143,-25],[150,-34]]}");

        double length = JTurfMeasurement.length(line, Units.MILES);

        assertEquals(length, 2738.9663893575207, 13);
    }

    @Test
    public void bboxTest() {
        LineString line = LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":[[-74,40],[-78,42],[-82,35]]}");

        BoundingBox bbox = JTurfMeasurement.bbox(line);

        Polygon bboxPolygon = JTurfMeasurement.bboxPolygon(bbox);

        Feature<Polygon> same = Feature.fromJson("{\"type\":\"Feature\",\"bbox\":[-82,35,-74,42],\"properties\":{},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[-82,35],[-74,35],[-74,42],[-82,42],[-82,35]]]}}", Polygon.class);

        assertTrue(JTurfBooleans.booleanEqual(bboxPolygon, same.geometry()));
    }

    @Test
    public void centerTest() {
        Feature<Point> p1 = Feature.fromGeometry(Point.fromLngLat(-97.522259, 35.4691));
        Feature<Point> p2 = Feature.fromGeometry(Point.fromLngLat(-97.502754, 35.463455));
        Feature<Point> p3 = Feature.fromGeometry(Point.fromLngLat(-97.508269, 35.463245));

        FeatureCollection<Point> features = FeatureCollection.fromFeatures(Arrays.asList(p1, p2, p3));

        Point center = JTurfMeasurement.center(features);

        Feature<Point> same = Feature.fromJson("{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[-97.5125065,35.4661725]}}", Point.class);

        assertTrue(JTurfBooleans.booleanEqual(center, same.geometry()));
    }

    @Test
    public void bboxPolygonTest() {
        BoundingBox bbox = BoundingBox.fromLngLats(0, 0, 10, 10);

        Polygon bboxPolygon = JTurfMeasurement.bboxPolygon(bbox);

        Feature<Polygon> same = Feature.fromJson("{\"type\":\"Feature\",\"bbox\":[0,0,10,10],\"properties\":{},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[0,0],[10,0],[10,10],[0,10],[0,0]]]}}", Polygon.class);

        assertTrue(JTurfBooleans.booleanEqual(bboxPolygon, same.geometry()));
    }

    @Test
    public void centroidTest() {
        Polygon polygon = Polygon.fromJson("{\"type\":\"Polygon\",\"coordinates\":[[[-81,41],[-88,36],[-84,31],[-80,33],[-77,39],[-81,41]]]}");

        Point centroid = JTurfMeasurement.centroid(polygon);

        Feature<Point> same = Feature.fromJson("{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[-82,36]}}", Point.class);

        assertTrue(JTurfBooleans.booleanEqual(centroid, same.geometry()));
    }

    @Test
    public void squareTest() {
        BoundingBox bbox = BoundingBox.fromLngLats(-20, -20, -15, 0);

        BoundingBox squared = JTurfMeasurement.square(bbox);

        BoundingBox same = BoundingBox.fromJson("[-27.5,-20,-7.5,0]");

        assertEquals(squared, same);
    }

    @Test
    public void alongTest() {
        LineString line = LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":[[-83,30],[-84,36],[-78,41]]}");

        Point along = JTurfMeasurement.along(line, 200, Units.MILES);

        Feature<Point> same = Feature.fromJson("{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[-83.4608648621918,32.8678095806294]}}", Point.class);

        assertTrue(JTurfBooleans.booleanEqual(along, same.geometry()));
    }

    @Test
    public void greatCircleTest() {
        Point start = Point.fromLngLat(-122, 48);
        Point end = Point.fromLngLat(-77, 39);

        Geometry greatCircle = JTurfMeasurement.greatCircle(start, end);

        Feature<LineString> same = Feature.fromJson("{\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-122,48],[-121.49670597260395,48.006695584053034],[-120.9933027193627,48.011192319649155],[-120.48983314250054,48.01348947620715],[-119.98634018734154,48.013586680390496],[-119.4828668144069,48.01148391639968],[-118.97945597148815,48.007181525984585],[-118.47615056574062,48.00068020817685],[-117.97299343584108,47.991981018742806],[-117.47002732425332,47.981085369357956],[-116.96729484964568,47.967995026504404],[-116.4648384795031,47.95271211009349],[-115.96270050297673,47.93523909181596],[-115.4609230040126,47.91557879322269],[-114.9595478348006,47.89373438353978],[-114.45861658958391,47.86970937722146],[-113.95817057886768,47.84350763124574],[-113.45825080406522,47.81513334215742],[-112.95889793261823,47.78459104286371],[-112.46015227362618,47.75188559918844],[-111.96205375401924,47.71702220619068],[-111.46464189530656,47.68000638425442],[-110.96795579093103,47.64084397495606],[-110.47203408425958,47.599541136716965],[-109.97691494723627,47.556104340248396],[-109.48263605972384,47.510540363796785],[-108.9892345895576,47.462856288197194],[-108.49674717333333,47.413059491743375],[-108.0052098979493,47.36115764488277],[-107.51465828292066,47.307158704745284],[-107.02512726348186,47.2510709095145],[-106.53665117449182,47.192902772650505],[-106.04926373515357,47.13266307697324],[-105.56299803455904,47.07036086861578],[-105.07788651806698,47.006005450856634],[-104.59396097452058,46.939606377840555],[-104.11125252430944,46.87117344819688],[-103.62979160827808,46.80071669856529],[-103.14960797748235,46.7282463970377],[-102.67073068379234,46.65377303652598],[-102.1931880713394,46.57730732806457],[-101.7170077688026,46.498860194057016],[-101.24221668252876,46.41844276147571],[-100.76884099047857,46.33606635502345],[-100.29690613698943,46.2517424902657],[-99.82643682834477,46.16548286674227],[-99.3574570291378,46.077299361066586],[-98.88998995941655,45.98720402002102],[-98.4240580925958,45.89520905365619],[-97.95968315412041,45.80132682840214],[-97.49688612086358,45.70556986019895],[-97.03568722124199,45.6079508076541],[-96.57610593603007,45.50848246523389],[-96.11816099985333,45.407177756495486],[-95.66187040334134,45.30404972736659],[-95.20725139591926,45.1991115394787],[-94.75432048921667,45.09237646356026],[-94.3030934610721,44.98385787289545],[-93.85358536011054,44.87356923685414],[-93.4058105108719,44.76152411449811],[-92.95978251946687,44.6477361482688],[-92.51551427973754,44.5322190577608],[-92.07301797989918,44.41498663358605],[-91.63230510963972,44.29605273133223],[-91.19338646765388,44.17543126561971],[-90.75627216958802,44.05313620426017],[-90.32097165637266,43.929181562520576],[-89.8874937029196,43.803581397495066],[-89.45584642716022,43.676349802587914],[-89.02603729940269,43.54750090210971],[-88.59807315198498,43.4170488459892],[-88.17196018920231,43.28500780460256],[-87.7477039974863,43.15139196372203],[-87.32530955581525,43.01621551958524],[-86.90478124633385,42.8794926740865],[-86.48612286516241,42.74123763009114],[-86.06933763337499,42.60146458687378],[-85.65442820812737,42.46018773568085],[-85.24139669391563,42.317421255418104],[-84.83024465394682,42.17317930846318],[-84.42097312160388,42.027476036603204],[-84.01358261198783,41.880325557097365],[-83.60807313351988,41.73174195886403],[-83.2044441995878,41.581739298792144],[-82.80269484022104,41.430331598176004],[-82.40282361377945,41.27753283927283],[-82.00482861864141,41.123356961982175],[-81.60870750487793,40.96781786064601],[-81.21445748589922,40.81092938096863],[-80.82207535006172,40.652705317054696],[-80.43155747222355,40.493159408564395],[-80.04289982523697,40.33230533798404],[-79.65609799136769,40.170156728010646],[-79.27114717363055,40.006727139048735],[-78.88804220703217,39.84203006681773],[-78.5067775697119,39.67607894006818],[-78.1273473939725,39.50888711840477],[-77.74974547719275,39.34046789021446],[-77.37396529261488,39.170834470697606],[-77,38.99999999999999]]},\"type\":\"Feature\",\"properties\":{}}", LineString.class);

        assertTrue(JTurfBooleans.booleanEqual(greatCircle, same.geometry()));
    }

    @Test
    public void rhumbDestinationtest() {
        Point pt = Point.fromLngLat(-75.343, 39.984);

        Point destination = JTurfMeasurement.rhumbDestination(pt, 50, 90, Units.MILES);

        Feature<Point> same = Feature.fromJson("{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[-74.3985529486182,39.984]}}", Point.class);

        assertTrue(JTurfBooleans.booleanEqual(destination, same.geometry()));
    }

    @Test
    public void envelopeTest() {
        Feature<Point> pt1 = Feature.fromGeometry(Point.fromLngLat(-75.343, 39.984));
        Feature<Point> pt2 = Feature.fromGeometry(Point.fromLngLat(-75.833, 39.284));
        Feature<Point> pt3 = Feature.fromGeometry(Point.fromLngLat(-75.534, 39.123));

        FeatureCollection<Point> features = FeatureCollection.fromFeatures(Arrays.asList(pt1, pt2, pt3));

        Polygon enveloped = JTurfMeasurement.envelope(features);

        Feature<Polygon> same = Feature.fromJson("{\"type\":\"Feature\",\"bbox\":[-75.833,39.123,-75.343,39.984],\"properties\":{},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[-75.833,39.123],[-75.343,39.123],[-75.343,39.984],[-75.833,39.984],[-75.833,39.123]]]}}", Polygon.class);

        assertTrue(JTurfBooleans.booleanEqual(enveloped, same.geometry()));
    }

    @Test
    public void polygonTangentsTest() {
        Polygon polygon = Polygon.fromJson("{\"type\":\"Polygon\",\"coordinates\":[[[11,0],[22,4],[31,0],[31,11],[21,15],[11,11],[11,0]]]}");
        Point point = Point.fromLngLat(61, 5);

        FeatureCollection<Point> tangents = JTurfMeasurement.polygonTangents(point, polygon);

        FeatureCollection<Point> same = FeatureCollection.fromJson("{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[21,15]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[31,0]}}]}", Point.class);

        assertTrue(JTurfBooleans.booleanEqual(tangents, same));
    }

}
