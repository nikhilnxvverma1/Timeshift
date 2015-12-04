package com.nikhil.command.item.toggle;

import com.nikhil.controller.ItemViewController;
import javafx.scene.control.CheckBox;

/**
 * Created by NikhilVerma on 03/12/15.
 */
public class ToggleItemVisibility extends ToggleSwitchItemCommand{

    public ToggleItemVisibility(ItemViewController itemViewController, boolean wasSetBefore, boolean isSetNow, CheckBox checkBox) {
        super(itemViewController, wasSetBefore, isSetNow, checkBox);
    }

    @Override
    protected void toggleSwitchTo(boolean set) {
        itemViewController.setVisible(set);
    }
}
