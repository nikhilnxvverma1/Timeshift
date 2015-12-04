package com.nikhil.command.item;

import com.nikhil.command.Command;
import com.nikhil.controller.ItemViewController;
import com.nikhil.editor.workspace.Workspace;

import java.util.LinkedList;
import java.util.List;

/**
 * Commands that particularly work on an item.
 * Created by NikhilVerma on 27/11/15.
 */
public abstract class ItemCommand extends Command {

    /**
     * Convenience method for requiring a single item in a list.Mostly useful when dealing with single item command
     * @param itemViewController the single item that needs to exist in the list
     * @return list containing only a single item view controller.
     */
    public static List<ItemViewController> listForSingleItem(ItemViewController itemViewController){
        LinkedList<ItemViewController> list=new LinkedList<>();
        list.add(itemViewController);
        return list;
    }

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

    /**
     * Makes selection of all the items in this item command
     * @param workspace the workspace that holds the items of the workspace
     */
    protected void makeSelection(Workspace workspace) {
        workspace.getSelectedItems().clearSelection();
        List<ItemViewController> itemList = getItemList();
        for(ItemViewController itemViewController:itemList){
            workspace.getSelectedItems().addToSelection(itemViewController);
        }
    }
}
