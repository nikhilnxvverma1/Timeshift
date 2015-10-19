package com.nikhil.view.item.record;

import com.nikhil.controller.ItemViewController;
import com.nikhil.controller.PolygonViewController;
import com.nikhil.timeline.KeyValue;
import com.nikhil.view.custom.DraggableTextValue;
import com.nikhil.view.custom.DraggableTextValueDelegate;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * Created by NikhilVerma on 13/10/15.
 */
public class PolygonMetadata extends Metadata{
    private PolygonViewController polygonViewController;

    public PolygonMetadata(String name, int tag, PolygonViewController polygonViewController) {
        super(name, tag);
        this.polygonViewController = polygonViewController;
    }

    @Override
    public ItemViewController getItemViewController() {
        return polygonViewController;
    }


    @Override
    public Node getValueNode() {
        switch (tag){
            case PolygonViewController.HEADER_TAG:
                return new Button("Reset");//TODO delegation and visual size
            case PolygonViewController.SCALE_TAG:
                return getScaleValueNode();
            case PolygonViewController.ROTATION_TAG:
                return getRotationValueNode();
            case PolygonViewController.TRANSLATION_TAG:
                return getTranslationValueNode();
            case PolygonViewController.ANCHOR_POINT_TAG:
                return getAnchorPointValueNode();
            case PolygonViewController.VERTICES_TAG:
                return getVertexCountValueNode();
        }
        return null;
    }

    private KeyValue getKeyValue(){
        switch (tag){
            case PolygonViewController.SCALE_TAG:
                return new KeyValue(polygonViewController.getPolygonView().getScale());
            case PolygonViewController.ROTATION_TAG:
                return new KeyValue(polygonViewController.getPolygonView().getRotation());
            case PolygonViewController.TRANSLATION_TAG:
                return new KeyValue(polygonViewController.getPolygonView().getTranslationX(),
                        polygonViewController.getPolygonView().getTranslationY());
            case PolygonViewController.ANCHOR_POINT_TAG:
                return new KeyValue(polygonViewController.getPolygonView().getAnchorPointX(),
                        polygonViewController.getPolygonView().getAnchorPointY());
            case PolygonViewController.VERTICES_TAG:
                return new KeyValue(polygonViewController.getPolygonView().getPolygonPoints().size());
        }
        return null;
    }

    private HBox getScaleValueNode(){
        DraggableTextValue draggableTextValue=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double newValue) {

            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double finalValue) {

            }
        });
        draggableTextValue.setValue(polygonViewController.getPolygonView().getScale());
        return new HBox(draggableTextValue);
    }

    private HBox getRotationValueNode(){
        DraggableTextValue draggableTextValue=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double newValue) {

            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double finalValue) {

            }
        });
        draggableTextValue.setValue(polygonViewController.getPolygonView().getRotation());
        return new HBox(draggableTextValue);
    }

    private HBox getTranslationValueNode(){
        DraggableTextValue xValue=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double newValue) {

            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double finalValue) {

            }
        });
        xValue.setValue(polygonViewController.getPolygonView().getTranslationX());

        DraggableTextValue yValue=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double newValue) {

            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double finalValue) {

            }
        });
        yValue.setValue(polygonViewController.getPolygonView().getTranslationY());
        return new HBox(xValue,new Label(","),yValue);
    }

    private HBox getAnchorPointValueNode(){
        DraggableTextValue xValue=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double newValue) {

            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double finalValue) {

            }
        });
        xValue.setValue(polygonViewController.getPolygonView().getAnchorPointX());

        DraggableTextValue yValue=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double newValue) {

            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double finalValue) {

            }
        });
        yValue.setValue(polygonViewController.getPolygonView().getAnchorPointY());
        return new HBox(xValue,new Label(","),yValue);
    }

    private HBox getVertexCountValueNode(){
        TextField textField=new TextField(""+polygonViewController.getPolygonView().getPolygonPoints().size());
        textField.setDisable(true);
        return new HBox(textField);
    }

}
