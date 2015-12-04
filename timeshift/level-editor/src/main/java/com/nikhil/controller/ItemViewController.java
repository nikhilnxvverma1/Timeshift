package com.nikhil.controller;

import com.nikhil.controller.item.ItemModelController;
import com.nikhil.model.ItemModel;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.item.delegate.ItemViewDelegate;
import com.nikhil.view.item.record.*;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.TreeItem;
import javafx.scene.shape.Shape;

import java.util.Iterator;


/**
 * Created by NikhilVerma on 30/08/15.
 */
public abstract class ItemViewController implements ItemViewDelegate {

    private static final double NON_SOLO_ITEM_VISIBILITY_OPACITY=0.5;
    private static final double FULL_OPACITY=1;

    protected CompositionViewController compositionViewController;
    protected TreeItem<Metadata> metadataTree;//TODO let subclasses take care of this
    private boolean locked=false;
    private boolean visible=true;//by default every item is visible
    private boolean solo=false;

    public ItemViewController(CompositionViewController compositionViewController) {
        this.compositionViewController = compositionViewController;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        if(locked){
            compositionViewController.getWorkspace().getSelectedItems().removeFromSelection(this);
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if(compositionViewController.getTotalSoloItems()==0||solo){
            getItemView().setVisible(visible);
        }
        if(!visible){
            //make sure that the item is not in the selection
            getCompositionViewController().getWorkspace().getSelectedItems().removeFromSelection(this);
        }
    }

    public void setSolo(boolean solo) {
        this.solo = solo;
        if(solo){
            compositionViewController.setTotalSoloItems(compositionViewController.getTotalSoloItems()+1);
//            getHeaderMetadata().getVisibility().setOpacity(FULL_OPACITY);
//            if (visible) {
//                getItemView().setVisible(true);
//            }
//            makeNonSoloItemsVisible(false);
        }else{
            compositionViewController.setTotalSoloItems(compositionViewController.getTotalSoloItems()-1);
//            if(compositionViewController.getTotalSoloItems()==0){
//                makeNonSoloItemsVisible(true);
//                makeNonSoloItemsVisible(true);
//            }
        }
    }

    private void makeNonSoloItemsVisible(boolean visible){

        //iterate over all items in parent composition
        Iterator<ItemViewController> itemViewControllerIterator = compositionViewController.getItemViewControllerIterator();
        while (itemViewControllerIterator.hasNext()){

            //make the view hidden without toggling the visibility flag
            ItemViewController itemViewController = itemViewControllerIterator.next();
            if(!itemViewController.solo){

                //do this only for visible and non-solo items
                if (itemViewController.visible) {
                    itemViewController.getItemView().setVisible(visible);
                }

                //also set a style on their visible checkbox switch
                HeaderMetadata headerMetadata = itemViewController.getHeaderMetadata();

                if (!visible) {
                    headerMetadata.getVisibility().setOpacity(NON_SOLO_ITEM_VISIBILITY_OPACITY);
                }else{
                    headerMetadata.getVisibility().setOpacity(FULL_OPACITY);
                }
            }
        }
    }

    public final boolean isInteractive(){
        if(compositionViewController.getTotalSoloItems()==0){
            return !locked && visible;
        }else{
            return !locked && visible && solo;
        }
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isSolo() {
        return solo;
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

    /**
     * gets the header metadata for this item.
     * Subclasses should override this if they plan to (unreasonably) have the header in
     * something other than root
     * @return the header metadata
     */
    public HeaderMetadata getHeaderMetadata() {
        return (HeaderMetadata)metadataTree.getValue();
    }

    public ItemModel getItemModel(){
        return getModelController().getItemModel();
    }

    public abstract Group getGizmo();
}
