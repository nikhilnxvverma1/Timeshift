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

    public abstract double getTime();
}
