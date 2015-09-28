package com.nikhil.view.modelview;

import com.nikhil.math.MathUtil;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 * Created by NikhilVerma on 24/08/15.
 */
public class GraphicalPolygonPoint {

    private static final double POINT_WIDTH=6;
    public static final Paint INCOMPLETE_POLYGON_POINT_COLOR= Color.RED;
    public static final Paint COMPLETE_POLYGON_POINT_COLOR= Color.GREEN;
    public static final Double JOINING_LINE_DASH=3d;

    private double x;
    private double y;
    private Line side;
    private Rectangle pointRect;

    public GraphicalPolygonPoint(double x, double y,Line side) {
        this.x = x;
        this.y = y;
        this.side=side;
        initializeGraphics();
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
        pointRect.setX(x-pointRect.getWidth()/2);
        if(side!=null){
            side.setStartX(x);
        }
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
        pointRect.setY(y - pointRect.getHeight() / 2);
        if(side!=null){
            side.setStartY(y);
        }
    }

    public Line getSide() {
        return side;
    }

    public void setSide(Line side) {
        this.side = side;
    }

    public Rectangle getPointRect() {
        return pointRect;
    }

    public void setPointRect(Rectangle pointRect) {
        this.pointRect = pointRect;
    }

    private void initializeGraphics(){
        pointRect=new Rectangle();
        pointRect.setWidth(POINT_WIDTH);
        pointRect.setHeight(POINT_WIDTH);
        pointRect.setFill(COMPLETE_POLYGON_POINT_COLOR);//assume its completed
        //calling this this is redundant as a setter ,but it updates the line and point model together
        setX(x);
        setY(y);

    }

    public void addAsChildrenTo(Pane pane){
        //side should be behind points
        if(side!=null){
            pane.getChildren().add(0,side);
        }
        //points should be above lines
        pane.getChildren().add(pointRect);
    }

    public void removeAsChildrenFrom(Pane pane){
        if(side!=null){
            pane.getChildren().remove(side);
        }
        pane.getChildren().remove(pointRect);
    }

    public double distanceFrom(GraphicalPolygonPoint polygonPoint){
        return MathUtil.distance(x,y,polygonPoint.x,polygonPoint.y);
    }

}
