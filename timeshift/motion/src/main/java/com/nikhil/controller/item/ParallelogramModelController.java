package com.nikhil.controller.item;

import com.nikhil.common.Subject;
import com.nikhil.controller.CompositionController;
import com.nikhil.model.ItemModel;
import com.nikhil.model.ModelVisitor;
import com.nikhil.model.shape.ParallelogramModel;

/**
 * Created by NikhilVerma on 20/08/15.
 */
public class ParallelogramModelController extends ItemModelController {

    protected ParallelogramModel parallelogramModel;

    public ParallelogramModelController() {
    }

    public ParallelogramModelController(ParallelogramModel parallelogramModel) {
        this.parallelogramModel = parallelogramModel;
    }

    public ParallelogramModel getParallelogramModel() {
        return parallelogramModel;
    }

    public void setParallelogramModel(ParallelogramModel parallelogramModel) {
        this.parallelogramModel = parallelogramModel;
    }

    @Override
    public void step(double delta, double totalTime, CompositionController parent) {

    }

    @Override
    public ItemModel getItemModel() {
        return parallelogramModel;
    }

    @Override
    public void acceptVisitor(ModelVisitor visitor) {
        parallelogramModel.acceptVisitor(visitor);
    }

    @Override
    public void update(Subject subject) {
        //expected to be overridden in subclasses
    }
}
