package com.nikhil.xml;

import com.nikhil.controller.item.*;
import com.nikhil.model.shape.PolygonModel;
import com.nikhil.model.shape.CircleModel;
import com.nikhil.model.shape.IsoscelesTriangle;
import com.nikhil.model.shape.Parallelogram;

/**
 * Controller factory which supplies controllers for each model class.
 * This is used in XMLReader class
 * This class can be extended and its methods can be overridden to provide custom
 * controller classes for each model.
 * Created by NikhilVerma on 20/08/15.
 */
public class ControllerFactory {

    public CircleModelController getItemControllerFor(CircleModel circleModel){
        return new CircleModelController(circleModel);
    }

    public ItemModelController getItemControllerFor(IsoscelesTriangle isoscelesTriangle){
        return new IsoscelesTriangleModelController(isoscelesTriangle);
    }

    public ItemModelController getItemControllerFor(Parallelogram parallelogram){
        return new ParallelogramModelController(parallelogram);
    }

    public PolygonModelController getItemControllerFor(PolygonModel polygonModel){
        return new PolygonModelController(polygonModel);
    }

    //TODO fill in more methods with instantiated controllers

}
