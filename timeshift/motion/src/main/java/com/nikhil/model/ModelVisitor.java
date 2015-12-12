package com.nikhil.model;

import com.nikhil.controller.CompositionController;
import com.nikhil.controller.RootController;
import com.nikhil.model.freeform.MovablePoint;
import com.nikhil.model.pathsystem.MovableLinkPoint;
import com.nikhil.model.pathsystem.TravelPath;
import com.nikhil.model.pathsystem.TravellingLinkPoint;
import com.nikhil.model.pathsystem.traveller.TravellerConfiguration;
import com.nikhil.model.shape.CircleModel;
import com.nikhil.model.shape.PolygonModel;
import com.nikhil.model.shape.TriangleModel;
import com.nikhil.model.shape.Parallelogram;

/**
 * Visitor of all data modal , which can be used flexibly to visit each data modal.
 * Methods in this interface act as callback for every data model visited.
 * The visitation is always done in an "In Traversal" fashion,
 * meaning the parent model is always visited first and only afterwards,containing children
 * models are visited.
 * Created by NikhilVerma on 20/08/15.
 */
public interface ModelVisitor {

    //=============================================================================================
    //Key Controller visitations
    //=============================================================================================

    void visit(RootController rootController);
    void visit(CompositionController compositionController);

    //=============================================================================================
    //Item visitations
    //=============================================================================================

    void visit(CircleModel circleModel);
    void visit(TriangleModel triangleModel);
    void visit(Parallelogram parallelogram);
    void visit(TravelPath travelPath);
    void visit(MovableLinkPoint movableLinkPoint);
    void visit(TravellingLinkPoint travellingLinkPoint);
    void visit(PolygonModel polygonModel);
    void visit(MovablePoint movablePoint);
    void visit(TravellerConfiguration travellerConfiguration);
}
