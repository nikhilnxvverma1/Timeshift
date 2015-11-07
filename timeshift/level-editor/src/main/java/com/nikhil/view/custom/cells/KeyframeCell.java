package com.nikhil.view.custom.cells;

import com.nikhil.view.item.record.Metadata;
import javafx.scene.control.Control;
import javafx.scene.control.TreeCell;

/**
 * Created by NikhilVerma on 25/10/15.
 */
public class KeyframeCell extends TreeCell<Metadata> {
    private static final String STYLE="-fx-border-width:1;" +
            "-fx-border-color: black;" +
            "-fx-border-style: hidden hidden solid hidden;" +
            "-fx-background-color:rgb(100,100,100)";

    public KeyframeCell() {
        this.setStyle(STYLE);
        this.setMaxWidth(Double.MAX_VALUE);
    }

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
            this.setGraphic(metadata.initKeyframePane(treeWidth));
        }
    }
}
