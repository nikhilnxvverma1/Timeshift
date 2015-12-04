package com.nikhil.command.item;

import com.nikhil.command.item.ItemCommand;
import com.nikhil.controller.ItemViewController;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by NikhilVerma on 20/09/15.
 */
public abstract class ActionOnItemSet extends ItemCommand {
    protected Set<ItemViewController> itemSet;

    public ActionOnItemSet(Set<ItemViewController> itemSet) {
        this.itemSet = itemSet;
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

    protected final ItemViewController getFirstItemViewController() {
        ItemViewController firstItemViewController=null;
        for (ItemViewController itemViewController:itemSet){
            firstItemViewController=itemViewController;
            break;
        }
        return firstItemViewController;
    }

    @Override
    public List<ItemViewController> getItemList() {
        return new LinkedList<>(itemSet);
    }
}
