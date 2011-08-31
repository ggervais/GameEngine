package com.ggervais.gameengine.math;

import java.util.ArrayList;
import java.util.List;

public class BezierCurve {
    private Point3D startPoint;
    private Point3D endPoint;
    private List<Point3D> controlPoints;
    private int resolution;

    public BezierCurve(Point3D startPoint, Point3D endPoint, List<Point3D> controlPoints, int resolution) {
        this();
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.controlPoints.addAll(controlPoints);
        this.resolution = resolution;
    }

    public BezierCurve() {
        this.controlPoints = new ArrayList<Point3D>();
    }

    public void clearControlPoints() {
        this.controlPoints.clear();
    }

    public void addControlPoint(Point3D point) {
        this.controlPoints.add(point);
    }

    public List<Point3D> computeCurve(int resolution) {
        this.resolution = resolution;
        return computeCurve();
    }

    public List<Point3D> computeCurve() {
        List<Point3D> curvePoints = new ArrayList<Point3D>();
        for (int i = 0; i <= this.resolution; i++) {
            float ratio = ((i + 0f) / this.resolution);
            List<Point3D> pointsToProcess = new ArrayList<Point3D>();
            pointsToProcess.add(this.startPoint);
            pointsToProcess.addAll(this.controlPoints);
            pointsToProcess.add(this.endPoint);

            while (pointsToProcess.size() > 1) {
                List<Point3D> generatedPoints = new ArrayList<Point3D>();
                for (int pointIndex = 0; pointIndex < pointsToProcess.size(); pointIndex++) {
                    Point3D point = pointsToProcess.get(pointIndex);
                    if (pointIndex < pointsToProcess.size() - 1) {
                        Point3D next = pointsToProcess.get(pointIndex + 1);
                        Vector3D diff = next.sub(point);
                        Point3D newPoint = Point3D.add(point, diff.multiplied(ratio));
                        generatedPoints.add(newPoint);
                    }
                }
                pointsToProcess.clear();
                pointsToProcess.addAll(generatedPoints);
            }

            if (pointsToProcess.size() == 1) {
                curvePoints.add(pointsToProcess.get(0));
            }
        }
        return curvePoints;
    }

}
