package com.nikhil.view.custom.keyframe;

import com.nikhil.view.item.record.Metadata;
import com.nikhil.view.item.record.SpatialMetadata;

/**
 * Created by NikhilVerma on 11/11/15.
 */
public class SpatialKeyframePane extends KeyframePane{

    private SpatialMetadata metadata;

    public SpatialKeyframePane(double totalTime, double length,SpatialMetadata metadata) {
        super(totalTime, length);
        this.metadata=metadata;
    }

    @Override
    protected SpatialMetadata getMetadata() {
        return metadata;
    }

    @Override
    protected void setKeyframeTime(KeyframeView keyframeView, double time) {
        //TODO shift keys and notify change
    }
}
