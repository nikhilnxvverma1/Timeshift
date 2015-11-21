package com.nikhil.controller;

import com.nikhil.Main;
import com.nikhil.controller.item.ItemModelController;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.logging.Logger;
import com.nikhil.view.custom.*;
import com.nikhil.view.custom.cells.KeyframeCell;
import com.nikhil.view.custom.cells.NameCell;
import com.nikhil.view.custom.cells.OptionCell;
import com.nikhil.view.custom.cells.ValueCell;
import com.nikhil.view.custom.keyframe.KeyframeView;
import com.nikhil.view.custom.keyframe.KeyframeTreeView;
import com.nikhil.view.item.record.HeaderMetadata;
import com.nikhil.view.item.record.Metadata;
import com.nikhil.view.item.record.MetadataTag;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.sun.javafx.scene.control.skin.VirtualScrollBar;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by NikhilVerma on 26/09/15.
 */
public class CompositionViewController {

    public static final double PLAYBACK_FEATURES_HEIGHT = 40d;
    public static int TOTAL_COMPOSITIONS_SO_FAR =0;
    private static final double NAME_COLUMN_WIDTH=175;
    private static final double VALUE_COLUMN_WIDTH=125;
    private static final double OPTION_COLUMN_WIDTH=100;
    public static final double NEGLIGIBLE_TIME_DIFFERENCE=0.1;

    private CompositionController compositionController;
    private Tab tab;
    private Workspace workspace;
    private List<ItemViewController> itemViewControllers = new LinkedList<ItemViewController>();
    private TreeItem<Metadata> rootTreeItem;
    private TreeTableView<Metadata> itemTable;
    private KeyframeTreeView keyframeTable;
    private ThumbSeeker thumbSeeker;

    public CompositionViewController(CompositionController compositionController,Workspace workspace) {
        this.compositionController=compositionController;
        this.workspace=workspace;
        this.tab=new Tab();
        String tabName = getNewTabName();
        this.tab.setText(tabName);
        this.tab.setOnSelectionChanged(e->{
            if(tab.isSelected()){
                Logger.log(tab.getText()+" is selected");
                workspace.setCurrentComposition(this);
            }
        });
        initView();
    }

    private String getNewTabName() {
        return "Comp " + ++TOTAL_COMPOSITIONS_SO_FAR;//TODO store the composition names in the model itself
    }

    public CompositionController getCompositionController() {
        return compositionController;
    }

    public Tab getTab() {
        return tab;
    }

    public void addItemViewController(ItemViewController itemViewController){
        itemViewControllers.add(itemViewController);
        TreeItem<Metadata> metadataTree = itemViewController.getMetadataTree();
        rootTreeItem.getChildren().add(metadataTree);
//        int totalNewRecords = countExpandedChildren(metadataTree);
//        commonScrollBar.setMax(commonScrollBar.getMax()+totalNewRecords);
        //TODO add to the timeline
    }

    public boolean removeItemViewController(ItemViewController itemViewController){
        boolean removed = itemViewControllers.remove(itemViewController);
        if(removed){
            TreeItem<Metadata> metadataTree = itemViewController.getMetadataTree();
            rootTreeItem.getChildren().remove(metadataTree);
//            int totalRecordsDeleted = countExpandedChildren(metadataTree);
//            commonScrollBar.setMax(commonScrollBar.getMax()-totalRecordsDeleted);
            //TODO remove from timeline
        }
        return removed;
    }

    /**
     * counts all children of this tree item recursively
     * @param treeItem children under this node(including itself)
     * @return total count expanded
     */
    private int countExpandedChildren(TreeItem treeItem){
        ObservableList children = treeItem.getChildren();
        int count=1;
        for(Object node: children){
            count+=countExpandedChildren((TreeItem)node);
        }
        return count;
    }

