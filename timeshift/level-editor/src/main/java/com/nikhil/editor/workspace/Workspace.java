package com.nikhil.editor.workspace;

import com.nikhil.command.*;
import com.nikhil.controller.CompositionController;
import com.nikhil.controller.CompositionViewController;
import com.nikhil.controller.ItemViewController;
import com.nikhil.controller.RootController;
import com.nikhil.editor.XMLLoader;
import com.nikhil.editor.selection.Clipboard;
import com.nikhil.editor.selection.SelectedItems;
import com.nikhil.logging.Logger;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.custom.keyframe.KeyframeTreeView;
import com.nikhil.view.custom.keyframe.KeyframeView;
import com.nikhil.view.util.AlertBox;
import com.nikhil.view.zoom.ZoomableScrollPane;
import com.nikhil.xml.XMLWriter;
import javafx.geometry.Point2D;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by NikhilVerma on 30/08/15.
 */
public class Workspace  {

    public static final String UNTITLED = "Untitled";
    public static final double PASTE_OFFSET_X=60;
    public static final double PASTE_OFFSET_Y=60;

    private RootController rootController;
    private List<CompositionViewController> compositionViewControllers=new LinkedList<CompositionViewController>();
    private CompositionViewController currentComposition;


    private Stack<Command> commandStack=new Stack<Command>();
    private Stack<Command> undoStack =new Stack<Command>();
    private boolean fileModified =false;
    private File file;

    /**currently selected items set*/
    private SelectedItems selectedItems;

    //=============================================================================================
    //UI components
    //=============================================================================================

    private ZoomableScrollPane zoomableScrollPane;
    private Pane containerPane;
    private Pane worksheetPane;

    /**
     * creates a new workspace
     * @param workspaceListener the delegate that will listen to certain mouse events
     */
    public Workspace(final WorkspaceListener workspaceListener) {
        //instantiate the workspace pane and the worksheet pane
        initializeGraphics(workspaceListener);
        selectedItems=new SelectedItems(this);
        selectedItems.initializeView();
        selectedItems.updateView();
        containerPane.getChildren().add(selectedItems);
    }

    /**
     * initializes the workspace for the specified file to load(possibly null).
     * This method should be called only after the worksheetPane has been added to the stage.
     * @param fileToOpen the file that needs to be loaded(this can be null too for an empty document)
     * @param compositionTabs
     */
    public void initializeSystem(File fileToOpen, TabPane compositionTabs) {
        file=fileToOpen;

        //reset any controllers in the list if they may exist
        compositionViewControllers.clear();
//        itemViewControllers.clear();
        if(file==null){
            rootController=new RootController();

            currentComposition = makeNewComposition();
            //add its tab after removing dummy
            compositionTabs.getTabs().clear();
            compositionTabs.getTabs().add(currentComposition.getTab());
        }else{
            try {
                open(file, compositionTabs);
            } catch (Exception e) {
                Logger.log(e);
                AlertBox.display("Error", "There were some problems opening the file.");
            }
        }
    }

    private void initializeGraphics(final WorkspaceListener workspaceListener){
        containerPane =new Pane();

        worksheetPane=new Pane();
        containerPane.getChildren().add(worksheetPane);

        //add events to the container
        containerPane.addEventHandler(MouseEvent.MOUSE_PRESSED,(e)->{
            selectedItems.clearSelection();
            workspaceListener.workspaceMousePressed(e);
        });
        containerPane.addEventHandler(MouseEvent.MOUSE_DRAGGED,(e)->{
            workspaceListener.workspaceMouseDragged(e);
        });
        containerPane.addEventHandler(MouseEvent.MOUSE_RELEASED,(e)->{
            workspaceListener.workspaceMouseReleased(e);
        });

        zoomableScrollPane=new ZoomableScrollPane(containerPane);
        zoomableScrollPane.addEventFilter(ScrollEvent.SCROLL, (e) -> {
            if (e.isAltDown()) {
                zoomableScrollPane.handle(e);//could also go in this class
            }
        });

        configureViews();
    }

