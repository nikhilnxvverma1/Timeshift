package com.nikhil.view.item.delegate;

import com.nikhil.editor.gizmo.CircleGizmo;
import com.nikhil.view.item.CircleView;

/**
 * Listener for any changes occurring to a {@link CircleView}.
 * Note that in most cases, these changes are by the {@link CircleGizmo}
 * Created by NikhilVerma on 07/12/15.
 */
public interface CircleViewDelegate extends ItemViewDelegate {
    void tweakingInnerRadius(double oldInnerRadius,double newInnerRadius);
    void tweakingOuterRadius(double oldOuterRadius,double newOuterRadius);
    void tweakingStartAngle(double oldStartAngle,double newStartAngle);
    void tweakingEndAngle(double oldEndAngle,double newEndAngle);
    void finishedTweakingInnerRadius(double initialInnerRadius);
    void finishedTweakingOuterRadius(double initialOuterRadius);
    void finishedTweakingStartAngle(double initialStartingAngle);
    void finishedTweakingEndAngle(double initialEndingAngle);
}
