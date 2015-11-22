package com.nikhil.view.custom.keyframe;

import apple.laf.JRSUIUtils;
import com.nikhil.editor.selection.SelectionArea;
import com.nikhil.editor.selection.SelectionOverlap;
import com.nikhil.view.custom.cells.KeyframeCell;
import com.nikhil.view.item.record.Metadata;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.sun.javafx.scene.control.skin.VirtualScrollBar;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;

import java.util.LinkedList;

/**
 * Custom TreeView for Keyframe table that encapsulates and handles all events.
 * Created by NikhilVerma on 27/10/15.
 */
public class KeyframeTreeView extends TreeView<Metadata> implements SelectionOverlap {

    private final SelectionArea selectionArea=new SelectionArea(this);

    //=============================================================================================
    //Event handlers that will need to be unhooked later
    //=============================================================================================
    private boolean startedPressOnVirtualFlow=false;//we assume that the press is initially not made on the virtual flow
    private final EventHandler<MouseEvent> mousePressedListener = e -> {
//        startedPressOnVirtualFlow=false;//we assume that the press is initially not made on the virtual flow
        if(e.getTarget() instanceof KeyframeView){
            return;
        }
        VirtualFlow virtualFlow=null;
        for(Node node:this.getChildrenUnmodifiable()){
            if(node instanceof VirtualFlow){
                virtualFlow = (VirtualFlow)node;
            }
        }
        if(virtualFlow==null){
            return;
        }

        VirtualScrollBar virtualScrollBar = null;
        for(Node node:virtualFlow.getChildrenUnmodifiable()){
            if(node instanceof VirtualScrollBar){
                virtualScrollBar=(VirtualScrollBar)node;
                if (virtualScrollBar.isVisible()) {
                    Point2D point2D = virtualScrollBar.sceneToLocal(e.getSceneX(), e.getSceneY());
                    if (virtualScrollBar.contains(point2D)) {
                        //mouse press inside virtual Scroll bar
//                        e.consume();
                        return;
                    }
                }
            }
        }
        //by now we know that that the press is on the virtual flow
        startedPressOnVirtualFlow=true;
        Point2D point2D = this.localToParent(e.getX(), e.getY());
        selectionArea.startSelection(point2D.getX(),point2D.getY());
        e.consume();
    };
    private final EventHandler<MouseEvent> mouseDraggedListener = e-> {
        if(!startedPressOnVirtualFlow||e.getTarget() instanceof KeyframeView){
            return;
        }
        VirtualFlow virtualFlow=null;
        for(Node node:this.getChildrenUnmodifiable()){
            if(node instanceof VirtualFlow){
                virtualFlow = (VirtualFlow)node;
            }
        }
        if(virtualFlow==null){
            return;
        }
        VirtualScrollBar virtualScrollBar = null;
        for(Node node:virtualFlow.getChildrenUnmodifiable()){
            if(node instanceof VirtualScrollBar){
                virtualScrollBar=(VirtualScrollBar)node;
                if (virtualScrollBar.isVisible()) {
                    Point2D point2D = virtualScrollBar.sceneToLocal(e.getSceneX(), e.getSceneY());
                    if (virtualScrollBar.contains(point2D)) {
                        //mouse press inside virtual Scroll bar
//                        e.consume();
                        return;
                    }
                }
            }
        }
        Point2D point2D = this.localToParent(e.getX(), e.getY());
        selectionArea.moveSelection(point2D.getX(), point2D.getY());
        e.consume();
    };
    private final EventHandler<MouseEvent> mouseReleasedListener = e->{

        if(e.getTarget() instanceof KeyframeView){
            return;
        }
        VirtualFlow virtualFlow=null;
        for(Node node:this.getChildrenUnmodifiable()){
            if(node instanceof VirtualFlow){
                virtualFlow = (VirtualFlow)node;
            }
        }
        if(virtualFlow==null){
            return;
        }
        VirtualScrollBar virtualScrollBar = null;
        for(Node node:virtualFlow.getChildrenUnmodifiable()){
            if(node instanceof VirtualScrollBar){
                virtualScrollBar=(VirtualScrollBar)node;
                if (virtualScrollBar.isVisible()) {
                    Point2D point2D = virtualScrollBar.sceneToLocal(e.getSceneX(), e.getSceneY());
                    if (virtualScrollBar.contains(point2D)) {
                        //mouse press inside virtual Scroll bar
//                        e.consume();
                        return;
                    }
                }
            }
        }
        Point2D point2D = this.localToParent(e.getX(), e.getY());
        selectionArea.endSelection();
        e.consume();
        startedPressOnVirtualFlow=false;//resetting the flag here ensures that it maintains the correct state for the next press
    };