    /** sets the dimension ,position and style of the views */
    private void configureViews() {
        //TODO remove hardcoding, make it dynamic to screen size
        int prefWidth = 850;
        int prefHeight = 550;
//        zoomableScrollPane.setPrefSize(prefWidth,prefHeight);
        zoomableScrollPane.setStyle("-fx-background:#111111");
        zoomableScrollPane.setPrefWidth(prefWidth);
        zoomableScrollPane.setPrefHeight(prefHeight);
        Logger.log("zoomable scorllpane width" + zoomableScrollPane.getWidth());

        containerPane.setPrefSize(prefWidth,prefHeight);
//        containerPane.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
//        containerPane.setMaxSize(Control.USE_COMPUTED_SIZE,Control.USE_COMPUTED_SIZE);
        containerPane.setLayoutX(0);
        containerPane.setLayoutY(0);
        containerPane.setStyle("-fx-background-color:#444444");


        double worksheetPrefWidth=566;
        double worksheetPrefHeight=330;
        worksheetPane.setPrefWidth(worksheetPrefWidth);
        worksheetPane.setPrefHeight(worksheetPrefHeight);
        worksheetPane.setLayoutX(prefWidth/2-(worksheetPrefWidth/2));
        worksheetPane.setLayoutY(prefHeight/2-(worksheetPrefHeight/2));
        worksheetPane.setStyle("-fx-background-color:#EEEEEE");
    }

    //=============================================================================================
    //Items management
    //=============================================================================================

    public boolean deleteCurrentComposition(){//TODO still need to work on removing from timeline

        if(compositionViewControllers.size()>1){ //stop at the last composition
            int index = compositionViewControllers.indexOf(currentComposition);
            compositionViewControllers.remove(currentComposition);
            removeFromTimelineSystem(currentComposition.getCompositionController());

            //remove tab of the current composition from the tab pane
            TabPane compositionTabs = currentComposition.getTab().getTabPane();
            compositionTabs.getTabs().remove(currentComposition.getTab());

            //select the previous tab if possible
            if(index>1){
                currentComposition=compositionViewControllers.get(index-1);
            }else{ //select the tab next to this tab(which already has the same index)
                currentComposition=compositionViewControllers.get(index);
            }
            compositionTabs.getSelectionModel().select(currentComposition.getTab());

            return true;//indicate composition removed
        }
        return false;// to indicate that the last composition will not be removed
    }

    public CompositionViewController makeNewComposition(){
        CompositionController compositionController=new CompositionController();
        CompositionViewController newComposition=new CompositionViewController(compositionController,this);
        addComposition(newComposition);
        return newComposition;
    }

    public void addComposition(CompositionViewController compositionViewController){
        rootController.addCompositionController(compositionViewController.getCompositionController());
        compositionViewControllers.add(compositionViewController);
    }

    public boolean removeFromTimelineSystem(CompositionController compositionController){
        return rootController.removeCompositionController(compositionController);
    }

    public int pasteFromClipboard(){
        int totalItemsPasted=0;
        //paste the new items at an offset if they are being duplicated
        double offsetX=0,offsetY=0;
        if(!Clipboard.getSharedInstance().isCutFromOriginalSource()){
            offsetX=PASTE_OFFSET_X;
            offsetY=PASTE_OFFSET_Y;
        }
        Set<ItemViewController> itemsToPaste= Clipboard.getSharedInstance().getDeepCopyOfItems(offsetX,offsetY);
        if(itemsToPaste!=null){
            totalItemsPasted=itemsToPaste.size();
            AddItemSet addItemSet =new AddItemSet(itemsToPaste, currentComposition);
            pushCommand(addItemSet);
        }
        return totalItemsPasted;
    }

    public void deleteSelectedKeyframes(){
        pushCommand(new DeleteKeyframes(getReusableListOfKeyframesIfPossible()));
    }

