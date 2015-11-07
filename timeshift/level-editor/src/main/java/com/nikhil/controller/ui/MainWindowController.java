package com.nikhil.controller.ui;

import com.nikhil.Main;
import com.nikhil.controller.CompositionViewController;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.editor.tool.*;
import com.nikhil.editor.workspace.WorkspaceListener;
import com.nikhil.logging.Logger;
import com.nikhil.view.util.AlertBox;
import com.nikhil.view.util.ConfirmBox;
import com.nikhil.view.zoom.ZoomableScrollPane;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;

import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;

/**
 * Created by NikhilVerma on 22/08/15.
 */
public class MainWindowController implements WorkspaceListener {

    public static final String OPEN_FILE = "Open file";
    public static final String SAVE_FILE_AS = "Save File As...";
    //=============================================================================================
    //Tools
    //=============================================================================================
    private Tool currentlySelectedTool;
    private SelectionTool selectionTool;
    private PenTool penTool;
    private CircleTool circleTool;
    private ParallelogramTool parallelogramTool;
    private PolygonTool polygonTool;
    private TravelPathTool travelPathTool;
    private IsoscelesTriangleTool isoscelesTriangleTool;

    //=============================================================================================
    //UI components
    //=============================================================================================

    @FXML private AnchorPane workspaceContainer;
    @FXML private ToolBar toolBar;
    @FXML private ToggleButton selectionToolToggleButton;
    @FXML private ScrollPane dummyWorkspace;
    @FXML private Pane workspacePane;//TODO this is inside dummy workspace, it will soon go away
    @FXML private TabPane compositionTabs;

    private Workspace workspace;

    public void init(File fileToOpen){
        Logger.log("Initializing Main window controller");
        workspaceContainer.getChildren().remove(dummyWorkspace);
        workspace=new Workspace(this);
        ZoomableScrollPane zoomableScrollPane = workspace.getZoomableScrollPane();
        workspaceContainer.getChildren().add(zoomableScrollPane);
        AnchorPane.setTopAnchor(zoomableScrollPane,0d);
        AnchorPane.setRightAnchor(zoomableScrollPane,0d);
        AnchorPane.setBottomAnchor(zoomableScrollPane,0d);
        AnchorPane.setLeftAnchor(zoomableScrollPane, toolBar.getWidth());

        workspace.initializeSystem(fileToOpen,compositionTabs );
        initializeTools();
    }

    private void initializeTools(){

        selectionTool =new SelectionTool(workspace);
        circleTool=new CircleTool(workspacePane);
        parallelogramTool=new ParallelogramTool(workspacePane);
        penTool=new PenTool(workspacePane);
        polygonTool=new PolygonTool(workspace);
        travelPathTool=new TravelPathTool(workspacePane);
        isoscelesTriangleTool=new IsoscelesTriangleTool(workspacePane);
        setCurrentlySelectedTool(ToolType.SELECTION);

        //experiments

        //Isosceles Triangle view
//        IsoscelesTriangleView isoscelesTriangleView=new IsoscelesTriangleView(100,30,250,300,0,1);
//        workspacePane.getChildren().add(isoscelesTriangleView);
//        IsoscelesTriangleGizmo isoscelesTriangleGizmo=new IsoscelesTriangleGizmo(workspacePane,isoscelesTriangleView);

        //polygon view
//        ArrayList<Point2D> points=new ArrayList<Point2D>();
//        points.add(new Point2D(87,44));
//        points.add(new Point2D(132,81));
//        points.add(new Point2D(112,181));
//        points.add(new Point2D(312,281));
//
//
//        PolygonView polygonView=new PolygonView(points,240,134,0,1);
//        workspacePane.getChildren().add(polygonView);
//        PolygonGizmo polygonGizmo=new PolygonGizmo(workspacePane,polygonView);

        //parallelogram view
//        ParallelogramView parallelogramView=new ParallelogramView(200,100,45,1,0,300,250,0.5,0.5);
//        workspacePane.getChildren().addAll(parallelogramView);
//        ParallelogramGizmo parallelogramGizmo=new ParallelogramGizmo(workspacePane,parallelogramView);

        //circle
//        CircleView circleView=new CircleView(1,0,300,250,0.5,0.5,
//                40,70,45,315);
//        workspacePane.getChildren().add(circleView.getPath());
//        CircleGizmo circleGizmo =new CircleGizmo(workspacePane,circleView);
//        ViewUtil.showPoint(workspacePane,circleGizmo.getCircleView().getTranslationX(), circleGizmo.getCircleView().getTranslationY());
    }


    @FXML
    public void workspaceMousePressed(MouseEvent mouseEvent){
        if(currentlySelectedTool !=null){
            currentlySelectedTool.mousePressed(mouseEvent);
        }
    }

    @FXML
    public void workspaceMouseDragged(MouseEvent mouseEvent){
        if(currentlySelectedTool !=null){
            currentlySelectedTool.mouseDragged(mouseEvent);
        }
    }
    @FXML
    public void workspaceMouseReleased(MouseEvent mouseEvent){
        if(currentlySelectedTool !=null){
            currentlySelectedTool.mouseReleased(mouseEvent);//tools will add commands internally
        }
    }

    @Override
    public void workspaceDidZoom(float oldZoom,float newZoom){
        //just a notification ,handle if needed
    }

    @FXML
    private void selectionToolClicked(ActionEvent actionEvent){
        setCurrentlySelectedTool(ToolType.SELECTION);
        selectionToolToggleButton.setSelected(true);
    }

