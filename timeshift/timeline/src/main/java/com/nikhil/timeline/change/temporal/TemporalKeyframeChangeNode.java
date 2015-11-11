package com.nikhil.timeline.change.temporal;

import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.change.ChangeNode;
import com.nikhil.timeline.keyframe.TemporalKeyframe;

/**
 * A ordered linked list of Temporal keyframes that controls value changes of this change node.
 * All Temporal keyframes of the list are created and removed by this class.
 * Supplying a {@link TemporalChangeHandler} is optional
 * Created by NikhilVerma on 10/11/15.
 */
public class TemporalKeyframeChangeNode extends ChangeNode {

    private TemporalKeyframe start;
    private TemporalKeyframe last;//only needed for quickly finding the ending time
    private KeyValue currentValue;
    private TemporalChangeHandler changeHandler;

    /**
     * Creates a new TemporalKeyframeChangeNode with no change handler
     * @param dimensions dimensions of the KeyValue
     */
    public TemporalKeyframeChangeNode(int dimensions){
        this(null,dimensions);
    }

    /**
     * Creates a new TemporalKeyframeChangeNode with no change handler
     * @param initialValue the initial value of the change node
     */
    public TemporalKeyframeChangeNode(KeyValue initialValue){
        this(null,initialValue);
    }

    /**
     * Creates a new TemporalKeyframeChangeNode where the dimensions of the KeyValue are specified
     * @param changeHandler the delegate that would receive notifications for any change
     * @param dimensions dimensions of the KeyValue
     */
    public TemporalKeyframeChangeNode(TemporalChangeHandler changeHandler,int dimensions) {
        this(changeHandler,new KeyValue(dimensions));
    }

    /**
     * Creates a new TemporalKeyframeChangeNode where the initial value of the
     * "current value" are specified
     * @param changeHandler the delegate that would receive notifications for any change
     * @param initialValue initial value of this property that will be set as the current value
     */
    public TemporalKeyframeChangeNode(TemporalChangeHandler changeHandler, KeyValue initialValue) {
        this.changeHandler=changeHandler;
        this.currentValue=initialValue;
    }

    public KeyValue getCurrentValue() {
        return currentValue;
    }

    /**
     * Inserts a new Keyframe at whatever positions it should fit in w.r.t other nodes in the keyframe list.
     * This is a linear operation
     * @param newKeyframe the keyframe to insert
     * @return the same keyframe
     * @throws MismatchingKeyframeDimensionException if they of the newKeyframe does not have the same
     * dimensions as the other nodes in the list
     */
    public TemporalKeyframe addKeyframe(TemporalKeyframe newKeyframe) throws MismatchingKeyframeDimensionException{
        if(newKeyframe==null){
            return null;
        }
        if(newKeyframe.getKeyValue().getDimension()!=currentValue.getDimension()){
            //throw exception if dimensions mismatch
            throw new MismatchingKeyframeDimensionException(currentValue.getDimension(),
                    newKeyframe.getKeyValue().getDimension(),
                    newKeyframe);
        }

        if(start==null){
            start=newKeyframe;
            last=start;
        }else{
            //find a nodes whose time is just greater than new node
            TemporalKeyframe after=start;
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
     * @param keyValue the value of the keyframe
     * @return the same keyframe
     * @throws MismatchingKeyframeDimensionException if they of the key value don't have the same dimensions as
     * the other nodes in the list
     */
    public TemporalKeyframe addKeyframe(double time,KeyValue keyValue){
        return addKeyframe(new TemporalKeyframe(time,keyValue));
    }

    /**
     * Removes the specified keyframe  in the list.Does not check for containment.
     * @param keyframe the keyframe to remove
     */
    public void removeKeyframe(TemporalKeyframe keyframe){
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
    public boolean shiftKeyframe(TemporalKeyframe keyframe,double dt){
        if(keyframe==null){
            return false;
        }

        //only rearrange if required
        double newTime = keyframe.getTime() + dt;
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

    public TemporalChangeHandler getChangeHandler() {
        return changeHandler;
    }

    public void setChangeHandler(TemporalChangeHandler changeHandler) {
        this.changeHandler = changeHandler;
    }

    @Override
    public boolean step(double delta, double time) {
        //TODO
        throw new RuntimeException("Unsupported");
    }

    @Override
    public boolean jump(double time) {
        //TODO
        throw new RuntimeException("Unsupported");
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
