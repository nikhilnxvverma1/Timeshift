package com.nikhil.command.item;

import com.nikhil.controller.ShapeViewController;
import com.nikhil.timeline.KeyValue;
import com.nikhil.view.item.record.MetadataTag;
import com.nikhil.view.item.record.TemporalMetadata;

/**
 * Created by NikhilVerma on 23/09/15.
 */
public class RotateShape extends TemporalActionOnSingleItem {

    private double initialAngle;
    private double finalAngle;

    public RotateShape(ShapeViewController shapeViewController, double initialAngle, double finalAngle) {
        super(shapeViewController);
        this.initialAngle = initialAngle;
        this.finalAngle = finalAngle;
    }

    @Override
    public void execute() {
        TemporalMetadata rotateMetadata = shapeViewController.getTemporalMetadata(MetadataTag.ROTATION);
        if (rotateMetadata.isKeyframable()) {
            double currentTime = shapeViewController.getCompositionViewController().getTime();
            rotateMetadata.getKeyframeChangeNode().setTime(currentTime);
        }else{
            double dRotation=finalAngle-initialAngle;
            shapeViewController.rotateBy(dRotation);
        }

    }

    @Override
    public void unexecute() {
        TemporalMetadata rotateMetadata = shapeViewController.getTemporalMetadata(MetadataTag.ROTATION);
        if (rotateMetadata.isKeyframable()) {
            double currentTime = shapeViewController.getCompositionViewController().getTime();
            rotateMetadata.getKeyframeChangeNode().setTime(currentTime);
        }else{
            double dRotation= initialAngle - finalAngle;
            shapeViewController.rotateBy(dRotation);
        }
    }

    @Override
    public KeyValue getInitialValue() {
        return new KeyValue(initialAngle);
    }

    @Override
    public KeyValue getFinalValue() {
        return new KeyValue(finalAngle);
    }
}
