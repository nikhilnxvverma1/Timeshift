package com.nikhil.view.item;

import com.nikhil.math.MathUtil;
import com.nikhil.util.modal.UtilPoint;
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

    /**
     * Every relative x,y coordinates in the path should first be converted by this
     * method so as to eliminate the change in orientation of the shape view.
     * @param x relative x to path
     * @param y relative y to path
     * @return point relative to the origin of the path ,which is rotated by the
     * current rotation of this shape
     */
    public UtilPoint rotatedAroundOrigin(double x,double y){
        return MathUtil.getRotatedPoint(new UtilPoint(x,y),getRotate());
    }
}
