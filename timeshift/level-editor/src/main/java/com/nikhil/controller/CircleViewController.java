package com.nikhil.controller;

import com.nikhil.controller.item.CircleModelController;
import com.nikhil.controller.item.ItemModelController;
import com.nikhil.controller.item.PolygonModelController;
import com.nikhil.editor.gizmo.CircleGizmo;
import com.nikhil.editor.gizmo.PolygonGizmo;
import com.nikhil.model.freeform.MovablePoint;
import com.nikhil.model.shape.CircleModel;
import com.nikhil.model.shape.PolygonModel;
import com.nikhil.model.shape.ShapeModel;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.item.CircleView;
import com.nikhil.view.item.PolygonView;
import com.nikhil.view.item.delegate.CircleViewDelegate;
import com.nikhil.view.item.record.Metadata;
import com.nikhil.view.item.record.MetadataTag;
import com.nikhil.view.item.record.SpatialMetadata;
import com.nikhil.view.item.record.TemporalMetadata;
import javafx.geometry.Bounds;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * View controller for the circle model
 * Created by NikhilVerma on 06/12/15.
 */
public class CircleViewController extends ShapeViewController implements CircleViewDelegate{

    private CircleModelController circleModelController;
    private CircleView circleView;
    private CircleGizmo circleGizmo;

    public CircleViewController(CompositionViewController compositionViewController,CircleView circleView) {
        super(compositionViewController);
        this.circleView=circleView;
        this.circleView.setDelegate(this);
        constructModelControllerUsingView();
        circleGizmo=new CircleGizmo(circleView);
        setSelfAsChangeHandler();
        initMetadataTree();
        compositionViewController.getWorkspace().getSelectedItems().requestFocus(this, false);
    }

    public CircleViewController(CompositionViewController compositionViewController,CircleModelController circleModelController){
        super(compositionViewController);
        this.circleModelController=circleModelController;
        constructViewUsingModelController();
        this.circleView.setDelegate(this);
        circleGizmo=new CircleGizmo(circleView);
        setSelfAsChangeHandler();
        initMetadataTree();
    }

    private void constructViewUsingModelController() {

        CircleModel circleModel=circleModelController.getCircleModel();

        //get the simple properties from the model
        double scale=circleModel.getScale();
        double rotation=circleModel.getRotation();
        UtilPoint translation=circleModel.getTranslation();
//        UtilPoint anchorPoint=circleModel.getAnchorPoint();//TODO anchor point not currently being developed

        this.circleView=new CircleView(circleModel.getInnerRadius(),circleModel.getOuterRadius(),
                circleModel.getStartingAngle(),circleModel.getEndingAngle());
        this.circleView.setScale(scale);
        this.circleView.setRotate(rotation);
        this.circleView.setLayoutX(translation.getX());
        this.circleView.setLayoutY(translation.getY());
    }

    @Override
    public ItemModelController getModelController() {
        return circleModelController;
    }

    @Override
    public ItemViewController deepCopy() {
        return null;
    }

    @Override
    public CircleView getItemView() {
        return circleView;
    }

    @Override
    public CircleGizmo getGizmo() {
        return circleGizmo;
    }

    @Override
    public CircleModel getItemModel() {
        return circleModelController.getCircleModel();
    }

    @Override
    protected void constructModelControllerUsingView() {
        CircleModel circleModel=new CircleModel(circleView.getInnerRadius(),circleView.getOuterRadius(),
                circleView.getStartingAngle(),circleView.getEndingAngle());
        circleModelController=new CircleModelController(circleModel);
    }

    @Override
    public void finishedTweakingInnerRadius(double initialInnerRadius) {

    }

    @Override
    public void finishedTweakingOuterRadius(double initialOuterRadius) {

    }

    @Override
    public void finishedTweakingStartingAngle(double initialStartingAngle) {

    }

    @Override
    public void finishedTweakingEndingAngle(double initialEndingAngle) {

    }
}
