package com.nikhil.model;

import com.nikhil.timeline.change.ChangeNodeIterator;

/**
 * Created by NikhilVerma on 18/10/15.
 */
public abstract class ItemModel {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract ChangeNodeIterator changeNodeIterator();
}
