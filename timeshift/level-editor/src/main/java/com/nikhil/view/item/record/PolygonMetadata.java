package com.nikhil.view.item.record;

import com.nikhil.controller.ItemViewController;
import com.nikhil.controller.PolygonViewController;

/**
 * Created by NikhilVerma on 13/10/15.
 */
public class PolygonMetadata extends Metadata{
    private PolygonViewController polygonViewController;

    public PolygonMetadata(PolygonViewController polygonViewController) {
        this.polygonViewController = polygonViewController;
    }

    @Override
    public ItemViewController getItemViewController() {
        return null;
    }
}
