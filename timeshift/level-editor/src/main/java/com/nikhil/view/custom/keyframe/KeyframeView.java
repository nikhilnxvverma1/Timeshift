package com.nikhil.view.custom.keyframe;

import com.nikhil.timeline.keyframe.Keyframe;
import com.nikhil.timeline.KeyValue;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Created by NikhilVerma on 27/10/15.
 */
public abstract class KeyframeView extends ImageView {

    private static final String TIME_PROPERTY_NAME="time";
    protected static final double DEFAULT_WIDTH =12;
    private static final Image MIDDLE_KEYFRAME=new Image("art/keyframe.png");
    private static final Image FIRST_KEYFRAME=new Image("art/first_keyframe.png");
    private static final Image LAST_KEYFRAME=new Image("art/last_keyframe.png");
    private static final String SELECTED_STYLE="-fx-base:rgba(0,0,255,0.5);-fx-background:rgba(0,0,255,0.5)";
    private static final String UNSELECTED_STYLE="-fx-base:rgba(0,0,0,0.5);-fx-background:rgba(0,0,0,0.5)";
    private static final ColorAdjust HIGHLIGHT_COLOR_ADJUST;
    static {
        HIGHLIGHT_COLOR_ADJUST =new ColorAdjust(-0.6,1d,0d,1);
    }
    private boolean selected=false;

    public KeyframeView() {
        this(DEFAULT_WIDTH);
    }

    public KeyframeView(double width) {
        super(MIDDLE_KEYFRAME);
        this.setFitWidth(width);
        this.setFitHeight(width);
        DoubleBinding centerAnchorBinding = this.fitWidthProperty().multiply(-0.5);
        translateXProperty().bind(centerAnchorBinding);
    }

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
        highlight(selected);
    }
    private void highlight(boolean highlight){
        if(highlight){
            this.setEffect(HIGHLIGHT_COLOR_ADJUST);
        }else{
            this.setEffect(null);

        }
    }

    public abstract KeyframePane getKeyframePane();

    public double getTime(){
        return getKeyframeModel().getTime();
    }

    /**
     * Sets the time of the keyframe model and makes the view itself,
     * in effect, adjusts in the keyframe pane
     * @param time the new time of the keyframe
     */
    public void setTime(double time){

        //move the keyframe view
        double newLayoutX = getKeyframePane().getLayoutXFor(time);
        this.setLayoutX(newLayoutX);

        //set the time on the keyframe model
        getKeyframeModel().setTime(time);
    }
    public abstract Keyframe getKeyframeModel();

    /** Adds itself to the parent keyframe pane*/
    public abstract void addToParentKeyframePane();

    /**
     * Removes itself from the parent keyframe pane. The reference to parent still exists
     * @return true if the data structure holding this changed, false otherwise
     */
    public abstract boolean removeFromParentKeyframePane();
}
