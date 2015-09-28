package com.nikhil.model.pathsystem;

/**
 * Created by NikhilVerma on 10/08/15.
 */
public class TravelPathItem {
    protected  TravelPath travelPath;
    protected  double distanceOnPath;

    private TravelPathItem next;

    public TravelPathItem getNext() {
        return next;
    }

    public void setNext(TravelPathItem next) {
        this.next = next;
    }
}
