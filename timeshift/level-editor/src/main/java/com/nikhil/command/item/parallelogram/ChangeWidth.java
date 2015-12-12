package com.nikhil.command.item.parallelogram;

import com.nikhil.command.item.TemporalActionOnSingleItem;
import com.nikhil.controller.ParallelogramViewController;
import com.nikhil.timeline.KeyValue;
import com.nikhil.view.item.record.MetadataTag;
import com.nikhil.view.item.record.TemporalMetadata;

/**
 * Command that changes width of parallelogram
 * Created by NikhilVerma on 11/12/15.
 */
public class ChangeWidth extends TemporalActionOnSingleItem {

    private ParallelogramViewController parallelogramViewController;
    private double initialValue;
    private double finalValue;

    public ChangeWidth(ParallelogramViewController parallelogramViewController, double initialValue, double finalValue) {
        super();
        this.parallelogramViewController = parallelogramViewController;
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
        TemporalMetadata widthMeta = parallelogramViewController.getTemporalMetadata(MetadataTag.PARALLELOGRAM_WIDTH);
        if (widthMeta.isKeyframable()) {
            double currentTime = parallelogramViewController.getCompositionViewController().getTime();
            widthMeta.getKeyframeChangeNode().setTime(currentTime);
        }else{
            parallelogramViewController.getItemView().setWidth(finalValue);
        }
        parallelogramViewController.getGizmo().updateView();
    }

    @Override
    public void unexecute() {
        TemporalMetadata widthMeta = parallelogramViewController.getTemporalMetadata(MetadataTag.PARALLELOGRAM_WIDTH);
        if (widthMeta.isKeyframable()) {
            double currentTime = parallelogramViewController.getCompositionViewController().getTime();
            widthMeta.getKeyframeChangeNode().setTime(currentTime);
        }else{
            parallelogramViewController.getItemView().setWidth(initialValue);
        }
        parallelogramViewController.getGizmo().updateView();
    }

    @Override
    public ParallelogramViewController getItemViewController() {
        return parallelogramViewController;
    }
}