    public Iterator<ItemViewController> getItemViewControllerIterator(){
        return itemViewControllers.iterator();
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public KeyframeTreeView getKeyframeTable() {
        return keyframeTable;
    }

    public void addToTimelineSystem(ItemViewController itemViewController){
        //add to the timeline
        ItemModelController itemModelController = itemViewController.getModelController();
        compositionController.addItemController(itemModelController);
    }

    /**
     * finds the virtual flow if present in a node
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
     * finds the vertical virtual scroll bar if present in a node
     * @param parent parent which has unmodifieable children
     * @return virtual scrollbar if found,else null
     */
    private VirtualScrollBar findVirtualScrollBar(Parent parent){
        for(Node node:parent.getChildrenUnmodifiable()){
            if(node instanceof VirtualScrollBar&& ((VirtualScrollBar)node).getOrientation()==Orientation.VERTICAL){
                return (VirtualScrollBar)node;
            }
        }
        return null;
    }

    private ScrollBar itemTableScrollBar=null;
    private ScrollBar keyframeScrollBar=null;
    ListChangeListener<Node> itemTableUnmodifiedChildrenListener = (ListChangeListener<Node>) c -> {
        while (c.next()) {
            if (c.wasAdded() && !c.wasRemoved()) {
                List<? extends Node> addedSubList = c.getAddedSubList();
                for (Node node : addedSubList) {
                    if (node instanceof VirtualFlow) {
                        itemTableScrollBar = CompositionViewController.this.findVirtualScrollBar((VirtualFlow) node);
                        if (keyframeScrollBar != null) {
                            keyframeScrollBar.valueProperty().bindBidirectional(itemTableScrollBar.valueProperty());
                        }
                    }
                }
            }
        }
    };
    ListChangeListener<Node> keyframeTableUnmodifiedChildrenListener = (ListChangeListener<Node>) c -> {
        while (c.next()) {
            if (c.wasAdded() && !c.wasRemoved()) {
                List<? extends Node> addedSubList = c.getAddedSubList();
                for (Node node : addedSubList) {
                    if (node instanceof VirtualFlow) {
                        keyframeScrollBar = CompositionViewController.this.findVirtualScrollBar((VirtualFlow) node);
                        if (itemTableScrollBar != null) {
                            itemTableScrollBar.valueProperty().bindBidirectional(keyframeScrollBar.valueProperty());
                        }
                    }
                }
            }
        }
    };
    private void initView(){
        final double LEFT_COMPONENT_WIDTH=NAME_COLUMN_WIDTH+VALUE_COLUMN_WIDTH+OPTION_COLUMN_WIDTH;
        final double RIGHT_COMPONENT_WIDTH = Main.WIDTH - LEFT_COMPONENT_WIDTH;

        AnchorPane anchorPane = new AnchorPane();
        tab.setContent(anchorPane);

        HBox outerHBox = initSearchAndPlayback();
        outerHBox.setSpacing(5);

        SelectionBar selectionBar=new SelectionBar(RIGHT_COMPONENT_WIDTH,null);
        Ruler ruler=new Ruler(30, RIGHT_COMPONENT_WIDTH);
        thumbSeeker = new ThumbSeeker(RIGHT_COMPONENT_WIDTH);
        thumbSeeker.setPrefHeight(PLAYBACK_FEATURES_HEIGHT);
        thumbSeeker.setMaxHeight(Control.USE_PREF_SIZE);

        rootTreeItem=new TreeItem<>(new HeaderMetadata("Root", MetadataTag.ROOT));
        itemTable = initItemTable(rootTreeItem);
        keyframeTable =new KeyframeTreeView(rootTreeItem);

        //bind the scrollbars of the two tables TODO unhook these listeners later

        itemTable.getChildrenUnmodifiable().addListener(itemTableUnmodifiedChildrenListener);
        keyframeTable.getChildrenUnmodifiable().addListener(keyframeTableUnmodifiedChildrenListener);

        anchorPane.getChildren().addAll(outerHBox, itemTable, ruler, keyframeTable, selectionBar,
                thumbSeeker.getLineMark(), thumbSeeker, keyframeTable.getSelectionArea().getSelectRect());
        thumbSeeker.getLineMark().setLayoutX(LEFT_COMPONENT_WIDTH);
        thumbSeeker.getLineMark().endYProperty().//just below so that it doesn't increase with every resize
                bind(itemTable.heightProperty().add(PLAYBACK_FEATURES_HEIGHT - 2));

        AnchorPane.setLeftAnchor(outerHBox, 0d);
        AnchorPane.setTopAnchor(outerHBox, 0d);

        AnchorPane.setTopAnchor(itemTable, PLAYBACK_FEATURES_HEIGHT);
        AnchorPane.setLeftAnchor(itemTable, 0d);
        AnchorPane.setBottomAnchor(itemTable, 0d);

        AnchorPane.setTopAnchor(ruler,0d);
        AnchorPane.setLeftAnchor(ruler, LEFT_COMPONENT_WIDTH);
        AnchorPane.setRightAnchor(ruler,0d);

        AnchorPane.setTopAnchor(thumbSeeker, 0d);
        AnchorPane.setLeftAnchor(thumbSeeker,LEFT_COMPONENT_WIDTH);
        AnchorPane.setRightAnchor(thumbSeeker, 0d);

        AnchorPane.setTopAnchor(keyframeTable, PLAYBACK_FEATURES_HEIGHT+selectionBar.getAreaHeight());
        AnchorPane.setLeftAnchor(keyframeTable,LEFT_COMPONENT_WIDTH);
        AnchorPane.setRightAnchor(keyframeTable, 0d);
        AnchorPane.setBottomAnchor(keyframeTable, 0d);

        AnchorPane.setTopAnchor(selectionBar, PLAYBACK_FEATURES_HEIGHT);
        AnchorPane.setLeftAnchor(selectionBar,LEFT_COMPONENT_WIDTH);
        AnchorPane.setRightAnchor(selectionBar, 0d);

    }

    private HBox initSearchAndPlayback() {
        DraggableTextValue draggableTextValue=new DraggableTextValue(new DraggableTextValueDelegate() {
            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double initialValue, double oldValue, double newValue) {

            }
            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double initialValue, double finalValue, boolean dragged) {

            }
        });
        TextField searchField=new TextField();
        searchField.setPromptText("Search");
        Button gotoBeginning=new Button("|<");
        Button previousKeyframe=new Button("<|");
        previousKeyframe.setOnAction(event -> this.jumpTimeToPreviousKeyframe());
        Button playPause=new Button(">");
        Button nextKeyframe=new Button("|>");
        nextKeyframe.setOnAction(event -> this.jumpTimeToNextKeyframe());
        Button gotoEnd=new Button(">|");

