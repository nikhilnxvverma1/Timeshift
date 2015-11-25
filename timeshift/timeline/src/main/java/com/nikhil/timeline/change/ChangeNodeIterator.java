package com.nikhil.timeline.change;

/**
 * Simple iterator that takes an array of change nodes and provides the interface to iterate over them.
 * Created by NikhilVerma on 25/11/15.
 */
public class ChangeNodeIterator {

    private ChangeNode[] changeNodes;
    int cursor;

    public ChangeNodeIterator(ChangeNode[] changeNodes) {
        this.changeNodes = changeNodes;
    }

    public boolean hasNext(){
        return (changeNodes != null) && (cursor < changeNodes.length);
    }
    public ChangeNode next(){
        return hasNext()?changeNodes[cursor++]:null;
    }
    public int size(){
        return changeNodes!=null?changeNodes.length:0;
    }
}
