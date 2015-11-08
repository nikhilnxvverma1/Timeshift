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
public class Metadata { //TODO ideally this should be an abstract class. But just because of "Root" node its not

    public static final double CELL_HEIGHT= 25;
    public static final short ROOT_TAG=-1;
    protected StringProperty nameProperty;
    protected int tag;

    public Metadata(String name, int tag) {
        this.nameProperty=new SimpleStringProperty(name);
        this.tag = tag;
    }

    public ItemViewController getItemViewController(){
        return null;//Applicable for root metadata
    }

    public StringProperty nameProperty() {
        return nameProperty;
    }

    public void setName(String name){
        nameProperty.set(name);
    }

    public String getName(){
        return nameProperty.get();
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public Node getValueNode(){
        return null;
    }

    public Node getOptionNode(){ return null; }

    public void refresh(){//TODO might not be needed,handled by listeners
        //do nothing, based on the tag information subclasses are
        //supposed to override this method and ping for relevant information
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
}
