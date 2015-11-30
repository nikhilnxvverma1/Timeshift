package com.nikhil.timeline.change.spatial;

import com.nikhil.timeline.change.ChangeNode;
import com.nikhil.timeline.change.KeyframeChangeNode;
import com.nikhil.timeline.change.temporal.MismatchingKeyframeDimensionException;
import com.nikhil.timeline.keyframe.Keyframe;
import com.nikhil.timeline.keyframe.SpatialKeyframe;
import com.nikhil.timeline.keyframe.TemporalKeyframe;
import com.nikhil.util.modal.UtilPoint;

/**
 * A ordered linked list of Spatial keyframes that controls value changes of this change node.
 * All Spatial keyframes of the list are created and removed by this class.
 * Supplying a change handler is optional
 * Created by NikhilVerma on 10/11/15.
 */
public class SpatialKeyframeChangeNode extends KeyframeChangeNode {

    private SpatialKeyframe start;
    private SpatialKeyframe last;//only needed for quickly finding the ending time
    private UtilPoint currentPoint=new UtilPoint();
    private SpatialChangeHandler changeHandler;
    /**cache to reduce time taken when finding a keyframe at a given time*/
    private SpatialKeyframe nearestAccessedKeyframe;
    /**
     * Creates a SpatialKeyframeChangeNode with no change handler
     */
    public SpatialKeyframeChangeNode() {
        this(null);
    }

    /**
     * Creates a SpatialKeyframeChangeNode with a change handler
     * @param changeHandler the delegate that will receive notifications whenever a change happens
     */
    public SpatialKeyframeChangeNode(SpatialChangeHandler changeHandler) {
        this.changeHandler=changeHandler;
    }

    public void setCurrentPoint(double x,double y){
        this.currentPoint.set(x,y);
    }

    public UtilPoint getCurrentPoint() {
        return currentPoint;
    }


    /**
     * Inserts a new Keyframe at whatever positions it should fit in w.r.t other nodes in the keyframe list.
     * This is a linear operation
     * @param newKeyframe the keyframe to insert
     * @return the same keyframe
     */
    public SpatialKeyframe addKeyframe(SpatialKeyframe newKeyframe){
        if(newKeyframe==null){
            return null;
        }

        //new keyframe should never hold any old and/or outdated references
        newKeyframe.setNext(null);
        newKeyframe.setPrevious(null);

        if(start==null){
            start=newKeyframe;
            last=start;
        }else{
            //find a nodes whose time is just greater than new node
            SpatialKeyframe after=start;
            while(after!=null&&after.getTime()<newKeyframe.getTime()){
                after=after.getNext();
            }
            if(after==null){
                //new node is the last node
                last.setNext(newKeyframe);
                newKeyframe.setPrevious(last);
                last=newKeyframe;
            }else{
                //add before the found node
                newKeyframe.setNext(after);
                newKeyframe.setPrevious(after.getPrevious());

                //new node can also be the first node if after is start
                if(after==start){
                    start=newKeyframe;
                }else{
                    after.getPrevious().setNext(newKeyframe);
                }
                after.setPrevious(newKeyframe);
            }
        }
        return newKeyframe;
    }

    /**
     * Creatas and inserts a new Keyframe at whatever positions it should fit in w.r.t other nodes in the keyframe list.
     * This is a linear operation
     * @param time time at which the keyframe should be inserted
     * @param anchorPoint the value of the keyframe
     * @return the same keyframe
     */
    public SpatialKeyframe addKeyframe(double time,UtilPoint anchorPoint){
        return addKeyframe(new SpatialKeyframe(time,anchorPoint));
    }

    /**
     * Removes the specified keyframe  in the list.Does not check for containment.
     * @param keyframe the keyframe to remove
     */
    public void removeKeyframe(SpatialKeyframe keyframe){
        if(keyframe==null){
            return;
        }
        if(keyframe.getPrevious()==null){//first node
            if(keyframe.getNext()==null){//loner node
                start=null;
                last=null;
            }else{
                keyframe.getNext().setPrevious(null);
                start=keyframe.getNext();
            }
        }else if(keyframe.getNext()==null){//last node
            //loner node condition has already been covered above
            keyframe.getPrevious().setNext(null);
            last=keyframe.getPrevious();
        }else{//middle node
            keyframe.getPrevious().setNext(keyframe.getNext());
            keyframe.getNext().setPrevious(keyframe.getPrevious());
        }

        //if the removed keyframe is the nearest accessed keyframe, change it
        if(keyframe==nearestAccessedKeyframe){
            changeNearestAccessedKeyframe();
        }
    }

