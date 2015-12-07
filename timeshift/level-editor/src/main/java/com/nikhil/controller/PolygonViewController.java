package com.nikhil.controller;

import com.nikhil.command.item.MovePolygonPoint;
import com.nikhil.controller.item.ItemModelController;
import com.nikhil.controller.item.PolygonModelController;
import com.nikhil.editor.gizmo.Gizmo;
import com.nikhil.editor.gizmo.GizmoVisibilityOption;
import com.nikhil.editor.gizmo.PolygonGizmo;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.math.MathUtil;
import com.nikhil.model.freeform.MovablePoint;
import com.nikhil.model.shape.PolygonModel;
import com.nikhil.model.shape.ShapeModel;
import com.nikhil.timeline.change.spatial.SpatialKeyframeChangeNode;
import com.nikhil.timeline.change.temporal.TemporalKeyframeChangeNode;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.item.PolygonView;
import com.nikhil.view.item.delegate.PolygonViewDelegate;
import com.nikhil.view.item.record.*;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NikhilVerma on 19/09/15.
 */
public class PolygonViewController extends ShapeViewController implements PolygonViewDelegate{

    public static final int VERTEX_HEADER_INDEX=4;

    private PolygonModelController polygonModelController;
    private PolygonView polygonView;
    private PolygonGizmo polygonGizmo;

    public PolygonViewController(PolygonViewController polygonViewController){
        this(polygonViewController.compositionViewController,new PolygonView(polygonViewController.polygonView));
    }

    public PolygonViewController(CompositionViewController compositionViewController,PolygonView polygonView) {
        super(compositionViewController);
        this.polygonView=polygonView;
        this.polygonView.setDelegate(this);
        constructModelControllerUsingView();
        polygonGizmo=new PolygonGizmo(polygonView);
        setSelfAsChangeHandler();
        initMetadataTree();
        compositionViewController.getWorkspace().getSelectedItems().requestFocus(this, false);
    }

    public PolygonViewController(CompositionViewController compositionViewController,PolygonModelController polygonModelController){
        super(compositionViewController);
        this.polygonModelController=polygonModelController;
        constructViewUsingModelController();
        this.polygonView.setDelegate(this);
        polygonGizmo=new PolygonGizmo(polygonView);
        setSelfAsChangeHandler();
        initMetadataTree();
    }

    @Override
    protected void constructModelControllerUsingView(){
        PolygonModel polygonModel =new PolygonModel();
        List<UtilPoint> polygonPoints = polygonView.getPolygonPoints();

        //set values for translation and anchor point
        Workspace workspace=compositionViewController.getWorkspace();
        double x=workspace.workPointX(polygonView.getLayoutX());
        double y=workspace.workPointY(polygonView.getLayoutY());
        polygonModel.setTranslation(x,y);

        double ax=workspace.workPointX(polygonView.getTranslateX());
        double ay=workspace.workPointY(polygonView.getTranslateY());
        polygonModel.setAnchorPoint(ax,ay);

        //for each point in the polygon
        for(UtilPoint playPoint:polygonPoints){

            //convert to work point
            UtilPoint workPoint=workspace.workPoint(playPoint);

            //create a new movable point and add to the list of polygon points in the polygon model
            MovablePoint movablePoint=new MovablePoint();
            movablePoint.setPoint(workPoint);
            polygonModel.addPoint(movablePoint);
        }

        //once the model is created, create the model controller
        this.polygonModelController=new PolygonModelController(polygonModel);

        //make this class the listener for all changes on the model
//        polygonModel.setObserver(this);
    }

