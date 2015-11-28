package com.nikhil.view.bezier;

/**
 * Callbacks for mouse press-drag-release events on a bezier point
 * Created by NikhilVerma on 26/11/15.
 */
public interface BezierPointInteraction {
    /**
     * Called when a bezier point is about to be changed (mouse pressed)
     * @param interactiveBezierPoint the interactive bezier point which is about to change
     */
    void bezierPointBeganChanging(InteractiveBezierPoint interactiveBezierPoint);
    /**
     * Called when a bezier point is about to be currently changing(mouse dragged)
     * @param interactiveBezierPoint the interactive bezier point which is currently change
     */
    void bezierPointChanging(InteractiveBezierPoint interactiveBezierPoint);
    /**
     * Called when a bezier point is finished changing(mouse released)
     * @param interactiveBezierPoint the interactive bezier point which just finished changing
     */
    void bezierPointFinishedChanging(InteractiveBezierPoint interactiveBezierPoint);
}
