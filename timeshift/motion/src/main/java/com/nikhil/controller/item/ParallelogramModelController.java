package com.nikhil.controller.item;

import com.nikhil.common.Subject;
import com.nikhil.controller.CompositionController;
import com.nikhil.model.ModelVisitor;
import com.nikhil.model.shape.Parallelogram;
import com.nikhil.timeline.Timeline;

/**
 * Created by NikhilVerma on 20/08/15.
 */
public class ParallelogramModelController extends ItemModelController {

    protected Parallelogram parallelogram;

    public ParallelogramModelController() {
    }

    public ParallelogramModelController(Parallelogram parallelogram) {
        this.parallelogram = parallelogram;
    }

    public Parallelogram getParallelogram() {
        return parallelogram;
    }

    public void setParallelogram(Parallelogram parallelogram) {
        this.parallelogram = parallelogram;
    }

    @Override
    public void step(double delta, double totalTime, CompositionController parent) {

    }

    @Override
    public void acceptVisitor(ModelVisitor visitor) {
        parallelogram.acceptVisitor(visitor);
    }

    @Override
    public void addChangeNodes(Timeline timeline) {
        parallelogram.registerWithTimeline(timeline);
    }

    @Override
    public void update(Subject subject) {
        //expected to be overridden in subclasses
    }
}
