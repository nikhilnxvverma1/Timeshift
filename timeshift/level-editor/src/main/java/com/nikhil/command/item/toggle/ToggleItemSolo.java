package com.nikhil.command.item.toggle;

import com.nikhil.controller.ItemViewController;
import javafx.scene.control.CheckBox;

/**
 * Created by NikhilVerma on 04/12/15.
 */
public class ToggleItemSolo extends ToggleSwitchItemCommand {

    public ToggleItemSolo(ItemViewController itemViewController, boolean wasSetBefore, boolean isSetNow, CheckBox checkBox) {
        super(itemViewController, wasSetBefore, isSetNow, checkBox);
    }

    @Override
    protected void toggleSwitchTo(boolean set) {
        itemViewController.setSolo(set);

    }
}
