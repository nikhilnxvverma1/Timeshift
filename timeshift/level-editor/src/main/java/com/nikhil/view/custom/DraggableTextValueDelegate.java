package com.nikhil.view.custom;

/**
 * Delegate for every change made to the Draggable text value
 * Created by NikhilVerma on 11/10/15.
 */
public interface DraggableTextValueDelegate {
    /**
     * Called for each single step of the drag
     * @param draggableTextValue the DraggableTextValue that created this event
     * @param initialValue the very initial value before the dragging even began
     * @param oldValue the value before the step of this drag
     * @param newValue the new (and current) value after the step of this drag
     */
    void valueBeingDragged(DraggableTextValue draggableTextValue, double initialValue, double oldValue, double newValue);

    /**
     * Called once the dragging(or editing) is done
     * @param draggableTextValue the DraggableTextValue that created this event
     * @param initialValue value before the tweaking began
     * @param finalValue the final and current value of the DraggableTextValue
     * @param dragged true if this change was caused by drag ,false if the change was caused by manual entry in textfield
     */
    void valueFinishedChanging(DraggableTextValue draggableTextValue, double initialValue, double finalValue, boolean dragged);

    /**
     * Called by the draggable text value to know if field is enabled or not
     * @return true if interaction with the draggable text value should be editable ,false otherwise
     */
    boolean isEnabled();
}
