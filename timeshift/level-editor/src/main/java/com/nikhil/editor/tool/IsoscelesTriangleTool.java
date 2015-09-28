package com.nikhil.editor.tool;

import com.nikhil.command.Command;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * Created by NikhilVerma on 12/09/15.
 */
public class IsoscelesTriangleTool extends BaseTool{

    private Pane pane;

    public IsoscelesTriangleTool(Pane pane) {
        this.pane = pane;
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {

    }

    @Override
    public Command mouseReleased(MouseEvent mouseEvent) {
        return null;
    }

    @Override
    public ToolType getToolType() {
        return null;
    }
}
