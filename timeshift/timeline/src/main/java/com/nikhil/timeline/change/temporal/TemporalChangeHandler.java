package com.nikhil.timeline.change.temporal;

/**
 * Change handler specifically for temporal value changes.
 * Created by NikhilVerma on 11/11/15.
 */
public interface TemporalChangeHandler {
    /**
     * callback for a temporal change which necessitates
     * the model to change accordingly.
     * @param changeNode the temporal change node that invoked this call
     *                   Use {@code changeNode.getCurrentValue()} to get
     *                   the current value.
     */
    void valueChanged(TemporalKeyframeChangeNode changeNode);
}
