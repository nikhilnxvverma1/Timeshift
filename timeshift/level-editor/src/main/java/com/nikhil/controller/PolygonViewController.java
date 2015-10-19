package com.nikhil.controller;

import com.nikhil.command.MovePolygonPoint;
import com.nikhil.common.Observer;
import com.nikhil.common.Subject;
import com.nikhil.controller.item.ItemModelController;
import com.nikhil.controller.item.PolygonModelController;
import com.nikhil.editor.gizmo.GizmoVisibilityOption;
import com.nikhil.editor.gizmo.PolygonGizmo;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.logging.Logger;
import com.nikhil.math.MathUtil;
import com.nikhil.model.freeform.MovablePoint;
import com.nikhil.model.shape.PolygonModel;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.item.PolygonView;
import com.nikhil.view.item.delegate.PolygonViewDelegate;
import com.nikhil.view.item.record.Metadata;
import com.nikhil.view.item.record.PolygonMetadata;
import javafx.geometry.Bounds;
import javafx.scene.control.TreeItem;

import javax.rmi.CORBA.Util;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NikhilVerma on 19/09/15.
 */
public class PolygonViewController extends ShapeViewController implements Observer,PolygonViewDelegate {

    public static final short HEADER_TAG=1;
    public static final short SCALE_TAG=2;
    public static final short ROTATION_TAG=3;
    public static final short TRANSLATION_TAG=4;
    public static final short ANCHOR_POINT_TAG=5;
    public static final short VERTICES_TAG=6;

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
        polygonGizmo.initializeView();
        compositionViewController.getWorkspace().getSelectedItems().requestFocus(this,false);//TODO objectionable,what if the context doesn't need this to be highlighted
    }

    public PolygonViewController(CompositionViewController compositionViewController,PolygonModelController polygonModelController){
        super(compositionViewController);
        this.polygonModelController=polygonModelController;
        this.polygonModelController.getPolygonModel().setObserver(this);
        constructViewUsingModelController();
        this.polygonView.setDelegate(this);
        polygonGizmo=new PolygonGizmo(polygonView);
        polygonGizmo.initializeView();
    }

    /**
     * Uses the view to translate and create a polygon model
     * and corresponding model controller
     */
    private void constructModelControllerUsingView(){
        PolygonModel polygonModel =new PolygonModel();
        List<UtilPoint> polygonPoints = polygonView.getPolygonPoints();

        //set values for translation and anchor point
        Workspace workspace=compositionViewController.getWorkspace();
        double x=workspace.workPointX(polygonView.getTranslationX());
        double y=workspace.workPointY(polygonView.getTranslationY());
        polygonModel.setTranslation(x,y);

        double ax=workspace.workPointX(polygonView.getAnchorPointX());
        double ay=workspace.workPointY(polygonView.getAnchorPointY());
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
        polygonModel.setObserver(this);
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

    public PolygonView getPolygonView() {
        return polygonView;
    }

    public PolygonGizmo getPolygonGizmo() {
        return polygonGizmo;
    }

    @Override
    public double getScale() {
        return polygonView.getScale();
    }

    @Override
    public double getRotation(){
        return polygonView.getRotation();
    }

    @Override
    public UtilPoint getTranslation() {
        return new UtilPoint(polygonView.getTranslationX(),polygonView.getTranslationY());
    }

    //=============================================================================================
    //Item View Controller methods
    //=============================================================================================

    @Override
    public ItemModelController getModelController(){
        return polygonModelController;
    }

    @Override
    public Bounds getLayoutBoundsInWorksheet() {
        return polygonView.getBoundsInParent();
//        return polygonView.localToParent(polygonView.getLayoutBounds());
    }

    @Override
    public void addViewsToWorksheet(){
        Workspace workspace = compositionViewController.getWorkspace();

        workspace.getWorksheetPane().getChildren().add(polygonView);
        workspace.getWorksheetPane().getChildren().add(polygonGizmo);
    }

    @Override
    public void removeViewsFromWorksheet(){
        Workspace workspace=compositionViewController.getWorkspace();
        workspace.getWorksheetPane().getChildren().remove(polygonView);
        workspace.getWorksheetPane().getChildren().remove(polygonGizmo);
    }

    @Override
    public void moveBy(double dx, double dy) {
        //change translation component of the view and gizmo
        double x=polygonView.getTranslationX();
        double y=polygonView.getTranslationY();
        double newX = x + dx;
        double newY = y + dy;
        polygonView.setTranslationX(newX);
        polygonView.setTranslationY(newY);
        polygonGizmo.updateView();

        //convert to work point and update the business model
        Workspace workspace=compositionViewController.getWorkspace();
        double workPointX = workspace.workPointX(newX);
        double workPointY = workspace.workPointY(newY);
        polygonModelController.getPolygonModel().setTranslation(new UtilPoint(workPointX,workPointY));
    }

    @Override
    public boolean scaleBy(double dScale) {

        //change scale component of the view and gizmo
        double oldScale = polygonView.getScale();
        double newScale=dScale+ oldScale;
        if(newScale<0.1){
            return false;
        }
        polygonView.setScale(newScale);
        polygonGizmo.updateView();

        //TODO convert to work point scale and update the business model
        double workScale=newScale;
        polygonModelController.getPolygonModel().setScale((float)workScale);//TODO change types
        return true;
    }

    @Override
    public void setCompositionViewController(CompositionViewController compositionViewController) {
        //we do some processing if the composition controller changes
        super.setCompositionViewController(compositionViewController);
        constructModelControllerUsingView();//recomputes the model controller based on the new workspace
    }

    @Override
    public boolean rotateBy(double dAngle) {
        //change rotation component of the view and gizmo
        double oldRotation= polygonView.getRotation();
        double newRotation=dAngle+ oldRotation;
        newRotation= MathUtil.getAngleBetween0And360(newRotation);
        polygonView.setRotation(newRotation);
        polygonGizmo.updateView();

        //TODO convert to work point scale and update the business model
        double workRotation=newRotation;
        polygonModelController.getPolygonModel().setRotation((float)workRotation);//TODO change types
        return true;
    }

    @Override
    public boolean contains(double x, double y) {
        return polygonView.contains(x,y);
    }

    @Override
    public boolean overlapsWithSceneBounds(Bounds sceneBounds) {
        Bounds polygonBoundsInScene = polygonView.localToScene(polygonView.getLayoutBounds());
        return polygonBoundsInScene.intersects(sceneBounds);
    }

    @Override
    public void hasSelectionFocus(boolean isSelected) {
        hasSelectionFocus(isSelected,false);
    }

    @Override
    public void hasSelectionFocus(boolean isSelected, boolean isSelectedInDetail) {
        if(isSelected){
            if(isSelectedInDetail){
                polygonGizmo.showGizmo(GizmoVisibilityOption.SHOW_ALL);
            }else{
                polygonGizmo.showGizmo(GizmoVisibilityOption.SHOW_ONLY_OUTLINE);
            }
        }else{
            polygonGizmo.showGizmo(GizmoVisibilityOption.HIDE_ALL);
        }
    }

    @Override
    public void hoveredOver(boolean isHoveringOver, boolean isSelected){
        if(!isSelected){
            if(isHoveringOver){
                polygonGizmo.showGizmo(GizmoVisibilityOption.SHOW_ONLY_OUTLINE);
            }else{
                polygonGizmo.showGizmo(GizmoVisibilityOption.HIDE_ALL);
            }
        }
    }

    @Override
    public ItemViewController clone() {
        return new PolygonViewController(this);
    }

    @Override
    public TreeItem<Metadata> getMetadataTree() {
        if(metadataTree==null){
            TreeItem<Metadata> polygonHeader= new TreeItem<>(
                    new PolygonMetadata(polygonModelController.getPolygonModel().getName(),HEADER_TAG,this));
            TreeItem<Metadata> polygonScale= new TreeItem<>(new PolygonMetadata("Scale",SCALE_TAG,this));
            TreeItem<Metadata> polygonRotation= new TreeItem<>(new PolygonMetadata("Rotation",ROTATION_TAG,this));
            TreeItem<Metadata> polygonTranslation= new TreeItem<>(new PolygonMetadata("Translation",TRANSLATION_TAG,this));
            TreeItem<Metadata> polygonAnchorPoint= new TreeItem<>(new PolygonMetadata("Anchor Point", ANCHOR_POINT_TAG, this));
            TreeItem<Metadata> polygonVertices= new TreeItem<>(new PolygonMetadata("Vertices", VERTICES_TAG, this));
            polygonHeader.getChildren().addAll(polygonScale, polygonRotation, polygonTranslation, polygonAnchorPoint, polygonVertices);
            metadataTree=polygonHeader;
        }
        return metadataTree;
    }

    //=============================================================================================
    //Notifications from model
    //=============================================================================================

    @Override
    public void update(Subject subject) {

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
