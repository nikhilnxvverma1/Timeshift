package com.nikhil.view.bezier;

import com.nikhil.util.modal.UtilPoint;
import javafx.scene.shape.CubicCurve;

/**
 * Created by NikhilVerma on 26/11/15.
 */
public class InteractiveBezierPoint extends GraphicalBezierPoint{

    private BezierPointInteraction delegate;

    public InteractiveBezierPoint(UtilPoint anchorPoint, BezierPointInteraction delegate) {
        super(anchorPoint);
        this.delegate = delegate;
    }
}
