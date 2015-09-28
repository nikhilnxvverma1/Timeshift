package com.nikhil.controller.item;

import com.nikhil.common.Subject;
import com.nikhil.controller.CompositionController;
import com.nikhil.model.ModelVisitor;
import com.nikhil.model.shape.IsoscelesTriangle;
import com.nikhil.timeline.Timeline;

/**
 * Created by NikhilVerma on 20/08/15.
 */
public class IsoscelesTriangleModelController extends ItemModelController {

    protected IsoscelesTriangle isoscelesTriangle;

    public IsoscelesTriangleModelController() {
    }

    public IsoscelesTriangleModelController(IsoscelesTriangle isoscelesTriangle) {
        this.isoscelesTriangle = isoscelesTriangle;
    }

    public IsoscelesTriangle getIsoscelesTriangle() {
        return isoscelesTriangle;
    }

    public void setIsoscelesTriangle(IsoscelesTriangle isoscelesTriangle) {
        this.isoscelesTriangle = isoscelesTriangle;
    }

    @Override
    public void step(double delta, double totalTime, CompositionController parent) {

    }

    @Override
    public void acceptVisitor(ModelVisitor visitor) {
        isoscelesTriangle.acceptVisitor(visitor);
    }

    @Override
    public void addChangeNodes(Timeline timeline) {
        isoscelesTriangle.registerWithTimeline(timeline);
    }

    @Override
    public void update(Subject subject) {
        //expected to be overridden in subclasses
    }
}
