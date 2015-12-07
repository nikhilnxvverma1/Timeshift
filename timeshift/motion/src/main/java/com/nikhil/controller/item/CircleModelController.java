package com.nikhil.controller.item;

import com.nikhil.common.Observer;
import com.nikhil.common.Subject;
import com.nikhil.controller.CompositionController;
import com.nikhil.model.ItemModel;
import com.nikhil.model.ModelVisitor;
import com.nikhil.model.shape.CircleModel;

/**
 * Created by NikhilVerma on 10/08/15.
 */
public class CircleModelController extends ItemModelController implements Observer{

    private CircleModel circleModel;

    public CircleModelController(CircleModel circleModel) {
        this.circleModel = circleModel;
    }

    public CircleModel getCircleModel() {
        return circleModel;
    }

    @Override
    public void step(double delta, double totalTime, CompositionController parent) {
        //TODO
    }

    @Override
    public ItemModel getItemModel() {
        return circleModel;
    }

    @Override
    public void acceptVisitor(ModelVisitor visitor) {
        circleModel.acceptVisitor(visitor);
    }

    @Override
    public void update(Subject subject) {
        //expected to be overridden in subclasses
    }
}
