package com.nikhil.view.item;

import com.nikhil.model.shape.TriangleModel;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.item.delegate.TriangleViewDelegate;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;

/**
 * Customizable triangle view that uses path for rendering.
 * Created by NikhilVerma on 06/09/15.
 */
public class TriangleView extends ShapeView{

    private TriangleViewDelegate delegate;

    private DoubleProperty base=new SimpleDoubleProperty(TriangleModel.DEFAULT_BASE);
    private DoubleProperty height=new SimpleDoubleProperty(TriangleModel.DEFAULT_HEIGHT);


    //=============================================================================================
    //UI components
    //=============================================================================================

    private MoveTo right;
    private LineTo left;
    private LineTo tip;

    /**
     * Creates a carbon copy of this view using another view
     * @param triangleView the view to copy everything from(including layout properties)
     */
    public TriangleView(TriangleView triangleView) {
        this(triangleView.base.get(), triangleView.height.get());
        copyValuesFrom(triangleView);//too copy off srt values as well
        //since layout is not copied, we copy here manually
        this.setLayoutX(triangleView.getLayoutX());
        this.setLayoutY(triangleView.getLayoutY());

    }

    public TriangleView(double base, double height) {
        this.base.set(base);
        this.height.set(height);
        initView();
        updateView();
    }

    private void initView(){
        right=new MoveTo();
        left=new LineTo();
        tip=new LineTo();
        this.getElements().addAll(right,left,tip,new ClosePath());

        this.setFill(Color.ORANGE);
        this.setStroke(null);
    }

    public double getBase() {
        return base.get();
    }

    public DoubleProperty baseProperty() {
        return base;
    }

    public void setBase(double base) {
        this.base.set(base);
        updateView();
    }

    public double getHeight() {
        return height.get();
    }

    public DoubleProperty heightProperty() {
        return height;
    }

    public void setHeight(double height) {
        this.height.set(height);
        updateView();
    }

    @Override
    public void updateView(){
        UtilPoint rightPoint=getRightPoint();

        right.setX(rightPoint.getX());
        right.setY(rightPoint.getY());

        UtilPoint leftPoint=getLeftPoint();
        left.setX(leftPoint.getX());
        left.setY(leftPoint.getY());

        UtilPoint tipPoint=getTipPoint();
        tip.setX(tipPoint.getX());
        tip.setY(tipPoint.getY());
    }

    public UtilPoint getRightPoint(){
        double rx= base.get() /2;
        double ry=height.get()/2;
        return rotatedAroundOrigin(rx,ry);
    }

    public UtilPoint getLeftPoint(){
        double lx=-base.get() /2;
        double ly=height.get()/2;
        return rotatedAroundOrigin(lx,ly);
    }

    public UtilPoint getTipPoint(){
        double tx=0;
        double ty=-height.get()/2;
        return rotatedAroundOrigin(tx,ty);
    }

    public void setDelegate(TriangleViewDelegate delegate) {
        this.delegate = delegate;
        setDelegateAsEventHandler();
    }

    @Override
    public TriangleViewDelegate getDelegate() {
        return delegate;
    }

    /**
     * Copies all properties from the specified view <b>EXCEPT</b> layout x,y properties
     * @param triangleView the view to copy values from
     */
    public void copyValuesFrom(TriangleView triangleView){

        this.base.set(triangleView.getBase());
        this.height.set(triangleView.getHeight());

        setScale(triangleView.getScale());
        setRotate(triangleView.getRotate());
        setOriginRotate(triangleView.getOriginRotate());
        setTranslateX(triangleView.getTranslateX());
        setTranslateY(triangleView.getTranslateY());

        updateView();
    }
}


