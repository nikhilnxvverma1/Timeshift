package com.nikhil.command.item.circle;

import com.nikhil.command.item.TemporalActionOnSingleItem;
import com.nikhil.controller.CircleViewController;
import com.nikhil.timeline.KeyValue;
import com.nikhil.view.item.record.MetadataTag;
import com.nikhil.view.item.record.TemporalMetadata;

/**
 * Created by NikhilVerma on 09/12/15.
 */
public class ChangeInnerRadius extends TemporalActionOnSingleItem {

    private CircleViewController circleViewController;
    private double initialValue;
    private double finalValue;

    public ChangeInnerRadius(CircleViewController circleViewController, double initialValue, double finalValue) {
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
        TemporalMetadata innerRadiusMeta = circleViewController.getTemporalMetadata(MetadataTag.INNER_RADIUS);
        if (innerRadiusMeta.isKeyframable()) {
            double currentTime = circleViewController.getCompositionViewController().getTime();
            innerRadiusMeta.getKeyframeChangeNode().setTime(currentTime);
        }else{
            circleViewController.getItemView().setInnerRadius(finalValue);
            circleViewController.getItemModel().setInnerRadius(finalValue);
        }
        circleViewController.getGizmo().updateView();
    }

    @Override
    public void unexecute() {
        TemporalMetadata innerRadiusMeta = circleViewController.getTemporalMetadata(MetadataTag.INNER_RADIUS);
        if (innerRadiusMeta.isKeyframable()) {
            double currentTime = circleViewController.getCompositionViewController().getTime();
            innerRadiusMeta.getKeyframeChangeNode().setTime(currentTime);
        }else{
            circleViewController.getItemView().setInnerRadius(initialValue);
            circleViewController.getItemModel().setInnerRadius(initialValue);
        }
        circleViewController.getGizmo().updateView();
    }

    @Override
    public CircleViewController getItemViewController() {
        return circleViewController;
    }
}
