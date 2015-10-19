package com.nikhil.view.item.record;

import com.nikhil.controller.ItemViewController;
import com.nikhil.controller.PolygonViewController;

/**
 * Created by NikhilVerma on 13/10/15.
 */
public class PolygonMetadata extends Metadata{
    private PolygonViewController polygonViewController;

    public PolygonMetadata(String name, int tag, PolygonViewController polygonViewController) {
        super(name, tag);
        this.polygonViewController = polygonViewController;
    }

    public PolygonMetadata(String name, boolean header, int tag, PolygonViewController polygonViewController) {
        super(name, header, tag);
        this.polygonViewController = polygonViewController;
    }

    @Override
    public ItemViewController getItemViewController() {
        return null;
    }
}
