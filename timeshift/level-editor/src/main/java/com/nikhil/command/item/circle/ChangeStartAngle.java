package com.nikhil.command.item.circle;

import com.nikhil.command.item.TemporalActionOnSingleItem;
import com.nikhil.controller.CircleViewController;
import com.nikhil.timeline.KeyValue;
import com.nikhil.view.item.record.MetadataTag;
import com.nikhil.view.item.record.TemporalMetadata;

/**
 * Created by NikhilVerma on 09/12/15.
 */
public class ChangeStartAngle extends TemporalActionOnSingleItem {

    private CircleViewController circleViewController;
    private double initialValue;
    private double finalValue;

    public ChangeStartAngle(CircleViewController circleViewController, double initialValue, double finalValue) {
        super();
        this.circleViewController = circleViewController;
        this.initialValue = initialValue;
        this.finalValue = finalValue;
    }

    @Override
    public KeyValue getInitialValue() {
        return new KeyValue(initialValue);
    }

    @Override
    public KeyValue getFinalValue() {
        return new KeyValue(finalValue);
    }

    @Override
    public void execute() {
        TemporalMetadata startAngleMeta = circleViewController.getTemporalMetadata(MetadataTag.START_ANGLE);
        if (startAngleMeta.isKeyframable()) {
            double currentTime = circleViewController.getCompositionViewController().getTime();
            startAngleMeta.getKeyframeChangeNode().setTime(currentTime);
        }else{
            circleViewController.getItemView().setStartAngle(finalValue);
        }
        circleViewController.getGizmo().updateView();
    }

    @Override
    public void unexecute() {
        TemporalMetadata startAngleMeta = circleViewController.getTemporalMetadata(MetadataTag.START_ANGLE);
        if (startAngleMeta.isKeyframable()) {
            double currentTime = circleViewController.getCompositionViewController().getTime();
            startAngleMeta.getKeyframeChangeNode().setTime(currentTime);
        }else{
            circleViewController.getItemView().setStartAngle(initialValue);
        }
        circleViewController.getGizmo().updateView();
    }

    @Override
    public CircleViewController getItemViewController() {
        return circleViewController;
    }
}

