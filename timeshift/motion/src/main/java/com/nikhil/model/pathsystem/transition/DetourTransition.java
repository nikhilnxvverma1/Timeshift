package com.nikhil.model.pathsystem.transition;

/**
 * Created by NikhilVerma on 14/08/15.
 */
public class DetourTransition extends Transition{

    protected double fromDistance;
    protected boolean leftOrRight;

    public double getFromDistance() {
        return fromDistance;
    }

    public void setFromDistance(double fromDistance) {
        this.fromDistance = fromDistance;
    }

    /**
     * a toggle flag used in indicating which direction needs to be taken to enter this transition
     * (or in other words which direction to take,to take this detour)
     * @return false indicates left ,true indicates right
     */
    public boolean isLeftOrRight() {
        return leftOrRight;
    }

    /**
     * flag used in knowing which direction to take to take thie detour
     * @param leftOrRight false indicates left,true indicates right
     */
    public void setLeftOrRight(boolean leftOrRight) {
        this.leftOrRight = leftOrRight;
    }

    @Override
    public TransitionType getTransitionType() {
        return TransitionType.DETOUR;
    }
}
