package com.nikhil.editor.selection;

import com.nikhil.command.*;
import com.nikhil.command.composite.ItemCompositeCommand;
import com.nikhil.command.item.*;
import com.nikhil.controller.ItemViewController;
import com.nikhil.controller.ShapeViewController;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.logging.Logger;
import com.nikhil.math.MathUtil;
import com.nikhil.timeline.KeyValue;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.custom.keyframe.SpatialKeyframePane;
import com.nikhil.view.item.record.MetadataTag;
import com.nikhil.view.item.record.SpatialMetadata;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.*;


/**
 * Outline view that keeps track of all the items that are currently selected.
 * The view creates the outline and all the handles within it.
 * Created by NikhilVerma on 30/08/15.
 */
public class SelectedItems extends Group implements EventHandler<MouseEvent>{

    private static final double HANDLE_WIDTH=7;
    public static final double DETAIL_SELECTION_OUTLINE_OPACITY = 0.5;

    private Workspace workspace;
    private Set<ItemViewController> managedItems=new HashSet<ItemViewController>();//set required for eliminating repeated elements

    private Rectangle outlineRect;
    private Rectangle scaleHandle;
    private Rectangle anchorPointHandle;
    private Rectangle rotationHandle;

    private boolean singleItemSelectedInDetail =false;

    private double initialX;
    private double initialY;

    private double temporaryValue1;
    private double temporaryValue2;
    private ItemCompositeCommand temporaryCompositeCommand;

    public SelectedItems(Workspace workspace) {
        this.workspace=workspace;
    }

    public void initView(){
        outlineRect=new Rectangle();
        outlineRect.setFill(null);
        outlineRect.setStroke(Color.BLUE);
        outlineRect.getStrokeDashArray().add(7d);
        scaleHandle=getGenericHandle();

        scaleHandle.setCursor(new ImageCursor(new Image("art/cursor/NE_resize_cursor.png"),7,7));
        anchorPointHandle=getGenericHandle();
        anchorPointHandle.setCursor(new ImageCursor(new Image("art/cursor/move_cursor.png"),9,8));
        rotationHandle=getGenericHandle();
        rotationHandle.setCursor(new ImageCursor(new Image("art/cursor/rotate_cursor.png"),8,8));
        this.getChildren().addAll(outlineRect,scaleHandle,anchorPointHandle,rotationHandle);
    }

    private Rectangle getGenericHandle(){

        Rectangle genericHandle=new Rectangle(HANDLE_WIDTH,HANDLE_WIDTH);
        genericHandle.setFill(Color.WHITE);
        genericHandle.setStroke(Color.BLACK);
        genericHandle.addEventHandler(MouseEvent.MOUSE_PRESSED,this);
        genericHandle.addEventHandler(MouseEvent.MOUSE_DRAGGED,this);
        genericHandle.addEventHandler(MouseEvent.MOUSE_RELEASED,this);
        genericHandle.setCursor(Cursor.HAND);
        return genericHandle;
    }

    public double getTemporaryValue1() {
        return temporaryValue1;
    }

    public double getTemporaryValue2() {
        return temporaryValue2;
    }

    public boolean isSingleItemSelectedInDetail() {
        return singleItemSelectedInDetail;
    }

