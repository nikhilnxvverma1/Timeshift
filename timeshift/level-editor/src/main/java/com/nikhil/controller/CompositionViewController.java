package com.nikhil.controller;

import com.nikhil.Main;
import com.nikhil.controller.item.ItemModelController;
import com.nikhil.editor.workspace.Workspace;
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
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.eclipse.fx.FilterableTreeItem;
import org.eclipse.fx.TreeItemPredicate;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by NikhilVerma on 26/09/15.
 */
public class CompositionViewController {

    private static final double NON_SOLO_ITEM_VISIBILITY_OPACITY=0.5;
    private static final double FULL_OPACITY=1;

    public static final double DEFAULT_DURATION =30;
    public static final double PLAYBACK_FEATURES_HEIGHT = 40d;
    public static final int DEFAULT_WIDTH = 720;
    public static final int DEFAULT_HEIGHT = 480;
    public static int TOTAL_COMPOSITIONS_SO_FAR =0;
    private static final double NAME_COLUMN_WIDTH=175;
    private static final double VALUE_COLUMN_WIDTH=125;
    private static final double OPTION_COLUMN_WIDTH=100;
    public static final double NEGLIGIBLE_TIME_DIFFERENCE=0.1;

    private CompositionController compositionController;
    private double duration= DEFAULT_DURATION;
    private int totalSoloItems=0;
    private Tab tab;
    private Workspace workspace;
    private List<ItemViewController> itemViewControllers = new LinkedList<ItemViewController>();
    private FilterableTreeItem<Metadata> rootTreeItem;
    private Pane worksheet;
    private Group outlineGroup;
    private TreeTableView<Metadata> itemTable;
    private KeyframeTreeView keyframeTable;
    private Playback playback;
    private TextField filterField=new TextField();

    public CompositionViewController(CompositionController compositionController,Workspace workspace,
                                     String name,double width,double height){
        this.compositionController=compositionController;
        this.workspace=workspace;
        this.tab=new Tab();

        this.worksheet=new Pane();
        this.worksheet.setStyle("-fx-background-color:#EEEEEE");
        this.worksheet.setPrefSize(width,height);
        this.outlineGroup =new Group();
//        this.outlineGroup.setPrefSize(width, height);

        this.tab.setText(name);
        this.tab.setOnSelectionChanged(e->{
            if(tab.isSelected()){
                workspace.setCurrentComposition(this);
            }
        });
        initView();
    }