    /**
     * Uses the model controller to translate and create a polygon view
     */
    private void constructViewUsingModelController(){

        PolygonModel polygonModel=polygonModelController.getPolygonModel();

        //get the simple properties from the model
        double scale=polygonModel.getScale();
        double rotation=polygonModel.getRotation();
        UtilPoint translation=polygonModel.getTranslation();
//        UtilPoint anchorPoint=polygonModel.getAnchorPoint();//TODO anchor point not currently being developed

        //store all polygon "play" points in an array list where total count is known
        int totalPoints = polygonModel.countTotalPoints();
        List<UtilPoint> polygonPlayPoints=new ArrayList<>(totalPoints);

        //traverse through each movable point which is actually a CIRCULAR LIST
        MovablePoint t = polygonModel.getPolygonPointStart();

        do{
            //convert to corresponding play point and add to the list
            UtilPoint playPoint=compositionViewController.getWorkspace().playPoint(t.getPoint());
            polygonPlayPoints.add(playPoint);

            t=t.getNext();
        }while(t!=polygonModel.getPolygonPointStart());//mind the circular list

        this.polygonView=new PolygonView(polygonPlayPoints,translation.getX(),translation.getY(),rotation,scale);

    }

    public PolygonModelController getPolygonModelController() {
        return polygonModelController;
    }

    //=============================================================================================
    //Item View Controller methods
    //=============================================================================================

    @Override
    public ItemModelController getModelController(){
        return polygonModelController;
    }

    @Override
    public PolygonViewController deepCopy() {
        return new PolygonViewController(this);
    }

    @Override
    public void initMetadataTree() {
        super.initMetadataTree();
        TreeItem<Metadata> polygonVertices= new TreeItem<>(new PolygonMetadata("Vertices",  MetadataTag.POLYGON_VERTEX_HEADER,this));
        metadataTree.getChildren().add(VERTEX_HEADER_INDEX,polygonVertices);
    }

    @Override
    public PolygonView getItemView() {
        return polygonView;
    }

    @Override
    public boolean isKeyframableProperty(MetadataTag tag) {
        switch (tag){
            case POLYGON_VERTEX:
            case POLYGON_VERTEX_HEADER:
                return metadataTree.getChildren().get(VERTEX_HEADER_INDEX).getValue().isKeyframable();
            default:
                return super.isKeyframableProperty(tag);
        }
    }

    @Override
    public TemporalMetadata getTemporalMetadata(MetadataTag tag) {
        switch (tag){

            case SCALE:
                return (TemporalMetadata)metadataTree.getChildren().get(SCALE_INDEX).getValue();
            case ROTATION:
                return (TemporalMetadata)metadataTree.getChildren().get(ROTATION_INDEX).getValue();
            default:
                return null;
        }
    }

    @Override
    public SpatialMetadata getSpatialMetadata(MetadataTag tag) {
        switch (tag){
            case TRANSLATION:
                return (SpatialMetadata)metadataTree.getChildren().get(TRANSLATION_INDEX).getValue();
            case ANCHOR_POINT:
                return (SpatialMetadata)metadataTree.getChildren().get(ANCHOR_POINT_INDEX).getValue();
            default:
                return null;
        }
    }

    @Override
    public PolygonModel getItemModel() {
        return polygonModelController.getPolygonModel();
    }

    @Override
    public PolygonGizmo getGizmo() {
        return polygonGizmo;
    }

    @Override
    protected void setSelfAsChangeHandler() {
        super.setSelfAsChangeHandler();
        //TODO set change handler for each vertex
    }

    //=============================================================================================
    //Change Handlers
    //=============================================================================================

    @Override
    public void valueChanged(SpatialKeyframeChangeNode changeNode) {
        super.valueChanged(changeNode);
        polygonGizmo.updateView();
    }

    @Override
    public void valueChanged(TemporalKeyframeChangeNode changeNode) {
        super.valueChanged(changeNode);
        polygonGizmo.updateView();
    }

    //=============================================================================================
    //Polygon events coming from view
    //=============================================================================================

    @Override
    public void finishedTweakingPolygonPoint(int index,double initialX,double initialY) {

        //create a move polygon point command,accepting initial and final positions
        UtilPoint finalPosition=new UtilPoint(polygonView.getPolygonPoints().get(index));
        MovePolygonPoint movePolygonPoint=new MovePolygonPoint(this,
                index,
                new UtilPoint(initialX,initialY),
                finalPosition);
        getCompositionViewController().getWorkspace().pushCommand(movePolygonPoint, false);
    }
}
