package com.nikhil.model.pathsystem.traveller;

import com.nikhil.model.pathsystem.TravelPath;

/**
 * Defines every item that can travel on a path
 * Created by NikhilVerma on 10/08/15.
 */
public interface Traveller {

    /**
     * {@code TravellerConfiguration} is intended to be contained inside the implementing class.
     * Implementing classes should never return new copies of this class rather the same object every time.
     * This ensures that any changes made in configuration to that object get carried over to this traveller .
     * In other words, this method is almost like a getter for TravellerConfiguration object that would reside
     * as a property in the client class.
     * @return Single traveller configuration holding the initial configuration.
     */
    public TravellerConfiguration getTravellerConfiguration();

    /**
     * Callback for every time this traveller is moved on the travel paths
     * @param movingOn the current path the traveller is moving on
     * @param distanceOnPath distance travelled on current path, between 0 and 1.0
     */
    public void movedTo(TravelPath movingOn,double distanceOnPath);

    /**
     * Callback for traveller to know whenever this traveller transitions
     * @param fromTravelPath old path
     * @param toTravelPath new path
     */
    public void transitioned(TravelPath fromTravelPath,TravelPath toTravelPath);

    /**
     * Callback for traveller to know whenever this traveller reaches end of the path system
     * @param terminatingPath path at which this traveller ends
     * @param movingForward true if moving forward, false otherwise
     */
    public void reachedTerminalPath(TravelPath terminatingPath,boolean movingForward);

    /**
     * Callback for collision detection with other travellers on route
     * @param other the traveller that this traveller collided against
     */
    public void collidedWith(Traveller other);
}
