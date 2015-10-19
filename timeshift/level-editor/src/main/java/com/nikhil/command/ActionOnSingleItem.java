package com.nikhil.command;

import com.nikhil.controller.ItemViewController;
import com.nikhil.controller.ShapeViewController;
import com.nikhil.editor.selection.SelectedItems;

/**
 * Created by NikhilVerma on 21/09/15.
 */
public abstract class ActionOnSingleItem implements Command{
    protected ShapeViewController shapeViewController;

    public ActionOnSingleItem(ShapeViewController shapeViewController) {
        this.shapeViewController = shapeViewController;
    }

    protected void makeSelectionOfThisShape(){
        SelectedItems selectedItems = shapeViewController.getCompositionViewController().getWorkspace().getSelectedItems();
        selectedItems.clearSelection();
        selectedItems.addToSelection(shapeViewController);
    }
}
