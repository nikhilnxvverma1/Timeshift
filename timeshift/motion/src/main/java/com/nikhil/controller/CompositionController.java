package com.nikhil.controller;

import com.nikhil.common.PulseListener;
import com.nikhil.controller.item.ItemModelController;
import com.nikhil.model.ModelElement;
import com.nikhil.model.ModelVisitor;
import com.nikhil.playback.SimpleTimelinePlayer;
import com.nikhil.playback.TimelinePlayer;
import com.nikhil.timeline.TimelineTerminal;
import com.nikhil.timeline.Timeline;

/**
 * Composition controller holds a single timeline
 * Each Composition has a list of item controllers
 * Created by NikhilVerma on 10/08/15.
 */
public class CompositionController implements PulseListener,TimelineTerminal,ModelElement {
    private Timeline timeline;
    private CompositionController next;
    private ItemModelController itemModelControllerStart;
    private TimelinePlayer timelinePlayer;
    private TravellerRegistrarController travellerRegistrarController;

    public CompositionController() {
    }

    public CompositionController(Timeline timeline) {
        this(timeline,new SimpleTimelinePlayer());
    }

    public CompositionController(Timeline timeline, TimelinePlayer timelinePlayer) {
        this.timeline = timeline;
        this.timelinePlayer = timelinePlayer;
        travellerRegistrarController=new TravellerRegistrarController();
    }

    public void setTimelinePlayer(TimelinePlayer timelinePlayer) {
        this.timelinePlayer = timelinePlayer;
    }

    public CompositionController getNext() {
        return next;
    }

    public void setNext(CompositionController next) {
        this.next = next;
    }

    @Override
    public void step(double delta, double totalTime){
        if(!timelinePlayer.shouldRecieveTimestep(delta,totalTime )){
            return;
        }
        //revise timestep
        double revisedDelta=timelinePlayer.getRevisedDeltaTimestep(delta);

        timeline.step((float)revisedDelta);

        //step children item controller
        ItemModelController t= itemModelControllerStart;
        while (t != null) {
            t.step(revisedDelta,timeline.getCurrentTime(),this);
            t=t.getNext();
        }

        //step the travellers
        travellerRegistrarController.step(revisedDelta,timeline.getCurrentTime(),this);
    }

    public TimelinePlayer getTimelinePlayer() {
        return timelinePlayer;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    public void addItemController(ItemModelController itemModelController) {
        if (itemModelControllerStart == null) {
            itemModelControllerStart = itemModelController;
        }else{
            itemModelControllerStart.getLast().setNext(itemModelController);
        }
    }

    /**
     * remove the specified controller from this list of item controllers
     * @param itemModelController the controller to remove
     * @return true if the removal was successful(i.e. data structure changed), false otherwise
     */
    public boolean removeItemController(ItemModelController itemModelController){

        //traverse the list and remove the node if found
        ItemModelController previous=null;
        ItemModelController t = this.itemModelControllerStart;
        while(t!=null){
            //if node is found
            if(t==itemModelController){

                //remove from the list
                if(previous==null){//first node
                    //reset start
                    itemModelControllerStart=t.getNext();
                }else{ //node in the middle
                    previous.setNext(t.getNext());
                }
                //finally break the link that this controller has with next
                t.setNext(null);
                return true; //indicate node removed(data structure changed)
            }
            previous=t;
            t=t.getNext();
        }
        return false; //if node is not found, return false
    }

    public CompositionController getLast(){
        CompositionController t=this;
        while (t.next != null) {
            t=t.next;
        }
        return t;
    }

    @Override
    public void timelineReachedTerminal(Timeline timeline) {
        //simply notify the timeline player
        timelinePlayer.didReachEnd();
    }

    public ItemModelController getItemModelControllerStart() {
        return itemModelControllerStart;
    }

    public void setItemModelControllerStart(ItemModelController itemModelControllerStart) {
        this.itemModelControllerStart = itemModelControllerStart;
    }

    /**
     * Traveller Registrar Controller is a runtime controller that is not associated with any model.
     * Its sole purpose is to contain the list of "runtime" TravellerControllers that are used for
     * supporting other Item Controllers.
     * @return single traveller registrar instantiated in this class
     */
    public TravellerRegistrarController getTravellerRegistrarController() {
        return travellerRegistrarController;
    }

    public void traverseEachModel(){

    }

    @Override
    public void acceptVisitor(ModelVisitor visitor) {
        visitor.visit(this);
        //have all item controllers accept the visitors
        ItemModelController t= itemModelControllerStart;
        while (t != null) {
            t.acceptVisitor(visitor);
            t=t.getNext();
        }
    }

    /**
     * Checks if the specified controller exists in this composition or not
     * @param itemModelController the controller for which containment needs to be checked
     * @return true if it is contained, false otherwise.
     */
    public boolean contains(ItemModelController itemModelController){
        ItemModelController t = this.itemModelControllerStart;
        while(t!=null){
            if(t==itemModelController){
                return true;
            }
            t=t.getNext();
        }
        return false;
    }
}
