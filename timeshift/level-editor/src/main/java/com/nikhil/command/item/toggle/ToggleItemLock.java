package com.nikhil.command.item.toggle;

import com.nikhil.controller.ItemViewController;
import javafx.scene.control.CheckBox;

/**
 * Created by NikhilVerma on 03/12/15.
 */
public class ToggleItemLock extends ToggleSwitchItemCommand{

    public ToggleItemLock(ItemViewController itemViewController, boolean wasSetBefore, boolean isSetNow, CheckBox checkBox) {
        super(itemViewController, wasSetBefore, isSetNow, checkBox);
    }

    @Override
    protected void toggleSwitchTo(boolean set) {
        itemViewController.setLocked(set);
    }
}