    public KeyframeTreeView(TreeItem<Metadata> root) {
        super(root);
        this.setCellFactory(param -> new KeyframeCell());
        this.setShowRoot(false);
        this.setFixedCellSize(Metadata.CELL_HEIGHT);
        this.getStyleClass().add("no-horizontal-scrollbar");
        this.addEventFilter(MouseEvent.MOUSE_PRESSED, mousePressedListener);
        this.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDraggedListener);
        this.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseReleasedListener);
    }


    /**
     * finds the first virtual flow in this treeview
     * @return virtual flow if found,else null
     */
    private VirtualFlow findVirtualFlow(){
        return findVirtualFlow(this);
    }
    /**
     * finds the first virtual flow if present in a node
     * @param parent parent which has unmodifieable children
     * @return virtual flow if found,else null
     */
    private VirtualFlow findVirtualFlow(Parent parent){
        for(Node node:parent.getChildrenUnmodifiable()){
            if(node instanceof VirtualFlow){
                return (VirtualFlow)node;
            }
        }
        return null;
    }

    /**
     * @deprecated
     * finds the first virtual scroll bar if present in a node
     * @param parent parent which has unmodifieable children
     * @return virtual scrollbar if found,else null
     */
    private VirtualScrollBar findVirtualScrollBar(Parent parent){
        for(Node node:parent.getChildrenUnmodifiable()){
            if(node instanceof VirtualScrollBar){
                return (VirtualScrollBar)node;
            }
        }
        return null;
    }

    public SelectionArea getSelectionArea() {
        return selectionArea;
    }

    @Override
    public void selectOverlappingItems(SelectionArea selectionArea, Bounds sceneBounds) {
        VirtualFlow virtualFlow = findVirtualFlow();
        IndexedCell firstVisibleCell = virtualFlow.getFirstVisibleCell();
        IndexedCell lastVisibleCell = virtualFlow.getLastVisibleCell();
        for (int i = firstVisibleCell.getIndex(); i <= lastVisibleCell.getIndex(); i++) {
            KeyframeCell keyframeCell=(KeyframeCell)virtualFlow.getCell(i);
            Node graphic = keyframeCell.getGraphic();

            if (graphic instanceof KeyframePane) {
                KeyframePane keyframePane = (KeyframePane) graphic;
                keyframePane.selectOverlappingItems(selectionArea,sceneBounds);

            }
        }
    }

    @Override
    public void resetSelection() {
        resetSelectionOfEachChild(this.getRoot());
    }
    /**
     * recursively resets selection of each tree item
     * @param treeItem the tree item at which keyframe selections need to be reset
     */
    private void resetSelectionOfEachChild(TreeItem<Metadata> treeItem){
        KeyframePane keyframePane = treeItem.getValue().getKeyframePane();
        if(keyframePane!=null){
            keyframePane.resetSelection();
        }
        for(TreeItem<Metadata> child:treeItem.getChildren()){
            resetSelectionOfEachChild(child);
        }
    }

    /**
     * recursively resets selection of each tree item's KeyframePane starting at root
     * except the specified keyframe
     * @param notToReset the keyframe pane that should NOT be reset
     */
    public void resetSelectionOfEachExcept(KeyframePane notToReset){
        resetSelectionOfEachChildExcept(this.getRoot(),notToReset);
    }

    /**
     * recursively resets selection of each tree item except the specified keyframe
     * @param treeItem the tree item at which keyframe selections need to be reset
     * @param notToReset the keyframe pane that should NOT be reset
     */
    private void resetSelectionOfEachChildExcept(TreeItem<Metadata> treeItem,KeyframePane notToReset){
        KeyframePane keyframePane = treeItem.getValue().getKeyframePane();
        if(keyframePane!=notToReset&&keyframePane!=null){
            keyframePane.resetSelection();
        }
        for(TreeItem<Metadata> child:treeItem.getChildren()){
            resetSelectionOfEachChildExcept(child, notToReset);
        }
    }

    /**
     * recursively moves the keys of KeyframesPanes from each child starting at root
     * @param dl the amount by which the keys should move
     */
    public void moveSelectedKeysBy(double dl){
        moveSelectedKeysOfEachChild(this.getRoot(),dl);
    }

    /**
     * recursively moves the keys of KeyframesPanes from each child
     * @param treeItem the root from which children KeyframePanes' keys need to move
     * @param dl the amount by which the keys should move
     */
    private void moveSelectedKeysOfEachChild(TreeItem<Metadata> treeItem,double dl){
        KeyframePane keyframePane = treeItem.getValue().getKeyframePane();
        if(keyframePane!=null){
            keyframePane.moveSelectedKeysBy(dl);
        }
        for(TreeItem<Metadata> child:treeItem.getChildren()){
            moveSelectedKeysOfEachChild(child,dl);
        }
    }

    public KeyframeView findKeyframeAfter(double time){
        return bestNextKeyframeAfter(this.getRoot(),time,null);
    }

    private KeyframeView bestNextKeyframeAfter(TreeItem<Metadata> treeItem,double time,KeyframeView bestSoFar){

        if(treeItem.getValue().isHeader()){

            //amongst all children of this tree item,find the keyframe that's closest to the time
            for(TreeItem<Metadata> child:treeItem.getChildren()){
                KeyframeView childBest = bestNextKeyframeAfter(child, time, bestSoFar);
                if(bestSoFar==null||
                        (childBest!=null &&
                                childBest.getTime()<bestSoFar.getTime())){
                    bestSoFar=childBest;
                }
            }
            return bestSoFar;
        }else{
            KeyframePane keyframePane = treeItem.getValue().getKeyframePane();
            if(keyframePane==null){
                return bestSoFar;
            }
            KeyframeView keyframeRightAfter = keyframePane.findKeyframeAfter(time);
            if(bestSoFar==null||
                    (keyframeRightAfter!=null&&
                            keyframeRightAfter.getTime()<bestSoFar.getTime())){
                return keyframeRightAfter;
            }else{
                return bestSoFar;
            }
        }
    }

    public KeyframeView findKeyframeBefore(double currentTime) {
        return bestPreviousKeyframeBefore(this.getRoot(),currentTime,null);
    }

    private KeyframeView bestPreviousKeyframeBefore(TreeItem<Metadata> treeItem,double time,KeyframeView bestSoFar){

        if(treeItem.getValue().isHeader()){

            //amongst all children of this tree item,find the keyframe that's closest to the time
            for(TreeItem<Metadata> child:treeItem.getChildren()){
                KeyframeView childBest = bestPreviousKeyframeBefore(child, time, bestSoFar);
                if(bestSoFar==null||
                        (childBest!=null &&
                                childBest.getTime()>bestSoFar.getTime())){
                    bestSoFar=childBest;
                }
            }
            return bestSoFar;
        }else{
            KeyframePane keyframePane = treeItem.getValue().getKeyframePane();
            if(keyframePane==null){
                return bestSoFar;
            }
            KeyframeView keyframeRightBefore = keyframePane.findKeyframeBefore(time);
            if(bestSoFar==null||
                    (keyframeRightBefore!=null&&
                            keyframeRightBefore.getTime()>bestSoFar.getTime())){
                return keyframeRightBefore;
            }else{
                return bestSoFar;
            }
        }
    }

    public KeyframeView findLastSelectedKeyframe(){
        return bestLastKeyframe(this.getRoot(),null);
    }

    public KeyframeView bestLastKeyframe(TreeItem<Metadata> treeItem,KeyframeView bestSoFar){
        if(treeItem.getValue().isHeader()){
            for(TreeItem<Metadata> child:treeItem.getChildren()){
                KeyframeView childBest = bestLastKeyframe(child, bestSoFar);
                if(bestSoFar==null||
                        (childBest!=null &&
                                childBest.getTime()>bestSoFar.getTime())){
                    bestSoFar=childBest;
                }
            }
            return bestSoFar;
        }else{
            KeyframePane keyframePane = treeItem.getValue().getKeyframePane();
            if(keyframePane==null){
                return bestSoFar;
            }
            KeyframeView lastSelectedKeyframe = keyframePane.findLastSelectedKeyframe();
            if(bestSoFar==null||
                    (lastSelectedKeyframe!=null&&
                            lastSelectedKeyframe.getTime()>bestSoFar.getTime())){
                return lastSelectedKeyframe;
            }else{
                return bestSoFar;
            }
        }
    }

    public KeyframeView findFirstSelectedKeyframe() {
        return bestFirstKeyframe(this.getRoot(), null);
    }

    public KeyframeView bestFirstKeyframe(TreeItem<Metadata> treeItem,KeyframeView bestSoFar){
        if(treeItem.getValue().isHeader()){
            for(TreeItem<Metadata> child:treeItem.getChildren()){
                KeyframeView childBest = bestFirstKeyframe(child, bestSoFar);
                if(bestSoFar==null||
                        (childBest!=null &&
                                childBest.getTime()<bestSoFar.getTime())){
                    bestSoFar=childBest;
                }
            }
            return bestSoFar;
        }else{
            KeyframePane keyframePane = treeItem.getValue().getKeyframePane();
            if(keyframePane==null){
                return bestSoFar;
            }
            KeyframeView firstSelectedKeyframe = keyframePane.findFirstSelectedKeyframe();
            if(bestSoFar==null||
                    (firstSelectedKeyframe!=null &&
                            firstSelectedKeyframe.getTime()<bestSoFar.getTime())){
                return firstSelectedKeyframe;
            }else{
                return bestSoFar;
            }
        }
    }

    /**
     * Counts the number of selected keyframes from each keyframe pane
     * @return total selected keyframes
     */
    public int countSelectedKeyframe(){
        return countSelectedKeyframe(getRoot());
    }

    private int countSelectedKeyframe(TreeItem<Metadata>treeItem){
        if(treeItem.getValue().isHeader()){
            int total=0;
            for(TreeItem<Metadata> child:treeItem.getChildren()){
                total+=child.getValue().getKeyframePane().countSelectedKeyframes();
            }
            return total;
        }else{
            return treeItem.getValue().getKeyframePane().countSelectedKeyframes();
        }
    }

    /**
     * Checks if this tree view contains the supplied keyframe view
     * @param keyframeView the keyframe view that needs to be checked for containment
     * @return true if the keyframe exists in this keyframe pane,false otherwise
     */
    public boolean contains(KeyframeView keyframeView){
        return contains(getRoot(),keyframeView);
    }

    private boolean contains(TreeItem<Metadata> treeItem,KeyframeView keyframeView){
        if(treeItem.getValue().isHeader()){
            for(TreeItem<Metadata> child:treeItem.getChildren()){
                if (child.getValue().getKeyframePane().contains(keyframeView)) {
                    return true;
                }
            }
            return false;
        }else{
            return treeItem.getValue().getKeyframePane().contains(keyframeView);
        }
    }

    /**
     * Builds a linked list of selected keys by recursively visiting each keyframe pane
     * @return Linked list of selected keys
     */
    public LinkedList<KeyframeView> getSelectedKeys(){
        LinkedList<KeyframeView> selectedKeyframes=new LinkedList<>();
        populateWithSelectedKeys(getRoot(),selectedKeyframes);
        return selectedKeyframes;
    }

    private void populateWithSelectedKeys(TreeItem<Metadata> treeItem, LinkedList<KeyframeView> keyframeViewList){
        if(treeItem.getValue().isHeader()){
            for(TreeItem<Metadata> child:treeItem.getChildren()){
                populateWithSelectedKeys(child,keyframeViewList);
            }
        }else {
            KeyframePane keyframePane = treeItem.getValue().getKeyframePane();
            if (keyframePane!=null) {
                keyframePane.fillWithSelectedKeyframes(keyframeViewList);
            }
        }
    }

}
