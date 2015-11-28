package com.nikhil.command;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

/**
 * Created by NikhilVerma on 23/08/15.
 */
public class AddCircle extends Command {
    private Circle circle;
    private Pane pane;

    public AddCircle(Circle circle, Pane pane) {
        this.circle = circle;
        this.pane = pane;
    }

    @Override
    public void execute() {
        if(!pane.getChildren().contains(circle)){
            pane.getChildren().add(circle);
        }
    }

    @Override
    public void unexecute() {
        pane.getChildren().remove(circle);
    }
}
