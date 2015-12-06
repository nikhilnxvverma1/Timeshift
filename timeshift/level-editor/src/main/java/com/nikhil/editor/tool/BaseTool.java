package com.nikhil.editor.tool;

import com.nikhil.command.Command;
import com.nikhil.editor.workspace.Workspace;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * Created by NikhilVerma on 12/09/15.
 */
public abstract class BaseTool implements Tool {

    protected Workspace workspace;

    //TODO mandatory parametrized constructor for each subclass

    //handle these events on a need basis

    @Override
    public void toolAppointed(Tool lastSelectedTool) {

    }

    @Override
    public void toolDismissed(Tool newToolSelected) {

    }

    protected double xInWorksheet(MouseEvent mouseEvent){
        return workspace.getCurrentComposition().getWorksheet().parentToLocal(mouseEvent.getX(), mouseEvent.getY()).getX();
    }

    protected double yInWorksheet(MouseEvent mouseEvent){
        return workspace.getCurrentComposition().getWorksheet().parentToLocal(mouseEvent.getX(), mouseEvent.getY()).getY();
    }
}
