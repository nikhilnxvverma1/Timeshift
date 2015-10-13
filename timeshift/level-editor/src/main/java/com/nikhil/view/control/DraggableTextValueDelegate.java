package com.nikhil.view.control;

/**
 * Created by NikhilVerma on 11/10/15.
 */
public interface DraggableTextValueDelegate {
    void valueBeingDragged(DraggableTextValue draggableTextValue, double newValue);
    void valueFinishedChanging(DraggableTextValue draggableTextValue, double finalValue);
}
