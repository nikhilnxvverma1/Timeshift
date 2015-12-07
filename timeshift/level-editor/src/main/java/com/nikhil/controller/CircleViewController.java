package com.nikhil.controller;

import com.nikhil.controller.item.ItemModelController;
import com.nikhil.editor.gizmo.Gizmo;
import com.nikhil.model.shape.ShapeModel;
import com.nikhil.view.item.record.Metadata;
import com.nikhil.view.item.record.MetadataTag;
import com.nikhil.view.item.record.SpatialMetadata;
import com.nikhil.view.item.record.TemporalMetadata;
import javafx.geometry.Bounds;
import javafx.scene.control.TreeItem;
import javafx.scene.shape.Shape;

/**
 * View controller for the circle model
 * Created by NikhilVerma on 06/12/15.
 */
public class CircleViewController extends ShapeViewController{

    public CircleViewController(CompositionViewController compositionViewController) {
        super(compositionViewController);
    }

    @Override
    public Bounds getLayoutBoundsInWorksheet() {
        return null;
    }

    @Override
    public boolean overlapsWithSceneBounds(Bounds sceneBounds) {
        return false;
    }

    @Override
    public ItemModelController getModelController() {
        return null;
    }

    @Override
    public ItemViewController deepCopy() {
        return null;
    }

    @Override
    public TreeItem<Metadata> getMetadataTree() {
        return null;
    }

    @Override
    public Shape getItemView() {
        return null;
    }

    @Override
    public TemporalMetadata getTemporalMetadata(MetadataTag tag) {
        return null;
    }

    @Override
    public SpatialMetadata getSpatialMetadata(MetadataTag tag) {
        return null;
    }

    @Override
    public Gizmo getGizmo() {
        return null;
    }

    @Override
    protected ShapeModel getShapeModel() {
        return null;
    }

    @Override
    protected void constructModelControllerUsingView() {

    }

}
