package com.nikhil.view.item.record;

import com.nikhil.command.item.toggle.ToggleItemLock;
import com.nikhil.command.item.toggle.ToggleItemSolo;
import com.nikhil.command.item.toggle.ToggleItemVisibility;
import com.nikhil.controller.ItemViewController;
import com.nikhil.view.custom.keyframe.KeyframePane;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

/**
 * Header metadata contains the name of a heading and acts as a parent for leaf metadata.
 * Created by NikhilVerma on 11/11/15.
 */
public class HeaderMetadata extends Metadata {
    private boolean itemNameHeader;

    private CheckBox visibility;
    private CheckBox solo;
    private CheckBox lock;

    public HeaderMetadata(String name, MetadataTag tag, ItemViewController itemViewController, boolean itemNameHeader) {
        super(itemViewController,name, tag);
        this.itemNameHeader = itemNameHeader;
        this.itemViewController = itemViewController;
        if(itemNameHeader){
            initSwitches();
        }
    }

    public CheckBox getVisibility() {
        return visibility;
    }

    public CheckBox getSolo() {
        return solo;
    }

    public CheckBox getLock() {
        return lock;
    }

    public Node getValueNode(){
        return new Button("Reset");//TODO delegation and visual size
    }

    /**
     * Initializes the checkboxes for the headers.
     * These checkboxes are deliberately instantiated here because while getting recycled in the option cell,
     * we don't want new checkboxes to be created on a call to getOptionsNode().
     * FYI these checkboxes are referenced externally in their specific commands.
     */
    private void initSwitches(){

        //visibility toggle
        visibility=new CheckBox();
        visibility.setSelected(true);
        Tooltip.install(visibility, new Tooltip("Visible"));
        visibility.setOnAction(event -> {
            ToggleItemVisibility toggleItemVisibility=new ToggleItemVisibility(itemViewController,
                    !visibility.isSelected(),
                    visibility.isSelected(),
                    visibility);
            itemViewController.getCompositionViewController().getWorkspace().pushCommand(toggleItemVisibility);

        });

        //solo toggle
        solo=new CheckBox();
        Tooltip.install(solo,new Tooltip("Solo"));
        solo.setOnAction(event -> {
            ToggleItemSolo toggleItemSolo=new ToggleItemSolo(itemViewController,
                    !solo.isSelected(),
                    solo.isSelected(),
                    solo);
            itemViewController.getCompositionViewController().getWorkspace().pushCommand(toggleItemSolo);
        });


        //lock toggle
        lock=new CheckBox();
        Tooltip.install(lock, new Tooltip("Lock"));
        lock.setOnAction(e -> {
            final ToggleItemLock toggleItemLock = new ToggleItemLock(itemViewController,
                    !lock.isSelected(),
                    lock.isSelected(),
                    lock);
            itemViewController.getCompositionViewController().getWorkspace().pushCommand(toggleItemLock);
        });
    }

    public Node getOptionNode(){
        return new HBox(visibility,solo,lock);
    }

    @Override
    public String getName() {
        if(isItemNameHeader()){
            return itemViewController.getItemModel().getName();
        }else{
            return super.getName();
        }
    }

    public boolean isHeader(){
        return true;
    }

    /**
     * lazily builds and returns the keyframe pane
     * @param width the width of the keyframe pane
     * @return keyframe pane or null (if not relevant to this metadata)
     */
    public KeyframePane initKeyframePane(double width){
        return null;
    }
    public KeyframePane getKeyframePane(){
        return null;
    }

    @Override
    public Node getNameNode() {
        return null;
    }

    @Override
    public void setKeyframable(boolean keyframable) {
        //do nothing,
    }

    @Override
    public boolean isItemNameHeader() {
        return itemNameHeader;
    }
}
