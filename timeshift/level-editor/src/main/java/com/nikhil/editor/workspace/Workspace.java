package com.nikhil.editor.workspace;

import com.nikhil.command.*;
import com.nikhil.command.composite.CompositeCommand;
import com.nikhil.command.item.AddItemSet;
import com.nikhil.command.keyframe.ActionOnKeyframes;
import com.nikhil.command.keyframe.DeleteKeyframes;
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
import javafx.scene.Group;
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

    private RootController rootController=new RootController();

    //its very important that the composition linked list and the composition tabs have the same order
    //because they have a one to one relationship
    private List<CompositionViewController> compositionViewControllers=new LinkedList<CompositionViewController>();
    private TabPane compositionTabs;
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
    public static final int CONTAINER_WIDTH = 5000;
    public static final int CONTAINER_HEIGHT = 7000;

    /**
     * creates a new workspace
     * @param workspaceListener the delegate that will listen to certain mouse events
     */
    public Workspace(WorkspaceListener workspaceListener,TabPane compositionTabs) {

        this.compositionTabs =compositionTabs;
        selectedItems=new SelectedItems(this);
        selectedItems.initView();
        selectedItems.updateView();

        //initialize the workspace container
        initializeGraphics(workspaceListener);
        containerPane.getChildren().add(selectedItems);

        //remove any tab(there is a dummy tab in the beginning)
        compositionTabs.getTabs().clear();
        //make a new tab and add this tab to the list of tabs
        currentComposition = createNewComposition();
        addComposition(currentComposition,0);
    }

    /**
     * initializes the workspace for the specified file to load(possibly null).
     * This method should be called only after the worksheetPane has been added to the stage.
     * @param fileToOpen the file that needs to be loaded(this can be null too for an empty document)
     */
    public void initializeSystem(File fileToOpen) {
        file=fileToOpen;

        //reset any controllers in the list if they may exist
        compositionViewControllers.clear();
        if(file!=null){
            try {
                open(file, compositionTabs);
            } catch (Exception e) {
                Logger.log(e);
                AlertBox.display("Error", "There were some problems opening the file.");
            }
        }
    }

    private void initializeGraphics(WorkspaceListener workspaceListener){
        containerPane =new Pane();

        //add events to the container
        containerPane.addEventHandler(MouseEvent.MOUSE_PRESSED,(e)->{
            selectedItems.clearSelection();
            workspaceListener.workspaceMousePressed(e);
        });
        containerPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, workspaceListener::workspaceMouseDragged);
        containerPane.addEventHandler(MouseEvent.MOUSE_RELEASED, workspaceListener::workspaceMouseReleased);

        zoomableScrollPane=new ZoomableScrollPane(containerPane);
        zoomableScrollPane.addEventFilter(ScrollEvent.SCROLL, (e) -> {
            if (e.isAltDown()) {
                zoomableScrollPane.handle(e);//could also go in this class
            }
        });

        //sets the dimension ,position and style of the views
        zoomableScrollPane.setStyle("-fx-background:#111111");
        zoomableScrollPane.setPrefWidth(CONTAINER_WIDTH);
        zoomableScrollPane.setPrefHeight(CONTAINER_HEIGHT);
        zoomableScrollPane.setHvalue(0.5);
        zoomableScrollPane.setVvalue(0.5);
        Logger.log("zoomable scorllpane width" + zoomableScrollPane.getWidth());

        containerPane.setPrefSize(CONTAINER_WIDTH, CONTAINER_HEIGHT);
        containerPane.setLayoutX(0);
        containerPane.setLayoutY(0);
        containerPane.setStyle("-fx-background-color:#444444");
    }

    //=============================================================================================
    //Composition management
    //=============================================================================================

    private CompositionViewController createNewComposition(){
        CompositionController compositionController=new CompositionController();
        CompositionViewController newComposition=new CompositionViewController(compositionController,this,
                CompositionViewController.getNewTabName());
        return newComposition;
    }

    public void addComposition(CompositionViewController composition, int index) {
        rootController.addCompositionController(composition.getCompositionController());

        //because of the one to one relationship,
        //its very important that the composition linked list and the composition tabs have the same order
        compositionViewControllers.add(index,composition);
        compositionTabs.getTabs().add(index, composition.getTab());
    }

    public void addNewComposition(){
        CompositionViewController newComposition = createNewComposition();
        AddRemoveComposition addComposition=new AddRemoveComposition(newComposition,
                compositionTabs.getTabs().size(),
                true);
        pushCommand(addComposition);
    }

    public boolean removeCurrentComposition(){
        if(compositionViewControllers.size()>1) { //stop at the last composition

            //push a command to remove composition
            int index = compositionTabs.getTabs().indexOf(currentComposition.getTab());
            AddRemoveComposition removeComposition = new AddRemoveComposition(currentComposition, index, false);
            pushCommand(removeComposition);

            //select the previous tab if possible
            if (index > 1) {
                CompositionViewController compositionToSelect = compositionViewControllers.get(index-1);
                compositionTabs.getSelectionModel().select(compositionToSelect.getTab());
            }
            return true;
        }
        return false;
    }

    public boolean removeComposition(CompositionViewController composition){

        if (compositionViewControllers.remove(composition)) {

            //remove the model composition controller from root
            rootController.removeCompositionController(composition.getCompositionController());

            //remove tab of the composition from the tab pane
            compositionTabs.getTabs().remove(composition.getTab());

            return true;//indicate composition removed
        }
        return false;// to indicate that the composition was not removed
    }

    /**
     * Selects the next or previous tab relative to the current composition if possible
     * @param dIndex the amount by which the next or previous tab needs to be selected.
     *               Example: 1 for next tab, -1 for previous tab
     * @return true if it shifted to a new composition,false otherwise
     */
    public boolean selectComposition(int dIndex){

        //find the new index
        int currentIndex=compositionTabs.getTabs().indexOf(currentComposition.getTab());
        int newIndex=currentIndex+dIndex;

        //if it is within bounds, select that tab
        if(newIndex>=0&&newIndex<compositionTabs.getTabs().size()){

            //set the current composition as the composition at the new index
            CompositionViewController compositionToSelect = compositionViewControllers.get(newIndex);
            compositionTabs.getSelectionModel().select(compositionToSelect.getTab());
            return true;
        }
        return false;
    }

    public int pasteFromClipboard(){
        int totalItemsPasted=0;
        //paste the new items at an offset if they are being duplicated
        double offsetX=0,offsetY=0;
        if(!Clipboard.getSharedInstance().isCutFromOriginalSource()){
            offsetX=PASTE_OFFSET_X;
            offsetY=PASTE_OFFSET_Y;
        }
        Set<ItemViewController> itemsToPaste= Clipboard.getSharedInstance().getDeepCopyOfItems(offsetX, offsetY);
        if(itemsToPaste!=null){
            totalItemsPasted=itemsToPaste.size();
            AddItemSet addItemSet =new AddItemSet(itemsToPaste, currentComposition);
            pushCommand(addItemSet);
        }
        return totalItemsPasted;
    }

    public void removeSelectedKeyframes(){
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

    /**
     * @return tab pane for the compositions
     */
    public TabPane getCompositionTabs() {
        return compositionTabs;
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

    public SelectedItems getSelectedItems() {
        return selectedItems;
    }

    public CompositionViewController getCurrentComposition() {
        return currentComposition;
    }

    public void setCurrentComposition(CompositionViewController currentComposition) {

        Logger.log("setting current composition Called ");
        //remove the old worksheet from the container
        containerPane.getChildren().remove(this.currentComposition.getWorksheet());
        containerPane.getChildren().remove(this.currentComposition.getOutlineGroup());
        this.currentComposition = currentComposition;

        //add the worksheet pane of the current tab as the only child of the container in the middle
        Pane worksheet=currentComposition.getWorksheet();
        Group outlineGroup = currentComposition.getOutlineGroup();
        double width=worksheet.getPrefWidth();
        double height=worksheet.getPrefWidth();
        worksheet.setLayoutX(CONTAINER_WIDTH / 2 - (width / 2));
        worksheet.setLayoutY(CONTAINER_HEIGHT / 2 - (height / 2));
        outlineGroup.setLayoutX(CONTAINER_WIDTH / 2 - (width / 2));
        outlineGroup.setLayoutY(CONTAINER_HEIGHT / 2 - (height / 2));
        containerPane.getChildren().add(0,worksheet);//make sure to add with lowest z-index
        containerPane.getChildren().add(1,outlineGroup);//outline should be just above the worksheet

        //clear the selection as we don't want anything from the other composition to be acted on
        selectedItems.clearSelection();

        Logger.log(currentComposition.getTab().getText()+" is now selected");
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
        return currentComposition.getWorksheet().parentToLocal(containerX,containerY);
    }

    public Point2D toContainerPoint(double worksheetX,double worksheetY){
        return currentComposition.getWorksheet().localToParent(worksheetX,worksheetY);
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
