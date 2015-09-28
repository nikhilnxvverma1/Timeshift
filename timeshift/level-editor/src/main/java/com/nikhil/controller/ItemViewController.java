package com.nikhil.controller;

import com.nikhil.controller.item.ItemModelController;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.view.item.delegate.ItemViewDelegate;
import javafx.geometry.Bounds;

/**
 * Created by NikhilVerma on 30/08/15.
 */
public abstract class ItemViewController implements ItemViewDelegate {

    protected Workspace workspace;
    private boolean locked;

    public ItemViewController(Workspace workspace) {
        this.workspace = workspace;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
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

    /**
     * @return deep copy of this item controller
     */
    public abstract ItemViewController clone();

}
