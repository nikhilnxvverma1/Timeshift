package com.nikhil.command;

import com.nikhil.controller.ItemViewController;
import com.nikhil.editor.workspace.Workspace;

import java.util.List;

/**
 * Commands that particularly work on an item.
 * Created by NikhilVerma on 27/11/15.
 */
public abstract class ItemCommand extends Command{

    /**
     * list of items that this command is acting on.
     * @return a (possibly new)list of item view controllers.
     */
    public abstract List<ItemViewController> getItemList();

    @Override
    public void executedByWorkspace(Workspace workspace) {
        makeSelection(workspace);
    }

    @Override
    public void unexecutedByWorkspace(Workspace workspace) {
        makeSelection(workspace);
    }

    private void makeSelection(Workspace workspace) {
        workspace.getSelectedItems().clearSelection();
        List<ItemViewController> itemList = getItemList();
        for(ItemViewController itemViewController:itemList){
            workspace.getSelectedItems().addToSelection(itemViewController);
        }
    }
}
