package com.nikhil.timeline.change.spatial;

import com.nikhil.timeline.change.ChangeNode;
import com.nikhil.timeline.keyframe.SpatialKeyframe;
import com.nikhil.util.modal.UtilPoint;

/**
 * A ordered linked list of Spatial keyframes that controls value changes of this change node.
 * All Spatial keyframes of the list are created and removed by this class.
 * Supplying a change handler is optional
 * Created by NikhilVerma on 10/11/15.
 */
public class SpatialKeyframeChangeNode extends ChangeNode {

    private SpatialKeyframe start;
    private SpatialKeyframe last;//only needed for quickly finding the ending time
    private UtilPoint currentPoint=new UtilPoint();
    private SpatialChangeHandler changeHandler;

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
    }

    /**
     * shifts specified keyframe by delta time, rearranging
     * if required to maintain the order of the keyframe list
     * @param keyframe keyframe to shift
     * @param dt delta time, which can be negative or positive
     * @return true implies that a rearrangement was required, false implies rearranging keys was not required
     */
    public boolean shiftKeyframe(SpatialKeyframe keyframe,double dt){
        if(keyframe==null){
            return false;
        }

        //only rearrange if required
        double newTime = keyframe.getTime() + dt;
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
    public double findEndingTime() {
        if(last==null){
            return 0;
        }else{
            return last.getTime();
        }
    }
    public void notifyAnyChangeHandler(){
        if(changeHandler!=null){
            changeHandler.valueChanged(this);
        }
    }
}