    public CompositionViewController(CompositionController compositionController,Workspace workspace,String name) {
        this(compositionController,workspace,name, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public static String getNewTabName() {
        return "Comp " + ++TOTAL_COMPOSITIONS_SO_FAR;//TODO store the composition names in the model itself
    }

    public CompositionController getCompositionController() {
        return compositionController;
    }

    public Tab getTab() {
        return tab;
    }

    /**
     * Worksheet is where all the model graphics go.
     * @return worksheet pane
     */
    public Pane getWorksheet() {
        return worksheet;
    }

    /**
     * Outline group is a layer used for overlaying any outlines specific to a model
     * on top of the regular worksheet. Such as motion paths or item gizmos
     * @return the outline group which will always be above the worksheet
     */
    public Group getOutlineGroup() {
        return outlineGroup;
    }

    public int getTotalSoloItems() {
        return totalSoloItems;
    }

    /**
     * Sets the count of total solo items. This method is specifically designed to be called
     * by the item view controller's solo setter
     * @param totalSoloItems the total solo items in this composition
     */
    public void setTotalSoloItems(int totalSoloItems) {
        if(totalSoloItems<0){
            throw new RuntimeException("Total solo items can never be set to a negative number");
        }
        this.totalSoloItems = totalSoloItems;
        if(totalSoloItems>0){
            makeNonSoloItemsVisible(false);
        }else{
            makeNonSoloItemsVisible(true);
        }
    }

    /**
     * Makes other item in the composition hidden or visible  based on their solo and visiblity flag.
     * @param visible weather non solo items should be visible or not
     */
    public void makeNonSoloItemsVisible(boolean visible){

        //iterate over all items in parent composition
        Iterator<ItemViewController> itemViewControllerIterator = this.getItemViewControllerIterator();
        while (itemViewControllerIterator.hasNext()){

            //make the view hidden without toggling the visibility flag
            ItemViewController itemViewController = itemViewControllerIterator.next();
            if(!itemViewController.isSolo()){

                //do this only for visible and non-solo items
                if (itemViewController.isVisible()) {
                    itemViewController.getItemView().setVisible(visible);
                }

                //also set a style on their visible checkbox switch
                HeaderMetadata headerMetadata = itemViewController.getHeaderMetadata();

                if (!visible) {
                    headerMetadata.getVisibility().setOpacity(NON_SOLO_ITEM_VISIBILITY_OPACITY);
                    getWorkspace().getSelectedItems().removeFromSelection(itemViewController);
                }else{
                    headerMetadata.getVisibility().setOpacity(FULL_OPACITY);
                }
            }else{

                //make the solo item visible (if its allowed) and reinstate the checkbox
                if (itemViewController.isVisible()) {
                    itemViewController.getItemView().setVisible(true);
                }
                itemViewController.getHeaderMetadata().getVisibility().setOpacity(FULL_OPACITY);
            }
        }
    }

    public void addItemViewController(ItemViewController itemViewController){
        addItemViewController(itemViewController, false);
    }

    public void addItemViewController(ItemViewController itemViewController,boolean alreadyAddedToModelComposition){
        itemViewControllers.add(itemViewController);
        TreeItem<Metadata> metadataTree = itemViewController.getMetadataTree();
        rootTreeItem.getInternalChildren().add(metadataTree);
        worksheet.getChildren().add(itemViewController.getItemView());
        outlineGroup.getChildren().add(itemViewController.getGizmo());
//        itemViewController.addViewsTo(worksheet);

        //add to the timeline
        compositionController.getTimeline().add(itemViewController.getItemModel().changeNodeIterator());

        //add to the composition if needed
        if (!alreadyAddedToModelComposition) {
            ItemModelController itemModelController = itemViewController.getModelController();
            compositionController.addItemController(itemModelController);
        }
    }

    public boolean removeItemViewController(ItemViewController itemViewController){
        boolean removed = itemViewControllers.remove(itemViewController);
        if(removed){
            TreeItem<Metadata> metadataTree = itemViewController.getMetadataTree();
            rootTreeItem.getInternalChildren().remove(metadataTree);
            worksheet.getChildren().remove(itemViewController.getItemView());
            //remove from timeline
            compositionController.getTimeline().remove(itemViewController.getItemModel().changeNodeIterator());
            compositionController.removeItemController(itemViewController.getModelController());
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

        //thumb seeker and playback have a bi directional relationship
        ThumbSeeker thumbSeeker = new ThumbSeeker(RIGHT_COMPONENT_WIDTH);
        playback=new Playback(this,thumbSeeker);
        thumbSeeker.setDelegate(playback);
        thumbSeeker.setPrefHeight(PLAYBACK_FEATURES_HEIGHT);
        thumbSeeker.setMaxHeight(Control.USE_PREF_SIZE);

        //after initializing search and playback, we init root which will use the filter field
        HBox outerHBox = initSearchAndPlayback();
        outerHBox.setSpacing(5);
        initRoot();

        SelectionBar selectionBar=new SelectionBar(RIGHT_COMPONENT_WIDTH,null);
        Ruler ruler=new Ruler((int) duration, RIGHT_COMPONENT_WIDTH);

        itemTable = initItemTable(rootTreeItem);
        keyframeTable =new KeyframeTreeView(this, rootTreeItem);

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

    private void initRoot() {
        rootTreeItem=new FilterableTreeItem<>(new HeaderMetadata("Root", MetadataTag.ROOT, null, false));
        rootTreeItem.predicateProperty().bind(Bindings.createObjectBinding(() -> {
//            if (filterField.getText() == null || filterField.getText().isEmpty())
//                return null;
            return TreeItemPredicate.create(new Predicate<Metadata>() {
                @Override
                public boolean test(Metadata actor) {
                    return actor.getName().contains(filterField.getText());
                }
            });
        }, filterField.textProperty()));
    }

    private HBox initSearchAndPlayback() {
        DraggableTextValue timeDragger=playback.getTimeDragger();
        //filter field should already be initialized
        filterField.setPromptText("Search");
        Button gotoBeginning=new Button("|<");
        Button previousKeyframe=new Button("<|");
        previousKeyframe.setOnAction(event -> this.jumpTimeToPreviousKeyframe());
        ToggleButton playPause=new ToggleButton(">");
        playPause.setOnAction(e->{playback.togglePlayback(e,playPause);});
        Button nextKeyframe=new Button("|>");
        nextKeyframe.setOnAction(event -> this.jumpTimeToNextKeyframe());
        Button gotoEnd=new Button(">|");

        ToolBar playerControls=new ToolBar(gotoBeginning,previousKeyframe,playPause,nextKeyframe,gotoEnd);
        HBox outerHBox=new HBox(timeDragger, filterField,playerControls);
        outerHBox.setAlignment(Pos.CENTER_LEFT);
        outerHBox.setPadding(new Insets(0,2,0,2));
        return outerHBox;
    }

    private void jumpTimeToNextKeyframe(){
        //seek the current value from the value of the thumb
        double currentTime = getTime();//in seconds

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
            playback.seekTo(keyframeAfter.getTime());
            keyframeAfter.setSelected(true);
        }
    }

    private void jumpTimeToPreviousKeyframe(){
        //seek the current value from the value of the thumb
        double currentTime;//in seconds
        currentTime=getTime();

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
            playback.seekTo(keyframeBefore.getTime());
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
        name.setEditable(true);
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
        treeTableView.setEditable(true);
        treeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null &&//TODO NPE occurs during copy paste
                    newValue.getValue().getItemViewController().isInteractive()){
                workspace.getSelectedItems().selectOnly(newValue.getValue().getItemViewController());
                keyframeTable.getSelectionModel().select(newValue);
            }
        });
        treeTableView.getStyleClass().add("no-vertical-scrollbar");
        treeTableView.setFixedCellSize(Metadata.CELL_HEIGHT);

        return treeTableView;
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
        return playback.getTime();
    }

    public double getDuration() {
        return duration;
    }

    public Playback getPlayback() {
        return playback;
    }

    /**
     * @return main item table that contains all items.
     */
    public TreeTableView<Metadata> getItemTable() {
        return itemTable;
    }

    /**@return Unscaled width on the right side of the timeline pane that houses the keyframe panes*/
    public double getTimelineWidth(){
        return Main.WIDTH -(NAME_COLUMN_WIDTH+VALUE_COLUMN_WIDTH+OPTION_COLUMN_WIDTH);
    }
}
