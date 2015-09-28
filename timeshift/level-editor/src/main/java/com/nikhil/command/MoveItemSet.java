package com.nikhil.command;

import com.nikhil.controller.ItemViewController;
import com.nikhil.editor.selection.SelectedItems;
import com.nikhil.util.modal.UtilPoint;
import javafx.geometry.Point2D;

import javax.rmi.CORBA.Util;
import java.util.Set;

/**
 * Created by NikhilVerma on 20/09/15.
 */
public class MoveItemSet extends ActionOnItemSet {

    private boolean alreadyExecuted;
    private Point2D from;
    private Point2D to;

    public MoveItemSet(Set<ItemViewController> itemSet,SelectedItems selectedItems,Point2D from,Point2D to,boolean alreadyExecuted) {
        super(itemSet,selectedItems);
        this.from=from;
        this.to=to;
        this.alreadyExecuted=alreadyExecuted;
    }

    @Override
    public void execute() {
        if(!alreadyExecuted){
            double dx=to.getX()-from.getX();
            double dy=to.getY()-from.getY();
            for(ItemViewController itemViewController:itemSet){
                itemViewController.moveBy(dx,dy);
            }
            makeSelectionOfItemSet();
        }
        alreadyExecuted=true;
    }

    @Override
    public void unexecute() {
        if(alreadyExecuted){
            double dx=from.getX()-to.getX();
            double dy=from.getY()-to.getY();
            for(ItemViewController itemViewController:itemSet){
                itemViewController.moveBy(dx,dy);
            }
            makeSelectionOfItemSet();
        }
        alreadyExecuted=false;
    }
}
