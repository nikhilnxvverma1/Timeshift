package com.nikhil.command.item;

import com.nikhil.controller.ItemViewController;
import com.nikhil.editor.workspace.Workspace;

import java.util.Set;

/**
 * Created by NikhilVerma on 27/09/15.
 */
public class DeleteItemSet extends ActionOnItemSet {

    public DeleteItemSet(Set<ItemViewController> itemSet) {
        super(itemSet);
    }

    @Override
    public void execute() {

        for(ItemViewController itemViewController: itemSet){
            itemViewController.removeViewsFromWorksheet();
            itemViewController.getCompositionViewController().removeItemViewController(itemViewController);
            itemViewController.getCompositionViewController().removeFromTimelineSystem(itemViewController);
        }
    }

    @Override
    public void unexecute() {
        for(ItemViewController itemViewController: itemSet){
            itemViewController.addViewsToWorksheet();
            itemViewController.getCompositionViewController().addItemViewController(itemViewController);
            itemViewController.getCompositionViewController().addToTimelineSystem(itemViewController);
        }
    }

    @Override
    public void executedByWorkspace(Workspace workspace) {
        workspace.getSelectedItems().clearSelection();
    }
}
