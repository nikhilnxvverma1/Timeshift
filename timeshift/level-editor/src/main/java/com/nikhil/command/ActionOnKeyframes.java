package com.nikhil.command;

import com.nikhil.view.custom.keyframe.KeyframeTreeView;
import com.nikhil.view.custom.keyframe.KeyframeView;
import com.nikhil.view.item.record.Metadata;
import javafx.scene.control.TreeItem;

import java.util.LinkedList;

/**
 * Command for making a collective action on a list of keyframes.
 * For memory considerations, the list of this command is used across
 * several collective actions all of which have same keys.
 * Created by NikhilVerma on 21/11/15.
 */
public abstract class ActionOnKeyframes implements Command{
    protected LinkedList<KeyframeView> keyframeViews;

    /**
     * Creates ActionOnKeyframes command using the supplied keyframe list
     * @param keyframeViews a known keyframe view list
     */
    public ActionOnKeyframes(LinkedList<KeyframeView> keyframeViews) {
        this.keyframeViews = keyframeViews;
    }

    /**
     * Creates ActionOnKeyframes command using the keyframe tree view.
     * Constructs the keyframe view list using the currently selected keyframes
     * @param keyframeTreeView keyframe tree view holding all the keyframe panes
     *                         and keyframe views
     */
    public ActionOnKeyframes(KeyframeTreeView keyframeTreeView){
        keyframeViews=keyframeTreeView.getSelectedKeys();
    }

    /**
     * This protected constructor is reserved for subclasses only.
     * Subclasses should take the responsibility of populating the linked list of keyframes
     */
    protected ActionOnKeyframes() {
    }

    /**
     * Checks if this command contains the same keyframes as the ones that are selected in the
     * supplied keyframe tree view
     * @param keyframeTreeView  the keyframe tree view which contains selected keyframes views
     * @return true if they are same, false if even one is different
     */
    public boolean containsSameKeyframesAsCurrentlySelected(KeyframeTreeView keyframeTreeView){
        //keyframe tree view should
        //1. contain same number of keyframes
        //2. same keyframes as this list
        boolean sameKeys=true;
        for(KeyframeView keyframeView: keyframeViews){
            if(!keyframeTreeView.contains(keyframeView)){
                sameKeys=false;
                break;
            }
        }
        return sameKeys && keyframeTreeView.countSelectedKeyframe()==keyframeViews.size();
    }

    /**
     * The list of keyframes for this action(should not be modified,externally)
     * @return the linked list of keyframes for this action
     */
    public LinkedList<KeyframeView> getKeyframeViews() {
        return keyframeViews;
    }
}
