package com.nikhil.command.item.triangle;

import com.nikhil.command.item.TemporalActionOnSingleItem;
import com.nikhil.controller.TriangleViewController;
import com.nikhil.timeline.KeyValue;
import com.nikhil.view.item.record.MetadataTag;
import com.nikhil.view.item.record.TemporalMetadata;

/**
 * Created by NikhilVerma on 11/12/15.
 */
public class ChangeBase extends TemporalActionOnSingleItem{
    private TriangleViewController triangleViewController;
    private double initialValue;
    private double finalValue;

    public ChangeBase(TriangleViewController triangleViewController, double initialValue, double finalValue) {
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
        TemporalMetadata baseMeta = triangleViewController.getTemporalMetadata(MetadataTag.BASE);
        if (baseMeta.isKeyframable()) {
            double currentTime = triangleViewController.getCompositionViewController().getTime();
            baseMeta.getKeyframeChangeNode().setTime(currentTime);
        }else{
            triangleViewController.getItemView().setBase(finalValue);
        }
        triangleViewController.getGizmo().updateView();
    }

    @Override
    public void unexecute() {
        TemporalMetadata baseMeta = triangleViewController.getTemporalMetadata(MetadataTag.BASE);
        if (baseMeta.isKeyframable()) {
            double currentTime = triangleViewController.getCompositionViewController().getTime();
            baseMeta.getKeyframeChangeNode().setTime(currentTime);
        }else{
            triangleViewController.getItemView().setBase(initialValue);
        }
        triangleViewController.getGizmo().updateView();
    }

    @Override
    public TriangleViewController getItemViewController() {
        return triangleViewController;
    }
    
}
