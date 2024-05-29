package com.cgzz.mapbox.test.jturf;

import com.cgzz.mapbox.jturf.*;
import com.cgzz.mapbox.jturf.enums.Units;
import com.cgzz.mapbox.jturf.shape.*;
import com.cgzz.mapbox.jturf.util.JTurfHelper;
import org.omg.CORBA.DoubleHolder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class TurfTest {

    public static void main(String[] args) {
        // fenceId = 187

        // 200458
        Polygon area = repairCoordsToPolygon(new double[]{113.371414, 23.04973, 113.375114, 23.053324, 113.37745, 23.061012, 113.38661, 23.067928, 113.39204, 23.07561, 113.39649, 23.078936, 113.410614, 23.086185, 113.422165, 23.090443, 113.42467, 23.087898, 113.42718, 23.0875, 113.42915, 23.08494, 113.43219, 23.084274, 113.43562, 23.081898, 113.4332, 23.080103, 113.430916, 23.07809, 113.43077, 23.07731, 113.42914, 23.074932, 113.42883, 23.073969, 113.43081, 23.072489, 113.430885, 23.071459, 113.42754, 23.069614, 113.42319, 23.055725, 113.41468, 23.04228, 113.40972, 23.037758, 113.39995, 23.034567, 113.39276, 23.039621, 113.382484, 23.041817, 113.37063, 23.04075, 113.36322, 23.042349, 113.36078, 23.044106, 113.36046, 23.046103});
        printPoints(area);

        List<Polygon> slicesData = new ArrayList<>();

        // 206512
        Polygon uni2 = repairCoordsToPolygon(new double[]{113.37801361083984, 23.077646255493164, 113.37452697753906, 23.082679748535156, 113.36870574951172, 23.080123901367188, 113.36717224121094, 23.079387664794922, 113.36477661132812, 23.0822696685791, 113.3620834350586, 23.086027145385742, 113.36058807373047, 23.087785720825195, 113.3590087890625, 23.088783264160156, 113.35765075683594, 23.090625762939453, 113.3572769165039, 23.091167449951172, 113.35990905761719, 23.094825744628906, 113.36290740966797, 23.098222732543945, 113.36624145507812, 23.101333618164062, 113.36988830566406, 23.104135513305664, 113.37382507324219, 23.106605529785156, 113.3780288696289, 23.108720779418945, 113.38246154785156, 23.110454559326172, 113.38711547851562, 23.111785888671875, 113.39048767089844, 23.111833572387695, 113.3962173461914, 23.110456466674805, 113.401123046875, 23.10791015625, 113.41390991210938, 23.098735809326172, 113.41218566894531, 23.096878051757812, 113.41209411621094, 23.09624671936035, 113.41148376464844, 23.094467163085938, 113.40929412841797, 23.09362030029297, 113.40648651123047, 23.091575622558594, 113.40715789794922, 23.08949851989746, 113.40162658691406, 23.08796501159668, 113.39337158203125, 23.083364486694336, 113.38092041015625, 23.079906463623047, 113.37801361083984, 23.077646255493164});
        slicesData.add(uni2);
        printPoints(uni2);

        // 202009
        Polygon uni3 = repairCoordsToPolygon(new double[]{113.35896301269531, 23.04996109008789, 113.35423278808594, 23.05095100402832, 113.35262298583984, 23.055042266845703, 113.3514404296875, 23.059307098388672, 113.35071563720703, 23.063724517822266, 113.35047149658203, 23.068269729614258, 113.3505859375, 23.071382522583008, 113.3509292602539, 23.074438095092773, 113.35148620605469, 23.077430725097656, 113.35225677490234, 23.080350875854492, 113.35322570800781, 23.083194732666016, 113.35440063476562, 23.085952758789062, 113.35575103759766, 23.088619232177734, 113.35729217529297, 23.0911865234375, 113.35741424560547, 23.090986251831055, 113.3590316772461, 23.088815689086914, 113.36056518554688, 23.087902069091797, 113.36211395263672, 23.086074829101562, 113.36491394042969, 23.082151412963867, 113.36734771728516, 23.079204559326172, 113.36920166015625, 23.074848175048828, 113.37161254882812, 23.071741104125977, 113.37272644042969, 23.06490707397461, 113.37474060058594, 23.06008529663086, 113.37175750732422, 23.052671432495117, 113.35896301269531, 23.04996109008789});
        slicesData.add(uni3);
        printPoints(uni3);

        // ?
        Polygon uni4 = repairCoordsToPolygon(new double[]{113.369024, 23.054849, 113.377073, 23.050659, 113.3816, 23.057244, 113.371899, 23.059571});
        slicesData.add(uni4);
        printPoints(uni4);

        // ?
        Polygon uni5 = repairCoordsToPolygon(new double[]{113.396189, 23.087766, 113.40395, 23.079122, 113.415448, 23.082779, 113.405531, 23.092487});
        slicesData.add(uni5);
        printPoints(uni5);

        // ?
        Polygon uni6 = repairCoordsToPolygon(new double[]{113.39276,23.085758,113.395491,23.082633,113.402318,23.086955,113.40893,23.078311,113.397503,23.071462,113.38773,23.083431,113.39276,23.085758});
        slicesData.add(uni6);
        printPoints(uni6);

        // ?
        Polygon uni7 = repairCoordsToPolygon(new double[]{113.36856,23.078693,113.376393,23.063864,113.395437,23.079624,113.391413,23.082284,113.37395,23.073274,113.377759,23.077231,113.374453,23.081885,113.367985,23.079425,113.36856,23.078693});
        slicesData.add(uni7);
        printPoints(uni7);

        // 多边形圆，计算后的多边形只会在此之内
        Polygon circlePolygon = JTurfTransformation.circle(Point.fromLngLat(113.3993, 23.06827), 5000, 50, Units.METERS);

        DoubleHolder interAreaHolder = new DoubleHolder();
        Polygon ccc = unionAllPolygonRegion(area, slicesData, circlePolygon, interAreaHolder);
        if (ccc == area) {
            System.out.println("合并后的多边形为空~");
            return;
        }

        // BUG FIX : slicesData 需要获取到被合并的切片集合

        // total area
        double totalArea = totalArea(area, slicesData);
        double intersectArea = interAreaHolder.value;
        double unionArea = JTurfMeasurement.area(ccc);

        System.out.println("所有区域总面积和：" + BigDecimal.valueOf(totalArea / 1000000).setScale(2, RoundingMode.HALF_UP).toString() + "平方公里");
        System.out.println("交集区域总面积和：" + BigDecimal.valueOf(intersectArea / 1000000).setScale(2, RoundingMode.HALF_UP).toString() + "平方公里");
        System.out.println("合并区域的总面积：" + BigDecimal.valueOf(unionArea / 1000000).setScale(2, RoundingMode.HALF_UP).toString() + "平方公里");
        System.out.println("空白区域的总面积：" + BigDecimal.valueOf((unionArea - (totalArea - intersectArea)) / 1000000).setScale(2, RoundingMode.HALF_UP).toString() + "平方米");

        System.out.println("====" + ccc.coordinates().size());

        StringBuilder buf = new StringBuilder();
        buf.append("[[");

        List<Point> pointList = ccc.coordinates();
        for (int i = 0, size = pointList.size(); i < size; i++) {
            if (i > 0) {
                buf.append(",");
            }
            Point p = pointList.get(i);
            buf.append("[").append(p.getX()).append(",").append(p.getY()).append("]");
        }
        buf.append("]]");

        System.out.println(buf);
    }

    /**
     * 将多个多边形与指定的多边形合并，返回多块不相连的坐标点集合
     *
     * @param area            指定的多边形
     * @param slicesData      其他待合并多边形
     * @param circle          合并后的多边形不会超出此圆的范围
     * @param interAreaHolder 交集区域的总面积存储容器
     * @return 多个不相连坐标点集合
     */
    private static Polygon unionAllPolygonRegion(Polygon area, List<Polygon> slicesData, Polygon circle, DoubleHolder interAreaHolder) {
        List<List<Point>> sliceUnionAreas = new ArrayList<>(); // 切片围栏的并集最终合并图形
        List<Polygon> remainingList = new ArrayList<>();

        boolean hasUnion = unionRemainingPolygonRegion(area, slicesData, circle, sliceUnionAreas, remainingList, interAreaHolder);

        // 如果最终合并图集为空中，返回空。
        if (sliceUnionAreas.isEmpty()) {
            return area;
        } else {
            // 否则获取面积最大的一块围栏
            Polygon maxPolygon = getMaxAreaPolygon(sliceUnionAreas.stream().map(Polygon::fromLngLats).collect(Collectors.toList()));
            if (remainingList.isEmpty() || !hasUnion) {
                return maxPolygon;
            } else {
                return unionAllPolygonRegion(maxPolygon, remainingList, circle, interAreaHolder);
            }
        }
    }

    /**
     * 将剩余的多边形与指定的多边形合并，返回多块不相连的坐标点集合
     *
     * @param area                指定的多边形
     * @param slicesData          其他待合并多边形
     * @param circle              合并后的多边形不会超出此圆的范围
     * @param sliceUnionAreas     合并后存储的多个多边形集合容器
     * @param remainingList       记录剩余未合并的多边形
     * @param interAreaHolder     交集区域的总面积存储容器
     * @return 如果本次执行有交集图形，则返回true
     */
    private static boolean unionRemainingPolygonRegion(Polygon area, List<Polygon> slicesData, Polygon circle, List<List<Point>> sliceUnionAreas, List<Polygon> remainingList, DoubleHolder interAreaHolder) {
        // 是否交集过（用于是否需要与半径公里圆计算交集）
        boolean hasUnion = false;

        Geometry sliceUnionArea = area;
        for (Polygon slice : slicesData) {
            // 交集
            Geometry polygonIntersect = JTurfTransformation.intersect(sliceUnionArea, slice);
            // 如果存在交集，则需要进行联合或者裁剪
            if (polygonIntersect != null) {
                // 合集
                sliceUnionArea = JTurfTransformation.union(sliceUnionArea, slice);
                interAreaHolder.value += JTurfMeasurement.area(polygonIntersect);

                hasUnion = true;

                // 则记录合并后的结果集
                List<Polygon> piecePolygons = null;
                if (sliceUnionArea.type() == GeometryType.MULTI_POLYGON) {
                    List<Polygon> tempPolygons = new ArrayList<>();

                    for (List<Point> coordinate : MultiPolygon.multiPolygon(sliceUnionArea).coordinates()) {
                        List<Polygon> calcTempPolygons = calcCircleDifferenceFromPoints(coordinate, circle);
                        if (calcTempPolygons.size() > 0) {
                            tempPolygons.addAll(calcTempPolygons);
                        }
                    }
                    piecePolygons = tempPolygons;
                } else {
                    piecePolygons = repairSplitAndUnionPolygon(turfToPointList(sliceUnionArea));
                    // 与圆做交集
                    piecePolygons = calcCircleDifferenceData(piecePolygons, circle);
                }

                // 这里还需要判定是否重叠，如果完全重叠则需要拆分区块
                List<List<Point>> pieces = new ArrayList<>();
                for (Polygon polygon : piecePolygons) {
                    // 面积符合要求
                    if (!checkLessMinSquareMetres(polygon)) {
                        pieces.add(turfToPointList(polygon));
                    }
                }

                sliceUnionAreas.addAll(pieces);
            } else {
                remainingList.add(slice);
            }
        }

        return hasUnion;
    }

    /**
     * 计算多边形与圆的交集
     *
     * @param piecePolygons 要与圆进行交集的多边形集合
     * @param circle        多边形圆
     * @return 返回交集之后的多边形组合
     */
    private static List<Polygon> calcCircleDifferenceData(List<Polygon> piecePolygons, Polygon circle) {
        List<Polygon> tempPolygons = new ArrayList<>();
        for (Polygon polygon : piecePolygons) {
            Geometry circleIntersectArea = JTurfTransformation.intersect(circle, polygon);
            if (circleIntersectArea != null) {
                // 拆分不相邻的区块
                List<Polygon> circlePiecePolygons = repairSplitAndUnionPolygon(turfToPointList(circleIntersectArea));

                tempPolygons.addAll(circlePiecePolygons);
            }
        }
        return tempPolygons;
    }

    /**
     * 将传入的坐标点与原进行交集处理，内部首先会对坐标点进行点修复处理
     *
     * @param piecePoints 要处理的坐标点
     * @param circle      多边形圆
     * @return 返回多边形集合
     */
    private static List<Polygon> calcCircleDifferenceFromPoints(List<Point> piecePoints, Polygon circle) {
        List<Polygon> piecePolygons = repairSplitAndUnionPolygon(piecePoints);

        return calcCircleDifferenceData(piecePolygons, circle);
    }

    /**
     * 修复坐标，如异常的坐标，重复的坐标以及精度计算等
     *
     * @param points 要修复的坐标点集合
     * @return 修复后生成的多边形
     */
    private static Polygon repairCoordsToPolygon(double[] points) {
        Polygon polygon = Polygon.fromLngLats(points);

        return repairCoordsToPolygon(polygon);
    }

    /**
     * 修复坐标，如异常的坐标，重复的坐标以及精度计算等
     *
     * @param pointList 要修复的坐标点集合
     * @return 修复后生成的多边形
     */
    private static Polygon repairCoordsToPolygon(List<Point> pointList) {
        Polygon polygon = Polygon.fromLngLats(pointList);

        return repairCoordsToPolygon(polygon);
    }

    /**
     * 修复坐标，如异常的坐标，重复的坐标以及精度计算等
     *
     * @param polygon 要修复的多边形
     * @return 修复后生成的多边形
     */
    private static Polygon repairCoordsToPolygon(Polygon polygon) {
        // 控制坐标精度
        polygon = JTurfCoordinateMutation.truncate(polygon, 10, true);

        return Polygon.fromLngLats(amendPoints(polygon.coordinates(), 2));
    }

    /**
     * 将多边形展开返回点集合
     *
     * @param geometry 多边形
     * @return 返回坐标点集合
     */
    private static List<Point> turfToPointList(Geometry geometry) {
        if (geometry instanceof MultiPolygon) {
            // 将多维坐标展开
            return completionLastPoint(MultiPolygon.multiPolygon(geometry).coordinates().stream().flatMap(Collection::stream).collect(Collectors.toList()));
        } else {
            return completionLastPoint(Polygon.polygon(geometry).coordinates());
        }
    }

    /**
     * 如果尾坐标点与首坐标点不一致，则将首坐标点复制一份并防止到尾部。
     *
     * @param pointList 要补充尾坐标点的集合
     * @return 返回新的点集合
     */
    private static List<Point> completionLastPoint(List<Point> pointList) {
        Point first = pointList.get(0), last = pointList.get(pointList.size() - 1);

        double firstLng = first.getLongitude(), firstLat = first.getLatitude(), lastLng = last.getLongitude(), lastLat = last.getLatitude();
        if (firstLng != lastLng || firstLat != lastLat) {
            pointList.add(Point.fromLngLat(firstLng, firstLat));
        }

        return pointList;
    }

    /**
     * 修复坐标
     *
     * @param pointList 要修复的坐标点
     * @return Polygon
     */
    private static Polygon repairCoords(List<Point> pointList) {
        if (pointList.size() < 3) {
            return Polygon.fromLngLats(pointList);
        }

        try {
            // 1.修复并创建区块对象
            Polygon polygon = createPolygonAfterRepair(pointList);
            // 2.清除多边形不相交的面积较小的区块
            return clearNoIntersectPolygon(polygon);
        } catch (Exception e) {
            e.printStackTrace();
            return Polygon.fromLngLats(pointList);
        }
    }

    /**
     * 修复并合并重叠区块，注意返回的是N组点集合
     *
     * @param pointList 要修复的坐标点
     * @return List<Polygon>
     */
    private static List<Polygon> repairSplitAndUnionPolygon(List<Point> pointList) {
        if (pointList.size() < 3) {
            return Collections.singletonList(Polygon.fromLngLats(pointList));
        }

        try {
            // 1.修复并创建区块对象
            Polygon polygon = createPolygonAfterRepair(pointList);
            // 2.切割并合并
            return splitAndUnionPolygon(polygon);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.singletonList(Polygon.fromLngLats(pointList));
        }
    }

    /**
     * 创建修复后的Polygon对象
     *
     * @param pointList 围栏多边形坐标点
     * @return Polygon
     */
    private static Polygon createPolygonAfterRepair(List<Point> pointList) {
        Polygon polygon = Polygon.fromLngLats(pointList);
        // 1.去重坐标
        polygon = JTurfCoordinateMutation.truncate(polygon, 10, true);
        // 2.将相距太近的点合并
        List<Point> newPoints = amendPoints(polygon.coordinates(), 2);
        // 3.如果坐标有变化，则生成新的多边形
        if (newPoints != polygon.coordinates()) {
            return Polygon.fromLngLats(newPoints);
        }
        return polygon;
    }

    /**
     * 基本原理就是将两个过近的相邻点合并，修复多边形坐标点，防止过于相邻的坐标点发生相交造成无法正常提交
     *
     * @param pointList 围栏多边形坐标点
     * @param distance  米，默认值2米
     * @return List<Point>
     */
    private static List<Point> amendPoints(List<Point> pointList, int distance) {
        if (pointList.size() < 3) {
            return pointList;
        }

        List<Point> newPoints = new ArrayList<>();
        for (int i = 1, size = pointList.size(); i < size; i++) {
            Point from = pointList.get(i - 1), to = pointList.get(i);
            if (JTurfMeasurement.rhumbDistance(from, to, Units.METERS) > distance) {
                newPoints.add(pointList.get(i - 1));
            }
        }

        if (newPoints.size() < 3) {
            return pointList;
        }

        return completionLastPoint(newPoints);
    }

    /**
     * 将一块图形中不相交的区块进行拆分成多块，相交的区块合并成一块
     *
     * @param polygon 多边形图形
     * @return 返回多组多边形集合
     */
    private static List<Polygon> splitAndUnionPolygon(Polygon polygon) {
        // 1.切割多边形图形
        List<Polygon> pieces = splitPolygon(polygon);
        // 未切割出来的，则直接返回
        if (pieces == null || pieces.isEmpty()) {
            return null;
        }
        if (pieces.size() == 1) {
            return pieces;
        }
        // 2.将图形尽量合并成全新的图形，如果不相交则自成一块
        return unionAndReturnPolygon(pieces);
    }

    /**
     * 将相交的区块合并，不相邻的区块自成一片，返回一组图形数据
     *
     * @param polygons 要组合的图形集合
     * @return 返回多组多边形集合
     */
    private static List<Polygon> unionAndReturnPolygon(List<Polygon> polygons) {
        List<Polygon> p = new ArrayList<>();
        List<Polygon> needUnionList = new ArrayList<>(); // 需要合并的集合
        Set<Integer> unionIndex = new HashSet<>();

        // 首先检索出完全没有任何交集的区块
        for (int i = 0, size = polygons.size(); i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                Geometry newPolyGon = JTurfTransformation.union(polygons.get(i), polygons.get(j));
                if (newPolyGon.type() == GeometryType.POLYGON) {
                    unionIndex.add(i);
                    unionIndex.add(j);
                }
            }
        }

        // 再循环一遍，将未有任何合并的也添加到p中
        for (int i = 0, size = polygons.size(); i < size; i++) {
            if (!unionIndex.contains(i)) {
                p.add(polygons.get(i));
            } else {
                needUnionList.add(polygons.get(i));
            }
        }

        // 不断吞噬，直至完全无法合并
        boolean hasUnion = true;
        while (hasUnion) {
            List<Polygon> temp = new ArrayList<>();
            hasUnion = false;
            for (int i = 0, size = needUnionList.size(); i < size - 1; i++) {
                Polygon unionPolygon = needUnionList.get(i);
                if (!p.isEmpty()) {
                    for (int j = i + 1; j < size; j++) {
                        Geometry newPolyGon = JTurfTransformation.union(unionPolygon, p.get(j));
                        if (newPolyGon.type() == GeometryType.POLYGON) {
                            unionPolygon = Polygon.polygon(newPolyGon);
                            hasUnion = true;
                        }
                    }
                }
                temp.add(unionPolygon);
            }
            needUnionList = temp;
        }

        // 合并到p集合中
        p.addAll(needUnionList);

        return p;
    }

    /**
     * 将一块图形中不相邻的图形进行拆分成多块
     *
     * @param polygon 要拆分的多边形
     * @return 返回拆分后的多边形集合
     */
    private static List<Polygon> splitPolygon(Polygon polygon) {
        // 1.获取自相交点
        List<Point> kinksPoints = JTurfMisc.kinks(polygon);
        // 没有自相交点，则返回
        if (kinksPoints == null || kinksPoints.isEmpty()) {
            return Collections.singletonList(polygon);
        }
        // 2.过滤重复的自相交的点
        List<Point> intersectPoints = filterDuplicatePoints(kinksPoints);
        // 3.获取完全不相交的线作为切割线
        Line cutLine = getNoInAreaLine(polygon, intersectPoints);
        if (cutLine == null) {
            return Collections.singletonList(polygon);
        }
        // 4.切割多边形图形
        return cutPolygon(polygon, cutLine, 1, Units.METERS);
    }

    /**
     * 根据cutLine对指定区域进行切割
     *
     * @param polygon       需要切割的图形
     * @param cutLine       切割线
     * @param tolerance     切割线生成polygon图形的宽度
     * @param toleranceType 切割线polygon图形的宽度的单位
     * @return 返回被切割之后的多边形集合
     */
    private static List<Polygon> cutPolygon(Polygon polygon, Line cutLine, int tolerance, Units toleranceType) {
        // 1. 计算交点，并把线的点合并
        List<Point> lineIntersect = JTurfMisc.lineIntersect(cutLine, polygon);
        // 2. 计算线的缓冲区
        Geometry lineBuffer = JTurfTransformation.buffer(cutLine, tolerance, toleranceType);
        // 3. 计算线缓冲和多边形的difference，返回"MultiPolygon"，所以将其拆开
        Geometry _body = JTurfTransformation.difference(polygon, lineBuffer);
        // 4. 将拆分后的多块存储到pieces中
        List<Polygon> pieces = new ArrayList<>();
        if (_body instanceof Polygon) {
            pieces.add(Polygon.polygon(_body));
        } else {
            for (List<Point> pointList : MultiPolygon.multiPolygon(_body).coordinates()) {
                pieces.add(Polygon.fromLngLats(pointList));
            }
        }
        // 5. 处理点数据，其实就是将相交点放入到piece中
        for (Polygon piece : pieces) {
            List<Point> pointList = piece.coordinates();
            for (int c = 0, size = pointList.size(); c < size; c++) {
                Point p = pointList.get(c);
                for (Point point : lineIntersect) {
                    if (JTurfMeasurement.distance(point, p) <= tolerance * 2) {
                        pointList.set(c, point);
                    }
                }
            }
        }
        // 6.过滤掉重复点
        List<Polygon> newPieces = new ArrayList<>(pieces.size());
        for (Polygon piece : pieces) {
            Polygon c = JTurfCoordinateMutation.cleanCoords(piece, true);
            if (c == null) {
                continue;
            }
            newPieces.add(c);
        }

        return newPieces;
    }

    /**
     * 获取不在区域中的线条
     *
     * @param polygon   多边形图形
     * @param pointList 传入的是相交点集合
     * @return 返回切割线
     */
    private static Line getNoInAreaLine(Polygon polygon, List<Point> pointList) {
        for (int i = 0, size = pointList.size(); i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                Line line = Line.fromLngLats(pointList.get(i), pointList.get(j));
                // 这里先必须要缩放一下，保持在围栏点内，否则计算包含是没有用的
                Line scaleLine = JTurfTransformation.transformScale(line, 0.9);
                // 这里只要算出一条在外围的线即可保证能将不相交的片切出来
                if (JTurfBooleans.booleanDisjoint(polygon, scaleLine)) {
                    return line;
                }
            }
        }
        return null;
    }

    /**
     * 清除一个多边形不相交的面积小的区块，因为推送给饿百和美团这种被切割有问题的区块是不会成功的
     *
     * @param polygon 要修复的多边形
     * @return Polygon
     */
    private static Polygon clearNoIntersectPolygon(Polygon polygon) {
        // 1.切割多边形图形
        List<Polygon> pieces = splitPolygon(polygon);
        // 未切割出来的，则直接返回
        if (pieces == null || pieces.isEmpty()) {
            return null;
        }
        // 2. 将面积最大的模块与其他模块合并并排除掉其他不相邻的模块
        return unionAndReturnMaxPolygon(pieces);
    }

    /**
     * 计算出面积最大的模块并其相交的模块合并，这样就能将不相交的排除掉了
     *
     * @param polygons 合并多块多边形区域并返回最大的一块
     * @return Polygon
     */
    private static Polygon unionAndReturnMaxPolygon(List<Polygon> polygons) {
        Polygon maxAreaPolygon = getMaxAreaPolygon(polygons);
        Polygon unionPolygon = maxAreaPolygon;

        // 两两判断是否相交
        for(Polygon polygon : polygons) {
            if(polygon == maxAreaPolygon) {
                continue;
            }
            Geometry newPolyGon = JTurfTransformation.union(unionPolygon, polygon);
            if (newPolyGon.type() == GeometryType.POLYGON) {
                unionPolygon = Polygon.polygon(newPolyGon);
            }
        }
        return unionPolygon;
    }

    /**
     * 计算多个区块面积最大的区块
     *
     * @param polygons 要计算的多边形集合
     * @return 返回面积最大的多边形
     */
    private static Polygon getMaxAreaPolygon(List<Polygon> polygons) {
        double maxArea = -1;
        Polygon maxPolygon = null;

        for (Polygon polygon : polygons) {
            double area = JTurfMeasurement.area(polygon);
            if (area > maxArea) {
                maxPolygon = polygon;
                maxArea = area;
            }
        }
        return maxPolygon;
    }

    /**
     * 检查是否小于最小要求的区块面积
     *
     * @param geometry 要计算的多边形
     * @return boolean
     */
    private static boolean checkLessMinSquareMetres(Geometry geometry) {
        return JTurfMeasurement.area(geometry) < 2000;
    }

    /**
     * 处理点性质的重复点集合
     *
     * @param points 点集合
     * @return 处理完的新的集合
     */
    private static List<Point> filterDuplicatePoints(List<Point> points) {
        int size = points.size();
        if (size == 2 && !JTurfHelper.equals(points.get(0), points.get(1))) {
            return points;
        }

        Set<String> existing = new HashSet<>();
        List<Point> newPoints = new ArrayList<>();
        for (Point point : points) {
            String key = point.getLongitude() + "-" + point.getLatitude();
            if (existing.add(key)) {
                newPoints.add(point);
            }
        }

        return newPoints;
    }

    /**
     * 求出多个多边形区域的总大小（重叠区域会重复计算，后面的逻辑会处理掉重叠区域）
     * @param area       指定的多边形
     * @param slicesData 其他待合并多边形
     * @return double
     */
    private static double totalArea(Polygon area, List<Polygon> slicesData) {
        double a = JTurfMeasurement.area(area);

        if (slicesData != null && !slicesData.isEmpty()) {
            for (Polygon polygon : slicesData) {
                a += JTurfMeasurement.area(polygon);
            }
        }

        return a;
    }

    /**
     * 检查多边形是否自相交
     *
     * @param polygon 多边形
     * @return 如果自相交则返回true，否则返回false
     */
    private static boolean isKinds(Polygon polygon) {
        // 获取自相交点
        List<Point> kinksPoints = JTurfMisc.kinks(polygon);
        // 没有自相交点，则返回
        return kinksPoints != null && kinksPoints.size() > 0;
    }

    private static void printPoints(Polygon polygon) {
        List<Point> points = polygon.coordinates();

        StringBuilder buf = new StringBuilder();
        buf.append("[");

        for (int i = 0, size = points.size(); i < size; i++) {
            if (i > 0) {
                buf.append(",");
            }
            Point p = points.get(i);
            buf.append("[").append(p.getX()).append(",").append(p.getY()).append("]");
        }

        buf.append("]");

        System.out.println(buf);
    }

}
