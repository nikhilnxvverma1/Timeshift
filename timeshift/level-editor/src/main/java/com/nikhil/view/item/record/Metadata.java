package com.nikhil.view.item.record;

import com.nikhil.controller.ItemViewController;
import com.nikhil.view.custom.keyframe.KeyframePane;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;

import java.util.Random;

/**
 * Metadata stores information about the properties of the model. Except the root, every other instantiation
 * will be from a subclass .
 * Created by NikhilVerma on 13/10/15.
 */
public abstract class Metadata { //TODO Property Metadata required

    protected static final Random random=new Random();//only experimentation purposes
    public static final double CELL_HEIGHT= 25;
    public static final short ROOT_TAG=-1;
    protected StringProperty nameProperty;
    protected MetadataTag tag;

    public Metadata(String name, MetadataTag tag) {
        this.nameProperty=new SimpleStringProperty(name);
        this.tag = tag;
    }

    public abstract ItemViewController getItemViewController();

    public StringProperty nameProperty() {
        return nameProperty;
    }

    public MetadataTag getTag() {
        return tag;
    }

    public abstract Node getValueNode();

    public abstract Node getOptionNode();

    public abstract boolean isHeader();

    /**
     * lazily builds and returns the keyframe pane
     * @param width the width of the keyframe pane
     * @return keyframe pane or null (if not relevant to this metadata)
     */
    public abstract KeyframePane initKeyframePane(double width);
    public abstract KeyframePane getKeyframePane();

    public abstract Node getNameNode();
    public boolean isKeyframable(){
        return false;
    }

    public abstract void setKeyframable(boolean keyframable);
}
