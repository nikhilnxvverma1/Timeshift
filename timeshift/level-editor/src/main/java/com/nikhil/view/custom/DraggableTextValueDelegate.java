package com.nikhil.view.custom;

/**
 * Created by NikhilVerma on 11/10/15.
 */
public interface DraggableTextValueDelegate {
    void valueBeingDragged(DraggableTextValue draggableTextValue, double newValue);
    void valueFinishedChanging(DraggableTextValue draggableTextValue, double finalValue);
}
