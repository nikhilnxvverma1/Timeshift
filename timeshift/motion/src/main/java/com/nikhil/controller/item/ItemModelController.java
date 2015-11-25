package com.nikhil.controller.item;

import com.nikhil.common.Observer;
import com.nikhil.controller.CompositionController;
import com.nikhil.model.ItemModel;
import com.nikhil.model.ModelElement;
import com.nikhil.model.pathsystem.TravelPath;
import com.nikhil.timeline.Timeline;

/**
 * Item controllers hold model
 * Created by NikhilVerma on 10/08/15.
 */
public abstract class ItemModelController implements ModelElement,Observer{
    protected ItemModelController next;

    public ItemModelController getNext() {
        return next;
    }

    public void setNext(ItemModelController next) {
        this.next = next;
    }

    public ItemModelController getLast(){
        ItemModelController t=this;
        while (t.next != null) {
            t=t.next;
        }
        return t;
    }

    public boolean isTravelPathControllerFor(TravelPath travelPath){
        return false;
    }

    /**
     * step method call , parent should call this method to register a pulse to this controller subtree
     * @param delta increment in time since last pulse
     * @param totalTime total time in timeline . this can always go up and down because its timeline time
     * @param parent parent controller that called this method
     */
    public abstract void step(double delta,double totalTime,CompositionController parent);


    public abstract ItemModel getItemModel();
}