    public void updateView(){
        this.setRotate(0);
        if(managedItems.isEmpty()){
            this.setVisible(false);
        }else{
            this.setVisible(true);
            //find the containing bounds of the selection
            Bounds containingBounds = getContainingBounds();
//            Logger.log("containing bounds" + containingBounds.toString());
            double x= (containingBounds.getMinX()+containingBounds.getMaxX())/2;
            double y= (containingBounds.getMinY()+containingBounds.getMaxY())/2;
            //while updating view, capture the x,y positions, that will help us later during dragging movement
            initialX=x;
            initialY=y;
            Point2D containerPoint=workspace.toContainerPoint(x,y);
            this.setLayoutX(containerPoint.getX());
            this.setLayoutY(containerPoint.getY());

            outlineRect.setWidth(containingBounds.getWidth());
            outlineRect.setHeight(containingBounds.getHeight());
            outlineRect.setTranslateX(-containingBounds.getWidth()/2);
            outlineRect.setLayoutX(0);
            outlineRect.setLayoutY(0);
            outlineRect.setTranslateY(-containingBounds.getHeight()/2);

            //scale handle at the top right
            scaleHandle.setTranslateX(containingBounds.getWidth()/2-HANDLE_WIDTH/2);
            scaleHandle.setTranslateY(-containingBounds.getHeight() / 2 -HANDLE_WIDTH/2);

            //rotation handle on the bottom left
            rotationHandle.setTranslateX(-containingBounds.getWidth()/2-HANDLE_WIDTH/2);
            rotationHandle.setTranslateY(containingBounds.getHeight() / 2 -HANDLE_WIDTH/2);

            //anchor point handle in the middle(TODO for now,but depends on the item)
            anchorPointHandle.setTranslateX(-HANDLE_WIDTH/2);
            anchorPointHandle.setTranslateY(-HANDLE_WIDTH/2);

            int size=managedItems.size();
            if(size==1){
                if(singleItemSelectedInDetail){
                    outlineRect.setOpacity(DETAIL_SELECTION_OUTLINE_OPACITY);
                }else{
                    outlineRect.setOpacity(1);
                    showHandles(true);
                }
            }else{
                outlineRect.setOpacity(1);
                showHandles(false);
            }
        }
    }

    public Bounds getContainingBounds() {
//        if(true){
//            return new BoundingBox(200,200,300,300);//for testing purposes
//        }

        double minX=999999;
        double minY=999999;
        double maxX=-999999;
        double maxY=-999999;
        for(ItemViewController itemViewController: managedItems){
            Bounds itemLayoutBounds=itemViewController.getLayoutBoundsInWorksheet();

            if(itemLayoutBounds.getMinX()<minX){
                minX=itemLayoutBounds.getMinX();
            }
            if(itemLayoutBounds.getMinY()<minY){
                minY=itemLayoutBounds.getMinY();
            }

            if(itemLayoutBounds.getMaxX()>maxX){
                maxX=itemLayoutBounds.getMaxX();
            }
            if(itemLayoutBounds.getMaxY()>maxY){
                maxY=itemLayoutBounds.getMaxY();
            }
        }
        return new BoundingBox(minX,minY,maxX-minX,maxY-minY);
    }

    public void clearSelection(){
        for(ItemViewController itemViewController:managedItems){
            itemViewController.hasSelectionFocus(false);
            showMotionPathFor(itemViewController, false);
        }
        managedItems=null;//if no reference is held to this hash set,it should be garbage collected
        managedItems=new HashSet<>();
        singleItemSelectedInDetail =false;
        updateView();
    }

    public boolean addToSelection(ItemViewController itemViewController){
        return addToSelection(itemViewController,true);
    }

    private boolean addToSelection(ItemViewController itemViewController, boolean selectRecordFromItemTable){
        boolean wasNewlyAdded = managedItems.add(itemViewController);
        if(wasNewlyAdded){
            itemViewController.hasSelectionFocus(true);
            showMotionPathFor(itemViewController,true);
            if((selectRecordFromItemTable)&&(managedItems.size()==1)){
                itemViewController.selectFromItemTable();
            }
            updateView();
        }
        singleItemSelectedInDetail =false;
        return wasNewlyAdded;
    }

    public boolean removeFromSelection(ItemViewController itemViewController){
        boolean wasRemoved = managedItems.remove(itemViewController);
        if(wasRemoved){
            itemViewController.hasSelectionFocus(false);
            showMotionPathFor(itemViewController, true);
            updateView();
        }
        singleItemSelectedInDetail =false;
        return wasRemoved;
    }

    private void showMotionPathFor(ItemViewController itemViewController, boolean visible) {
        SpatialMetadata translationMetadata = itemViewController.getSpatialMetadata(MetadataTag.TRANSLATION);
        if(translationMetadata!=null){
            SpatialKeyframePane spatialKeyframePane = translationMetadata.getKeyframePane();
            //there is a chance that the keyframe pane didn't get initialized
            if(spatialKeyframePane!=null){
                spatialKeyframePane.showMotionPath(visible);
            }
        }
    }

