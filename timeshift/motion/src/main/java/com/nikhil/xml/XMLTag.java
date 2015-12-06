package com.nikhil.xml;

import com.nikhil.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * XML tag for any element in the XML.
 * It is important to note that the tag strings are in lower camel case.
 *
 * Created by NikhilVerma on 26/09/15.
 */
public enum XMLTag {
    ROOT,
    COMPOSITIONS,
    COMPOSITION,
    ITEMS,
    SHAPE,
    POLYGON,
    VERTICES,
    MOVABLE_POINT,
    SCALE,
    ROTATION,
    TRANSLATION,
    ANCHOR_POINT,
    POSITION,
    X,
    Y,
    BEZIER_POINT,
    BEZIER_ANCHOR_POINT,
    PREVIOUS_CONTROL_POINT,
    NEXT_CONTROL_POINT,
    KEY_VALUE,
    COMPONENT,
    TEMPORAL_KEYFRAMES,
    TEMPORAL_KEYFRAME,
    SPATIAL_KEYFRAMES,
    SPATIAL_KEYFRAME;

    @Override
    public String toString(){
        switch (this){
            case ROOT:
                return "root";
            case COMPOSITIONS:
                return "compositions";
            case COMPOSITION:
                return "composition";
            case ITEMS:
                return "items";
            case SHAPE:
                return "shape";
            case POLYGON:
                return "polygon";
            case VERTICES:
                return "vertices";
            case MOVABLE_POINT:
                return "movablePoint";
            case SCALE:
                return "scale";
            case ROTATION:
                return "rotation";
            case TRANSLATION:
                return "translation";
            case ANCHOR_POINT:
                return "anchorPoint";
            case POSITION:
                return "position";
            case X:
                return "x";
            case Y:
                return "y";
            case BEZIER_POINT:
                return "bezierPoint";
            case BEZIER_ANCHOR_POINT:
                return "bezierAnchorPoint";
            case PREVIOUS_CONTROL_POINT:
                return "previousControlPoint";
            case NEXT_CONTROL_POINT:
                return "nextControlPoint";
            case KEY_VALUE:
                return "keyValue";
            case COMPONENT:
                return "component";
            case TEMPORAL_KEYFRAMES:
                return "temporalKeyframes";
            case TEMPORAL_KEYFRAME:
                return "temporalKeyframe";
            case SPATIAL_KEYFRAMES:
                return "spatialKeyframes";
            case SPATIAL_KEYFRAME:
                return "spatialKeyframe";
        }
        return null;
    }

    /**
     * returns the respective tag for the corresponding string
     * @param element the string tag in lower camel case.
     * @return corresponding tag, or null if none found
     */
    public static XMLTag toTag(String element){

        switch (element){
            case "root":
                return ROOT;
            case "compositions":
                return COMPOSITIONS;
            case "composition":
                return COMPOSITION;
            case "items":
                return ITEMS;
            case "shape":
                return SHAPE;
            case "polygon":
                return POLYGON;
            case "vertices":
                return VERTICES;
            case "movablePoint":
                return MOVABLE_POINT;
            case "scale":
                return SCALE;
            case "rotation":
                return ROTATION;
            case "translation":
                return TRANSLATION;
            case "anchorPoint":
                return ANCHOR_POINT;
            case "position":
                return POSITION;
            case "x":
                return X;
            case "y":
                return Y;
            case "bezierPoint":
                return BEZIER_POINT;
            case "bezierAnchorPoint":
                return BEZIER_ANCHOR_POINT;
            case "previousControlPoint":
                return PREVIOUS_CONTROL_POINT;
            case "nextControlPoint":
                return NEXT_CONTROL_POINT;
            case "component":
                return COMPONENT;
            case "keyValue":
                return KEY_VALUE;
            case "temporalKeyframes":
                return TEMPORAL_KEYFRAMES;
            case "temporalKeyframe":
                return TEMPORAL_KEYFRAME;
            case "spatialKeyframes":
                return SPATIAL_KEYFRAMES;
            case "spatialKeyframe":
                return SPATIAL_KEYFRAME;
            default:
                Logger.log("No tag found for:" + element);
        }
        return null;
    }

    /**
     * Creates a new element tag using the supplied document
     * @param document document that is used to create the element tag
     * @return A new Element for this xml tag
     */
    public Element element(Document document){
        return document.createElement(this.toString());
    }

    /**
     * @deprecated there can be custom keyframes that may not be accounted for by this method
     * @return true if this tag is a spatial or temporal keyframe
     */
    public boolean isKeyframe(){
        return this==SPATIAL_KEYFRAME||this==TEMPORAL_KEYFRAME;
    }

}
