package com.nikhil.command;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

/**
 * Created by NikhilVerma on 23/08/15.
 */
public class AddParallelogram extends Command{
    private Rectangle rectangle;
    private Pane pane;

    public AddParallelogram(Rectangle rectangle, Pane pane) {
        this.rectangle = rectangle;
        this.pane = pane;
    }

    @Override
    public void execute() {
        if(!pane.getChildren().contains(rectangle)){
            pane.getChildren().add(rectangle);
        }
    }

    @Override
    public void unexecute() {
        pane.getChildren().remove(rectangle);
    }
}
