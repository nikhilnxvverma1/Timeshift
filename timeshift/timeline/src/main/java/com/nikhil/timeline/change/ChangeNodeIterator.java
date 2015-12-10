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

    /**
     * Creates a new ChangeNode iterator which comprises of change nodes from another iterator and
     * additional array of change nodes.
     * @param copyFrom the iterator to copy first set of change nodes from.This iterator itself will not be changed.
     * @param additional array of change nodes that need to be lumped on to the copied change nodes.
     */
    public ChangeNodeIterator(ChangeNodeIterator copyFrom,ChangeNode[] additional){
        int total=copyFrom.changeNodes.length+additional.length;
        this.changeNodes=new ChangeNode[total];
        int count=0;
        for (int i = 0; i < copyFrom.changeNodes.length; i++) {
            this.changeNodes[count++]=copyFrom.changeNodes[i];
        }
        for (int i = 0; i < additional.length; i++) {
            this.changeNodes[count++]=additional[i];
        }
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
