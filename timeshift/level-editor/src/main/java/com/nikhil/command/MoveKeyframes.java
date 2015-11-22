package com.nikhil.command;

import com.nikhil.view.custom.keyframe.KeyframeTreeView;
import com.nikhil.view.custom.keyframe.KeyframeView;

import java.util.LinkedList;

/**
 * Created by NikhilVerma on 22/11/15.
 */
public class MoveKeyframes extends ActionOnKeyframes{

    private double dTime;

    /**
     * Creates a new Move keyframe command
     * @param keyframeViews list of keyframes to move
     * @param dTime delta time by which to move
     */
    public MoveKeyframes(LinkedList<KeyframeView> keyframeViews,double dTime) {
        super(keyframeViews);
        this.dTime = dTime;
    }

    /**
     * Creates a new Move keyframe command
     * @param keyframeTreeView the tree view containing all the keyframe panes
     * @param dTime delta time by which to move
     */
    public MoveKeyframes(KeyframeTreeView keyframeTreeView,double dTime) {
        super(keyframeTreeView);
        this.dTime = dTime;
    }

    @Override
    public void execute() {
        for(KeyframeView keyframeView: keyframeViews){
            double newTime = keyframeView.getTime() + dTime;
            keyframeView.setTime(newTime);
        }
    }

    @Override
    public void unexecute() {
        for(KeyframeView keyframeView: keyframeViews){
            double oldTime = keyframeView.getTime() - dTime;
            keyframeView.setTime(oldTime);
        }
    }
}
