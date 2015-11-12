package com.nikhil.view.custom.keyframe;

import com.nikhil.timeline.keyframe.TemporalKeyframe;
import com.nikhil.view.item.record.Metadata;
import com.nikhil.view.item.record.TemporalMetadata;

/**
 * Created by NikhilVerma on 11/11/15.
 */
public class TemporalKeyframePane extends KeyframePane{

    private TemporalMetadata metadata;

    public TemporalKeyframePane(double totalTime, double length,TemporalMetadata metadata) {
        super(totalTime, length);
        this.metadata=metadata;
    }

    @Override
    public TemporalMetadata getMetadata() {
        return metadata;
    }

    @Override
    protected void setKeyframeTime(KeyframeView keyframeView, double time) {
        //TODO shift keys and notify change
    }
}
