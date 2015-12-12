package com.nikhil.view.item.delegate;

import com.nikhil.controller.ItemViewController;


/**
 * Listener for any changes occurring to a {@link com.nikhil.view.item.ParallelogramView}.
 * Note that in most cases, these changes are by the {@link com.nikhil.editor.gizmo.ParallelogramGizmo}
 * Created by NikhilVerma on 12/12/15.
 */
public interface  ParallelogramViewDelegate extends ItemViewDelegate{
    void tweakingWidth(double oldWidth,double newWidth);
    void tweakingHeight(double oldHeight,double newHeight);
    void tweakingSwayAngle(double oldSwayAngle,double newSwayAngle);

    void finishedTweakingWidth(double initialWidth);
    void finishedTweakingHeight(double initialHeight);
    void finishedTweakingSwayAngle(double initialSwayingAngle);
}
