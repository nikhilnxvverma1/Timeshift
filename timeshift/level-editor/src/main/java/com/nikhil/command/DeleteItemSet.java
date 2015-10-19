package com.nikhil.command;

import com.nikhil.controller.ItemViewController;
import com.nikhil.editor.selection.SelectedItems;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.logging.Logger;

import java.util.Set;

/**
 * Created by NikhilVerma on 27/09/15.
 */
public class DeleteItemSet extends ActionOnItemSet {//TODO this always adds to the current composition

    public DeleteItemSet(Set<ItemViewController> itemSet, SelectedItems selectedItems) {
        super(itemSet, selectedItems);
    }

    @Override
    public void execute() {

//        Workspace workspace = getFirstItemViewController().getWorkspace();
        for(ItemViewController itemViewController: itemSet){
            itemViewController.removeViewsFromWorksheet();
            itemViewController.getCompositionViewController().removeItemViewController(itemViewController);
            itemViewController.getCompositionViewController().removeFromTimelineSystem(itemViewController);
        }
        selectedItems.clearSelection();
    }

    @Override
    public void unexecute() {
//        Workspace workspace = getFirstItemViewController().getWorkspace();
        for(ItemViewController itemViewController: itemSet){
            itemViewController.addViewsToWorksheet();
            itemViewController.getCompositionViewController().addItemViewController(itemViewController);
            itemViewController.getCompositionViewController().addToTimelineSystem(itemViewController);
        }
        makeSelectionOfItemSet();
    }
}
