package com.nikhil.controller;

import com.nikhil.Main;
import com.nikhil.controller.item.ItemModelController;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.logging.Logger;
import com.nikhil.view.custom.*;
import com.nikhil.view.item.record.Metadata;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by NikhilVerma on 26/09/15.
 */
public class CompositionViewController {

    public static int TOTAL_COMPOSITIONS_SO_FAR =0;

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
        AnchorPane anchorPane = new AnchorPane();
        tab.setContent(anchorPane);

        HBox outerHBox = initSearchAndPlayback();
        itemTable = initItemTable();
        Ruler ruler=new Ruler(30, Main.WIDTH);//testing
        SelectionBar selectionBar=new SelectionBar(Main.WIDTH,null);
        ThumbSeeker thumbSeeker=new ThumbSeeker(Main.WIDTH);
        VBox vBox=new VBox(outerHBox, itemTable);
        AnchorPane.setLeftAnchor(vBox,0d);
        AnchorPane.setBottomAnchor(vBox, 0d);
        AnchorPane.setTopAnchor(vBox,0d);
        AnchorPane.setRightAnchor(vBox,0d);
        anchorPane.getChildren().add(vBox);
    }

    private HBox initSearchAndPlayback() {
        DraggableTextValue draggableTextValue=new DraggableTextValue(new DraggableTextValueDelegate() {
            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double newValue) {

            }
            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double finalValue) {

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

    private TreeTableView initItemTable(){

        rootTreeItem=new TreeItem<>(new Metadata("Root",true,Metadata.ROOT_TAG));

        TreeTableColumn<Metadata,String> name=new TreeTableColumn<>("Name");
        name.setCellValueFactory(param -> param.getValue().getValue().nameProperty());
        TreeTableColumn<Metadata,String> value=new TreeTableColumn<>("Value");
        TreeTableColumn<Metadata,String> option=new TreeTableColumn<>("Option");

        TreeTableView<Metadata> treeTableView=new TreeTableView<>(rootTreeItem);
        treeTableView.getColumns().addAll(name,value,option);
        treeTableView.setShowRoot(false);
        treeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null){//TODO NPE occurs during copy paste
                workspace.getSelectedItems().selectOnly(newValue.getValue().getItemViewController());
            }
        });
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
