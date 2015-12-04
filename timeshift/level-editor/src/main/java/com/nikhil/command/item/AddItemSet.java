package com.nikhil.command.item;

import com.nikhil.controller.CompositionViewController;
import com.nikhil.controller.ItemViewController;
import com.nikhil.editor.workspace.Workspace;

import java.util.Set;

/**
 * This command is primarily used by the paste operations.
 * Created by NikhilVerma on 28/09/15.
 */
public class AddItemSet extends ActionOnItemSet {

    private CompositionViewController compositionViewController;

    public AddItemSet(Set<ItemViewController> itemSet, CompositionViewController compositionViewController) {
        super(itemSet);
        this.compositionViewController=compositionViewController;
    }

    @Override
    public void execute() {
        for(ItemViewController itemViewController: itemSet){
            itemViewController.setCompositionViewController(compositionViewController);
            itemViewController.addViewsToWorksheet();
            compositionViewController.addItemViewController(itemViewController);
        }
    }

    @Override
    public void unexecute() {
        for(ItemViewController itemViewController: itemSet){
            itemViewController.setCompositionViewController(compositionViewController);
            itemViewController.removeViewsFromWorksheet();
            compositionViewController.removeItemViewController(itemViewController);
        }
    }

    @Override
    public void unexecutedByWorkspace(Workspace workspace) {
        workspace.getSelectedItems().clearSelection();
    }
}