    public void selectOnly(ItemViewController itemViewController){
        clearSelection();
        addToSelection(itemViewController,false);
    }

    /**
     * Allows an item to gain focus either by being added
     * to existing selection or alone on its own.
     * @param itemViewController the itemViewController for which focus needs to be gained
     * @param addWithOthers if true the focus will be made collectively with the
     *                      items already present in set.
     * @return true if the item is in focus,false if item got deselected in case it already had focus
     */
    public boolean requestFocus(ItemViewController itemViewController,boolean addWithOthers){
        if(!addWithOthers){
            clearSelection();
        }
        boolean wasNewlyAdded = addToSelection(itemViewController);

        if(!wasNewlyAdded &&
                addWithOthers){
            //if done collectively, it should be deselected, as it was toggled
            removeFromSelection(itemViewController);
            return false;
        }else{
            return true;
        }
    }

    public void requestFocusInDetail(ItemViewController itemViewController){
        clearSelection();
        addToSelection(itemViewController);
        singleItemSelectedInDetail =true;
        showHandles(false);
        outlineRect.setOpacity(DETAIL_SELECTION_OUTLINE_OPACITY);
        itemViewController.hasSelectionFocus(true,true);
        showMotionPathFor(itemViewController, true);
    }

    private void showHandles(boolean visible){
        scaleHandle.setVisible(visible);
        rotationHandle.setVisible(visible);
        anchorPointHandle.setVisible(visible);
    }

    @Override
    public void handle(MouseEvent event) {
        if(event.getTarget()==scaleHandle){
            tweakScale(event);
        }else if(event.getTarget()==rotationHandle){
            tweakRotation(event);
        }else if(event.getTarget()==anchorPointHandle){

        }
    }

    private void tweakRotation(MouseEvent mouseEvent) {
        mouseEvent.consume();

        ShapeViewController shapeViewController;
        ItemViewController firstItemViewController = getFirstItemViewController();
        if(firstItemViewController!=null&&
                firstItemViewController instanceof ShapeViewController){
            shapeViewController = (ShapeViewController)firstItemViewController;
        }else{
            return;
        }

        double x = mouseEvent.getX();
        double y = mouseEvent.getY();

        if(mouseEvent.getEventType()==MouseEvent.MOUSE_PRESSED){

            Point2D wrtOutline=rotationHandle.localToParent(x,y);
            double initialAngleOfHandle = MathUtil.angleOfPoint(0, 0, wrtOutline.getX(), wrtOutline.getY());
            //store the initial angle offset of the rotation handle
            captureTemporaryValues(shapeViewController.getRotation(), initialAngleOfHandle);
        }else if(mouseEvent.getEventType()==MouseEvent.MOUSE_DRAGGED){
            Point2D wrtOutline=rotationHandle.localToParent(x,y);
            double angleWrtCenter = MathUtil.angleOfPoint(0, 0, wrtOutline.getX(), wrtOutline.getY());
            double dRotation=angleWrtCenter-temporaryValue2;//subtract the offset for the rotation handle
            double oldValue=shapeViewController.getRotation();
            double newValue=shapeViewController.rotateBy(dRotation);
            shapeViewController.getTemporalMetadata(MetadataTag.ROTATION).
                    registerContinuousChange(new KeyValue(oldValue), new KeyValue(newValue));
            this.setRotate(this.getRotate() + dRotation);
        }else{
            //Push the rotation command on the command stack without executing it.
            double initialAngle=temporaryValue1;
            double finalAngle=shapeViewController.getRotation();
            RotateShape rotateShape=new RotateShape(shapeViewController,initialAngle,finalAngle);
            shapeViewController.getTemporalMetadata(MetadataTag.ROTATION).
                    pushWithKeyframe(rotateShape,false);

            this.updateView();
        }
    }

