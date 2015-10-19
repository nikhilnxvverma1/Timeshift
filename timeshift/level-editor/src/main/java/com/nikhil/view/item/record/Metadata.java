package com.nikhil.view.item.record;

import com.nikhil.controller.ItemViewController;
import com.nikhil.view.custom.DraggableTextValue;
import com.nikhil.view.custom.DraggableTextValueDelegate;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

/**
 * Metadata stores information about the properties of the model. Except the root, every other instantiation
 * will be from a subclass .
 * Created by NikhilVerma on 13/10/15.
 */
public class Metadata {
    public static final short ROOT_TAG=-1;
    protected StringProperty nameProperty;
//    protected boolean header;
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
}
