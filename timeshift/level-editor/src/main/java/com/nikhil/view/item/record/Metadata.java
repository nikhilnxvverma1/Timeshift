package com.nikhil.view.item.record;

import com.nikhil.Main;
import com.nikhil.controller.ItemViewController;
import com.nikhil.timeline.KeyValue;
import com.nikhil.view.custom.DraggableTextValue;
import com.nikhil.view.custom.DraggableTextValueDelegate;
import com.nikhil.view.custom.KeyframePane;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;

import java.util.Random;

/**
 * Metadata stores information about the properties of the model. Except the root, every other instantiation
 * will be from a subclass .
 * Created by NikhilVerma on 13/10/15.
 */
public class Metadata {
    private static final Random random=new Random();
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

    public KeyframePane getKeyframePane(double width){
        KeyframePane keyframePane = new KeyframePane(30, width);
        int r=random.nextInt(10);
        for (int i = 0; i < r; i++) {
            keyframePane.addKeyAt(random.nextInt(30), null);
        }
//        keyframePane.addKeyAt(7, null);
//        keyframePane.addKeyAt(10, null);
//        keyframePane.addKeyAt(12, null);
//        keyframePane.addKeyAt(23, null);
//        keyframePane.addKeyAt(26, null);
//        keyframePane.addKeyAt(19, null);
        keyframePane.layoutXProperty().addListener((observable, oldValue, newValue) -> {
            ((DoubleProperty)observable).set(0);//downcast to double because we know that layoutx is a double property
        });
        return keyframePane;
    }
}