    @FXML
    private void circleToolClicked(ActionEvent actionEvent){
        setCurrentlySelectedTool(ToolType.CIRCLE);
    }

    @FXML
    private void parallelogramToolClicked(ActionEvent actionEvent){
        setCurrentlySelectedTool(ToolType.PARALLELOGRAM);
    }

    @FXML
    private void penToolClicked(ActionEvent actionEvent){
        setCurrentlySelectedTool(ToolType.PEN);
    }

    @FXML
    private void polygonToolClicked(ActionEvent actionEvent){
        setCurrentlySelectedTool(ToolType.POLYGON);
    }

    @FXML
    private void travelPathToolClicked(ActionEvent actionEvent){
        setCurrentlySelectedTool(ToolType.TRAVEL_PATH);
    }

    @FXML
    private void undo(ActionEvent event) {
        workspace.undo();
    }

    @FXML
    private void redo(ActionEvent event) {
        workspace.redo();
    }


    @FXML
    private void newFile(ActionEvent event) {
        Logger.log("New file");

        //open new instance of the application
        Platform.runLater(new Runnable() {
            public void run() {
                try {
                    new Main().start(new Stage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @FXML
    private void open(ActionEvent event) {
        Logger.log("Opening document");
        FileChooser fileChooser=new FileChooser();
        fileChooser.setTitle(OPEN_FILE);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Timeshift XML","*.xml"));
        File file = fileChooser.showOpenDialog(workspaceContainer.getScene().getWindow());
        Logger.log("File to open is "+file);

        if(workspace.isEmptyDocument()){
            //open in the same instance of the application
            workspace.initializeSystem(file,compositionTabs );
        }else{
            //open new instance of the application running in a parallel thread
            Platform.runLater(new Runnable() {
                public void run() {
                    try {
                        new Main(file).start(new Stage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @FXML
    private void save(ActionEvent event) {
        Logger.log("Saving document ");
        if(workspace.getFile()==null){
            saveAsNewFile();
        }else{
            workspace.save();
        }
    }

    @FXML
    private void saveAs(ActionEvent event) {
        Logger.log("Saving document as ...");
        saveAsNewFile();
    }

    private boolean saveAsNewFile(){
        //open the file chooser pop up
        FileChooser fileChooser=new FileChooser();
        fileChooser.setTitle(SAVE_FILE_AS);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Timeshift XML","*.xml"));
        Window window = workspaceContainer.getScene().getWindow();
        File file = fileChooser.showSaveDialog(window);
        if(file!=null){
            Logger.log("File chosen is "+file);
            workspace.saveAs(file);
            return true;
        }else{
            Logger.log("no file chosen ");
            return false;
        }

    }

    @FXML
    private void close(ActionEvent event){

        //TODO move this to the close handle.This doesn't get called on quitting javaFX application
        if (workspace.isFileModified()) {
            boolean saveChanges = ConfirmBox.display("File Modified", "The file has been modified. Do you want to save changes?");
            if(saveChanges){
                if(workspace.getFile()==null){
                    if(saveAsNewFile()){
                        ((Stage)workspaceContainer.getScene().getWindow()).close();
                    }
                }else{
                    workspace.save();
                    ((Stage)workspaceContainer.getScene().getWindow()).close();
                }
            }else{
                ((Stage)workspaceContainer.getScene().getWindow()).close();
            }
        }else{
            ((Stage)workspaceContainer.getScene().getWindow()).close();
        }
    }

    @FXML
    private void delete(ActionEvent actionEvent){
        workspace.getSelectedItems().deleteSelection();
    }

    @FXML
    private void cut(ActionEvent actionEvent){
        workspace.getSelectedItems().cutToClipboard();
    }

    @FXML
    private void copy(ActionEvent actionEvent){
        workspace.getSelectedItems().copyToClipboard();
    }

    @FXML
    private void paste(ActionEvent actionEvent){
        workspace.pasteFromClipboard();
    }

    public void setCurrentlySelectedTool(ToolType toolType){
        Tool lastTool=currentlySelectedTool;
        Tool newTool =getToolFrom(toolType);

        //prevent NPE in case this is the first attempt for setting tool
        if (lastTool!=null) {
            lastTool.toolDismissed(newTool);
        }
        newTool.toolAppointed(lastTool);
        currentlySelectedTool=newTool;

        Logger.log("Selected toolType is " + currentlySelectedTool.getToolType());

        //cursors can be handled individually within the tools themselves too,in their constructors
        if(toolType==ToolType.SELECTION){
            workspacePane.setCursor(Cursor.DEFAULT);
        }else{
            workspacePane.setCursor(Cursor.CROSSHAIR);
        }
    }

    private Tool getToolFrom(ToolType toolType){

        switch (toolType){
            case SELECTION:
                return selectionTool;
            case CIRCLE:
                return circleTool;
            case PARALLELOGRAM:
                return parallelogramTool;
            case PEN:
                return penTool;
            case POLYGON:
                return polygonTool;
            case TRAVEL_PATH:
                return travelPathTool;
            case ISOSCELES_TRIANGLE:
                return isoscelesTriangleTool;
            default:
                return selectionTool;
        }
    }

    @FXML
    private void newComposition(ActionEvent actionEvent){
        Logger.log("New composition to be created");
        //create new composition
        Tab tab = workspace.makeNewComposition().getTab();
        compositionTabs.getTabs().add(tab);
    }
    @FXML
    private void deleteComposition(ActionEvent actionEvent){
        boolean wasDeleted = workspace.deleteCurrentComposition();
        if(!wasDeleted){
            AlertBox.display("Last composition","At least one composition must exist");
        }
    }
}
