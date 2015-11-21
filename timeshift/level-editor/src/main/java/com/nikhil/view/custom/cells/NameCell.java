package com.nikhil.view.custom.cells;

import com.nikhil.view.item.record.Metadata;
import javafx.scene.control.TreeTableCell;

/**
 * Created by NikhilVerma on 12/11/15.
 */
public class NameCell  extends TreeTableCell<Metadata, Metadata> {

    @Override
    protected void updateItem(Metadata metadata, boolean empty) {
        super.updateItem(metadata, empty);
        if (empty || metadata == null) {
            this.setText(null);
            this.setGraphic(null);
        } else {
            this.setText(metadata.nameProperty().getValue());
            this.setGraphic(metadata.getNameNode());
        }
    }
}