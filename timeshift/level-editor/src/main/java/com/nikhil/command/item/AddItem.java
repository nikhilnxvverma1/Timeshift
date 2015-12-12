package com.nikhil.command.item;


import com.nikhil.controller.ItemViewController;
import com.nikhil.controller.TriangleViewController;
import com.nikhil.editor.workspace.Workspace;

import java.util.List;

/**
 * Command to add a generic item view controller to the composition
 * Created by NikhilVerma on 12/12/15.
 */
public class AddItem extends ItemCommand {

    private ItemViewController itemViewController;

    /**
     * Creates a new AddItem command for the specified item view controller
     * @param itemViewController the controller to add. <b>Important:</b> this must hold reference
     *                             to the composition view controller that it needs to attach to.
     */
    public AddItem(ItemViewController itemViewController) {
        if(itemViewController.getCompositionViewController()==null){
            throw new RuntimeException("No composition defined for this item");
        }
        this.itemViewController = itemViewController;
    }

    @Override
    public void execute() {
        itemViewController.getCompositionViewController().addItemViewController(itemViewController);
    }

    @Override
    public void unexecute() {
        itemViewController.getCompositionViewController().removeItemViewController(itemViewController);
    }


    @Override
    public List<ItemViewController> getItemList() {
        return listForSingleItem(itemViewController);
    }

    @Override
    public void unexecutedByWorkspace(Workspace workspace) {
        workspace.getSelectedItems().clearSelection();
    }
}
