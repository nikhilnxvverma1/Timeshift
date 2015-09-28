package com.nikhil.editor.gizmo;

import com.nikhil.model.pathsystem.TravelPath;
import com.nikhil.view.item.travelpath.LinkPointView;
import com.nikhil.view.item.travelpath.TravelPathView;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * TODO Might not need this, just configure the event handlers on Travel path
 * Created by NikhilVerma on 12/09/15.
 */
public class TravelPathGizmo implements EventHandler<MouseEvent>{

    private TravelPathView travelPathView;
    private TravelPathView outlineTravelPathView;

    public TravelPathGizmo(Pane pane,TravelPathView travelPathView) {
        this.travelPathView = travelPathView;
        initializeGraphics(pane);
        updateGraphics();
    }

    private void initializeGraphics(Pane pane){

        outlineTravelPathView=new TravelPathView(travelPathView);
        pane.getChildren().add(outlineTravelPathView);
        outlineTravelPathView.addEventHandler(MouseEvent.MOUSE_DRAGGED,this);
        outlineTravelPathView.getStrokeDashArray().add(7d);
        outlineTravelPathView.setStrokeWidth(2);

    }

    private void updateGraphics(){
        outlineTravelPathView.linkPointChanged(travelPathView.getPrevious());
        outlineTravelPathView.linkPointChanged(travelPathView.getNext());
    }

    @Override
    public void handle(MouseEvent event) {
        if(event.getTarget()==outlineTravelPathView){
            tweakTravelPathOnBothSides(event);
        }
    }

    public void tweakTravelPathOnBothSides(MouseEvent mouseEvent){
        double dx=mouseEvent.getX();
        double dy=mouseEvent.getY();

        double px=travelPathView.getPrevious().getX()+dx;
        double py=travelPathView.getPrevious().getY()+dy;
        travelPathView.getPrevious().setX(px);
        travelPathView.getPrevious().setY(py);

        double nx=travelPathView.getNext().getX()+dx;
        double ny=travelPathView.getNext().getY()+dy;
        travelPathView.getNext().setX(nx);
        travelPathView.getNext().setY(ny);

        updateGraphics();
        mouseEvent.consume();
    }

}
