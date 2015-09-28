package com.nikhil.controller;

/**
 * Runtime controller which gets created in the composition controller,
 * this controller is used to register all traveller controllers which need to get pulsed.
 * A traveller controller should not be pulsed by any other controller otherwise traveller collisions will not be detected.
 * Register traveller controllers here and keep reference to it.
 * Created by NikhilVerma on 17/08/15.
 */
public class TravellerRegistrarController {

    private TravellerController travellerControllerStart;

    /**
     * step method call , parent should call this method to register a pulse to this controller subtree
     * @param delta increment in time since last pulse
     * @param totalTime total time in timeline . this can always go up and down because its timeline time
     * @param parent parent controller that called this method
     */
    public void step(double delta, double totalTime,CompositionController parent) {
        //step all the children traveller controllers
        TravellerController t=travellerControllerStart;
        while (t != null) {
            t.step(delta,totalTime,this, 0);
            t=t.getNext();
        }
    }

    public TravellerController getTravellerControllerStart() {
        return travellerControllerStart;
    }

    public void setTravellerControllerStart(TravellerController travellerControllerStart) {
        this.travellerControllerStart = travellerControllerStart;
    }

    /**
     * Register a new traveller controller to the registrar. Does not check if the said controller already exists in list
     * @param travellerController the traveller controller to register
     */
    public void registerTravellerController(TravellerController travellerController){
        travellerController.setNext(travellerControllerStart);
//        travellerController.setPrevious(travellerController);
        travellerControllerStart=travellerController;
    }

    /**
     * removes a registered traveller controllers
     * @param travellerController the controller to deregister if it exists in list
     * @return the same travller controller deregistered , if it doesn't exist it returns null
     */
    public TravellerController deregisterTravellerController(TravellerController travellerController){
        //search for the traveller controller;
        TravellerController found=null;
        TravellerController t=travellerControllerStart;
        while (t != null) {

            //on finding this node
            if(t==travellerController){
                found=t;

                //remove this node from the doubly linked list
                if (t == travellerControllerStart) {
                    travellerControllerStart=t.getNext();
                    if(travellerController.getNext()!=null){
//                        travellerController.getNext().setPrevious(null);
                    }
                }else{
//                    t.getPrevious().setNext(t.getNext());
                    if (t != null) {
//                        t.getNext().setPrevious(t.getPrevious());
                    }
                }
                break;
            }
            t=t.getNext();
        }
        return found;
    }
}
