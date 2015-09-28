package com.nikhil.view.modelview;

import com.nikhil.space.bezier.path.BezierPoint;
import com.nikhil.util.modal.UtilPoint;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 * Created by NikhilVerma on 24/08/15.
 */
public class GraphicalBezierPoint extends BezierPoint {

    private static final double POINT_CIRCLE_RADIUS=3;
    private static final Paint POINT_COLOR= Color.BLUE;
    private static final Double CONTROL_LINK_DASHES=5d;
    private static final Double CURVE_DASHES=2d;

    private CubicCurve cubicCurve;
    private Rectangle anchorPointRect;
    private Circle previousControlPointCircle;
    private Line previousControlLine;
    private Circle nextControlPointCircle;
    private Line nextControlLine;

    public GraphicalBezierPoint(UtilPoint anchorPoint, CubicCurve cubicCurve) {
        super(anchorPoint);
        this.cubicCurve = cubicCurve;
        initializeGraphics(anchorPoint);
    }

    private void initializeGraphics(UtilPoint anchorPoint) {
        if(this.cubicCurve!=null){
            this.cubicCurve.getStrokeDashArray().add(CURVE_DASHES);
        }
        anchorPointRect =new Rectangle();
        anchorPointRect.setWidth(2 * POINT_CIRCLE_RADIUS);
        anchorPointRect.setHeight(2*POINT_CIRCLE_RADIUS);
        anchorPointRect.setFill(POINT_COLOR);
        previousControlPointCircle=new Circle();
        previousControlPointCircle.setRadius(POINT_CIRCLE_RADIUS);
        previousControlPointCircle.setFill(POINT_COLOR);
        previousControlLine=new Line();
        nextControlPointCircle=new Circle();
        nextControlLine=new Line();
        setAnchorPoint(anchorPoint);
        setControlPointWithPrevious(new UtilPoint(0, 0));
        setControlPointWithNext(new UtilPoint(0, 0));
        previousControlPointCircle.setVisible(false);
        previousControlLine.setVisible(false);
        previousControlLine.getStrokeDashArray().add(CONTROL_LINK_DASHES);
        nextControlLine.setVisible(false);
        nextControlLine.getStrokeDashArray().add(CONTROL_LINK_DASHES);
        nextControlPointCircle.setVisible(false);
        nextControlPointCircle.setRadius(POINT_CIRCLE_RADIUS);
        nextControlPointCircle.setFill(POINT_COLOR);
    }

    public Rectangle getAnchorPointRect() {
        return anchorPointRect;
    }

    public void setAnchorPointRect(Rectangle anchorPointRect) {
        this.anchorPointRect = anchorPointRect;
    }

    public CubicCurve getCubicCurve() {
        return cubicCurve;
    }

    public void setCubicCurve(CubicCurve cubicCurve) {
        this.cubicCurve = cubicCurve;
    }

    public Circle getPreviousControlPointCircle() {
        return previousControlPointCircle;
    }

    public void setPreviousControlPointCircle(Circle previousControlPointCircle) {
        this.previousControlPointCircle = previousControlPointCircle;
    }

    public Line getPreviousControlLine() {
        return previousControlLine;
    }

    public void setPreviousControlLine(Line previousControlLine) {
        this.previousControlLine = previousControlLine;
    }

    public Circle getNextControlPointCircle() {
        return nextControlPointCircle;
    }

    public void setNextControlPointCircle(Circle nextControlPointCircle) {
        this.nextControlPointCircle = nextControlPointCircle;
    }

    public Line getNextControlLine() {
        return nextControlLine;
    }

    public void setNextControlLine(Line nextControlLine) {
        this.nextControlLine = nextControlLine;
    }

    @Override
    public void setAnchorPoint(UtilPoint anchorPoint) {
        super.setAnchorPoint(anchorPoint);
        double x=anchorPoint.getX();
        double y=anchorPoint.getY();
//        anchorPointRect.setCenterX(x);
//        anchorPointRect.setCenterY(y);
        anchorPointRect.setLayoutX(x-anchorPointRect.getWidth()/2);
        anchorPointRect.setLayoutY(y-anchorPointRect.getHeight()/2);

        previousControlLine.setStartX(x);
        previousControlLine.setStartY(y);
        nextControlLine.setStartX(x);
        nextControlLine.setStartY(y);
    }

    @Override
    public void setControlPointWithPrevious(UtilPoint controlPointWithPrevious) {
        super.setControlPointWithPrevious(controlPointWithPrevious);
        float absoluteX = (float) (getAnchorPoint().getX() + controlPointWithPrevious.getX());
        float absoluteY = (float) (getAnchorPoint().getY() + controlPointWithPrevious.getY());
        if(cubicCurve !=null){
            //control points are relative to layout x,y
            cubicCurve.setControlX1(controlPointWithPrevious.getX());
            cubicCurve.setControlY1(controlPointWithPrevious.getY());
        }

        previousControlPointCircle.setCenterX(absoluteX);
        previousControlPointCircle.setCenterY(absoluteY);
        previousControlPointCircle.setVisible(true);

        previousControlLine.setEndX(absoluteX);
        previousControlLine.setEndY(absoluteY);
        previousControlLine.setVisible(true);
    }

    @Override
    public void setControlPointWithNext(UtilPoint controlPointWithNext) {
        super.setControlPointWithNext(controlPointWithNext);

        float absoluteX = (float) (getAnchorPoint().getX() + controlPointWithNext.getX());
        float absoluteY = (float) (getAnchorPoint().getY() + controlPointWithNext.getY());

        nextControlPointCircle.setCenterX(absoluteX);
        nextControlPointCircle.setCenterY(absoluteY);
        nextControlPointCircle.setVisible(true);

        nextControlLine.setEndX(absoluteX);
        nextControlLine.setEndY(absoluteY);
        nextControlLine.setVisible(true);

    }

    public void extendControlPointsUsing(double x,double y){
        this.setControlPointWithPrevious(new UtilPoint(x, y));
        //inverted coordinates will be set on the control point with next
        this.setControlPointWithNext(new UtilPoint(-x, -y));
    }

    public void addAsChildrenTo(Pane parent){
        //since points should always have higher Z value
        //prepend the lines and the curve
        parent.getChildren().add(0,previousControlLine);
        parent.getChildren().add(0,nextControlLine);
        if(cubicCurve!=null){
            parent.getChildren().add(0,cubicCurve);
        }
        //append the points
        parent.getChildren().add(previousControlPointCircle);
        parent.getChildren().add(nextControlPointCircle);
        parent.getChildren().add(anchorPointRect);


    }

    public void removeAsChildrenFrom(Pane parent){
        parent.getChildren().remove(anchorPointRect);
        if(cubicCurve!=null){
            parent.getChildren().remove(cubicCurve);
        }
        parent.getChildren().remove(previousControlPointCircle);
        parent.getChildren().remove(previousControlLine);
        parent.getChildren().remove(nextControlLine);
        parent.getChildren().remove(nextControlPointCircle);

    }



}