    private void tweakScale(MouseEvent mouseEvent){
        mouseEvent.consume();

        ShapeViewController shapeViewController;
        ItemViewController firstItemViewController = getFirstItemViewController();
        if(firstItemViewController!=null&&
                firstItemViewController instanceof ShapeViewController){
            shapeViewController = (ShapeViewController)firstItemViewController;
        }else{
            return;
        }

        double x = mouseEvent.getX();
        double y = mouseEvent.getY();

        if(mouseEvent.getEventType()==MouseEvent.MOUSE_PRESSED){
            captureTemporaryValues(shapeViewController.getScale(), 0);
        }else if(mouseEvent.getEventType()==MouseEvent.MOUSE_DRAGGED){
            Point2D wrtOutline=scaleHandle.localToParent(x,y);
            double newWidth = 2*wrtOutline.getX();//assuming scale handle is at the top left
            double oldWidth=getLayoutBounds().getWidth();
            double expectedSelectionScale=newWidth/oldWidth;
            double dScale= expectedSelectionScale - 1;
            double oldScale=shapeViewController.getScale();
            double newScale = shapeViewController.scaleBy(dScale);
            if(newScale!=oldScale){//if there was a change
                temporaryValue2+=dScale;
                shapeViewController.getTemporalMetadata(MetadataTag.SCALE).
                        registerContinuousChange(new KeyValue(oldScale), new KeyValue(newScale));
            }
            updateView();
        }else{
            double initialScale=temporaryValue1;
            double finalScale=initialScale+temporaryValue2;
            ScaleShape scaleShape =new ScaleShape(shapeViewController,initialScale,finalScale);
            shapeViewController.getTemporalMetadata(MetadataTag.SCALE).
                    pushWithKeyframe(scaleShape,false);
        }

    }

    private ItemViewController getFirstItemViewController() {
        ItemViewController firstItemViewController=null;
        for (ItemViewController itemViewController:managedItems){
            firstItemViewController=itemViewController;
            break;
        }
        return firstItemViewController;
    }

    public void captureTemporaryValues(double value1, double value2){
        this.temporaryValue1 =value1;
        this.temporaryValue2 =value2;
    }

    public void moveSelectionBy(double dx, double dy){
        if(temporaryCompositeCommand==null){
            temporaryCompositeCommand=new ItemCompositeCommand();
        }
        for(ItemViewController itemViewController:managedItems){
            UtilPoint oldPoint = itemViewController.getTranslation();
            itemViewController.moveTo(oldPoint.getX() + dx, oldPoint.getY()+dy);
            UtilPoint newPoint = itemViewController.getTranslation();
            SpatialMetadata spatialMetadata = itemViewController.getSpatialMetadata(MetadataTag.TRANSLATION);
            spatialMetadata.registerContinuousChange(oldPoint,newPoint,temporaryCompositeCommand);
        }
        this.setLayoutX(getLayoutX()+dx);
        this.setLayoutY(getLayoutY()+dy);
    }

    public boolean finishMovingSelection(){
        Point2D finalPoint = workspace.toWorksheetPoint(getLayoutX(), getLayoutY());
        double x=finalPoint.getX();
        double y=finalPoint.getY();
        boolean moveCommandWasIssued;
        if(Math.abs(x-initialX)<=1 && Math.abs(initialY-y)<=1){//floating point round off margins falsify this condition
            moveCommandWasIssued=false;
        }else{
            //make a move command
            Point2D initialPoint=new Point2D(initialX,initialY);

            Set<ItemViewController> itemSetForNewCommand = getItemSetForNewCommand();
            pushMoveCommand(itemSetForNewCommand, initialPoint, finalPoint);
            moveCommandWasIssued=true;
        }
        //update initialX,initialY to current position in worksheet points
        initialX=x;
        initialY=y;
        return moveCommandWasIssued;
    }

