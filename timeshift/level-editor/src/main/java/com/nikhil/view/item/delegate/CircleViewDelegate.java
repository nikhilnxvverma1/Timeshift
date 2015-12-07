package com.nikhil.view.item.delegate;

import com.nikhil.controller.ItemViewController;
import com.nikhil.view.item.CircleView;

/**
 * Listener for any changes occurring to a {@link CircleView}
 *
 * Created by NikhilVerma on 07/12/15.
 */
public interface CircleViewDelegate extends ItemViewDelegate {
    void finishedTweakingInnerRadius(double initialInnerRadius);
    void finishedTweakingOuterRadius(double initialOuterRadius);
    void finishedTweakingStartingAngle(double initialStartingAngle);
    void finishedTweakingEndingAngle(double initialEndingAngle);
}
