package com.nikhil.view.item;

import com.nikhil.view.item.delegate.ItemViewDelegate;
import javafx.scene.shape.Path;

/**
 * Base class for all shape views in the application. This ensures
 * that certain common stuff gets handled right here
 * Created by NikhilVerma on 07/12/15.
 */
public abstract class ShapeView extends Path {

    public ShapeView() {
        this.scaleYProperty().bind(this.scaleXProperty());
    }

    public double getScale() {
        return getScaleX();
    }

    public void setScale(double scale) {
        this.setScaleX(scale);
    }

    public abstract ItemViewDelegate getDelegate();
    protected void setDelegateAsEventHandler(){
        ItemViewDelegate delegate=getDelegate();
        if(delegate!=null){
            this.setOnMousePressed(delegate::mousePressed);
            this.setOnMouseDragged(delegate::mouseDragged);
            this.setOnMouseReleased(delegate::mouseReleased);
        }else{
            this.setOnMousePressed(null);
            this.setOnMouseDragged(null);
            this.setOnMouseReleased(null);
        }
    }
}
