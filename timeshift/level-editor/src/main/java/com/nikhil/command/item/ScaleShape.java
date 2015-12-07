package com.nikhil.command.item;

import com.nikhil.controller.ShapeViewController;
import com.nikhil.timeline.KeyValue;
import com.nikhil.view.item.record.MetadataTag;
import com.nikhil.view.item.record.TemporalMetadata;

/**
 * Created by NikhilVerma on 21/09/15.
 */
public class ScaleShape extends TemporalActionOnSingleItem {

    private double initialScale;
    private double finalScale;


    public ScaleShape(ShapeViewController shapeViewController,double initialScale, double finalScale) {
        super(shapeViewController);
        this.initialScale = initialScale;
        this.finalScale = finalScale;
    }

    @Override
    public void execute() {
        TemporalMetadata scaleMetadata = shapeViewController.getTemporalMetadata(MetadataTag.SCALE);
        if (scaleMetadata.isKeyframable()) {
            double currentTime = shapeViewController.getCompositionViewController().getTime();
            scaleMetadata.getKeyframeChangeNode().setTime(currentTime);
        }else{
            double dScale = finalScale - initialScale;
            shapeViewController.scaleBy(dScale);
        }
    }

    @Override
    public void unexecute() {
        TemporalMetadata scaleMetadata = shapeViewController.getTemporalMetadata(MetadataTag.SCALE);
        if (scaleMetadata.isKeyframable()) {
            double currentTime = shapeViewController.getCompositionViewController().getTime();
            scaleMetadata.getKeyframeChangeNode().setTime(currentTime);
        }else{
            double dScale = initialScale - finalScale;
            shapeViewController.scaleBy(dScale);
        }

    }

    @Override
    public KeyValue getInitialValue() {
        return new KeyValue(initialScale);
    }

    @Override
    public KeyValue getFinalValue() {
        return new KeyValue(finalScale);
    }
}
