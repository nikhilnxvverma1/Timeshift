package com.nikhil.command.item.triangle;

import com.nikhil.command.item.TemporalActionOnSingleItem;
import com.nikhil.controller.TriangleViewController;
import com.nikhil.timeline.KeyValue;
import com.nikhil.view.item.record.MetadataTag;
import com.nikhil.view.item.record.TemporalMetadata;

/**
 * Created by NikhilVerma on 11/12/15.
 */
public class ChangeHeight extends TemporalActionOnSingleItem{

    private TriangleViewController triangleViewController;
    private double initialValue;
    private double finalValue;

    public ChangeHeight(TriangleViewController triangleViewController, double initialValue, double finalValue) {
        super();
        this.triangleViewController = triangleViewController;
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
        TemporalMetadata heightMeta = triangleViewController.getTemporalMetadata(MetadataTag.TRIANGLE_HEIGHT);
        if (heightMeta.isKeyframable()) {
            double currentTime = triangleViewController.getCompositionViewController().getTime();
            heightMeta.getKeyframeChangeNode().setTime(currentTime);
        }else{
            triangleViewController.getItemView().setHeight(finalValue);
            triangleViewController.getItemModel().setHeight(finalValue);
        }
        triangleViewController.getGizmo().updateView();
    }

    @Override
    public void unexecute() {
        TemporalMetadata heightMeta = triangleViewController.getTemporalMetadata(MetadataTag.TRIANGLE_HEIGHT);
        if (heightMeta.isKeyframable()) {
            double currentTime = triangleViewController.getCompositionViewController().getTime();
            heightMeta.getKeyframeChangeNode().setTime(currentTime);
        }else{
            triangleViewController.getItemView().setHeight(initialValue);
        }
        triangleViewController.getGizmo().updateView();
    }

    @Override
    public TriangleViewController getItemViewController() {
        return triangleViewController;
    }
}
