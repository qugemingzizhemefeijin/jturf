package com.cgzz.mapbox.test.utils;

import com.cgzz.mapbox.jturf.*;
import com.cgzz.mapbox.jturf.enums.Units;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.*;
import com.cgzz.mapbox.test.model.PolygonMergeContext;
import com.cgzz.mapbox.test.model.PolygonMergeInformation;

import java.util.*;
import java.util.stream.Collectors;

public final class JTurfTools {

    private JTurfTools() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 计算指定圆是否与传入的坐标的交集
     *
     * @param centerPoint 中心点
     * @param radius      圆半径，单位米
     * @param polygon     要交集的多边形
     * @return Polygon 如果返回 empty or null 则代表没有交集，如果交集后有多块围栏，则返回面积最大的一块
     */
    public static Polygon calcCircleAndPolygonIntersection(Point centerPoint, Integer radius, Polygon polygon) {
        Polygon circle = JTurfTransformation.circle(centerPoint, radius, 100, Units.METERS);

        Geometry circleIntersectArea = JTurfTransformation.intersect(circle, polygon);
        if (circleIntersectArea != null) {
            List<Point> pointList = turfToPointList(circleIntersectArea);
            if (pointList.size() >= 4) {
                // 拆分不相邻的区块
                List<Polygon> circlePiecePolygons = repairSplitAndUnionPolygon(pointList);

                if (circlePiecePolygons != null && !circlePiecePolygons.isEmpty()) {
                    return getMaxAreaPolygon(circlePiecePolygons);
                }
            }
        }
        return null;
    }

    /**
     * 将多块多边形合并成一块多边形
     *
     * @param centerPoint            中心点
     * @param radius                 圆的半径，米
     * @param currPolygon            当前主多边形
     * @param polygonList            要被合并的多块多边形
     * @param minSliceArea           舍弃被合并的时候形成区块的小于此面积，平方米
     * @param maxBlankArea           合并的时候形成的被填充的最大允许的空白区域
     * @param needFstMoreThanBlankId 是否需要返回第一块超过最大填充空白区域的多边形ID
     * @return PolygonMergeInformation
     */
    public static PolygonMergeInformation mergeAllPolygon(Point centerPoint, int radius, Polygon currPolygon, List<Feature<Polygon>> polygonList, double minSliceArea, double maxBlankArea, boolean needFstMoreThanBlankId) {
        // 没有选中的切片，则返回自己
        if (polygonList == null || polygonList.isEmpty()) {
            return new PolygonMergeInformation(currPolygon, null);
        }
        // 多边形圆，计算后的多边形只会在此之内
        Polygon circle = JTurfTransformation.circle(centerPoint, radius, 100, Units.METERS);

        // 当前主多边形的面积
        double currMainArea = JTurfMeasurement.area(currPolygon);
        // 合并的上下文变量传递
        PolygonMergeContext mergeContext = new PolygonMergeContext(currMainArea, minSliceArea, maxBlankArea, needFstMoreThanBlankId);

        Polygon unionPolygon = unionAllPolygonRegion(currPolygon, polygonList, circle, mergeContext);
        // 跟本没合并，返回自己
        if (unionPolygon == currPolygon) {
            return new PolygonMergeInformation(currPolygon, polygonList.stream().filter(polygon -> polygon.id() != null).map(Feature::id).collect(Collectors.toList()));
        }

        // 计算所有多边形的总面积
        double totalArea = totalArea(currPolygon, polygonList);

        // 计算合并之后的多边形和面积等信息
        return calMergeFencePolygon(unionPolygon, totalArea, mergeContext);
    }

    /**
     * 修复坐标信息
     *
     * @param pointList 坐标点集合
     * @return 返回多边形
     */
    public static Polygon repairCoords(List<Point> pointList) {
        if (pointList == null || pointList.size() < 3) {
            return null;
        }

        // 1.修复并创建区块对象
        Polygon polygon = createPolygonAfterRepair(pointList);
        // 2.清除多边形不相交的面积较小的区块
        return clearNoIntersectPolygon(polygon);
    }

