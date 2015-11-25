package com.nikhil.timeline.change;

import com.nikhil.timeline.KeyValue;

/**
 * ChangeNode defines the changes in the values occurring in the timeline.
 * These changes in values from start to end can be handled by a change handler
 * to create vivid rigged time driven simulations.For example : animations can
 * be performed by making changes in the transformation properties of a view.
 * @author Nikhil Verma
 * Created by NikhilVerma on 10/11/15.
 */
public abstract class ChangeNode {

    /**general purpose tag that the client can safely use(not used by change node),default is -1*/
    public int tag=-1; //TODO why public,almost not required?

    protected ChangeNode next;
    protected ChangeNode previous;

    /**
     * gets the interpolated value between the starting and the ending values
     * @param startValue the starting value
     * @param endValue the ending value
     * @param progression between 0.0 to 1.0
     * @return interpolated value which will have the same dimension
     */
    public static KeyValue getInterpolatedValue(KeyValue startValue,KeyValue endValue,double progression){
        KeyValue interpolatedValue=new KeyValue(startValue);
        for(int i=0;i<interpolatedValue.getDimension();i++){
            double progressedValue=startValue.get(i)+progression*(endValue.get(i)-startValue.get(i));
            interpolatedValue.set(i, progressedValue);
        }
        return interpolatedValue;
    }

    public ChangeNode getNext() {
        return next;
    }

    public void setNext(ChangeNode next) {
        this.next = next;
    }

    public ChangeNode getPrevious() {
        return previous;
    }

    public void setPrevious(ChangeNode previous) {
        this.previous = previous;
    }

    /**
     * traverses the entire list and gives the last node in list
     * @return tail of the linked list
     */
    final public ChangeNode getLastNode(){
        ChangeNode t=this;
        ChangeNode lastNode;
        //traverse till end
        do{
            //preserve the node before
            lastNode=t;
            //go till the very end
            t=t.next;
        }while(t!=null);

        return lastNode;
    }

    /**
     * traverses the entire list and gives the first node in list
     * @return head of the linked list
     */
    final public ChangeNode getFirstNode(){
        ChangeNode t=this;
        ChangeNode firstNode;
        //traverse till beginning
        do{
            //preserve the node now
            firstNode=t;
            //go till the very beginning
            t=t.previous;
        }while(t!=null);

        return firstNode;
    }


    /**
     * inserts a node(or list of nodes) after this node in list taking care of the nodes
     * currently ahead of this node
     * @param nextNodeInList node(or list of nodes) to insert after this node in list.
     * if null, this call is ignored and returns "this" object
     * @return the last node of the argument list just inserted ,or "this" if argument is null
     */
    final public ChangeNode insertAfterMe(ChangeNode nextNodeInList) {
        if(nextNodeInList==null){
            return this;
        }
        ChangeNode lastNode=nextNodeInList.getLastNode();
        lastNode.next=this.next;
        if(this.next!=null){
            this.next.previous=lastNode;
        }
        nextNodeInList.previous=this;
        this.next=nextNodeInList;
        return lastNode;
    }

    /**
     * Removes this node from the list
     * by simply taking care of its next and previous references.
     * This does not check the head and tail references in a timeline.
     * Use {@code Timeline.remove(ChangeNode)} for those checks
     */
    final public void removeFromList(){
        if(previous!=null){
            previous.next=next;
        }
        if(next!=null){
            next.previous=previous;
        }

        //nullify existing
        previous=null;
        next=null;
    }

    /**
     * steps this change node ahead by delta time.
     * Simulation is performed by making calls to this method every delta seconds.
     *
     * @param delta timestep that was used to increment timeline forward
     * @param time the time in the timeline (inclusive of the delta timestep)
     * @return true if this change node is changed during this step,false otherwise
     */
    public abstract boolean step(double delta,double time);

    /**
     * Jumps the change node to the exact time supplied.
     *
     * @param time the time setTime needs to be made
     * @return true if a change was made, false otherwise
     */
    public abstract boolean setTime(double time);

    /**
     * computes and finds the ending time of this ChangeNode
     * @return the ending time of this ChangeNode
     */
    public abstract double findEndingTime();

}