        ToolBar playerControls=new ToolBar(gotoBeginning,previousKeyframe,playPause,nextKeyframe,gotoEnd);
        HBox outerHBox=new HBox(draggableTextValue,searchField,playerControls);
        outerHBox.setAlignment(Pos.CENTER_LEFT);
        outerHBox.setPadding(new Insets(0,2,0,2));
        return outerHBox;
    }


    private void jumpTimeToNextKeyframe(){
        //seek the current value from the value of the thumb
        double currentTime;//in seconds
        currentTime=thumbSeeker.getCurrentValueAcross(30);

        //find the last selected keyframe which possibly might be where the thumb is at
        //as a result of a similar last "next" operation
        KeyframeView lastSelectedKeyframe = keyframeTable.findLastSelectedKeyframe();
        if (lastSelectedKeyframe != null) {

            //if the last selected keyframe from the keyframe table has its time SLIGHTLY greater
            //than current time, then use that.(this is so as to prevent time mismatch as a result of
            //double rounding problem where last selected keyframe maybe where the thumb is set at
            //and the thumb gives a lower value (which doesn't move the thumb forward,later)
            double timeDifference = lastSelectedKeyframe.getTime() - currentTime;
            if (timeDifference>0 && timeDifference <= NEGLIGIBLE_TIME_DIFFERENCE) {
                //the current time becomes the time of the last selected keyframe
                currentTime = lastSelectedKeyframe.getTime();
            }
        }


        KeyframeView keyframeAfter = keyframeTable.findKeyframeAfter(currentTime);
        if (keyframeAfter!=null) {
            keyframeTable.resetSelection();
            thumbSeeker.setCurrentValueAcross(keyframeAfter.getTime(), 30);
            keyframeAfter.setSelected(true);
        }
    }

    private void jumpTimeToPreviousKeyframe(){
        //seek the current value from the value of the thumb
        double currentTime;//in seconds
        currentTime=thumbSeeker.getCurrentValueAcross(30);

        //find the last selected keyframe which possibly might be where the thumb is at
        //as a result of a similar last "next" operation
        KeyframeView firstSelectedKeyframe = keyframeTable.findFirstSelectedKeyframe();
        if (firstSelectedKeyframe != null) {

            //if the first selected keyframe from the keyframe table has its time SLIGHTLY lesser
            //than current time, then use that.(this is so as to prevent time mismatch as a result of
            //double rounding problem where first selected keyframe maybe where the thumb is set at
            //and the thumb gives a higher value (which doesn't move the thumb forward,later)
            double timeDifference = currentTime - firstSelectedKeyframe.getTime();
            if (timeDifference>0 && timeDifference <= NEGLIGIBLE_TIME_DIFFERENCE) {
                //the current time becomes the time of the last selected keyframe
                currentTime = firstSelectedKeyframe.getTime();
            }
        }


        KeyframeView keyframeBefore = keyframeTable.findKeyframeBefore(currentTime);
        if (keyframeBefore!=null) {
            keyframeTable.resetSelection();
            thumbSeeker.setCurrentValueAcross(keyframeBefore.getTime(), 30);
            keyframeBefore.setSelected(true);
        }
    }

    /**
     * @deprecated use custom {@link KeyframeTreeView} instead
     * @return a treeview for keyframes
     */
    private TreeView<Metadata> initKeyframeTable(){
        TreeView<Metadata> treeView=new TreeView<>(rootTreeItem);
        treeView.setCellFactory(param -> new KeyframeCell());
        treeView.setShowRoot(false);
        treeView.setFixedCellSize(Metadata.CELL_HEIGHT);
        treeView.getStyleClass().add("no-horizontal-scrollbar");
        return treeView;
    }

    private TreeTableView<Metadata> initItemTable(TreeItem<Metadata> root){

        TreeTableColumn<Metadata,Metadata> name=new TreeTableColumn<>("Name");
        name.setCellFactory(param -> new NameCell());
        name.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getValue()));
        name.setPrefWidth(NAME_COLUMN_WIDTH);

        TreeTableColumn<Metadata,Metadata> value=new TreeTableColumn<>("Value");
        value.setCellFactory(param -> new ValueCell());
        value.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getValue()));
        value.setPrefWidth(VALUE_COLUMN_WIDTH);

        TreeTableColumn<Metadata,Metadata> option=new TreeTableColumn<>("Option");
        option.setCellFactory(param -> new OptionCell());
        option.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getValue()));
        option.setPrefWidth(OPTION_COLUMN_WIDTH);

        TreeTableView<Metadata> treeTableView=new TreeTableView<>(root);
        treeTableView.getColumns().addAll(name,value,option);
        treeTableView.setShowRoot(false);
        treeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null){//TODO NPE occurs during copy paste
                workspace.getSelectedItems().selectOnly(newValue.getValue().getItemViewController());
                keyframeTable.getSelectionModel().select(newValue);
            }
        });
        treeTableView.getStyleClass().add("no-vertical-scrollbar");
        treeTableView.setFixedCellSize(Metadata.CELL_HEIGHT);

        return treeTableView;
    }

    /**
     * remove from timeline system
     * @param itemViewController the associated item view controller whose model controller needs to be removed
     * @return indicate weather the node has been removed
     */
    public boolean removeFromTimelineSystem(ItemViewController itemViewController) {
        return compositionController.removeItemController(itemViewController.getModelController());
    }

    /**
     * selects the header record from the item table for the supplied metadata tag,
     * if it exists
     * @param headerTreeItem the metadata tag to select
     */
    public void selectRecordFromItemTable(TreeItem<Metadata> headerTreeItem){
        itemTable.getSelectionModel().select(headerTreeItem);
        keyframeTable.getSelectionModel().select(headerTreeItem);
    }

    public double getTime(){
        return thumbSeeker.getCurrentValueAcross(30);
    }

}
