package com.cgzz.mapbox.test;

import com.cgzz.mapbox.jturf.JTurfBooleans;
import com.cgzz.mapbox.jturf.JTurfMisc;
import com.cgzz.mapbox.jturf.enums.Units;
import com.cgzz.mapbox.jturf.shape.impl.*;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class JTurfMiscTest {

    @Test
    public void kinksTest() {
        Polygon poly = Polygon.fromJson("{\"type\":\"Polygon\",\"coordinates\":[[[-12.034835,8.901183],[-12.060413,8.899826],[-12.03638,8.873199],[-12.059383,8.871418],[-12.034835,8.901183]]]}");

        FeatureCollection<Point> kinks = JTurfMisc.kinks(poly);

        FeatureCollection<Point> same = FeatureCollection.fromJson("{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[-12.047632373646445,8.885665897727455]}}]}", Point.class);

        assertTrue(JTurfBooleans.booleanEqual(kinks, same));
    }

    @Test
    public void lineSegmentTest() {
        Polygon polygon = Polygon.fromJson("{\"type\":\"Polygon\",\"coordinates\":[[[-50,5],[-40,-10],[-50,-10],[-40,5],[-50,5]]]}");

        FeatureCollection<LineString> segments = JTurfMisc.lineSegment(polygon);

        FeatureCollection<LineString> same = FeatureCollection.fromJson("{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-50,5],[-40,-10]]},\"bbox\":[-50,-10,-40,5],\"id\":0},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-40,-10],[-50,-10]]},\"bbox\":[-50,-10,-40,-10],\"id\":1},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-50,-10],[-40,5]]},\"bbox\":[-50,-10,-40,5],\"id\":2},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-40,5],[-50,5]]},\"bbox\":[-50,5,-40,5],\"id\":3}]}", LineString.class);

        assertTrue(JTurfBooleans.booleanEqual(segments, same));
    }

    @Test
    public void lineIntersectTest() {
        LineString line1 = LineString.fromLngLats(Point.fromLngLat(126, -11), Point.fromLngLat(129, -21));
        LineString line2 = LineString.fromLngLats(Point.fromLngLat(123, -18), Point.fromLngLat(131, -14));

        FeatureCollection<Point> intersects = JTurfMisc.lineIntersect(line1, line2);

        FeatureCollection<Point> same = FeatureCollection.fromJson("{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[127.43478260869566,-15.782608695652174]}}]}", Point.class);

        assertTrue(JTurfBooleans.booleanEqual(intersects, same));
    }

    @Test
    public void lineOverlapTest() {
        LineString line1 = LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":[[115,-25],[125,-30],[135,-30],[145,-25]]}");
        LineString line2 = LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":[[115,-35],[125,-30],[135,-30],[145,-35]]}");

        FeatureCollection<LineString> overlapping = JTurfMisc.lineOverlap(line1, line2);

        FeatureCollection<LineString> same = FeatureCollection.fromJson("{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[125,-30],[135,-30]]}}]}", LineString.class);

        assertTrue(JTurfBooleans.booleanEqual(overlapping, same));
    }

    @Test
    public void nearestPointOnLineTest() {
        LineString line = LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":[[-77.031669,38.878605],[-77.029609,38.881946],[-77.020339,38.884084],[-77.025661,38.885821],[-77.021884,38.889563],[-77.019824,38.892368]]}");
        Point pt = Point.fromLngLat(-77.037076, 38.884017);

        Feature<Point> snapped = JTurfMisc.nearestPointOnLine(line, pt, Units.MILES);

        Feature<Point> same = Feature.fromJson("{\"type\":\"Feature\",\"properties\":{\"dist\":0.4239819718628416,\"location\":0.2112562928236486,\"index\":0},\"geometry\":{\"type\":\"Point\",\"coordinates\":[-77.02996941477018,38.881361463229524]}}", Point.class);

        assertTrue(JTurfBooleans.booleanEqual(snapped, same));
    }

    @Test
    public void lineArcTest() {
        Point center = Point.fromLngLat(-75, 40);
        int radius = 5;
        int bearing1 = 25;
        int bearing2 = 47;

        Feature<LineString> arc = JTurfMisc.lineArc(center, radius, bearing1, bearing2);

        Feature<LineString> same = Feature.fromJson("{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-74.97517792609881,40.04075040571227],[-74.97485905333149,40.0406355919303],[-74.97454108712354,40.04051931470826],[-74.97422403893587,40.04040157823899],[-74.97390792019605,40.040282386767934],[-74.973592742298,40.04016174459297],[-74.97327851660145,40.04003965606426],[-74.97296525443174,40.039916125584114],[-74.9726529670792,40.0397911576068],[-74.97234166579882,40.03966475663839],[-74.97203136180988,40.03953692723662],[-74.97172206629553,40.03940767401068],[-74.9714137904023,40.03927700162111],[-74.97110654523982,40.03914491477954],[-74.97080034188035,40.03901141824863],[-74.97049519135838,40.0388765168418],[-74.97019110467025,40.03874021542312],[-74.96988809277379,40.03860251890709],[-74.96958616658777,40.0384634322585],[-74.96928533699172,40.038322960492245],[-74.96898561482539,40.038181108673086],[-74.96868701088839,40.03803788191554],[-74.96838953593983,40.03789328538367],[-74.96809320069791,40.037747324290876],[-74.9677980158395,40.03760000389973],[-74.96750399199983,40.037451329521794],[-74.96721113977205,40.037301306517385],[-74.96691946970681,40.037149940295436],[-74.96662899231204,40.03699723631324],[-74.96633971805235,40.03684320007631],[-74.9660516573488,40.03668783713815],[-74.96576482057851,40.03653115310004],[-74.96547921807422,40.03637315361088],[-74.965194860124,40.036213844366934],[-74.96491175697079,40.03605323111165],[-74.96462991881211,40.03589131963545],[-74.96434935579964,40.03572811577551],[-74.9640700780389,40.03556362541558],[-74.9637920955888,40.03539785448573],[-74.96351541846138,40.03523080896214],[-74.9632400566214,40.035062494866914],[-74.96296601998598,40.03489291826785],[-74.96269331842424,40.0347220852782],[-74.96242196175693,40.03455000205646],[-74.96215195975611,40.03437667480617],[-74.96188332214481,40.03420210977564],[-74.96161605859662,40.034026313257776],[-74.96135017873537,40.033849291589824],[-74.96108569213482,40.03367105115314],[-74.96082260831825,40.033491598372976],[-74.96056093675814,40.03331093971822],[-74.96030068687588,40.03312908170119],[-74.96004186804136,40.03294603087737],[-74.95978448957266,40.03276179384522],[-74.95952856073568,40.03257637724586],[-74.95927409074393,40.03238978776291],[-74.959021088758,40.03220203212222],[-74.95876956388545,40.03201311709159],[-74.95851952518028,40.03182304948055],[-74.95827098164274,40.031631836140164],[-74.9580239422189,40.0314394839627],[-74.9577784158005,40.03124599988142],[-74.95753441122443,40.03105139087033],[-74.95729193727253,40.03085566394393],[-74.95705100267124,40.03065882615696]]}}", LineString.class);

        assertTrue(JTurfBooleans.booleanEqual(arc, same));
    }

    @Test
    public void lineSliceAlongTest() {
        LineString line = LineString.fromLngLats(new double[]{7, 45, 9, 45, 14, 40, 14, 41});
        double start = 12.5;
        int stop = 25;
        Feature<LineString> sliced = JTurfMisc.lineSliceAlong(line, start, stop, Units.MILES);

        Feature<LineString> same = Feature.fromJson("{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[7.25584134370955,45.00194719009643],[7.511697527558178,45.003323144337116]]}}", LineString.class);

        assertTrue(JTurfBooleans.booleanEqual(sliced, same));
    }

    @Test
    public void lineChunkTest() {
        LineString line = LineString.fromLngLats(new double[]{-95, 40, -93, 45, -85, 50});

        FeatureCollection<LineString> chunk = JTurfMisc.lineChunk(line, 15, Units.MILES);

        FeatureCollection<LineString> same = FeatureCollection.fromJson("{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-95,40],[-94.9227817922225,40.20890898364752]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-94.9227817922225,40.20890898364752],[-94.84508616492565,40.41776634209382]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-94.84508616492565,40.41776634209382],[-94.76690671336515,40.626571369747296]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-94.76690671336515,40.626571369747296],[-94.68823693202754,40.835323349582225]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-94.68823693202754,40.835323349582225],[-94.60907021259803,41.04402155289995]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-94.60907021259803,41.04402155289995],[-94.52939984188114,41.252665239084244]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-94.52939984188114,41.252665239084244],[-94.44921899967287,41.461253655350504]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-94.44921899967287,41.461253655350504],[-94.3685207565831,41.66978603648899]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-94.3685207565831,41.66978603648899],[-94.28729807180731,41.87826160460174]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-94.28729807180731,41.87826160460174],[-94.20554379084568,42.086679568832835]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-94.20554379084568,42.086679568832835],[-94.12325064316842,42.29503912509213]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-94.12325064316842,42.29503912509213],[-94.04041123982606,42.50333945577204]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-94.04041123982606,42.50333945577204],[-93.95701807100299,42.71157972945735]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-93.95701807100299,42.71157972945735],[-93.8730635035128,42.91975910062752]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-93.8730635035128,42.91975910062752],[-93.7885397782338,43.127876709351796]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-93.7885397782338,43.127876709351796],[-93.70343900748324,43.33593168097646]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-93.70343900748324,43.33593168097646],[-93.61775317232855,43.54392312580413]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-93.61775317232855,43.54392312580413],[-93.53147411983355,43.751850138764986]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-93.53147411983355,43.751850138764986],[-93.44459356023845,43.95971179907958]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-93.44459356023845,43.95971179907958],[-93.35710306407134,44.16750716991297]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-93.35710306407134,44.16750716991297],[-93.26899405918955,44.37523529801998]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-93.26899405918955,44.37523529801998],[-93.1802578277489,44.58289521338136]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-93.1802578277489,44.58289521338136],[-93.09088550309868,44.790485928830364]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-93.09088550309868,44.790485928830364],[-93.0008680666008,44.998006439669815]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-93.0008680666008,44.998006439669815],[-93,45],[-92.78694943582444,45.153608485223984]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-92.78694943582444,45.153608485223984],[-92.57066503769474,45.3083031252262]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-92.57066503769474,45.3083031252262],[-92.35319844078758,45.462587333405345]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-92.35319844078758,45.462587333405345],[-92.13453994731577,45.61645663461031]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-92.13453994731577,45.61645663461031],[-91.9146798229516,45.7699065046171]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-91.9146798229516,45.7699065046171],[-91.69360829825284,45.92293236969218]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-91.69360829825284,45.92293236969218],[-91.47131557015544,46.07552960615855]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-91.47131557015544,46.07552960615855],[-91.24779180353507,46.22769353996491]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-91.24779180353507,46.22769353996491],[-91.02302713283888,46.379419446258275]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-91.02302713283888,46.379419446258275],[-90.79701166378996,46.53070254896018]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-90.79701166378996,46.53070254896018],[-90.56973547516593,46.681538020347254]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-90.56973547516593,46.681538020347254],[-90.34118862065392,46.83192098063621]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-90.34118862065392,46.83192098063621],[-90.11136113078372,46.98184649757375]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-90.11136113078372,46.98184649757375],[-89.8802430149412,47.13130958603188]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-89.8802430149412,47.13130958603188],[-89.64782426346397,47.28030520760908]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-89.64782426346397,47.28030520760908],[-89.4140948498212,47.42882827023753]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-89.4140948498212,47.42882827023753],[-89.17904473287997,47.57687362779724]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-89.17904473287997,47.57687362779724],[-88.94266385925984,47.72443607973732]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-88.94266385925984,47.72443607973732],[-88.7049421657779,47.87151037070482]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-88.7049421657779,47.87151037070482],[-88.46586958198635,48.01809119018197]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-88.46586958198635,48.01809119018197],[-88.22543603280492,48.16417317213199]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-88.22543603280492,48.16417317213199],[-87.98363144124973,48.30975089465438]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-87.98363144124973,48.30975089465438],[-87.74044573126127,48.45481887964973]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-87.74044573126127,48.45481887964973],[-87.49586883063337,48.59937159249542]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-87.49586883063337,48.59937159249542],[-87.249890674045,48.743403441732]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-87.249890674045,48.743403441732],[-87.0025012061977,48.88690877876133]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-87.0025012061977,48.88690877876133],[-86.75369038505995,49.02988189755716]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-86.75369038505995,49.02988189755716],[-86.50344818522127,49.1723170343885]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-86.50344818522127,49.1723170343885],[-86.25176460135764,49.31420836755671]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-86.25176460135764,49.31420836755671],[-85.99862965181042,49.45555001714691]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-85.99862965181042,49.45555001714691],[-85.74403338228133,49.59633604479445]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-85.74403338228133,49.59633604479445],[-85.48796586964437,49.73656045346719]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-85.48796586964437,49.73656045346719],[-85.23041722587809,49.87621718726427]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-85.23041722587809,49.87621718726427],[-85,50]]}}]}", LineString.class);

        assertTrue(JTurfBooleans.booleanEqual(chunk, same));
    }

    @Test
    public void lineSliceTest() {
        LineString line = LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":[[-77.031669,38.878605],[-77.029609,38.881946],[-77.020339,38.884084],[-77.025661,38.885821],[-77.021884,38.889563],[-77.019824,38.892368]]}");

        Point start = Point.fromLngLat(-77.029609, 38.881946);
        Point stop = Point.fromLngLat(-77.021884, 38.889563);

        Feature<LineString> sliced = JTurfMisc.lineSlice(start, stop, line);

        Feature<LineString> same = Feature.fromJson("{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-77.029609,38.881946],[-77.020339,38.884084],[-77.025661,38.885821],[-77.021884,38.889563],[-77.021884,38.889563]]}}", LineString.class);

        assertTrue(JTurfBooleans.booleanEqual(sliced, same));
    }

    @Test
    public void sectorTest() {
        Point center = Point.fromLngLat(-75, 40);
        int radius = 5;
        int bearing1 = 25;
        int bearing2 = 45;

        Polygon polygon = JTurfMisc.sector(center, radius, bearing1, bearing2);

        Polygon same = Polygon.fromJson("{\"type\":\"Polygon\",\"coordinates\":[[[-75,40],[-74.97517792609881,40.04075040571227],[-74.97488800447623,40.04064609008216],[-74.97459883121424,40.040540564683965],[-74.97431041492696,40.04043383266243],[-74.97402276420584,40.040325897198244],[-74.9737358876194,40.04021676150795],[-74.97344979371289,40.04010642884385],[-74.97316449100822,40.03999490249387],[-74.97287998800351,40.039882185781536],[-74.97259629317297,40.03976828206579],[-74.97231341496659,40.039653194740964],[-74.97203136180988,40.03953692723662],[-74.97175014210367,40.039419483017475],[-74.9714697642238,40.0393008655833],[-74.9711902365209,40.03918107846881],[-74.97091156732012,40.03906012524356],[-74.9706337649209,40.0389380095118],[-74.97035683759677,40.038814734912464],[-74.97008079359495,40.038690305118934],[-74.96980564113629,40.03856472383905],[-74.96953138841488,40.038437994814906],[-74.96925804359793,40.03831012182281],[-74.96898561482539,40.038181108673086],[-74.96871411020982,40.03805095921006],[-74.96844353783607,40.03791967731187],[-74.96817390576109,40.03778726689039],[-74.9679052220137,40.03765373189108],[-74.96763749459427,40.03751907629292],[-74.96737073147455,40.03738330410819],[-74.96710494059745,40.03724641938249],[-74.96684012987672,40.037108426194514],[-74.96657630719677,40.03696932865595],[-74.96631348041247,40.03682913091138],[-74.9660516573488,40.03668783713815],[-74.96579084580074,40.03654545154621],[-74.965531053533,40.036401978378045],[-74.96527228827969,40.03625742190849],[-74.96501455774428,40.03611178644467],[-74.96475786959918,40.03596507632578],[-74.96450223148564,40.035817295923046],[-74.96424765101345,40.03566844963954],[-74.96399413576076,40.03551854191006],[-74.96374169327382,40.035367577200994],[-74.96349033106677,40.035215560010215],[-74.9632400566214,40.035062494866914],[-74.96299087738701,40.03490838633147],[-74.96274280078002,40.0347532389953],[-74.9624958341839,40.03459705748077],[-74.9622499849489,40.034439846441],[-74.96200526039182,40.03428161055977],[-74.96176166779581,40.03412235455136],[-74.96151921441012,40.0339620831604],[-74.96127790744991,40.033800801161725],[-74.96103775409608,40.03363851336027],[-74.96079876149496,40.03347522459089],[-74.96056093675814,40.03331093971822],[-74.9603242869623,40.03314566363653],[-74.96008881914896,40.03297940126962],[-74.95985454032424,40.03281215757058],[-74.95962145745872,40.032643937521755],[-74.95938957748717,40.032474746134504],[-74.95915890730839,40.032304588449065],[-74.958929453785,40.03213346953449],[-74.95870122374316,40.031961394488384],[-74.95847422397252,40.0317883684368],[-75,40]]]}");

        assertTrue(JTurfBooleans.booleanEqual(polygon, same));
    }

}