    /**
     * If the top command is also a collective action on keyframes, and has
     * the same keyframes list, it returns that list, else creates a new lise of
     * selected keyframes from the <b>current</b> composition
     * @return Linked list of selected keyframes which might be recycled from
     * older commands
     */
    public LinkedList<KeyframeView> getReusableListOfKeyframesIfPossible(){

        KeyframeTreeView keyframeTable = currentComposition.getKeyframeTable();

        //check if the top command is also a collection action on keyframes
        Command topCommand = peekCommandStack();
        if((topCommand != null) && (topCommand instanceof ActionOnKeyframes)){
            ActionOnKeyframes actionOnKeyframes=(ActionOnKeyframes)topCommand;

            //reuse the same collection if top command is of the same type and contains same keyframes
            if(actionOnKeyframes.containsSameKeyframesAsCurrentlySelected(keyframeTable)){
                //return the same list for reuse
                return actionOnKeyframes.getKeyframeViews();
            }else{
                //build a new list of selected keyframes
                return keyframeTable.getSelectedKeys();
            }
        }else{
            //build a new list of selected keyframes
            return keyframeTable.getSelectedKeys();
        }
    }

    //=============================================================================================
    //File I/O
    //=============================================================================================

    public File getFile() {
        return file;
    }

    public boolean isFileModified() {
        return fileModified;
    }

    public void save(){
        saveAs(file);//save the same file again
    }

    public void saveAs(File newFile){
        String xml= null;
        try {
            XMLWriter xmlWriter=new XMLWriter();
            xmlWriter.writeToFile(newFile,rootController);

        } catch (Exception e) {
            Logger.log(e);
            AlertBox.display("Error","Exception on file save");
        }
        file=newFile;
        fileModified=true;//<<--- the next method depends on the flag values to be different
        fileModified(false);//<--here we force the title to be changed to the new file
    }

    private void open(File file, TabPane compositionTabs) throws ParserConfigurationException, IOException, SAXException {
        XMLLoader xmlLoader=new XMLLoader(this,compositionTabs);
        rootController=xmlLoader.loadFile(file);
        ((Stage) zoomableScrollPane.getScene().getWindow()).setTitle(getWorkspaceTitle());
    }

    private void fileModified(boolean justModifiedNow) {
        if(fileModified==justModifiedNow){//nothing more to do,don't bother operating further
            return;
        }

        //add a star to the title of the window
        String title = getWorkspaceTitle() ;
        if(justModifiedNow){
            title+="*";
        }
        ((Stage) zoomableScrollPane.getScene().getWindow()).setTitle(title);
        fileModified = justModifiedNow;
    }

    public boolean isEmptyDocument(){
        return !fileModified&&file==null;
    }

    public String getWorkspaceTitle() {
        return file==null? UNTITLED :file.toString();
    }

    public ZoomableScrollPane getZoomableScrollPane() {
        return zoomableScrollPane;
    }

    public Pane getContainerPane() {
        return containerPane;
    }

    public Pane getWorksheetPane() {
        return worksheetPane;
    }

    public SelectedItems getSelectedItems() {
        return selectedItems;
    }

    public CompositionViewController getCurrentComposition() {
        return currentComposition;
    }

    public void setCurrentComposition(CompositionViewController currentComposition) {
        this.currentComposition = currentComposition;
    }

    public List<CompositionViewController> getCompositionViewControllers() {
        return compositionViewControllers;
    }

    //=============================================================================================
    //Command Stack
    //=============================================================================================


    /**
     * Pushes a command on top of the command stack,
     * executes it,
     * and clears the undo stack
     * @param command the latest command that needs to be placed
     *                on top of the command stack.
     */
    public void pushCommand(Command command) {
        pushCommand(command,true);
    }

    /**
     * Pushes a command on top of the command stack,
     * and clears the undo stack
     * @param command the latest command that needs to be placed
     *                on top of the command stack.
     * @param executeBeforePushing should this command be executed before being pushed on the command stack
     */
    public void pushCommand(Command command,boolean executeBeforePushing) {
        if(command!=null){
            if(executeBeforePushing){
                command.execute();
                command.executedByWorkspace(this);
            }
            commandStack.push(command);
            Logger.log(command.getClass().getSimpleName()+" Pushed");
            undoStack.removeAllElements();
            fileModified(true);
        }
    }

