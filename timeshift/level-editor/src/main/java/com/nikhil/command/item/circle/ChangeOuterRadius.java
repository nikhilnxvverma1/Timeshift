package com.nikhil.command.item.circle;

import com.nikhil.command.item.TemporalActionOnSingleItem;
import com.nikhil.controller.CircleViewController;
import com.nikhil.timeline.KeyValue;
import com.nikhil.view.item.record.MetadataTag;
import com.nikhil.view.item.record.TemporalMetadata;

/**
 * Created by NikhilVerma on 09/12/15.
 */
public class ChangeOuterRadius extends TemporalActionOnSingleItem {

    private CircleViewController circleViewController;
    private double initialValue;
    private double finalValue;

    public ChangeOuterRadius(CircleViewController circleViewController, double initialValue, double finalValue) {
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
        TemporalMetadata outerRadiusMeta = circleViewController.getTemporalMetadata(MetadataTag.OUTER_RADIUS);
        if (outerRadiusMeta.isKeyframable()) {
            double currentTime = circleViewController.getCompositionViewController().getTime();
            outerRadiusMeta.getKeyframeChangeNode().setTime(currentTime);
        }else{
            circleViewController.getItemView().setOuterRadius(finalValue);
            circleViewController.getItemModel().setOuterRadius(finalValue);
        }
        circleViewController.getGizmo().updateView();
    }

    @Override
    public void unexecute() {
        TemporalMetadata outerRadiusMeta = circleViewController.getTemporalMetadata(MetadataTag.OUTER_RADIUS);
        if (outerRadiusMeta.isKeyframable()) {
            double currentTime = circleViewController.getCompositionViewController().getTime();
            outerRadiusMeta.getKeyframeChangeNode().setTime(currentTime);
        }else{
            circleViewController.getItemView().setOuterRadius(initialValue);
            circleViewController.getItemModel().setOuterRadius(initialValue);
        }
        circleViewController.getGizmo().updateView();
    }

    @Override
    public CircleViewController getItemViewController() {
        return circleViewController;
    }
}
