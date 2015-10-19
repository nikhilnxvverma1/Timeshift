package com.nikhil.view.custom;

import com.nikhil.controller.ItemViewController;
import com.nikhil.view.item.record.Metadata;
import javafx.scene.control.Button;
import javafx.scene.control.TreeTableCell;

/**
 * Created by NikhilVerma on 19/10/15.
 */
public class ItemTableValueCell extends TreeTableCell<Metadata, Metadata> {

    @Override
    protected void updateItem(Metadata metadata, boolean empty) {
        super.updateItem(metadata, empty);
        if (empty || metadata == null) {
            this.setText(null);
            this.setGraphic(null);
        } else {
            //get the item view controller for proper communication
            this.setText(null);
            this.setGraphic(metadata.getValueNode());
        }
    }
}
