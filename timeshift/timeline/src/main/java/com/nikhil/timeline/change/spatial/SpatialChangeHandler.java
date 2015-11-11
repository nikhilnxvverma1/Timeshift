package com.nikhil.timeline.change.spatial;


/**
 * Change handler specifically for spatial value changes.
 * Created by NikhilVerma on 11/11/15.
 */
public interface SpatialChangeHandler {
    /**
     * callback for a spatial change which necessitates
     * the model to change accordingly.
     * @param changeNode a spatial change node that invoked this call.
     *                   Use {@code changeNode.getCurrentPoint()} to get
     *                   the current position.
     */
    void valueChanged(SpatialKeyframeChangeNode changeNode);
}
