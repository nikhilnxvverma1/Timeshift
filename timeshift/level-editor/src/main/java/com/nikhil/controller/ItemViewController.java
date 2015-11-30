package com.nikhil.controller;

import com.nikhil.controller.item.ItemModelController;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.model.ItemModel;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.item.delegate.ItemViewDelegate;
import com.nikhil.view.item.record.Metadata;
import com.nikhil.view.item.record.MetadataTag;
import com.nikhil.view.item.record.SpatialMetadata;
import com.nikhil.view.item.record.TemporalMetadata;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.TreeItem;
import javafx.scene.shape.Shape;


/**
 * Created by NikhilVerma on 30/08/15.
 */
public abstract class ItemViewController implements ItemViewDelegate {

    protected CompositionViewController compositionViewController;
    protected TreeItem<Metadata> metadataTree;//TODO let subclasses take care of this
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
    public abstract UtilPoint getTranslation();

    /**
     * rotates the item by a specified delta angle
     * @param dAngle the delta angle by which to rotate (in degrees).This can be negative
     * @return the new angle of the item
     */
    public abstract double rotateBy(double dAngle);

    /**
     * scales the shape by specified difference
     * @param dScale the margin by which to scale.This can be negative
     * @return the new scale of the item
     */
    public abstract double scaleBy(double dScale);
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
    public abstract Shape getItemView();

    /**@return true if the property is keyframable based on the associated metadata for that property*/
    public boolean isKeyframableProperty(MetadataTag tag){
        TemporalMetadata temporalMetadata = getTemporalMetadata(tag);
        if(temporalMetadata!=null){
            return temporalMetadata.isKeyframable();
        }
        SpatialMetadata spatialMetadata=getSpatialMetadata(tag);
        if(spatialMetadata!=null){
            return spatialMetadata.isKeyframable();
        }
        return false;
    }

    /**
     * fetches the associated temporal metadata property for the tag.
     * @param tag tag for which the temporal metadata is required
     * @return Temporal metadata for the tag, null if this is an irrelevant tag for the item
     */
    public abstract TemporalMetadata getTemporalMetadata(MetadataTag tag);
    /**
     * fetches the associated spatial metadata property for the tag.
     * @param tag tag for which the spatial metadata is required
     * @return Spatial metadata for the tag, null if this is an irrelevant tag for the item
     */
    public abstract SpatialMetadata getSpatialMetadata(MetadataTag tag);

    public ItemModel getItemModel(){
        return getModelController().getItemModel();
    }

    public abstract Group getGizmo();
}
