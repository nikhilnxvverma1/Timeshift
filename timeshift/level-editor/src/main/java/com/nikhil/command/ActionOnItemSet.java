package com.nikhil.command;

import com.nikhil.controller.ItemViewController;
import com.nikhil.editor.selection.SelectedItems;

import java.util.Set;

/**
 * Created by NikhilVerma on 20/09/15.
 */
public abstract class ActionOnItemSet implements Command{
    protected Set<ItemViewController> itemSet;
    protected SelectedItems selectedItems;

    public ActionOnItemSet(Set<ItemViewController> itemSet, SelectedItems selectedItems) {
        this.itemSet = itemSet;
        this.selectedItems = selectedItems;
    }

    public Set<ItemViewController> getItemSet() {
        return itemSet;
    }

    public boolean containsSameItems(Set<ItemViewController> other){

        if(other.size()!=itemSet.size()){
            return false;
        }

        for(ItemViewController itemViewController:itemSet){
            if(!other.contains(itemViewController)){
                return false;
            }
        }
        return true;
    }

    protected void makeSelectionOfItemSet(){
        selectedItems.clearSelection();
        for(ItemViewController itemViewController:itemSet){
            selectedItems.addToSelection(itemViewController);
        }
    }

    protected final ItemViewController getFirstItemViewController() {
        ItemViewController firstItemViewController=null;
        for (ItemViewController itemViewController:itemSet){
            firstItemViewController=itemViewController;
            break;
        }
        return firstItemViewController;
    }
}
