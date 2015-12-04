package com.nikhil.command.keyframe;

import com.nikhil.view.custom.keyframe.KeyframePane;
import com.nikhil.view.custom.keyframe.KeyframeTreeView;
import com.nikhil.view.custom.keyframe.KeyframeView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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
        shiftKeyframesBy(dTime);
    }

    @Override
    public void unexecute() {
        shiftKeyframesBy(-dTime);
    }

    private void shiftKeyframesBy(double dTime) {
        //maintain a map that keeps track of all keyframes panes that are affected by this movement
        Map<KeyframePane,Integer> affectedKeyframePanes=new HashMap<>();

        //set time for each keyframe
        for(KeyframeView keyframeView: keyframeViews){
            double newTime = keyframeView.getTime() + dTime;
            keyframeView.setTime(newTime);

            //keep track of how many keyframes exist in each keyframe pane
            final KeyframePane keyframePane = keyframeView.getKeyframePane();
            Integer keyframesMoved = affectedKeyframePanes.get(keyframePane);
            if(keyframesMoved==null){
                affectedKeyframePanes.put(keyframePane, 1);
            }else{
                affectedKeyframePanes.put(keyframePane, keyframesMoved + 1);
            }
        }

        //finally notify the keyframe pane by iterating over the map
        for(Map.Entry<KeyframePane,Integer> entry:affectedKeyframePanes.entrySet()){
            final KeyframePane keyframePane = entry.getKey();
            final Integer totalKeyframeMoved= entry.getValue();
            keyframePane.keyframesMoved(totalKeyframeMoved);
        }
    }
}
