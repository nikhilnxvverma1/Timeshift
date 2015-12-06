package com.nikhil.controller;

import com.nikhil.command.item.MovePolygonPoint;
import com.nikhil.controller.item.ItemModelController;
import com.nikhil.controller.item.PolygonModelController;
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

    public static final int SCALE_INDEX=0;
    public static final int ROTATION_INDEX=1;
    public static final int TRANSLATION_INDEX=2;
    public static final int ANCHOR_POINT_INDEX=3;
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
        polygonGizmo.initializeView();
        setSelfAsChangeHandler();
        initMetadataTree();
        compositionViewController.getWorkspace().getSelectedItems().requestFocus(this,false);
    }

    public PolygonViewController(CompositionViewController compositionViewController,PolygonModelController polygonModelController){
        super(compositionViewController);
        this.polygonModelController=polygonModelController;
        constructViewUsingModelController();
        this.polygonView.setDelegate(this);
        polygonGizmo=new PolygonGizmo(polygonView);
        polygonGizmo.initializeView();
        setSelfAsChangeHandler();
        initMetadataTree();
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
        return polygonView.getRotate();
    }

    @Override
    public UtilPoint getTranslation() {
        return new UtilPoint(polygonView.getLayoutX(),polygonView.getLayoutY());
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
    public void addViewsTo(Pane pane){
        pane.getChildren().add(polygonView);
        pane.getChildren().add(polygonGizmo);
    }

    @Override
    public void removeViews(Pane pane){
        pane.getChildren().remove(polygonView);
        pane.getChildren().remove(polygonGizmo);
    }

    @Override
    public void moveTo(double newX, double newY) {
        //change translation component of the view and gizmo
//        double x=polygonView.getLayoutX();
//        double y=polygonView.getLayoutY();
//        double newX = x + dx;
//        double newY = y + dy;
        polygonView.setLayoutX(newX);
        polygonView.setLayoutY(newY);
        polygonGizmo.updateView();

        //convert to work point and update the business model
        Workspace workspace=compositionViewController.getWorkspace();
        double workPointX = workspace.workPointX(newX);
        double workPointY = workspace.workPointY(newY);
        polygonModelController.getPolygonModel().setTranslation(new UtilPoint(workPointX,workPointY));
    }

    @Override
    public double scaleBy(double dScale) {

        //change scale component of the view and gizmo
        double oldScale = polygonView.getScale();
        double newScale=dScale+ oldScale;
        if(newScale<0.1){
            return polygonView.getScale();
        }
        polygonView.setScale(newScale);
        polygonGizmo.updateView();

        //TODO convert to work point scale and update the business model
        double workScale=newScale;
        polygonModelController.getPolygonModel().setScale((float)workScale);//TODO change types
        return polygonView.getScale();
    }

    @Override
    public void setCompositionViewController(CompositionViewController compositionViewController) {
        //we do some processing if the composition controller changes
        super.setCompositionViewController(compositionViewController);
        constructModelControllerUsingView();//recomputes the model controller based on the new workspace
    }

    @Override
    public double rotateBy(double dAngle) {
        //change rotation component of the view and gizmo
        double oldRotation= polygonView.getRotate();
        double newRotation=dAngle+ oldRotation;
        newRotation= MathUtil.under360(newRotation);
        polygonView.setRotate(newRotation);
        polygonGizmo.updateView();

        //TODO convert to work point scale and update the business model
        double workRotation=newRotation;
        polygonModelController.getPolygonModel().setRotation((float)workRotation);//TODO change types
        return newRotation;
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
    public PolygonViewController clone() {
        return new PolygonViewController(this);
    }

    public TreeItem<Metadata> initMetadataTree() {
        if(metadataTree==null){
            //using generic metadata wherever possible
            TreeItem<Metadata> polygonHeader= new TreeItem<>(
                    new HeaderMetadata(polygonModelController.getPolygonModel().getName(), MetadataTag.HEADER, this, true));
            TreeItem<Metadata> polygonScale= new TreeItem<>(new TemporalMetadata(MetadataTag.SCALE,
                    polygonModelController.getPolygonModel().scaleChange(),
                    this));
            TreeItem<Metadata> polygonRotation= new TreeItem<>(new TemporalMetadata(MetadataTag.ROTATION,
                    polygonModelController.getPolygonModel().rotationChange(),
                    this));
            TreeItem<Metadata> polygonTranslation= new TreeItem<>(new SpatialMetadata(MetadataTag.TRANSLATION,
                    polygonModelController.getPolygonModel().translationChange(),
                    this));
            TreeItem<Metadata> polygonAnchorPoint= new TreeItem<>(new SpatialMetadata(MetadataTag.ANCHOR_POINT,
                    polygonModelController.getPolygonModel().anchorPointChange(),
                    this));
            TreeItem<Metadata> polygonVertices= new TreeItem<>(new PolygonMetadata("Vertices",  MetadataTag.POLYGON_VERTEX_HEADER,this));

            polygonHeader.getChildren().add(SCALE_INDEX,polygonScale);
            polygonHeader.getChildren().add(ROTATION_INDEX,polygonRotation);
            polygonHeader.getChildren().add(TRANSLATION_INDEX,polygonTranslation);
            polygonHeader.getChildren().add(ANCHOR_POINT_INDEX,polygonAnchorPoint);
            polygonHeader.getChildren().add(VERTEX_HEADER_INDEX,polygonVertices);
            metadataTree=polygonHeader;
        }
        return metadataTree;
    }

    @Override
    public TreeItem<Metadata> getMetadataTree() {
        return metadataTree;
    }

    @Override
    public Shape getItemView() {
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
    protected ShapeModel getShapeModel() {
        return polygonModelController.getPolygonModel();
    }

    @Override
    public Group getGizmo() {
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
