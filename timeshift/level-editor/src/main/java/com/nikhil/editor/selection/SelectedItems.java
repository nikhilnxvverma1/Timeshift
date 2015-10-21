package com.nikhil.editor.selection;

import com.nikhil.command.*;
import com.nikhil.controller.ItemViewController;
import com.nikhil.controller.ShapeViewController;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.logging.Logger;
import com.nikhil.math.MathUtil;
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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


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

    public SelectedItems(Workspace workspace) {
        this.workspace=workspace;
    }

    public void initializeView(){
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
            updateView();
        }
        singleItemSelectedInDetail =false;
        return wasRemoved;
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
            shapeViewController.rotateBy(dRotation);
            this.setRotate(this.getRotate()+dRotation);
        }else{
            //Push the rotation command on the command stack without executing it.
            double initialAngle=temporaryValue1;
            double finalAngle=shapeViewController.getRotation();
            RotateShape rotateShape=new RotateShape(shapeViewController,initialAngle,finalAngle);
            workspace.pushCommand(rotateShape,false);
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
            boolean didScale = shapeViewController.scaleBy(dScale);
            if(didScale){
                temporaryValue2+=dScale;
            }
            updateView();
        }else{
            double initialScale=temporaryValue1;
            double finalScale=initialScale+temporaryValue2;
            ScaleShape scaleShape =new ScaleShape(shapeViewController,initialScale,finalScale);
            workspace.pushCommand(scaleShape,false);
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
        for(ItemViewController itemViewController:managedItems){
            itemViewController.moveBy(dx,dy);
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
            MoveItemSet moveItemSet=new MoveItemSet(itemSetForNewCommand,this,initialPoint,finalPoint);
            workspace.pushCommand(moveItemSet,false);

            moveCommandWasIssued=true;
        }
        //update initialX,initialY to current position in worksheet points
        initialX=x;
        initialY=y;
        return moveCommandWasIssued;
    }

    public Set<ItemViewController> getItemSetForNewCommand(){
        Command topCommand=workspace.getTopCommandOnCommandStack();
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
        DeleteItemSet deleteItemSet=new DeleteItemSet(itemSetForNewCommand,this);
        workspace.pushCommand(deleteItemSet);
    }

    public boolean contains(ItemViewController itemViewController){
        return managedItems.contains(itemViewController);
    }

}