    /**
     * Changes the "cached" keyframe to the next keyframe if one exists.
     * If a next doesn't exist and a previous keyframe exists,cache becomes
     * that keyframe, otherwise it cache becomes null.
     */
    protected void changeNearestAccessedKeyframe(){
        if(nearestAccessedKeyframe.getNext()!=null){
            nearestAccessedKeyframe=nearestAccessedKeyframe.getNext();
        }else if(nearestAccessedKeyframe.getPrevious()!=null){
            nearestAccessedKeyframe=nearestAccessedKeyframe.getPrevious();
        }else{
            nearestAccessedKeyframe=null;
        }
    }


    /**
     * shifts specified keyframe to a new time, rearranging
     * if required to maintain the order of the keyframe list
     * @param keyframe keyframe to shift
     * @param newTime new time of the keyframe
     * @return true implies that a rearrangement was required (which is a linear operation),
     * false implies rearranging keys was not required (which is a constant operation)
     */
    public boolean shiftKeyframe(SpatialKeyframe keyframe,double newTime){
        if(keyframe==null){
            return false;
        }

        //only rearrange if required
        keyframe.setTime(newTime);
        if (
                ((keyframe.getNext() != null) && (newTime > keyframe.getNext().getTime())) ||
                        ((keyframe.getPrevious() != null) && (newTime < keyframe.getPrevious().getTime()))
                ) {
            //remove from list
            removeKeyframe(keyframe);//O(1)
            //add it back again
            addKeyframe(keyframe);//O(n)
            return true;
        }else{
            return false;
        }
    }

    public SpatialChangeHandler getChangeHandler() {
        return changeHandler;
    }

    public void setChangeHandler(SpatialChangeHandler changeHandler) {
        this.changeHandler = changeHandler;
    }

    @Override
    public boolean step(double delta, double time) {
        //TODO
        return false;
    }

    @Override
    public void setTime(double time) {
        //TODO
    }

    @Override
    public Keyframe getStart() {
        return start;
    }

    @Override
    public Keyframe getLast() {
        return last;
    }

    @Override
    public SpatialKeyframe findNearbyKeyframe(double time, double nearByMargin) {
        if(start==null){
            return null;
        }

        if(nearestAccessedKeyframe==null) {
            nearestAccessedKeyframe=start;
        }

        //this is what we will return
        SpatialKeyframe nearest=null;
        double currentNearestCloseness = Math.abs(nearestAccessedKeyframe.getTime() - time);
        if (currentNearestCloseness <= nearByMargin) {
            nearest = nearestAccessedKeyframe;
        } else {

            SpatialKeyframe possiblyNearer = null;

            //search in the forward direction
            if (nearestAccessedKeyframe.getTime() + nearByMargin < time) {
                SpatialKeyframe t = nearestAccessedKeyframe.getNext();
                possiblyNearer=nearestAccessedKeyframe.getNext();
                while (t != null && (t.getTime() < time || t.getTime() - nearByMargin < time)) {
                    possiblyNearer = t;
                    t = t.getNext();
                }
                if(t!=null){
                    possiblyNearer=t;
                }
            }
            //search in the backward direction
            else {
                SpatialKeyframe t = nearestAccessedKeyframe.getPrevious();
                possiblyNearer=nearestAccessedKeyframe.getPrevious();
                while (t != null && (t.getTime() > time || t.getTime() - nearByMargin > time)) {
                    possiblyNearer = t;
                    t = t.getPrevious();
                }
                if(t!=null){
                    possiblyNearer=t;
                }
            }

            //update nearestAccessedKeyframe only if it is closer
            if (possiblyNearer != null) {
                double closeness = Math.abs(possiblyNearer.getTime() - time);
                if (closeness < currentNearestCloseness) {
                    nearestAccessedKeyframe = possiblyNearer;

                    //also if it less than the nearby margin ,this becomes the return value
                    if (closeness <= nearByMargin) {
                        nearest = possiblyNearer;
                    }
                }
            }
        }
        return nearest;
    }

    public void notifyAnyChangeHandler(){
        if(changeHandler!=null){
            changeHandler.valueChanged(this);
        }
    }
}
