package com.nikhil.editor.tool;

/**
 * Created by NikhilVerma on 23/08/15.
 */
public enum ToolType {
    SELECTION,
    POINT_SELECT,
    CIRCLE,
    PARALLELOGRAM,
    TRIANGLE,
    POLYGON,
    PEN,
    TRAVEL_PATH;

    @Override
    public String toString() {
        String nameOfTool=null;
        switch (this){
            case SELECTION:
                nameOfTool="Selection";
                break;
            case POINT_SELECT:
                nameOfTool="Point Select";
                break;
            case CIRCLE:
                nameOfTool="Circle";
                break;
            case PARALLELOGRAM:
                nameOfTool="Parallelogram";
                break;
            case TRIANGLE:
                nameOfTool="Isosceles Triangle";
                break;
            case POLYGON:
                nameOfTool="Polygon";
                break;
            case TRAVEL_PATH:
                nameOfTool="Travel Path";
                break;
            case PEN:
                nameOfTool="Pen";
                break;
        }
        return nameOfTool;
    }
}
