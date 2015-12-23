package com.nikhil.view.custom.keyframe;

import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.bezier.GraphicalBezierPoint;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;

/**
 * Created by NikhilVerma on 20/12/15.
 */
public class GraphNodeBezierPoint extends GraphicalBezierPoint{

    private GraphNode graphNode;

    protected GraphNodeBezierPoint(UtilPoint anchorPoint) {
        super(anchorPoint,new CubicCurve());
        cubicCurve.setLayoutX(anchorPoint.getX());
        cubicCurve.setLayoutY(anchorPoint.getY());
        cubicCurve.setStartX(0);
        cubicCurve.setStartY(0);
        cubicCurve.setStroke(Color.RED);//TODO color will be decided on the type of property being represented
        cubicCurve.setFill(null);

        //by default this is hidden
        this.setVisible(false);
        anchorPointRect.setFill(Color.WHITE);
        previousControlPointCircle.setFill(Color.WHITE);
        nextControlPointCircle.setFill(Color.WHITE);
    }
}
