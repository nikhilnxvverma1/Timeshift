package com.nikhil.command;

import com.nikhil.view.item.record.TemporalMetadata;

/**
 * Created by NikhilVerma on 12/11/15.
 */
public class ToggleTemporalMetadataKeyframes implements Command{
    private TemporalMetadata temporalMetadata;
    private boolean keyframable;

    public ToggleTemporalMetadataKeyframes(TemporalMetadata temporalMetadata,boolean keyframable) {
        this.temporalMetadata = temporalMetadata;
        this.keyframable=keyframable;
    }

    @Override
    public void execute() {
        if(keyframable){
            temporalMetadata.restoreKeyframeConfiguration();
        }else{
            temporalMetadata.saveKeyframeConfiguration();
            temporalMetadata.removeAllKeyframes();
        }
    }

    @Override
    public void unexecute() {
        if(keyframable){
            temporalMetadata.saveKeyframeConfiguration();
            temporalMetadata.removeAllKeyframes();
        }else{
            temporalMetadata.restoreKeyframeConfiguration();
        }
    }
}
