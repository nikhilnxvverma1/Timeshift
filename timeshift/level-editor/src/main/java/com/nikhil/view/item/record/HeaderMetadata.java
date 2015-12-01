package com.nikhil.view.item.record;

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
    private ItemViewController itemViewController;
    private boolean itemNameHeader;

    public HeaderMetadata(String name, MetadataTag tag, boolean itemNameHeader, ItemViewController itemViewController) {
        super(name, tag);
        this.itemNameHeader = itemNameHeader;
        this.itemViewController = itemViewController;
    }

    public ItemViewController getItemViewController(){
        return itemViewController;
    }

    public Node getValueNode(){
        return new Button("Reset");//TODO delegation and visual size
    }

    public Node getOptionNode(){
        CheckBox visibility=new CheckBox();
        visibility.setSelected(true);//TODO register listener
        Tooltip.install(visibility, new Tooltip("Visible"));

        CheckBox solo=new CheckBox();
        Tooltip.install(solo,new Tooltip("Solo"));

        CheckBox lock=new CheckBox();
        Tooltip.install(lock,new Tooltip("Lock"));
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

    /**
     * Called just before this metadata is about to be removed from the view.
     * Subclasses are expected to override this method and remove any event handlers
     * that may leak memory later
     */
    public void cleanUp(){

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
