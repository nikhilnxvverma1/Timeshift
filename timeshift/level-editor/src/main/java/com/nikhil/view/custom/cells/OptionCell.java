package com.nikhil.view.custom.cells;

import com.nikhil.view.item.record.Metadata;
import javafx.scene.control.TreeTableCell;

/**
 * Created by NikhilVerma on 25/10/15.
 */
public class OptionCell extends TreeTableCell<Metadata,Metadata>{
    @Override
    protected void updateItem(Metadata metadata, boolean empty) {
        super.updateItem(metadata, empty);
        if (empty || metadata == null) {
            this.setText(null);
            this.setGraphic(null);
        } else {
            this.setText(null);
            this.setGraphic(metadata.getOptionNode());
        }
    }
}
