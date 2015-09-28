package com.nikhil.xml;

import com.nikhil.logging.Logger;
import javafx.scene.shape.Shape;

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
    KEYFRAMES;

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
            case KEYFRAMES:
                return "keyframes";
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
            case "keyframes":
                return KEYFRAMES;
            default:
                Logger.log("No tag found for:" + element);
        }
        return null;
    }

}
