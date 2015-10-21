package com.nikhil.controller;

import com.nikhil.Main;
import com.nikhil.controller.item.ItemModelController;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.logging.Logger;
import com.nikhil.view.custom.*;
import com.nikhil.view.item.record.Metadata;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
    private static final double CELL_SIZE= 25;



    private CompositionController compositionController;
    private Tab tab;
    private Workspace workspace;
    private List<ItemViewController> itemViewControllers = new LinkedList<ItemViewController>();
    private TreeItem<Metadata> rootTreeItem;
    private TreeTableView<Metadata> itemTable;

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
        rootTreeItem.getChildren().add(itemViewController.getMetadataTree());
        //TODO add to the timeline
    }

    public boolean removeItemViewController(ItemViewController itemViewController){
        boolean removed = itemViewControllers.remove(itemViewController);
        if(removed){
            rootTreeItem.getChildren().remove(itemViewController.getMetadataTree());
            //TODO remove from timeline
        }
        return removed;
    }

    public Iterator<ItemViewController> getItemViewControllerIterator(){
        return itemViewControllers.iterator();
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void addToTimelineSystem(ItemViewController itemViewController){
        //add to the timeline
        ItemModelController itemModelController = itemViewController.getModelController();
        compositionController.addItemController(itemModelController);
    }

    private void initView(){
        final double LEFT_COMPONENT_WIDTH=NAME_COLUMN_WIDTH+VALUE_COLUMN_WIDTH+OPTION_COLUMN_WIDTH;
        final double RIGHT_COMPONENT_WIDTH = Main.WIDTH - LEFT_COMPONENT_WIDTH;

        AnchorPane anchorPane = new AnchorPane();
        tab.setContent(anchorPane);

        HBox outerHBox = initSearchAndPlayback();
        outerHBox.setSpacing(5);
        itemTable = initItemTable();


        SelectionBar selectionBar=new SelectionBar(RIGHT_COMPONENT_WIDTH,null);
        Ruler ruler=new Ruler(30, RIGHT_COMPONENT_WIDTH);
        ThumbSeeker thumbSeeker=new ThumbSeeker(RIGHT_COMPONENT_WIDTH);
        TreeTableView keyframeTable=initKeyframeTable();
        thumbSeeker.setPrefHeight(PLAYBACK_FEATURES_HEIGHT);
        thumbSeeker.setMaxHeight(Control.USE_PREF_SIZE);
        anchorPane.getChildren().addAll(outerHBox, itemTable, ruler, keyframeTable,selectionBar, thumbSeeker.getLineMark(),thumbSeeker);
        thumbSeeker.getLineMark().setLayoutX(LEFT_COMPONENT_WIDTH);
        thumbSeeker.getLineMark().endYProperty().bind(anchorPane.heightProperty());

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

        AnchorPane.setTopAnchor(keyframeTable, PLAYBACK_FEATURES_HEIGHT);
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
        Button playPause=new Button(">");
        Button nextKeyframe=new Button("|>");
        Button gotoEnd=new Button(">|");

        ToolBar playerControls=new ToolBar(gotoBeginning,previousKeyframe,playPause,nextKeyframe,gotoEnd);
        HBox outerHBox=new HBox(draggableTextValue,searchField,playerControls);
        outerHBox.setAlignment(Pos.CENTER_LEFT);
        outerHBox.setPadding(new Insets(0,2,0,2));
        return outerHBox;
    }

    private TreeTableView initKeyframeTable(){
        TreeTableColumn keyframe=new TreeTableColumn<>();
        final double LEFT_COMPONENT_WIDTH=NAME_COLUMN_WIDTH+VALUE_COLUMN_WIDTH+OPTION_COLUMN_WIDTH;
        double RIGHT_COMPONENT_WIDTH = Main.WIDTH - LEFT_COMPONENT_WIDTH;
        keyframe.setPrefWidth(RIGHT_COMPONENT_WIDTH);

        TreeTableView<Metadata> treeTableView=new TreeTableView<>(rootTreeItem);
        treeTableView.getColumns().addAll(keyframe);
        treeTableView.setShowRoot(false);
        treeTableView.setFixedCellSize(CELL_SIZE);
        return treeTableView;
    }

    private TreeTableView<Metadata> initItemTable(){

        rootTreeItem=new TreeItem<>(new Metadata("Root",Metadata.ROOT_TAG));

        TreeTableColumn<Metadata,String> name=new TreeTableColumn<>("Name");
        name.setCellValueFactory(param -> param.getValue().getValue().nameProperty());
        name.setPrefWidth(NAME_COLUMN_WIDTH);

        TreeTableColumn<Metadata,Metadata> value=new TreeTableColumn<>("Value");
        value.setCellFactory(param -> new ItemTableValueCell());
        value.setCellValueFactory(param -> new SimpleObjectProperty<Metadata>(param.getValue().getValue()));
        value.setPrefWidth(VALUE_COLUMN_WIDTH);

        TreeTableColumn<Metadata,String> option=new TreeTableColumn<>("Option");
        option.setPrefWidth(OPTION_COLUMN_WIDTH);

        TreeTableView<Metadata> treeTableView=new TreeTableView<>(rootTreeItem);
        treeTableView.getColumns().addAll(name,value,option);
        treeTableView.setShowRoot(false);
        treeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null){//TODO NPE occurs during copy paste
                workspace.getSelectedItems().selectOnly(newValue.getValue().getItemViewController());
            }
        });
        treeTableView.setFixedCellSize(CELL_SIZE);
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
    }
}
