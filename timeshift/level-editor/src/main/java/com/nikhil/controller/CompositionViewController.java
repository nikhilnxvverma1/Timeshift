package com.nikhil.controller;

import com.nikhil.Main;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.logging.Logger;
import com.nikhil.view.custom.DraggableTextValue;
import com.nikhil.view.custom.DraggableTextValueDelegate;
import com.nikhil.view.custom.Ruler;
import com.nikhil.view.item.record.Metadata;
import com.nikhil.view.item.record.PolygonMetadata;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Created by NikhilVerma on 26/09/15.
 */
public class CompositionViewController {

    public static int TOTAL_COMPOSITIONS_SO_FAR =0;

    private CompositionController compositionController;
    private Tab tab;
    private Workspace workspace;
    private AnchorPane anchorPane;

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

    private void initView(){
        anchorPane=new AnchorPane();
        tab.setContent(anchorPane);

        HBox outerHBox = initSearchAndPlayback();
        TreeTableView itemTable = initItemTable();
        Ruler ruler=new Ruler(30, Main.WIDTH);//testing
        VBox vBox=new VBox(outerHBox,ruler);
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
        //making a sample Metadata model for a composition controller
        TreeItem<Metadata> polygonHeader= new TreeItem<>(new PolygonMetadata("Polygon 1",true,1,null));
        TreeItem<Metadata> polygonScale= new TreeItem<>(new PolygonMetadata("Scale",false,2,null));
        TreeItem<Metadata> polygonRotation= new TreeItem<>(new PolygonMetadata("Rotation",false,3,null));
        TreeItem<Metadata> polygonTranslation= new TreeItem<>(new PolygonMetadata("Translation",false,4,null));
        TreeItem<Metadata> polygonAnchorPoint= new TreeItem<>(new PolygonMetadata("Anchor Point", false, 5, null));
        TreeItem<Metadata> polygonVertices= new TreeItem<>(new PolygonMetadata("Vertices", false, 6, null));
        polygonHeader.getChildren().addAll(polygonScale, polygonRotation, polygonTranslation, polygonAnchorPoint, polygonVertices);


        TreeTableColumn<Metadata,String> name=new TreeTableColumn<>("Name");
        TreeTableColumn<Metadata,String> value=new TreeTableColumn<>("Value");
        TreeTableColumn<Metadata,String> option=new TreeTableColumn<>("Option");

        TreeTableView<Metadata> treeTableView=new TreeTableView<>(polygonHeader);
        treeTableView.getColumns().addAll(name,value,option);
        return treeTableView;
    }
}
