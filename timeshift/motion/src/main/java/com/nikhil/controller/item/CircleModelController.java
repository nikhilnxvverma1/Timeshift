package com.nikhil.controller.item;

import com.nikhil.common.Observer;
import com.nikhil.common.Subject;
import com.nikhil.controller.CompositionController;
import com.nikhil.model.ModelVisitor;
import com.nikhil.model.shape.Circle;
import com.nikhil.timeline.Timeline;

/**
 * Created by NikhilVerma on 10/08/15.
 */
public class CircleModelController extends ItemModelController implements Observer{

    private Circle circle;

    public CircleModelController(Circle circle) {
        this.circle = circle;
    }

    public Circle getCircle() {
        return circle;
    }

    @Override
    public void step(double delta, double totalTime, CompositionController parent) {
        //TODO
    }

    @Override
    public void acceptVisitor(ModelVisitor visitor) {
        circle.acceptVisitor(visitor);
    }

    @Override
    public void update(Subject subject) {
        //expected to be overridden in subclasses
    }
}
