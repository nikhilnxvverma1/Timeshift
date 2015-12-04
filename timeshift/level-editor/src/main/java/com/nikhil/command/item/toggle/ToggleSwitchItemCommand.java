package com.nikhil.command.item.toggle;

import com.nikhil.command.item.ItemCommand;
import com.nikhil.controller.ItemViewController;
import com.nikhil.editor.workspace.Workspace;
import javafx.scene.control.CheckBox;

import java.util.List;

/**
 * Base class for toggling any switch for an item
 * Created by NikhilVerma on 04/12/15.
 */
public abstract class ToggleSwitchItemCommand extends ItemCommand {
    protected ItemViewController itemViewController;
    private boolean wasSetBefore;
    private boolean isSetNow;
    private CheckBox checkBox;

    public ToggleSwitchItemCommand(ItemViewController itemViewController, boolean wasSetBefore, boolean isSetNow, CheckBox checkBox) {
        this.itemViewController = itemViewController;
        this.wasSetBefore = wasSetBefore;
        this.isSetNow = isSetNow;
        this.checkBox=checkBox;
    }

    @Override
    public List<ItemViewController> getItemList() {
        return listForSingleItem(itemViewController);
    }

    @Override
    public void execute() {
        toggleSwitchTo(isSetNow);
        checkBox.setSelected(isSetNow);
    }

    @Override
    public void unexecute() {
        toggleSwitchTo(wasSetBefore);
        checkBox.setSelected(wasSetBefore);
    }

    //we override these methods because we want the item to be selected only if it got unlocked,and not otherwise

    @Override
    public void executedByWorkspace(Workspace workspace) {
        if(itemViewController.isInteractive()){
            makeSelection(workspace);
        }
    }

    @Override
    public void unexecutedByWorkspace(Workspace workspace) {
        if(itemViewController.isInteractive()){
            makeSelection(workspace);
        }
    }

    /**
     * Callback by base class to let the subclasses perform actions specific to the switch toggle.
     * @param set the state of the switch to set
     */
    protected abstract void toggleSwitchTo(boolean set);
}
