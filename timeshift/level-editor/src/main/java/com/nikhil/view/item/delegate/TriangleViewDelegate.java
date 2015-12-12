package com.nikhil.view.item.delegate;

import com.nikhil.editor.gizmo.CircleGizmo;
import com.nikhil.editor.gizmo.TriangleGizmo;
import com.nikhil.view.item.CircleView;
import com.nikhil.view.item.TriangleView;

/**
 * Listener for any changes occurring to a {@link TriangleView}.
 * Note that in most cases, these changes are by the {@link TriangleGizmo}
 * Created by NikhilVerma on 11/12/15.
 */
public interface TriangleViewDelegate extends ItemViewDelegate{
    void tweakingBase(double oldBase,double newBase);
    void tweakingHeight(double oldHeight,double newHeight);
    void finishedTweakingBase(double initialBase);
    void finishedTweakingHeight(double initialHeight);
    
}
