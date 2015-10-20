package com.nikhil.controller;

import com.nikhil.controller.item.ItemModelController;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.view.item.delegate.ItemViewDelegate;
import com.nikhil.view.item.record.Metadata;
import javafx.geometry.Bounds;
import javafx.scene.control.TreeItem;

/**
 * Created by NikhilVerma on 30/08/15.
 */
public abstract class ItemViewController implements ItemViewDelegate {

    protected CompositionViewController compositionViewController;
    protected TreeItem<Metadata> metadataTree;
    private boolean locked;

    public ItemViewController(CompositionViewController compositionViewController) {
        this.compositionViewController = compositionViewController;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public CompositionViewController getCompositionViewController() {
        return compositionViewController;
    }

    public void setCompositionViewController(CompositionViewController compositionViewController) {
        this.compositionViewController = compositionViewController;
    }

    /** selects this record from the item table */
    public void selectFromItemTable(){
        compositionViewController.selectRecordFromItemTable(getMetadataTree());
    }

    public abstract boolean contains(double x, double y);
    public abstract void hoveredOver(boolean isHoveringOver, boolean isSelected);
    public abstract Bounds getLayoutBoundsInWorksheet();
    public abstract void moveBy(double dx, double dy);
    public abstract boolean rotateBy(double dAngle);
    public abstract boolean scaleBy(double dScale);
    public abstract boolean overlapsWithSceneBounds(Bounds sceneBounds);
    public abstract void hasSelectionFocus(boolean isSelected);
    public abstract void hasSelectionFocus(boolean isSelected,boolean isSelectedInDetail);
    public abstract ItemModelController getModelController();
    public abstract void addViewsToWorksheet();
    public abstract void removeViewsFromWorksheet();
    /**@return deep copy of this item controller*/
    public abstract ItemViewController clone();
    /**@return returns the metadata that contains all information about the properties of this item(lazily created)*/
    public abstract TreeItem<Metadata> getMetadataTree();
    /** call this method anytime the properties change so as to refresh the metadata*/
    public abstract void refreshMetadata();
}
