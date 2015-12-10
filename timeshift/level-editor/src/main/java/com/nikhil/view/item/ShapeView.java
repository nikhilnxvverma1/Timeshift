package com.nikhil.view.item;

import com.nikhil.logging.Logger;
import com.nikhil.math.MathUtil;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.item.delegate.ItemViewDelegate;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.shape.Path;

/**
 * Base class for all shape views in the application. This ensures
 * that certain common stuff gets handled right here.
 * <p>
 *     Any rotation changes on this class should be done through
 *     the "<b>originRotate</b>" property and not the simple "rotateProperty" of a {@link Node}.
 *     That's because, changing Node's rotate property rotates it inside its own bounding box
 *     without concerns about the origin of this Shape. This in turn changes the location of the origin
 *     on the screen.
 * </p>
 * Created by NikhilVerma on 07/12/15.
 */
public abstract class ShapeView extends Path {

    protected DoubleProperty originRotate=new SimpleDoubleProperty(0);

    public ShapeView() {
        this.scaleYProperty().bind(this.scaleXProperty());
    }

    public double getScale() {
        return getScaleX();
    }

    public void setScale(double scale) {
        this.setScaleX(scale);
    }

    public double getOriginRotate() {
        return originRotate.get();
    }

    public DoubleProperty originRotateProperty() {
        return originRotate;
    }

    public void setOriginRotate(double originRotate) {
        this.originRotate.set(originRotate);
        updateView();
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

    /**Updates the view based on currently set properties */
    public abstract void updateView();

    /**
     * Every relative x,y coordinates in the path should first be converted by this
     * method so as to eliminate the change in orientation of the shape view.
     * @param x relative x to path
     * @param y relative y to path
     * @return point relative to the origin of the path ,which is rotated by the
     * current rotation of this shape
     */
    public UtilPoint rotatedAroundOrigin(double x,double y){
        return MathUtil.getRotatedPoint(new UtilPoint(x,y),getOriginRotate());
    }

    /**
     * Every relative x,y coordinates in the path should first be converted by this
     * method so as to eliminate the change in orientation of the shape view.
     * @param point relative point to the shape path
     * @return point relative to the origin of the path ,which is rotated by the
     * current rotation of this shape
     */
    public UtilPoint rotatedAroundOrigin(UtilPoint point){
        return rotatedAroundOrigin(point.getX(),point.getY());
    }

    /**
     * Every angle used inside shape should be rotated around the shape's rotation
     * @param angDeg angle in degrees
     * @return angle in degrees relative to the shape's rotation
     */
    public double rotatedAngle(double angDeg){
        return MathUtil.below360(getRotate()+angDeg);
    }
}
