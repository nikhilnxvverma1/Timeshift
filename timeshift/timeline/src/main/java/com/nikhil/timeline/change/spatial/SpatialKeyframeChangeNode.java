package com.nikhil.timeline.change.spatial;

import com.nikhil.space.bezier.path.BezierPoint;
import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.change.ChangeNode;
import com.nikhil.timeline.change.KeyframeChangeNode;
import com.nikhil.timeline.change.temporal.MismatchingKeyframeDimensionException;
import com.nikhil.timeline.interpolation.InterpolationCurve;
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
    private final UtilPoint currentPoint=new UtilPoint();
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
        //get to the keyframe just before the current time
        SpatialKeyframe keyframeBefore = findKeyframeBefore(time);
        if(keyframeBefore!=null){

            //time is between two keyframes
            if(keyframeBefore.getNext()!=null){

                SpatialKeyframe keyframeAfter = keyframeBefore.getNext();

                //find out how much time has progressed from the keyframe before (this will be between 0.0 to 1.0)
                double progressTillNext=(time-keyframeBefore.getTime())/
                        (keyframeAfter.getTime()-keyframeBefore.getTime());

                //get the interpolated progress for the above progress using the interpolation curve
                InterpolationCurve interpolation= keyframeBefore.getInterpolationWithNext();
                double interpolatedProgress= interpolation.valueFor(progressTillNext);

                //find and then set the interpolated point b/w the two keyframes using the interpolated progress
                final UtilPoint interpolatedPointBetween = BezierPoint.getInterpolatedPointBetween(
                        keyframeBefore.getBezierPoint(),
                        keyframeAfter.getBezierPoint(),
                        interpolatedProgress);
                currentPoint.set(interpolatedPointBetween);

            }
            //time is beyond the last keyframe
            else{
                //the current value is the value of the last keyframe
                currentPoint.set(keyframeBefore.getBezierPoint().getAnchorPoint());
            }
        }
        //time is before even the first keyframe
        else if(!isEmpty()){
            //the value is the value of the first keyframe
            currentPoint.set(start.getBezierPoint().getAnchorPoint());
        }//else there are no keyframes in which case , the model remains unaffected

        notifyAnyChangeHandler();
    }

    /**
     * Finds the keyframe exactly before the specified time (using cache)
     * @param time the time exactly after the keyframe that needs to be found
     * @return spatial keyframe ,which might be null in case there are no keyframes
     * or time is before start
     */
    protected SpatialKeyframe findKeyframeBefore(double time){
        if(isEmpty()){
            return null;
        }else if(nearestAccessedKeyframe==null){
            nearestAccessedKeyframe=last;
        }

        SpatialKeyframe keyframeBeforeTime=null;

        //the keyframe could lie after the time
        if(nearestAccessedKeyframe.getTime()>time){

            //keep going back until we reach exactly the keyframe where its time is before the supplied time
            while((nearestAccessedKeyframe!= null) &&
                    (nearestAccessedKeyframe.getTime() > time)){
                nearestAccessedKeyframe=nearestAccessedKeyframe.getPrevious();
            }

            //its possible that we reach the starting keyframe and the supplied time is before that
            if(nearestAccessedKeyframe!=null){
                //but in case we don't, just go to the next node, (because this node is the failing node)

//                nearestAccessedKeyframe=nearestAccessedKeyframe.getNext();
                keyframeBeforeTime=nearestAccessedKeyframe;
            }

        }
        //or the keyframe can lie before the time
        else{
            //keep going forward until we reach exactly the keyframe
            //where its time is after the supplied time or till we reach the last keyframe
            while((nearestAccessedKeyframe.getNext() != null) &&
                    (nearestAccessedKeyframe.getNext().getTime() < time)){
                nearestAccessedKeyframe=nearestAccessedKeyframe.getNext();
            }
            keyframeBeforeTime=nearestAccessedKeyframe;
        }
        return keyframeBeforeTime;
    }

    @Override
    public SpatialKeyframe getStart() {
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

    /**
     * Sets the list of keyframes using the references to start and last
     * nodes of the linked list. Care should be taken(by the caller) to ensure
     * that both the nodes belong to the same list and that the list is valid.
     * @param start starting node of the list
     * @param last last node of the list
     */
    public void setKeyframes(SpatialKeyframe start,SpatialKeyframe last){
        this.start=start;
        this.last=last;
        this.nearestAccessedKeyframe=null;
        if (start!=null) {
            this.currentPoint.set(start.getBezierPoint().getAnchorPoint());
        }
    }
}
