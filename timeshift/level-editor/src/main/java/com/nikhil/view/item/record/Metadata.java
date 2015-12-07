package com.nikhil.view.item.record;

import com.nikhil.controller.ItemViewController;
import com.nikhil.view.custom.keyframe.KeyframePane;
import javafx.scene.Node;

/**
 * Metadata stores information about the properties of the model. Except the root, every other instantiation
 * will be from a subclass .
 * Created by NikhilVerma on 13/10/15.
 */
public abstract class Metadata { //TODO Property Metadata required

    public static final double CELL_HEIGHT= 25;
    protected String name;
    protected MetadataTag tag;
    protected ItemViewController itemViewController;

    public Metadata(ItemViewController itemViewController,String name, MetadataTag tag) {
        this.itemViewController=itemViewController;
        this.name=name;
        this.tag = tag;
    }

    public String getName(){
        return name;
    }

    public ItemViewController getItemViewController(){
        return itemViewController;
    }

    public MetadataTag getTag() {
        return tag;
    }

    public abstract Node getValueNode();

    public abstract Node getOptionNode();

    public abstract boolean isHeader();

    /**
     * @return true if this metadata is the main header that displays item's name
     */
    public boolean isItemNameHeader(){
        return false;
    }

    /**
     * lazily builds and returns the keyframe pane
     * @param width the width of the keyframe pane
     * @return keyframe pane or null (if not relevant to this metadata)
     */
    public abstract KeyframePane initKeyframePane(double width);

    /**
     * Keyframe pane associated with this metadata which may be null because
     * it never got initialized.If caller does not know this metdata's keyframing scenario,
     * it should always check for nulls
     * @return Keyframe pane associated with this metadata.
     */
    public abstract KeyframePane getKeyframePane();

    public abstract Node getNameNode();
    public boolean isKeyframable(){
        return false;
    }

    public abstract void setKeyframable(boolean keyframable);
}
