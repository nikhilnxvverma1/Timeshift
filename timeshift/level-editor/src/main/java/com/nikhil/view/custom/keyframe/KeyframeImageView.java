package com.nikhil.view.custom.keyframe;

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
public class KeyframeImageView extends ImageView {

//    enum KeyFill{
//        LEFT_UNFILLED,
//        BOTH_FILLED,
//        RIGHT_UNFILLED
//    };
    private static final String TIME_PROPERTY_NAME="time";
    private static final double DEFAULT_WIDTH =12;
    private static final Image MIDDLE_KEYFRAME=new Image("art/keyframe.png");
    private static final Image FIRST_KEYFRAME=new Image("art/first_keyframe.png");
    private static final Image LAST_KEYFRAME=new Image("art/last_keyframe.png");
    private static final String SELECTED_STYLE="-fx-base:rgba(0,0,255,0.5);-fx-background:rgba(0,0,255,0.5)";
    private static final String UNSELECTED_STYLE="-fx-base:rgba(0,0,0,0.5);-fx-background:rgba(0,0,0,0.5)";
    private static final ColorAdjust HIGHLIGHT_COLOR_ADJUST;
    static {
        HIGHLIGHT_COLOR_ADJUST =new ColorAdjust(-0.6,1d,0d,1);
    }
    private DoubleProperty time;
    private KeyValue keyValue;
    private boolean selected=false;
    private KeyframePane keyframePane;

    public KeyframeImageView(KeyframePane keyframePane) {
        this(keyframePane,DEFAULT_WIDTH);
    }

    public KeyframeImageView(KeyframePane keyframePane,double width) {
        super(MIDDLE_KEYFRAME);
        this.keyframePane=keyframePane;
        this.setFitWidth(width);
        this.setFitHeight(width);
        time=new SimpleDoubleProperty(this, TIME_PROPERTY_NAME);
        DoubleBinding centerAnchorBinding = this.fitWidthProperty().multiply(-0.5);
        translateXProperty().bind(centerAnchorBinding);
    }

    public double getTime() {
        return time.doubleValue();
    }
    public void setTime(double time) {
        this.time.set(time);
    }
    public KeyValue getKeyValue() {
        return keyValue;
    }
    public void setKeyValue(KeyValue keyValue) {
        this.keyValue = keyValue;
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
}
