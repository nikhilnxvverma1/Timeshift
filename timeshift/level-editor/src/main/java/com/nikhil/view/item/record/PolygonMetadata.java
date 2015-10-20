package com.nikhil.view.item.record;

import com.nikhil.command.ScaleShape;
import com.nikhil.controller.ItemViewController;
import com.nikhil.controller.PolygonViewController;
import com.nikhil.math.MathUtil;
import com.nikhil.timeline.KeyValue;
import com.nikhil.view.custom.DraggableTextValue;
import com.nikhil.view.custom.DraggableTextValueDelegate;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * TODO Each draggable text field registers a listener on the polygon view. This might leak memory
 * because the table cells are getting recycled(they are virtualized).
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
                return new KeyValue(polygonViewController.getPolygonView().getRotate());
            case PolygonViewController.TRANSLATION_TAG:
                return new KeyValue(polygonViewController.getPolygonView().getLayoutX(),
                        polygonViewController.getPolygonView().getLayoutY());
            case PolygonViewController.ANCHOR_POINT_TAG:
                return new KeyValue(polygonViewController.getPolygonView().getTranslateX(),
                        polygonViewController.getPolygonView().getTranslateY());
            case PolygonViewController.VERTICES_TAG:
                return new KeyValue(polygonViewController.getPolygonView().getPolygonPoints().size());
        }
        return null;
    }

    private HBox getScaleValueNode(){
        DraggableTextValue draggableTextValue=new DraggableTextValue(new DraggableTextValueDelegate() {

            private double initialScale=polygonViewController.getPolygonView().getScale();

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double newValue) {
                polygonViewController.scaleBy(newValue - polygonViewController.getPolygonView().getScale());
            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double finalValue) {
                double finalScale=finalValue;
                ScaleShape scaleShape =new ScaleShape(polygonViewController,initialScale,finalScale);
                polygonViewController.getCompositionViewController().getWorkspace().pushCommand(scaleShape,false);
                //TODO now the new initialValue is the final value, next time this happens,
                initialScale=finalScale;//it will take this to be the initial value on undoing
            }
        });
        draggableTextValue.setLowerLimit(0);
        draggableTextValue.setLowerLimitExists(true);
        draggableTextValue.setStep(0.01);
        draggableTextValue.setValue(polygonViewController.getPolygonView().getScale());
        polygonViewController.getPolygonView().scaleProperty().addListener((observable, oldValue, newValue) -> {
            draggableTextValue.setValue(newValue.doubleValue());
        });
        return new HBox(draggableTextValue);
    }

    private HBox getRotationValueNode(){
        DraggableTextValue draggableTextValue=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double newValue) {
                if(newValue<0||newValue>=360){
                    draggableTextValue.setValue(MathUtil.under360(newValue));
                }
            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double finalValue) {
                if(finalValue<0||finalValue>=360){
                    draggableTextValue.setValue(MathUtil.under360(finalValue));
                }
            }
        });
        draggableTextValue.setValue(polygonViewController.getPolygonView().getRotate());
        polygonViewController.getPolygonView().rotateProperty().addListener((observable, oldValue, newValue) -> {
            draggableTextValue.setValue(newValue.doubleValue());
        });
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
        xValue.setValue(polygonViewController.getPolygonView().getLayoutX());
        polygonViewController.getPolygonView().layoutXProperty().addListener((observable, oldValue, newValue) -> {
            xValue.setValue(newValue.doubleValue());
        });

        DraggableTextValue yValue=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double newValue) {

            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double finalValue) {

            }
        });
        yValue.setValue(polygonViewController.getPolygonView().getLayoutY());
        polygonViewController.getPolygonView().layoutYProperty().addListener((observable, oldValue, newValue) -> {
            yValue.setValue(newValue.doubleValue());
        });
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
        xValue.setValue(polygonViewController.getPolygonView().getTranslateX());
        polygonViewController.getPolygonView().translateXProperty().addListener((observable, oldValue, newValue) -> {
            xValue.setValue(newValue.doubleValue());
        });

        DraggableTextValue yValue=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double newValue) {

            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double finalValue) {

            }
        });
        yValue.setValue(polygonViewController.getPolygonView().getTranslateY());
        polygonViewController.getPolygonView().translateYProperty().addListener((observable, oldValue, newValue) -> {
            yValue.setValue(newValue.doubleValue());
        });
        return new HBox(xValue,new Label(","),yValue);
    }

    private HBox getVertexCountValueNode(){
        TextField textField=new TextField(""+polygonViewController.getPolygonView().getPolygonPoints().size());
        textField.setDisable(true);
        return new HBox(textField);
    }

}
