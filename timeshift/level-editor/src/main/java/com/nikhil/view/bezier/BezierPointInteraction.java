package com.nikhil.view.bezier;

import javafx.scene.input.MouseEvent;

/**
 * Callbacks for mouse press-drag-release events on a bezier point
 * Created by NikhilVerma on 26/11/15.
 */
public interface BezierPointInteraction {
    /**
     * Called when a bezier point is about to be changed (mouse pressed)
     * @param interactiveBezierPoint the interactive bezier point which is about to change
     * @param mouseEvent mouse press event that triggered this callback
     */
    void bezierPointBeganChanging(InteractiveBezierPoint interactiveBezierPoint,MouseEvent mouseEvent);
    /**
     * Called when a bezier point is currently changing(mouse dragged). It is the responsibility of the delegate
     * to ensure that the cubic curve of the graphical bezier point is updated with the next bezier point.
     * @param interactiveBezierPoint the interactive bezier point which is currently change
     * @param mouseEvent mouse drag event that triggered this callback
     */
    void bezierPointChanging(InteractiveBezierPoint interactiveBezierPoint,MouseEvent mouseEvent);
    /**
     * Called when a bezier point is finished changing(mouse released)
     * @param interactiveBezierPoint the interactive bezier point which just finished changing
     * @param mouseEvent mouse release event that triggered this callback
     */
    void bezierPointFinishedChanging(InteractiveBezierPoint interactiveBezierPoint,MouseEvent mouseEvent);
}
