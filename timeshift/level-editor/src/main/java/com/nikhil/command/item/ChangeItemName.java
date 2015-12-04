package com.nikhil.command.item;

import com.nikhil.command.item.ItemCommand;
import com.nikhil.controller.ItemViewController;
import com.nikhil.view.item.record.Metadata;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by NikhilVerma on 30/11/15.
 */
public class ChangeItemName extends ItemCommand {

    private Metadata metadata;
    private String oldName;
    private String newName;

    public ChangeItemName(Metadata metadata, String oldName, String newName) {
        this.metadata = metadata;
        this.oldName = oldName;
        this.newName = newName;
    }

    @Override
    public List<ItemViewController> getItemList() {
        LinkedList<ItemViewController> list=new LinkedList<>();
        list.add(metadata.getItemViewController());
        return list;
    }

    @Override
    public void execute() {
        metadata.getItemViewController().getItemModel().setName(newName);
        refreshItemTable();
    }

    @Override
    public void unexecute() {
        metadata.getItemViewController().getItemModel().setName(oldName);
        refreshItemTable();
    }

    /**
     * A clean workaround to refresh the item table externally.
     */
    private void refreshItemTable(){
        final TreeTableView<Metadata> itemTable = metadata.getItemViewController().
                getCompositionViewController().getItemTable();

        final TreeTableColumn<Metadata, ?> firstColumn = itemTable.getColumns().get(0);
        firstColumn.setVisible(false);
        firstColumn.setVisible(true);

    }
}