    /**
     * Pushes a cummalative move item command for a bunch of items.
     * if needed, this set will be broken into several pieces and grouped in a composite command.
     * @param itemSet the set of items that just moved
     * @param initialPoint initial point before movement
     * @param finalPoint final point after movement
     */
    private void pushMoveCommand(Set<ItemViewController> itemSet, Point2D initialPoint, Point2D finalPoint) {

        //if any item in set is keyframable,a composite command will be used
        boolean atLeastOneItemKeyframable = false;

        //all non keyframable items are collected in this set
        Set<ItemViewController> nonKeyframableItems = new HashSet<>();

        UtilPoint distanceMoved=new UtilPoint(finalPoint.getX()-initialPoint.getX(),finalPoint.getY()-initialPoint.getY());

        for (ItemViewController itemViewController : itemSet) {

            //check up with the translation metadata
            SpatialMetadata spatialMetadata = itemViewController.getSpatialMetadata(MetadataTag.TRANSLATION);
            if (spatialMetadata.isKeyframable()) {
                atLeastOneItemKeyframable=true;

                //create a dedicated move item command for this item, and send to metadata for adding
                //it to a keyframing command by "ending" this continious change
                UtilPoint finalPointForThisItem = itemViewController.getTranslation();
                UtilPoint initialPointForThisItem=finalPointForThisItem.subtract(distanceMoved);
                MoveItem moveItem=new MoveItem(itemViewController,initialPointForThisItem,finalPointForThisItem);
                spatialMetadata.endContinuousChange(moveItem,false,temporaryCompositeCommand);
            } else {
                nonKeyframableItems.add(itemViewController);
            }
        }

        if (atLeastOneItemKeyframable) {
            //issue a common move item set command for remaining non keyframable items
            MoveItemSet moveItemSet = new MoveItemSet(nonKeyframableItems, initialPoint, finalPoint);
            workspace.pushCommand(temporaryCompositeCommand,false);

            //very important to set it to null, so that for the next drag this thing is reset
            temporaryCompositeCommand=null;
        } else {
            //disregard the composite item command, and just push a common move item set command for all items
            //Note: since we want to reuse the set in the command stack, we use "itemSet" and not "nonKeyframableItems"
            MoveItemSet moveItemSet = new MoveItemSet(itemSet, initialPoint, finalPoint);
            workspace.pushCommand(moveItemSet, false);
        }
    }

    /**
     * Builds a set of items in current selection and if possible, reuses an existing set
     * if the current top command also uses the same set.
     * @return set containing the items in current selection
     */
    public Set<ItemViewController> getItemSetForNewCommand(){
        Command topCommand=workspace.peekCommandStack();
        if(topCommand instanceof ActionOnItemSet){

            //if top command is an action on multiple items
            ActionOnItemSet actionOnItemSet=(ActionOnItemSet)topCommand;
            if(actionOnItemSet.containsSameItems(managedItems)){

                //then re use the set of that one
                return actionOnItemSet.getItemSet();
            }else{
                return getManagedItemsInNewSet();
            }
        }else{
            return getManagedItemsInNewSet();
        }
    }

    /**
     * @return a new set containing the items in current selection
     */
    public Set<ItemViewController> getManagedItemsInNewSet(){
        HashSet<ItemViewController> copy=new HashSet<>(managedItems.size());
        for(ItemViewController itemViewController:managedItems){
            copy.add(itemViewController);
        }
        return copy;
    }

    public int copyToClipboard(){
        return copyToClipboard(false);
    }

    private int copyToClipboard(boolean willBeCut){
        //ignore if nothing is selected
        int size=managedItems.size();
        if (size == 0) {
            return size;
        }

        //create a list of managed items
        List<ItemViewController> selectedItems=new LinkedList<ItemViewController>();
        for(ItemViewController itemViewController:managedItems){
            selectedItems.add(itemViewController);
        }
        Clipboard.getSharedInstance().setItemsInClipboard(selectedItems,willBeCut);
        return size;
    }

    public int cutToClipboard(){
        int totalItemsCopied=copyToClipboard(true);
        deleteSelection();
        return totalItemsCopied;
    }

    public void deleteSelection(){
        Logger.log("Deleting selected items");
        Set<ItemViewController> itemSetForNewCommand = getItemSetForNewCommand();
        DeleteItemSet deleteItemSet=new DeleteItemSet(itemSetForNewCommand);
        workspace.pushCommand(deleteItemSet);
    }

    public boolean contains(ItemViewController itemViewController){
        return managedItems.contains(itemViewController);
    }

}
