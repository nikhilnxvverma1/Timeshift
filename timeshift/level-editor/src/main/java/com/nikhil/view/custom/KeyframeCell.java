package com.nikhil.view.custom;

import com.nikhil.view.item.record.Metadata;
import javafx.scene.control.TreeCell;

/**
 * Created by NikhilVerma on 25/10/15.
 */
public class KeyframeCell extends TreeCell<Metadata> {
    @Override
    protected void updateItem(Metadata metadata, boolean empty) {
        super.updateItem(metadata, empty);
        this.setDisclosureNode(null);
        this.setGraphicTextGap(0);

        if (empty || metadata == null) {
            this.setText(null);
            this.setGraphic(null);
        } else {
            this.setText(null);
            double treeWidth = this.getTreeView().getWidth();
            this.setGraphic(metadata.getKeyframePane(treeWidth));
        }
    }
}
