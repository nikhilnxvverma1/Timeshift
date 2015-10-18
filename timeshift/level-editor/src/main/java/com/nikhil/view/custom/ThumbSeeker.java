package com.nikhil.view.custom;

import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

/**
 * Created by NikhilVerma on 18/10/15.
 */
public class ThumbSeeker extends Pane {

    private static final double THUMB_WIDTH=10;
    private static final double THUMB_HEIGHT=20;
    private double start;
    private double end;
    private double current;
    private double width;
    private ThumbSeekerDelegate delegate;

    private VBox thumb;

    public ThumbSeeker(double width) {
        this(width,0,100,null);
    }

    public ThumbSeeker(double width,ThumbSeekerDelegate delegate){
        this(width,0,100,delegate);
    }

    public ThumbSeeker(double width, double start, double end,ThumbSeekerDelegate delegate) {
        this.width=width;
        this.start = start;
        this.end = end;
        this.delegate=delegate;
//        current= (start + end) / 2;
        current=0;
        this.setPrefSize(width, THUMB_HEIGHT);
        this.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        initView();

    }

    private void initView(){
        Path thumbShape=createThumb(THUMB_WIDTH,THUMB_HEIGHT);

        Line line=new Line(0,0,0,300);//TODO hardcoded
        line.setStroke(Color.RED);

        thumb=new VBox(thumbShape,line);
        thumb.setAlignment(Pos.CENTER);
        thumb.translateXProperty().bind(thumb.widthProperty().multiply(-.5));
        this.getChildren().add(thumb);
        this.addEventHandler(MouseEvent.MOUSE_PRESSED,this::mousePressed);
        this.addEventHandler(MouseEvent.MOUSE_DRAGGED,this::mouseDragged);
        this.addEventHandler(MouseEvent.MOUSE_RELEASED,this::mouseReleased);
    }

    private void mousePressed(MouseEvent mouseEvent) {
        double oldValue=current;
        setThumbAt(getValueForPosition(mouseEvent.getX()));
        notifyDelegate(oldValue);
    }

    private void mouseDragged(MouseEvent mouseEvent) {
        double oldValue=current;
        setThumbAt(getValueForPosition(mouseEvent.getX()));
        notifyDelegate(oldValue);
    }

    private void mouseReleased(MouseEvent mouseEvent) {

    }

    private Path createThumb(double width,double height){
        MoveTo origin=new MoveTo(0,0);
        LineTo a=new LineTo(.5*width,-.33*height);
        LineTo b=new LineTo(.5*width,-1*height);
        LineTo c=new LineTo(-.5*width,-1*height);
        LineTo d=new LineTo(-.5*width,-.33*height);
        Path thumb=new Path(origin,a,b,c,d,new ClosePath());
        thumb.setFill(Color.ORANGE);
        thumb.setStroke(Color.BLACK);
        return thumb;
    }
    public void setThumbAt(double value){
        current=value;
        double h = getPositionOf(current);
        thumb.setLayoutX(h);
    }

    private double getPositionOf(double value){
        return width * ((value - start) / (end - start));
    }

    private double getValueForPosition(double x){
        return (x/width)*(end-start)+start;
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        width=getWidth();
        setThumbAt(current);
    }

    void notifyDelegate(double oldValue){
        if(delegate!=null){
            delegate.thumbWasSeeked(this,oldValue,current);
        }
    }
}