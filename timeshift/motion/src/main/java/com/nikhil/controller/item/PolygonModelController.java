package com.nikhil.controller.item;

import com.nikhil.common.Subject;
import com.nikhil.controller.CompositionController;
import com.nikhil.model.ModelVisitor;
import com.nikhil.model.shape.PolygonModel;
import com.nikhil.timeline.Timeline;

/**
 * Created by NikhilVerma on 21/08/15.
 */
public class PolygonModelController extends ItemModelController {
    protected PolygonModel polygonModel;

    public PolygonModelController(PolygonModel polygonModel) {
        this.polygonModel = polygonModel;
        this.polygonModel.setObserver(this);
    }

    public PolygonModel getPolygonModel() {
        return polygonModel;
    }

    public void setPolygonModel(PolygonModel polygonModel) {
        this.polygonModel = polygonModel;
        this.polygonModel.setObserver(this);
    }

    @Override
    public void step(double delta, double totalTime, CompositionController parent) {

    }

    @Override
    public void acceptVisitor(ModelVisitor visitor) {
        polygonModel.acceptVisitor(visitor);
    }

    @Override
    public void update(Subject subject) {
        //expected to be overridden in subclasses
    }
}
