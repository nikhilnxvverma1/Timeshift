package com.nikhil.view.item.record;

/**
 * Created by NikhilVerma on 11/11/15.
 */
public enum MetadataTag {
    ROOT,
    HEADER,

    //Shape
    SCALE,
    ROTATION,
    TRANSLATION,
    ANCHOR_POINT,

    //Triangle
    TRIANGLE_BASE,
    TRIANGLE_HEIGHT,

    //Parallelogram
    PARALLELOGRAM_WIDTH,
    PARALLELOGRAM_HEIGHT,
    PARALLELOGRAM_SWAY_ANGLE,

    //Polygon
    POLYGON_VERTEX_HEADER,
    POLYGON_VERTEX,

    //Circle
    INNER_RADIUS,
    OUTER_RADIUS,
    START_ANGLE,
    END_ANGLE

    ;


    @Override
    public String toString() {
        switch (this){

            case ROOT:
                return "Root";
            case HEADER:
                return "Header";
            case SCALE:
                return "Scale";
            case ROTATION:
                return "Rotation";
            case TRANSLATION:
                return "Translation";
            case ANCHOR_POINT:
                return "Anchor Point";
            case TRIANGLE_BASE:
                return "Base";
            case TRIANGLE_HEIGHT:
                return "Height";
            case PARALLELOGRAM_WIDTH:
                return "Width";
            case PARALLELOGRAM_HEIGHT:
                return "Height";
            case PARALLELOGRAM_SWAY_ANGLE:
                return "Sway Angle";
            case POLYGON_VERTEX_HEADER:
                return "Vertices";
            case POLYGON_VERTEX:
                return "Vertex";
            case INNER_RADIUS:
                return "Inner Radius";
            case OUTER_RADIUS:
                return "Outer Radius";
            case START_ANGLE:
                return "Start Angle";
            case END_ANGLE:
                return "End Angle";
        }
        return null;
    }
}
