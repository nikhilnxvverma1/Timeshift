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

    //Polygon
    POLYGON_VERTEX_HEADER,
    POLYGON_VERTEX;

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
            case POLYGON_VERTEX_HEADER:
                return "Vertices";
            case POLYGON_VERTEX:
                return "Vertex";
        }
        return null;
    }
}
