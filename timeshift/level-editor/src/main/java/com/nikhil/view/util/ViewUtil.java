package com.nikhil.view.util;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Created by NikhilVerma on 03/09/15.
 */
public class ViewUtil {
    public static void showPoint(Pane pane,double x,double y){
        Circle point =new Circle(x,y,1);
        point.setFill(Color.RED);
        pane.getChildren().add(point);
    }

    public static void showPoint(Pane pane,double x,double y,Color color){
        Circle point =new Circle(x,y,1);
        point.setFill(color);
        pane.getChildren().add(point);
    }

    public static void showCircle(Pane pane,double x,double y){
        Circle point =new Circle(x,y,10);
        point.setFill(Color.RED);
        pane.getChildren().add(point);
    }

    public static void showCircle(Pane pane,double x,double y,Color color){
        Circle point =new Circle(x,y,10);
        point.setFill(color);
        pane.getChildren().add(point);
    }

    public static void showCircle(Pane pane,double x,double y,double radius,Color color){
        Circle point =new Circle(x,y,radius);
        point.setFill(color);
        pane.getChildren().add(point);
    }
}
