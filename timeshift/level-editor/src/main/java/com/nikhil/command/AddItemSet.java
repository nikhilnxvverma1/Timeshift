package com.nikhil.command;

import com.nikhil.controller.ItemViewController;
import com.nikhil.editor.selection.SelectedItems;
import com.nikhil.editor.workspace.Workspace;

import java.util.Set;

/**
 * Created by NikhilVerma on 28/09/15.
 */
public class AddItemSet extends ActionOnItemSet {

    private Workspace workspace;//required to support cross application pasting,where workspaces can be different

    public AddItemSet(Set<ItemViewController> itemSet, SelectedItems selectedItems, Workspace workspace) {
        super(itemSet, selectedItems);
        this.workspace=workspace;
    }

    @Override
    public void execute() {
        for(ItemViewController itemViewController: itemSet){
            itemViewController.setWorkspace(workspace);
            itemViewController.addViewsToWorksheet();
            workspace.getItemViewControllers().add(itemViewController);
            workspace.addToTimelineSystem(itemViewController);
        }
        makeSelectionOfItemSet();
    }

    @Override
    public void unexecute() {
        for(ItemViewController itemViewController: itemSet){
            itemViewController.setWorkspace(workspace);
            itemViewController.removeViewsFromWorksheet();
            workspace.getItemViewControllers().remove(itemViewController);
            workspace.removeFromTimelineSystem(itemViewController);
        }
        selectedItems.clearSelection();
    }
}
