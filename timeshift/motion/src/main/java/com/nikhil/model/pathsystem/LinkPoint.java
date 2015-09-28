package com.nikhil.model.pathsystem;

import com.nikhil.model.ModelElement;
import com.nikhil.util.modal.UtilPoint;

/**
 * A link point connects two travel points. A series of alternate link point and travel point make up the "Alt" list.
 * Created by NikhilVerma on 15/08/15.
 */
public abstract class LinkPoint implements ModelElement {
    protected UtilPoint conrolPointWithPrevious;
    protected UtilPoint anchorPoint;
    protected UtilPoint controlPointWithNext;

    protected TravelPath pathBefore;
    protected TravelPath pathAfter;

    public UtilPoint getConrolPointWithPrevious() {
        return conrolPointWithPrevious;
    }
    public void setConrolPointWithPrevious(UtilPoint conrolPointWithPrevious) {
        this.conrolPointWithPrevious = conrolPointWithPrevious;
    }
    public UtilPoint getAnchorPoint() {
        return anchorPoint;
    }
    public void setAnchorPoint(UtilPoint anchorPoint) {
        this.anchorPoint = anchorPoint;
    }
    public UtilPoint getControlPointWithNext() {
        return controlPointWithNext;
    }
    public void setControlPointWithNext(UtilPoint controlPointWithNext) {
        this.controlPointWithNext = controlPointWithNext;
    }
    public TravelPath getPathBefore() {
        return pathBefore;
    }
    public void setPathBefore(TravelPath pathBefore) {
        this.pathBefore = pathBefore;
    }
    public TravelPath getPathAfter() {
        return pathAfter;
    }
    public void setPathAfter(TravelPath pathAfter) {
        this.pathAfter = pathAfter;
    }

}
