package com.nikhil.editor.selection;

import com.nikhil.controller.ItemViewController;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by NikhilVerma on 27/09/15.
 */
public class Clipboard {

    private static Clipboard sharedInstance=null;

    private List<ItemViewController> itemsInClipboard;
    private boolean cutFromOriginalSource;
    private Clipboard(){
        //singleton
        //this class can only be instantiated from here
    }

    public static Clipboard getSharedInstance(){
        if(sharedInstance==null) {
            sharedInstance = new Clipboard();
        }
        return sharedInstance;
    }

    public List<ItemViewController> getItemsInClipboard() {
        return itemsInClipboard;
    }

    public void setItemsInClipboard(List<ItemViewController> itemsInClipboard, boolean cutOperation) {
        this.itemsInClipboard = itemsInClipboard;
        this.cutFromOriginalSource=cutOperation;
    }

    public boolean isCutFromOriginalSource() {
        return cutFromOriginalSource;
    }

    /**
     * creates a deep copy of all items in the clipboard
     * @param offsetX distance to shift each clone in the x axis
     * @param offsetY distance to shift each clone in the y axis
     * @return set containing cloned item view controllers, null if clipboard is empty
     */
    public Set<ItemViewController> getDeepCopyOfItems(double offsetX,double offsetY){
        if(isEmpty()){
            return null;
        }
        Set<ItemViewController> deepCopyOfItems=new HashSet<ItemViewController>();
        for(ItemViewController itemViewController: itemsInClipboard){
            //make a deep copy of this controller
            ItemViewController deepCopy=itemViewController.clone();
            deepCopy.moveBy(offsetX,offsetY);
            deepCopyOfItems.add(deepCopy);
        }
        return deepCopyOfItems;
    }

    public boolean isEmpty(){
        if(itemsInClipboard==null||itemsInClipboard.size()==0){
            return true;
        }
        return false;
    }
}
