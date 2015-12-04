package com.nikhil.command.keyframe;

import com.nikhil.view.custom.keyframe.KeyframeTreeView;
import com.nikhil.view.custom.keyframe.KeyframeView;

import java.util.LinkedList;

/**
 * Created by NikhilVerma on 21/11/15.
 */
public class DeleteKeyframes extends ActionOnKeyframes{

    public DeleteKeyframes(LinkedList<KeyframeView> keyframeViews) {
        super(keyframeViews);
    }

    public DeleteKeyframes(KeyframeTreeView keyframeTreeView) {
        super(keyframeTreeView);
    }

    @Override
    public void execute() {
        for(KeyframeView keyframeView:keyframeViews){
            keyframeView.removeFromParentKeyframePane();
        }
    }

    @Override
    public void unexecute() {
        for(KeyframeView keyframeView:keyframeViews){
            keyframeView.addToParentKeyframePane();
        }
    }
}
