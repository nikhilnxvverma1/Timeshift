package com.nikhil.command;

import com.nikhil.controller.CompositionViewController;
import com.nikhil.controller.ItemViewController;
import com.nikhil.editor.selection.SelectedItems;
import com.nikhil.editor.workspace.Workspace;

import java.util.Set;

/**
 * This command is primarily used by the paste operations.
 * Created by NikhilVerma on 28/09/15.
 */
public class AddItemSet extends ActionOnItemSet {

    private CompositionViewController compositionViewController;

    public AddItemSet(Set<ItemViewController> itemSet, SelectedItems selectedItems, CompositionViewController compositionViewController) {
        super(itemSet, selectedItems);
        this.compositionViewController=compositionViewController;
    }

    @Override
    public void execute() {
        for(ItemViewController itemViewController: itemSet){
            itemViewController.setCompositionViewController(compositionViewController);
            itemViewController.addViewsToWorksheet();
            compositionViewController.addItemViewController(itemViewController);
//            compositionViewController.addToTimelineSystem(itemViewController);
        }
        makeSelectionOfItemSet();
    }

    @Override
    public void unexecute() {
        for(ItemViewController itemViewController: itemSet){
            itemViewController.setCompositionViewController(compositionViewController);
            itemViewController.removeViewsFromWorksheet();
            compositionViewController.removeItemViewController(itemViewController);
//            compositionViewController.removeFromTimelineSystem(itemViewController);
        }
        selectedItems.clearSelection();
    }
}
