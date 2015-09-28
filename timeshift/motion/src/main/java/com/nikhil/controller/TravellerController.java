package com.nikhil.controller;

import com.nikhil.controller.item.ItemModelController;
import com.nikhil.controller.item.TravelPathModelController;
import com.nikhil.math.MathUtil;
import com.nikhil.model.pathsystem.TravelPath;
import com.nikhil.model.pathsystem.transition.DetourTransition;
import com.nikhil.model.pathsystem.transition.PortalTransition;
import com.nikhil.model.pathsystem.transition.Transition;
import com.nikhil.model.pathsystem.transition.TransitionType;
import com.nikhil.model.pathsystem.traveller.Traveller;

/**
 * Created by NikhilVerma on 11/08/15.
 */
public class TravellerController {

    public static final double DISTANCE_STARTS_AT=0;
    public static final double TOTAL_DISTANCE=1.0;
    protected Traveller traveller;

    /** Holds the current travel path the traveller was on in the last pulse*/
    protected TravelPath currentTravelPathOldState=null;
    /** Holds the current distance the traveller has traversed so far till the last pulse*/
    protected double distanceForCurrentTravelPathOldState=0;

    /** Holds the current travel path the traveller is on*/
    protected TravelPath currentTravelPath =null;
    /** Holds the current distance the traveller has traversed so far*/
    protected double distanceForCurrentTravelPath =0;

    private TravellerController next;

    public TravellerController(Traveller traveller) {
        this(traveller,null);
    }

    public TravellerController(Traveller traveller, CompositionController compositionController) {
        this.traveller = traveller;
        this.currentTravelPath =traveller.getTravellerConfiguration().getInitialTravelPath();
        this.distanceForCurrentTravelPath =traveller.getTravellerConfiguration().getInitialDistanceTravelled();
    }

    public Traveller getTraveller() {
        return traveller;
    }

    public void setTraveller(Traveller traveller) {
        this.traveller = traveller;
    }

    /**
     * step method call , parent should call this method to register a pulse to this controller subtree
     * @param delta increment in time since last pulse
     * @param totalTime total time in timeline . this can always go up and down because its timeline time
     * @param parent parent controller that called this method
     * @param transitionChoice useful in deciding the path to take if a multi path transition happens.
     *                         For example in detour transition,
     *                         0 means continuing on the same path its at.
     *                         1 means making transition to a new path.
     */
    public void step(double delta, double totalTime, TravellerRegistrarController parent, double transitionChoice) {

        //store the old state
        currentTravelPathOldState=currentTravelPath;
        distanceForCurrentTravelPathOldState=distanceForCurrentTravelPath;

        double deltaDistanceTravelled=getDistanceToTravel(delta,currentTravelPath,1);

        double newDistance=distanceForCurrentTravelPath+deltaDistanceTravelled;

        //check for intermediate transitions ,if supported
        Transition intermediateTransition=null;
        if (traveller.getTravellerConfiguration().isSupportsComplexTransitions()) {
            intermediateTransition=checkForIntermediateTransitions(distanceForCurrentTravelPath,newDistance,true);
        }

        if (intermediateTransition != null) {
            //make transition to intermediate path
            makeTransition(delta, deltaDistanceTravelled, true, intermediateTransition.getToPath());
        }else if (newDistance > TOTAL_DISTANCE) {
            //make a transition in the forward direction if possible
            makeTransition(delta, deltaDistanceTravelled, true, currentTravelPath.getEndingPoint().getPathAfter());
        }else if (newDistance < DISTANCE_STARTS_AT) {
            //make a transition in the backward direction if possible
            makeTransition(delta, deltaDistanceTravelled, false, currentTravelPath.getStartingPoint().getPathBefore());
        }else{
            //no transition required simply add the distance
            distanceForCurrentTravelPath=newDistance;
            //notify the traveller of the new distance on the existing path
            traveller.movedTo(currentTravelPath,distanceForCurrentTravelPath);
        }

        //report any collisions to the travellers with whom this traveller could have collided after this step
        reportApparentCollisions(parent.getTravellerControllerStart());
    }

    /**
     * check for intermediate transitions like detours and portals.
     * if suitable don't call this method for bacward moving travellers and some special kind of travellers.
     * @param oldDistance old distance of the traveller before step
     * @param newDistance new distance of the traveller.
     *                    keep in mind traveller has still not stepped yet,so this can be outside bezier boundaries.
     * @param movingForward if true traveller is moving forward,false otherwise
     * @return the first transition that occurs in the distance
     */
    protected Transition checkForIntermediateTransitions(double oldDistance,double newDistance,boolean movingForward){
        Transition firstIntermediateTransition=null;
        double bestSoFar=movingForward?-1:999;
        Transition t=currentTravelPath.getTransitionStart();
        while (t != null) {
            if (t.getTransitionType() == TransitionType.DETOUR) {

                DetourTransition detourTransition=(DetourTransition)t;
                double fromDistance = detourTransition.getFromDistance();

                if ((MathUtil.isBetween(oldDistance, newDistance, fromDistance)) &&
                        ((movingForward && fromDistance > bestSoFar) ||
                                (!movingForward && fromDistance < bestSoFar))) {

                    bestSoFar = fromDistance;
                    firstIntermediateTransition = detourTransition;
                }

            }else if(t.getTransitionType()==TransitionType.PORTAL){
                PortalTransition portalTransition=(PortalTransition)t;
                double fromDistance = portalTransition.getFromDistance();
                if ((MathUtil.isBetween(oldDistance, newDistance, fromDistance)) &&
                        ((movingForward && fromDistance > bestSoFar) ||
                                (!movingForward && fromDistance < bestSoFar))) {

                    bestSoFar = fromDistance;
                    firstIntermediateTransition = portalTransition;
                }
            }
            t = t.getNext();
        }
        return firstIntermediateTransition;
    }

