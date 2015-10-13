package com.nikhil.controller;

import com.nikhil.editor.workspace.Workspace;
import com.nikhil.logging.Logger;
import com.nikhil.view.control.DraggableTextValue;
import com.nikhil.view.control.DraggableTextValueDelegate;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

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
        HBox outerHBox = initSearchAndPlayback();

        anchorPane=new AnchorPane(outerHBox);
        tab.setContent(anchorPane);
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
        outerHBox.setAlignment(Pos.CENTER);
        outerHBox.setPadding(new Insets(0,2,0,2));
        return outerHBox;
    }
}
