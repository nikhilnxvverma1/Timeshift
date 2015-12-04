package com.nikhil.command.composite;

import com.nikhil.command.Command;
import com.nikhil.command.item.ItemCommand;
import com.nikhil.controller.ItemViewController;
import com.nikhil.editor.selection.SelectedItems;
import com.nikhil.editor.workspace.Workspace;

import java.util.LinkedList;
import java.util.List;

/**
 * Holds a bunch of item commands that would be executed in one go.
 * Created by NikhilVerma on 27/11/15.
 */
public class ItemCompositeCommand extends CompositeCommand{
    List<ItemCommand> itemCommands=new LinkedList<>();

    public void addItemCommand(ItemCommand itemCommand,boolean executeWhileAdding){
        itemCommands.add(itemCommand);
        if(executeWhileAdding){
            itemCommand.execute();
        }
    }

    public boolean removeItemCommand(ItemCommand itemCommand){
        return itemCommands.remove(itemCommand);
    }

    @Override
    public void execute() {
        for(ItemCommand itemCommand:itemCommands){
            itemCommand.execute();
        }
    }

    @Override
    public void unexecute() {
        for(ItemCommand itemCommand:itemCommands){
            itemCommand.unexecute();
        }
    }

    @Override
    public void executedByWorkspace(Workspace workspace) {
        makeSelection(workspace);
    }

    @Override
    public void unexecutedByWorkspace(Workspace workspace) {
        makeSelection(workspace);
    }

    private void makeSelection(Workspace workspace) {
        SelectedItems selectedItems = workspace.getSelectedItems();
        selectedItems.clearSelection();
        for(ItemCommand itemCommand:itemCommands){
            List<ItemViewController> itemList = itemCommand.getItemList();
            for(ItemViewController itemViewController:itemList){
                selectedItems.addToSelection(itemViewController);
            }
        }
    }

    @Override
    public List<? extends Command> getCommands() {
        return itemCommands;
    }
}
