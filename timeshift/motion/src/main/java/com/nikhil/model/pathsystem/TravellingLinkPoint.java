package com.nikhil.model.pathsystem;

import com.nikhil.model.ModelVisitor;
import com.nikhil.model.pathsystem.transition.DetourTransition;
import com.nikhil.model.pathsystem.transition.Transition;
import com.nikhil.model.pathsystem.transition.TransitionType;
import com.nikhil.model.pathsystem.transition.UniteTransition;
import com.nikhil.model.pathsystem.traveller.Traveller;
import com.nikhil.model.pathsystem.traveller.TravellerConfiguration;

/**
 * Created by NikhilVerma on 15/08/15.
 */
public class TravellingLinkPoint extends LinkPoint implements Traveller{

    private TravelPath freeEndPointSittingOnPath;
    private boolean freeEndPointIsLastEndPoint;

    private TravellerConfiguration travellerConfiguration;

    public TravellingLinkPoint(TravelPath freeEndPointSittingOnPath, boolean freeEndPointIsLastEndPoint,
                               double initialIdleTime, double freeEndPointLyingAtDistance) {
        this.freeEndPointSittingOnPath = freeEndPointSittingOnPath;
        this.freeEndPointIsLastEndPoint = freeEndPointIsLastEndPoint;
        travellerConfiguration=new TravellerConfiguration();
        travellerConfiguration.setInitialDistanceTravelled(freeEndPointLyingAtDistance);
        travellerConfiguration.setInitialIdleTimeSpent(initialIdleTime);
        travellerConfiguration.setInitialTravelPath(freeEndPointSittingOnPath);
        travellerConfiguration.setMovementFactor(1);
    }

    public TravellingLinkPoint(){
        travellerConfiguration=new TravellerConfiguration();
    }

    public TravelPath getFreeEndPointSittingOnPath() {
        return freeEndPointSittingOnPath;
    }

    public void setFreeEndPointSittingOnPath(TravelPath freeEndPointSittingOnPath) {
        this.freeEndPointSittingOnPath = freeEndPointSittingOnPath;
    }

    /**
     * if true this travel point is the second(and last) travel point of the path,if false its the first
    */
    public boolean isFreeEndPointIsLastEndPoint() {

        return freeEndPointIsLastEndPoint;
    }

    public void setFreeEndPointIsLastEndPoint(boolean freeEndPointIsLastEndPoint) {
        this.freeEndPointIsLastEndPoint = freeEndPointIsLastEndPoint;
    }

    public void setTravellerConfiguration(TravellerConfiguration travellerConfiguration) {
        this.travellerConfiguration = travellerConfiguration;
    }

    @Override
    public TravellerConfiguration getTravellerConfiguration() {
        return travellerConfiguration;
    }

    @Override
    public void movedTo(TravelPath movingOn, double distanceOnPath) {
        //change the "distanceInTargetPath" of all the UNITE transition of the path before for this link point
        Transition uniteTransition=pathBefore.getTransitionStart();
        while (uniteTransition != null) {
            if (uniteTransition.getTransitionType() == TransitionType.UNITE) {
                ((UniteTransition)uniteTransition).setDistanceInTargetPath(distanceOnPath);
            }
            uniteTransition = uniteTransition.getNext();
        }

        //change the "fromDistance" of all the Detour transition of the path this link point is moving on
        Transition detourTransition=movingOn.getTransitionStart();
        while (detourTransition!= null) {
            if (detourTransition.getTransitionType() == TransitionType.DETOUR) {
                ((DetourTransition)detourTransition).setFromDistance(distanceOnPath);
            }
            detourTransition= detourTransition.getNext();
        }

    }

    @Override
    public void transitioned(TravelPath fromTravelPath, TravelPath toTravelPath) {

        //change the "toPath" of all the UNITE transition of the path before for this link point
        Transition uniteTransition=pathBefore.getTransitionStart();
        while (uniteTransition != null) {
            if (uniteTransition.getTransitionType() == TransitionType.UNITE) {
                uniteTransition.setToPath(toTravelPath);
            }
            uniteTransition = uniteTransition.getNext();
        }

        //extract and remove all detour transitions from the old path(before transition)
        //and put in the list of transitions in the new path(after transition)
        Transition detourTransition=fromTravelPath.getTransitionStart();
        Transition previous=null;
        while (detourTransition != null) {
            if (detourTransition.getTransitionType() == TransitionType.DETOUR) {
                //remove from fromTravelPath's list
                if (previous == null) {
                    fromTravelPath.setTransitionStart(detourTransition.getNext());
                }else{
                    previous.setNext(detourTransition.getNext());
                }
                //add to the toTravelPath's list
                detourTransition.setFromPath(toTravelPath);
                toTravelPath.addTransition(detourTransition);
            }
            previous=detourTransition;
            detourTransition = detourTransition.getNext();
        }
    }

    @Override
    public void reachedTerminalPath(TravelPath terminatingPath, boolean movingForward) {

    }

    @Override
    public void collidedWith(Traveller other) {

    }

    @Override
    public void acceptVisitor(ModelVisitor visitor) {
        visitor.visit(this);
    }
}
