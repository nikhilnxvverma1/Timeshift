package com.nikhil.view.item.record;

import com.nikhil.controller.ItemViewController;
import com.nikhil.view.custom.keyframe.KeyframePane;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;

/**
 * Header metadata contains the name of a heading and acts as a parent for leaf metadata
 * Created by NikhilVerma on 11/11/15.
 */
public class HeaderMetadata extends Metadata {
    public HeaderMetadata(String name, MetadataTag tag) {
        super(name, tag);
    }

    public ItemViewController getItemViewController(){
        return null;//null for root metadata
    }

    public Node getValueNode(){
        return null;
    }

    public Node getOptionNode(){ return null; }

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
}
