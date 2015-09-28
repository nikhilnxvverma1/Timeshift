package com.nikhil.model.pathsystem.traveller;

import com.nikhil.model.ModelElement;
import com.nikhil.model.ModelVisitor;
import com.nikhil.model.pathsystem.TravelPath;

/**
 * Simple POJO that stores the initial state of a traveller.This prevents client implementing classes of Traveller interface from
 * defining their own getters and setters everytime. This class is intended to be composed inside the implementing classes.
 * Created by NikhilVerma on 15/08/15.
 */
public class TravellerConfiguration implements ModelElement{

    private double initialIdleTimeSpent;
    private double initialDistanceTravelled;
    private TravelPath initialTravelPath;
    private double movementFactor;
    private boolean supportsComplexTransitions;

    public void setInitialIdleTimeSpent(double initialIdleTimeSpent) {
        this.initialIdleTimeSpent = initialIdleTimeSpent;
    }

    public void setInitialDistanceTravelled(double initialDistanceTravelled) {
        this.initialDistanceTravelled = initialDistanceTravelled;
    }

    public void setInitialTravelPath(TravelPath initialTravelPath) {
        this.initialTravelPath = initialTravelPath;
    }

    public void setMovementFactor(double movementFactor) {
        this.movementFactor = movementFactor;
    }

    public void setSupportsComplexTransitions(boolean supportsComplexTransitions) {
        this.supportsComplexTransitions = supportsComplexTransitions;
    }

    /**
     * Time spent on travel path before it started moving
     * @return time spend on initial path .
     */
    public double getInitialIdleTimeSpent(){
        return initialIdleTimeSpent;
    }

    /**
     * Distance travelled on the initial path
     * @return distance between 0 and 1(as the travel path is a parametric curve)
     */
    public double getInitialDistanceTravelled(){
        return initialDistanceTravelled;
    }

    /**
     * Initial path on which this traveller resides
     * @return whatever path this traveller resides in
     */
    public TravelPath getInitialTravelPath(){
        return initialTravelPath;
    }

    /**
     * Movement factor decides the direction and speed of movement of this traveller
     * @return 0 if not moving, >0 if moving forward, <0 if moving backward
     */
    public double getMovementFactor(){
        return movementFactor;
    }

    /**
     * Complex transitions include transitions such as detours ,portals,bridges
     * @return if false, only unite and simple transitions will be supported,otherwise all transitions will be supported
     *
     */
    public boolean isSupportsComplexTransitions() {
        return supportsComplexTransitions;
    }

    @Override
    public void acceptVisitor(ModelVisitor visitor) {
        visitor.visit(this);
    }
}