    private void makeTransition(double delta, double deltaDistanceTravelled, boolean movingForward, TravelPath newPath){

        double newDistance=distanceForCurrentTravelPath+deltaDistanceTravelled;

        //prepare for transitioning to new path
        TravelPath oldPath=currentTravelPath;
        //new path is basis the direction of the transition (moving forward or backward)

        //if this traveller is about to cross its path boundaries
        if (newPath==null&&movingForward&&newDistance>=TOTAL_DISTANCE) {
            //check for UNITE transitions,if one exists, use its "toPath" as new path
            Transition t=currentTravelPath.getTransitionStart();
            while (t != null) {
                if (t.getTransitionType() == TransitionType.UNITE) {
                    newPath=t.getToPath();
                }
                t = t.getNext();
            }
        }

        if (newPath != null) {

            //find the fraction of distance that still might be needed to travel on this new path
            double difference = movingForward ?
                    newDistance - TOTAL_DISTANCE :
                    newDistance - DISTANCE_STARTS_AT;
            double fractionOfDistanceLeft = MathUtil.abs(difference / deltaDistanceTravelled);
            double deltaDistanceToTravelOnNewPath = getDistanceToTravel(delta, newPath, fractionOfDistanceLeft);

            //change
            currentTravelPath = newPath;
            distanceForCurrentTravelPath = deltaDistanceToTravelOnNewPath;

            //issue a callback to the traveller
            traveller.transitioned(oldPath, newPath);
            //after the transition ,move to callback will still be issued
            traveller.movedTo(currentTravelPath,distanceForCurrentTravelPath);
        }
        //if no path exists ahead ,terminate and issue a callback
        else {
            if (movingForward) {
                distanceForCurrentTravelPath = TOTAL_DISTANCE;
                traveller.reachedTerminalPath(currentTravelPath, true);
            }else{
                distanceForCurrentTravelPath=DISTANCE_STARTS_AT;
                traveller.reachedTerminalPath(currentTravelPath,false);
            }
        }
    }

    private double getDistanceToTravel(double delta,TravelPath path,double fractionOfDistanceToTravel){
        //for this time step compute the distance on travel path that is equivalent to the distance that would
        //be travelled on the travel path in that time
        double equivalentDistanceStep=(delta/path.getDuration())* TOTAL_DISTANCE;

        //multiply with the speed of the traveller (which can be negative too,or zero)
        double deltaDistanceTravelled=equivalentDistanceStep*
                traveller.getTravellerConfiguration().getMovementFactor()
                *fractionOfDistanceToTravel;

        return deltaDistanceTravelled;
    }

    private boolean reportApparentCollisions(TravellerController start){
        boolean atleastOneCollisionDidHappen=false;
        //go back in list for all "pulsed" controllers and check if there is a collision with any traveller so far
        TravellerController travellerControllerThatAlsoPulsed=start;
        while (travellerControllerThatAlsoPulsed != this) {

            //if collision happened
            if(didCollideWith(travellerControllerThatAlsoPulsed)){

                //make sure to make the callback to both the travellers
                travellerControllerThatAlsoPulsed.traveller.collidedWith(this.traveller);
                this.traveller.collidedWith(travellerControllerThatAlsoPulsed.traveller);
                atleastOneCollisionDidHappen=true;
            }
            travellerControllerThatAlsoPulsed=travellerControllerThatAlsoPulsed.next;
        }

        return atleastOneCollisionDidHappen;
    }

    public boolean didCollideWith(TravellerController other){
        boolean didCollide=false;
        boolean leadingBefore=true;
        boolean leadingAfter=false;
        //the travellers firstly must be on the same path,(Obviously)
        if(currentTravelPath==other.currentTravelPath) {
            //if the old paths are same
            if (currentTravelPathOldState == other.currentTravelPathOldState) {

                //check weather this traveller was leading the other traveller or not before
                if (distanceForCurrentTravelPathOldState >= other.distanceForCurrentTravelPathOldState) {
                    leadingBefore=true;
                }else{
                    leadingBefore=false;
                }

            }else {//old paths are not same but new paths are same

                //need to figure out which path(old ones) came before the other in order to know
                //which traveller is leading

                //scan throuh each transition of this travellers path(old state)
                Transition t=currentTravelPathOldState.getTransitionStart();
                leadingBefore=false;
                while (t != null) {

                    //if the transition "leads to" the travel path of the other traveller old state
                    //then it means that this traveller (old) travel path lied before the other travellers (old) travel path
                    if (t.getToPath() == other.currentTravelPathOldState) {
                        leadingBefore=true;
                        break;
                    }
                    t = t.getNext();
                }

            }
            //check if the traveller is leading the other traveller now or not
            if (distanceForCurrentTravelPath>= other.distanceForCurrentTravelPath) {
                leadingAfter=true;
            }else{
                leadingAfter=false;
            }
            //if the result is complementry ,they collided
            if (leadingBefore != leadingAfter) {
                didCollide=true;
            }
        }

        return didCollide;
    }

    private TravelPathModelController getTravelPathControllerFor(TravelPath travelPath,CompositionController parent) {
        ItemModelController t=parent.getItemModelControllerStart();
        TravelPathModelController travelPathController=null;
        while (t != null) {
            if(t.isTravelPathControllerFor(travelPath)){
                travelPathController=(TravelPathModelController)t;
                break;
            }
            t=t.getNext();
        }
        return travelPathController;
    }

    public TravellerController getNext() {
        return next;
    }

    public void setNext(TravellerController next) {
        this.next = next;
    }
}
