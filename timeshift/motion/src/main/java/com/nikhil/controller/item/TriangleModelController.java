package com.nikhil.controller.item;

import com.nikhil.common.Subject;
import com.nikhil.controller.CompositionController;
import com.nikhil.model.ItemModel;
import com.nikhil.model.ModelVisitor;
import com.nikhil.model.shape.TriangleModel;

/**
 * Created by NikhilVerma on 20/08/15.
 */
public class TriangleModelController extends ItemModelController {

    protected TriangleModel triangleModel;

    public TriangleModelController() {
    }

    public TriangleModelController(TriangleModel triangleModel) {
        this.triangleModel = triangleModel;
    }

    public TriangleModel getTriangleModel() {
        return triangleModel;
    }

    public void setTriangleModel(TriangleModel triangleModel) {
        this.triangleModel = triangleModel;
    }

    @Override
    public void step(double delta, double totalTime, CompositionController parent) {

    }

    @Override
    public ItemModel getItemModel() {
        return triangleModel;
    }

    @Override
    public void acceptVisitor(ModelVisitor visitor) {
        triangleModel.acceptVisitor(visitor);
    }

    @Override
    public void update(Subject subject) {
        //expected to be overridden in subclasses
    }
}