    /**
     * 创建修复后的Polygon对象
     *
     * @param pointList 多边形多边形坐标点
     * @return Polygon
     */
    private static Polygon createPolygonAfterRepair(List<Point> pointList) {
        Polygon polygon = Polygon.fromOuterInner(pointList);
        // 1.去重坐标
        polygon = JTurfCoordinateMutation.truncate(polygon, 10, 2, true);

        List<Point> pList = polygon.coordinates().stream().flatMap(Collection::stream).collect(Collectors.toList());
        // 2.将相距太近的点合并
        List<Point> newPoints = amendPoints(pList, 2);
        // 3.如果坐标有变化，则生成新的多边形
        if (newPoints != pList) {
            return Polygon.fromOuterInner(newPoints);
        }
        return polygon;
    }

    /**
     * 基本原理就是将两个过近的相邻点合并，修复多边形坐标点，防止过于相邻的坐标点发生相交造成无法正常提交
     *
     * @param pointList 多边形多边形坐标点
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
     * 将一块图形中不相邻的图形进行拆分成多块
     *
     * @param polygon 要拆分的多边形
     * @return 返回拆分后的多边形集合
     */
    private static List<Polygon> splitPolygon(Polygon polygon) {
        // 1.获取自相交点
        FeatureCollection<Point> kinksPoints = JTurfMisc.kinks(polygon);
        // 没有自相交点，则返回
        if (kinksPoints == null || kinksPoints.isEmpty()) {
            return Collections.singletonList(polygon);
        }
        // 2.过滤重复的自相交的点
        List<Point> intersectPoints = filterDuplicatePoints(kinksPoints.toItemList());
        // 3.获取完全不相交的线作为切割线
        LineString cutLine = getNoInAreaLine(polygon, intersectPoints);
        if (cutLine == null) {
            return Collections.singletonList(polygon);
        }
        // 4.切割多边形图形
        return cutPolygon(polygon, cutLine, 1, Units.METERS);
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
            String key = point.longitude() + "-" + point.latitude();
            if (existing.add(key)) {
                newPoints.add(point);
            }
        }

