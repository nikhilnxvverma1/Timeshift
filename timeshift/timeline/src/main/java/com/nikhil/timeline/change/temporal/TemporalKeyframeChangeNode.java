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
    private TemporalKeyframe nearestAccessedKeyframe;//cache to reduce time taken when finding a keyframe at a given time

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

    public TemporalKeyframe getStart() {
        return start;
    }

    public void setStart(TemporalKeyframe start) {
        this.start = start;
    }

    public TemporalKeyframe getLast() {
        return last;
    }

    public void setLast(TemporalKeyframe last) {
        this.last = last;
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

        //new keyframe should never hold any old and/or outdated references
        newKeyframe.setNext(null);
        newKeyframe.setPrevious(null);

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

    /**
     * Finds a nearby keyframe (if any) for the given time and margin.
     * Practically, this is an O(1) operation if you are seeking through the timeline
     * because the nearest keyframe is cached, otherwise its O(n)
     * @param time time near which a keyframe needs to be found
     * @param nearByMargin defines a range around a time, between which keyframes are considered acceptable.
     *                     This must always be >=0.
     * @return a nearby keyframe if it exists within margin,else null (null, also in case of empty list)
     */
    public TemporalKeyframe findNearbyKeyframe(double time,double nearByMargin){
        if(start==null){
            return null;
        }
        
        if(nearestAccessedKeyframe==null) {
            nearestAccessedKeyframe=start;
        }

        //this is what we will return
        TemporalKeyframe nearest=null;
        double currentNearestCloseness = Math.abs(nearestAccessedKeyframe.getTime() - time);
        if (currentNearestCloseness <= nearByMargin) {
            nearest = nearestAccessedKeyframe;
        } else {

            TemporalKeyframe possiblyNearer = null;

            //search in the forward direction
            if (nearestAccessedKeyframe.getTime() + nearByMargin < time) {
                TemporalKeyframe t = nearestAccessedKeyframe.getNext();
                possiblyNearer=nearestAccessedKeyframe.getNext();
                while (t != null && (t.getTime() < time || t.getTime() - nearByMargin < time)) {
                    possiblyNearer = t;
                    t = t.getNext();
                }
            }
            //search in the backward direction
            else {
                TemporalKeyframe t = nearestAccessedKeyframe.getPrevious();
                possiblyNearer=nearestAccessedKeyframe.getPrevious();
                while (t != null && (t.getTime() > time || t.getTime() - nearByMargin > time)) {
                    possiblyNearer = t;
                    t = t.getPrevious();
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
}
