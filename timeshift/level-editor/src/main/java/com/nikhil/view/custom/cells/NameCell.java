package com.nikhil.view.custom.cells;

import com.nikhil.command.ChangeItemName;
import com.nikhil.logging.Logger;
import com.nikhil.view.item.record.Metadata;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;
import javafx.scene.input.KeyCode;

/**
 * Custom name cell useful for showing item names and properties.
 * This custom cell takes care of changing item names.
 * Created by NikhilVerma on 12/11/15.
 */
public class NameCell extends TreeTableCell<Metadata, Metadata> {

    private TextField textField;

    @Override
    public void updateItem(Metadata metadata, boolean empty) {
        super.updateItem(metadata, empty);
        if(isEmpty()){
            this.setText(null);
            this.setGraphic(null);
        }else{
            if(isEditing()){
                if (textField != null) {
                    textField.setText(metadata.getName());
                }
                this.setText(null);
                this.setGraphic(textField);

            }else{
                if (empty || metadata == null) {
                    this.setText(null);
                    this.setGraphic(null);
                } else {
                    this.setText(metadata.getName());
                    this.setGraphic(metadata.getNameNode());
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override public void startEdit() {
        if (! isEditable()
                || ! getTreeTableView().isEditable()
                || ! getTableColumn().isEditable()
                || ! getItem().isItemNameHeader()) {
            return;
        }
        super.startEdit();

        if (isEditing()) {
            if (textField == null) {
                textField = createTextField();
            }

            if (textField != null) {
                textField.setText(this.getItem().getName());
            }
            this.setText(null);
            this.setGraphic(textField);

            textField.selectAll();

            // requesting focus so that key input can immediately go into the
            // TextField (see RT-28132)
            textField.requestFocus();
        }
    }

    /** {@inheritDoc} */
    @Override public void cancelEdit() {
        super.cancelEdit();
        this.setText(this.getItem().getName());
        this.setGraphic(this.getItem().getNameNode());
    }

    @Override
    public void commitEdit(Metadata newValue) {
        //its important that we set the name of the item here directly,
        //otherwise a subsequent call to super.commitEdit() will not refresh the result
        ChangeItemName changeItemName=new ChangeItemName(getItem(),
                getItem().getName(),
                textField.getText());
        this.getItem().getItemViewController().
                getCompositionViewController().getWorkspace().pushCommand(changeItemName);

        //make a call to super that will take care of doing anything relevant to the commit
        //such as updating this cell
        super.commitEdit(newValue);
    }

    private TextField createTextField() {
        final TextField textField = new TextField(this.getItem().getName());

        // Use onAction here rather than onKeyReleased (with check for Enter),
        // as otherwise we encounter RT-34685
        textField.setOnAction(event -> {
            this.commitEdit(this.getItem());
            event.consume();
        });
        textField.setOnKeyReleased(t -> {
            if (t.getCode() == KeyCode.ESCAPE) {
                this.cancelEdit();
                t.consume();
            }
        });
        return textField;
    }
}