        return newPoints;
    }

    /**
     * 获取不在区域中的线条
     *
     * @param polygon   多边形图形
     * @param pointList 传入的是相交点集合
     * @return 返回切割线
     */
    private static LineString getNoInAreaLine(Polygon polygon, List<Point> pointList) {
        for (int i = 0, size = pointList.size(); i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                LineString line = LineString.fromLngLats(pointList.get(i), pointList.get(j));
                // 这里先必须要缩放一下，保持在多边形点内，否则计算包含是没有用的
                LineString scaleLine = JTurfTransformation.transformScale(line, 0.9);
                // 这里只要算出一条在外围的线即可保证能将不相交的片切出来
                if (JTurfBooleans.booleanDisjoint(polygon, scaleLine)) {
                    return line;
                }
            }
        }
        return null;
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
    private static List<Polygon> cutPolygon(Polygon polygon, LineString cutLine, int tolerance, Units toleranceType) {
        // 1. 计算交点，并把线的点合并
        List<Point> lineIntersect = JTurfMisc.lineIntersect(cutLine, polygon).toItemList();
        // 2. 计算线的缓冲区
        Geometry lineBuffer = JTurfTransformation.buffer(cutLine, tolerance, toleranceType);
        // 3. 计算线缓冲和多边形的difference，返回"MultiPolygon"，所以将其拆开
        Geometry _body = JTurfTransformation.difference(polygon, lineBuffer);
        // 4. 将拆分后的多块存储到pieces中
        List<Polygon> pieces = new ArrayList<>();
        if (_body instanceof Polygon) {
            pieces.add(Polygon.polygon(_body));
        } else {
            for (List<List<Point>> pointList : MultiPolygon.multiPolygon(_body).coordinates()) {
                pieces.add(Polygon.fromLngLats(pointList));
            }
        }
        // 5. 处理点数据，其实就是将相交点放入到piece中
        for (Polygon piece : pieces) {
            List<Point> pointList = piece.coordinates().get(0);
            for (int c = 0, size = pointList.size(); c < size; c++) {
                Point p = pointList.get(c);
                for (Point point : lineIntersect) {
                    if (JTurfMeasurement.distance(point, p, toleranceType) <= tolerance * 2) {
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
     * 计算出面积最大的模块并其相交的模块合并，这样就能将不相交的排除掉了
     *
     * @param polygons 合并多块多边形区域并返回最大的一块
     * @return Polygon
     */
    private static Polygon unionAndReturnMaxPolygon(List<Polygon> polygons) {
        Polygon maxAreaPolygon = getMaxAreaPolygon(polygons);
        Polygon unionPolygon = maxAreaPolygon;

        // 两两判断是否相交
        for (Polygon polygon : polygons) {
            if (polygon == maxAreaPolygon) {
                continue;
            }
            Geometry newPolyGon = JTurfTransformation.union(unionPolygon, polygon);
            if (newPolyGon.geometryType() == GeometryType.POLYGON) {
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
     * 将多个多边形与指定的多边形合并，返回多块不相连的坐标点集合
     *
     * @param area         指定的多边形
     * @param polygonList  其他待合并多边形
     * @param circle       合并后的多边形不会超出此圆的范围
     * @param mergeContext 合并的上下文变量传递
     * @return 多个不相连坐标点集合
     */
    private static Polygon unionAllPolygonRegion(Polygon area, List<Feature<Polygon>> polygonList, Polygon circle, PolygonMergeContext mergeContext) {
        List<Polygon> sliceUnionAreas = new ArrayList<>(); // 切片多边形的并集最终合并图形
        List<Feature<Polygon>> remainingList = new ArrayList<>();

        boolean hasUnion = unionRemainingPolygonRegion(area, polygonList, circle, sliceUnionAreas, remainingList, mergeContext);

        // 如果最终合并图集为空中，返回空。
        if (sliceUnionAreas.isEmpty()) {
            // 如果本次没有任何合并的区块，则判断是否还有未被合并的，则加入到 remainingSliceArea 集合中
            if (!polygonList.isEmpty()) {
                mergeContext.getRemainingPolygon().addAll(polygonList);
            }
            return area;
        } else {
            // 否则获取面积最大的一块多边形
            Polygon maxPolygon = getMaxAreaPolygon(sliceUnionAreas);
            if (remainingList.isEmpty() || !hasUnion) {
                return maxPolygon;
            } else {
                return unionAllPolygonRegion(maxPolygon, remainingList, circle, mergeContext);
            }
        }
    }

    /**
     * 将剩余的多边形与指定的多边形合并，返回多块不相连的坐标点集合
     *
     * @param area            指定的多边形
     * @param slicesData      其他待合并多边形
     * @param circle          合并后的多边形不会超出此圆的范围
     * @param sliceUnionAreas 合并后存储的多个多边形集合容器
     * @param remainingList   记录剩余未合并的多边形
     * @param mergeContext    合并的上下文变量传递
     * @return 如果本次执行有交集图形，则返回true
     */
    private static boolean unionRemainingPolygonRegion(Polygon area, List<Feature<Polygon>> slicesData, Polygon circle, List<Polygon> sliceUnionAreas, List<Feature<Polygon>> remainingList, PolygonMergeContext mergeContext) {
        // 是否交集过（用于是否需要与半径公里圆计算交集）
        boolean hasUnion = false;

        Geometry sliceUnionArea = area;
        for (Feature<Polygon> slice : slicesData) {
            // 交集
            Geometry polygonIntersect = JTurfTransformation.intersect(sliceUnionArea, slice);
            // 如果存在交集，则需要进行联合或者裁剪
            if (polygonIntersect != null) {
                // 合集
                sliceUnionArea = JTurfTransformation.union(sliceUnionArea, slice);
                mergeContext.setInterArea(mergeContext.getInterArea() + JTurfMeasurement.area(polygonIntersect));

                hasUnion = true;
                // 记录被合并的切片
                mergeContext.getMergePolygon().add(slice);

                // 则记录合并后的结果集
                List<Polygon> piecePolygons = null;
                if (sliceUnionArea.geometryType() == GeometryType.MULTI_POLYGON) {
                    List<Polygon> tempPolygons = new ArrayList<>();

                    for (List<List<Point>> coordinate : MultiPolygon.multiPolygon(sliceUnionArea).coordinates()) {
                        List<Polygon> calcTempPolygons = calcCircleDifferenceFromPoints(coordinate.stream().flatMap(Collection::stream).collect(Collectors.toList()), circle);
                        if (calcTempPolygons.size() > 0) {
                            tempPolygons.addAll(calcTempPolygons);
                        }
                    }
                    piecePolygons = tempPolygons;
                } else {
                    piecePolygons = repairSplitAndUnionPolygon(turfToPointList(sliceUnionArea));
                    // 与圆做交集[经过检验，不能与圆交集，有可能会稍微超过一点点面积]
                    // piecePolygons = calcCircleDifferenceData(piecePolygons, circle);
                }

                // 这里还需要判定是否重叠，如果完全重叠则需要拆分区块
                List<Polygon> pieces = new ArrayList<>();
                for (Polygon polygon : piecePolygons) {
                    // 面积符合要求
                    if (!checkLessMinSquareMetres(polygon, mergeContext.getMinSliceArea())) {
                        pieces.add(polygon);
                    }
                }

                sliceUnionAreas.addAll(pieces);

                // 每次循环都计算一遍错误，看看到底哪个切片出错误了【只有第一次才需要记录，因为需要报错】
                if (mergeContext.isNeedFstMoreThanBlankId() && mergeContext.getFstMoreThanBlankAreaId() == null) {
                    Polygon maxPolygon = getMaxAreaPolygon(sliceUnionAreas);
                    // 当前已合并形成的多边形
                    double unionArea = JTurfMeasurement.area(maxPolygon);
                    // 当前的面积
                    double totalArea = totalArea(mergeContext.getMainArea(), mergeContext.getMergePolygon());
                    // 当前的空白区域面积
                    double blankArea = unionArea - (totalArea - mergeContext.getInterArea());
                    // 如果超过空白面积，则设置一下第一个超过空白面积的多边形ID
                    if (mergeContext.getMaxBlankArea() > 0 && blankArea > mergeContext.getMaxBlankArea()) {
                        mergeContext.setFstMoreThanBlankAreaId(slice.id());
                    }
                }
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
     * 修复并合并重叠区块，注意返回的是N组点集合
     *
     * @param pointList 要修复的坐标点
     * @return List<Polygon>
     */
    private static List<Polygon> repairSplitAndUnionPolygon(List<Point> pointList) {
        if (pointList.size() < 3) {
            return Collections.singletonList(Polygon.fromOuterInner(pointList));
        }

        try {
            // 1.修复并创建区块对象
            Polygon polygon = createPolygonAfterRepair(pointList);
            // 2.切割并合并
            return splitAndUnionPolygon(polygon);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.singletonList(Polygon.fromOuterInner(pointList));
        }
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
                if (newPolyGon.geometryType() == GeometryType.POLYGON) {
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
                        if (newPolyGon.geometryType() == GeometryType.POLYGON) {
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
     * 计算合并之后的多边形和面积等信息
     *
     * @param unionPolygon 合并后的多边形多边形
     * @param totalArea    所有多边形的总面积，平方米
     * @param mergeContext 合并过程中的上下文变量传递
     * @return
     */
    private static PolygonMergeInformation calMergeFencePolygon(Polygon unionPolygon, double totalArea, PolygonMergeContext mergeContext) {
        // 计算合并后的多边形面积
        double unionArea = JTurfMeasurement.area(unionPolygon);
        // 空白区域的面积
        double blankArea = unionArea - (totalArea - mergeContext.getInterArea());

        PolygonMergeInformation information = new PolygonMergeInformation();
        information.setUnionPolygon(unionPolygon);
        information.setUnionArea(unionArea);
        information.setTotalArea(totalArea);
        information.setInterArea(mergeContext.getInterArea());
        information.setBlankArea(blankArea);
        information.setMergeIdList(mergeContext.getMergePolygon().stream().filter(polygon -> polygon.id() != null).map(Feature::id).collect(Collectors.toList()));
        information.setRemainingIdList(mergeContext.getRemainingPolygon().stream().filter(polygon -> polygon.id() != null).map(Feature::id).collect(Collectors.toList()));
        information.setFstMoreThanBlankAreaId(mergeContext.getFstMoreThanBlankAreaId());

        return information;
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
            return completionLastPoint(MultiPolygon.multiPolygon(geometry)
                    .coordinates()
                    .stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList())
                    .stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList()));
        } else {
            return completionLastPoint(Polygon.polygon(geometry)
                    .coordinates()
                    .stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList()));
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

        double firstLng = first.longitude(), firstLat = first.latitude(), lastLng = last.longitude(), lastLat = last.latitude();
        if (firstLng != lastLng || firstLat != lastLat) {
            pointList.add(Point.fromLngLat(firstLng, firstLat));
        }

        return pointList;
    }

    /**
     * 将多边形拆分为点集合
     *
     * @param polygon 多边形
     * @return 返回点集合
     */
    public static List<Point> toPointList(Polygon polygon) {
        return JTurfFeatureConversion.explode(polygon);
    }

    /**
     * 检查多边形是否自相交
     *
     * @param polygon 多边形
     * @return 如果自相交则返回true，否则返回false
     */
    public static boolean isKinds(Polygon polygon) {
        // 获取自相交点
        FeatureCollection<Point> kinksPoints = JTurfMisc.kinks(polygon);
        // 没有自相交点，则返回
        return kinksPoints != null && kinksPoints.size() > 0;
    }

    /**
     * 计算多边形的面积
     *
     * @param polygon 多边形
     * @return 返回多边形面积，单位平方米
     */
    public static double area(Polygon polygon) {
        return JTurfMeasurement.area(polygon);
    }

    /**
     * 检查是否小于最小要求的区块面积
     *
     * @param geometry 要计算的多边形
     * @param minArea  要求的最小面积
     * @return boolean
     */
    private static boolean checkLessMinSquareMetres(Geometry geometry, double minArea) {
        return JTurfMeasurement.area(geometry) < minArea;
    }

    /**
     * 求出多个多边形区域的总大小（重叠区域会重复计算，后面的逻辑会处理掉重叠区域）
     *
     * @param area        指定的多边形
     * @param polygonList 其他多边形
     * @return double
     */
    private static double totalArea(Polygon area, List<Feature<Polygon>> polygonList) {
        return totalArea(JTurfMeasurement.area(area), polygonList);
    }

    /**
     * 求出多个多边形区域的总大小（重叠区域会重复计算，后面的逻辑会处理掉重叠区域）
     *
     * @param area        指定的多边形
     * @param polygonList 其他多边形
     * @return double
     */
    private static double totalArea(Feature<Polygon> area, List<Feature<Polygon>> polygonList) {
        return totalArea(JTurfMeasurement.area(area), polygonList);
    }

    /**
     * 求出多个多边形区域的总大小（重叠区域会重复计算，后面的逻辑会处理掉重叠区域）
     *
     * @param area        指定多边形的面积
     * @param polygonList 其他多边形
     * @return double
     */
    private static double totalArea(double area, List<Feature<Polygon>> polygonList) {
        double a = area;

        if (polygonList != null && !polygonList.isEmpty()) {
            for (Feature<Polygon> polygon : polygonList) {
                a += JTurfMeasurement.area(polygon);
            }
        }

        return a;
    }

}
