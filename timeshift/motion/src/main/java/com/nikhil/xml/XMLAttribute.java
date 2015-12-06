package com.nikhil.xml;

/**
 * XML Attributes in an xml elements.
 * These attributes are lower camel cased(just like tags)
 *
 * Created by NikhilVerma on 05/12/15.
 */
public enum XMLAttribute {
    NAME,
    TIME,
    INDEX,
    VALUE,
    SIZE,
    X,
    Y;


    @Override
    public String toString() {
        switch (this){
            case NAME:
                return "name";
            case TIME:
                return "time";
            case INDEX:
                return "index";
            case VALUE:
                return "value";
            case SIZE:
                return "size";
            case X:
                return "x";
            case Y:
                return "y";
        }
        return null;
    }

    /**
     * Converts a string into equivalent attribute tag
     * @param attribute string for the attribute in lower camel case
     * @return equivalent attribute tag
     */
    public static XMLAttribute toAttribute(String attribute){
        switch (attribute){
            case "name":
                return NAME;
            case "time":
                return TIME;
            case "index":
                return INDEX;
            case "value":
                return VALUE;
            case "size":
                return SIZE;
            case "x":
                return X;
            case "y":
                return Y;
        }
        return null;
    }

}
