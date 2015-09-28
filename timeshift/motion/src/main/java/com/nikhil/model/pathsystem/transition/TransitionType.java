package com.nikhil.model.pathsystem.transition;

/**
 * Created by NikhilVerma on 15/08/15.
 */
public enum TransitionType {
    SIMPLE,//no dedicated transition class for it,handled through shared travel points
    UNITE,
    DETOUR,
    BRIDGE,
    PORTAL
}
