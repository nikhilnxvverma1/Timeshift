package com.nikhil.controller.item;

import com.nikhil.common.Subject;
import com.nikhil.controller.CompositionController;
import com.nikhil.model.ItemModel;
import com.nikhil.model.ModelVisitor;
import com.nikhil.model.pathsystem.TravelPath;
import com.nikhil.timeline.Timeline;

/**
 * Created by NikhilVerma on 13/08/15.
 */
public class TravelPathModelController extends ItemModelController {
    protected TravelPath travelPath;

    public TravelPathModelController(TravelPath travelPath) {
        this.travelPath = travelPath;
    }

    @Override
    public boolean isTravelPathControllerFor(TravelPath travelPath) {
        return this.travelPath==travelPath;
    }

    @Override
    public void step(double delta, double totalTime, CompositionController parent) {

    }

    public TravelPath getTravelPath() {
        return travelPath;
    }

    public void setTravelPath(TravelPath travelPath) {
        this.travelPath = travelPath;
    }

    @Override
    public void acceptVisitor(ModelVisitor visitor) {
        travelPath.acceptVisitor(visitor);
    }

    @Override
    public void update(Subject subject) {
        //expected to be overridden in subclasses
    }

    @Override
    public ItemModel getItemModel() {
        return travelPath;
    }
}
