package com.nikhil.view.item.record;

import com.nikhil.controller.ItemViewController;

/**
 * Created by NikhilVerma on 13/10/15.
 */
public abstract class Metadata {
    protected String name;
    protected boolean header;
    protected int tag;

    public Metadata(String name, boolean header, int tag) {
        this.name = name;
        this.header = header;
        this.tag = tag;
    }

    public abstract ItemViewController getItemViewController();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }
}