    /**
     * Pushes a composite command on the command stack where only selected commands are executed before pushing
     * @param compositeCommand composite command that will be pushed on the command stack. This "whole" command
     *                         will not be executed.
     * @param selectiveExecutionBeforePushing a map that tells which of the commands from the composite command
     *                                        will be executed before pushing the "whole" composite command.
     */
    public void pushCommand(CompositeCommand compositeCommand,Map<? extends Command,Boolean> selectiveExecutionBeforePushing){
        if(compositeCommand!=null){

            //execute only selective commands as governed by the map
            List<? extends Command> commands=compositeCommand.getCommands();
            for(Command command: commands){
                if(selectiveExecutionBeforePushing.get(command)){
                    command.execute();
                    //no execute by workspace method will be called here,because its meant for the composite command
                }
            }
            //we don't execute the composite command, we just push it on the command stack
            commandStack.push(compositeCommand);
            Logger.log(compositeCommand.getClass().getSimpleName()+" Pushed");
            undoStack.removeAllElements();
            fileModified(true);
        }
    }

    /**
     * Pops from the command stack,Unexecutes the command,
     * and pushes that command on the undo stack
     */
    public void undo() {
        try {
            Command command=commandStack.pop();
            Logger.log("Undoing "+command.getClass().getSimpleName());
            command.unexecute();
            command.unexecutedByWorkspace(this);
            undoStack.push(command);

        } catch (EmptyStackException e) {
            //do nothing if stack is empty
            Logger.log("command stack is empty");
        }
    }

    /**
     * Pops from the undo stack,executes the command,
     * and pushes that command on the command stack
     */
    public void redo() {
        try {
            Command command= undoStack.pop();
            Logger.log("Redoing "+command.getClass().getSimpleName());
            command.execute();
            command.executedByWorkspace(this);
            commandStack.push(command);
        } catch (EmptyStackException e) {
            //do nothing if stack is empty
            Logger.log("undo stack is empty");
        }

    }

    /**
     * Gives the top of the command stack without popping anything from it
     * @return the top (which might be null,if stack is empty)
     */
    public Command peekCommandStack(){
        Command topCommand = null;
        try {
            topCommand = commandStack.peek();
        } catch (EmptyStackException e) {
            topCommand=null;
        }
        return topCommand;
    }

    public Point2D toWorksheetPoint(double containerX,double containerY){
        return worksheetPane.parentToLocal(containerX,containerY);
    }

    public Point2D toContainerPoint(double worksheetX,double worksheetY){
        return worksheetPane.localToParent(worksheetX,worksheetY);
    }

    //=============================================================================================
    //Work point - Play point translation
    //=============================================================================================

    //TODO conversion methods from work point to play point and vice versa to be revised

    /**
     * gets the work point x
     * @param x x in play points
     * @return x converted in work points
     */
    public double workPointX(double x){
        return x;
    }

    /**
     * gets the work point y
     * @param y y in play points
     * @return y converted in work points
     */
    public double workPointY(double y){
        return y;
    }

    /**
     * gets the work point using the play point
     * @param playPoint play point model
     * @return point which is converted to work point from play point
     */
    public UtilPoint workPoint(UtilPoint playPoint){
        return playPoint;
    }

    /**
     * gets the work point using the play point
     * @param playPoint play point Point2D from javafx
     * @return point which is converted to work point from play point
     */
    public UtilPoint workPoint(javafx.geometry.Point2D playPoint){
        return new UtilPoint(playPoint.getX(),playPoint.getY());
    }

    /**
     * gets the play point x
     * @param x x in work points
     * @return x converted in play points
     */
    public double playPointX(double x){
        return x;
    }

    /**
     * gets the play point y
     * @param y y in work points
     * @return y converted in play points
     */
    public double playPointY(double y){
        return y;
    }

    /**
     * gets the play point using the work point
     * @param workPoint play point model
     * @return point which is converted to play point from work point
     */
    public UtilPoint playPoint(UtilPoint workPoint){
        return workPoint;
    }

    /**
     * gets the play point using the work point
     * @param workPoint work point Point2D from javafx
     * @return point which is converted to play point from work point
     */
    public UtilPoint playPoint(javafx.geometry.Point2D workPoint){
        return new UtilPoint(workPoint.getX(),workPoint.getY());
    }
}
