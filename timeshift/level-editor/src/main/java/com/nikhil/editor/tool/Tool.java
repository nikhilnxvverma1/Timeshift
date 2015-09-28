package com.nikhil.editor.tool;

import com.nikhil.command.Command;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * Created by NikhilVerma on 23/08/15.
 */
public interface Tool {

    public void toolAppointed(Tool lastSelectedTool);
    public void toolDismissed(Tool newToolSelected);
    public void mousePressed(MouseEvent mouseEvent);
    public void mouseDragged(MouseEvent mouseEvent);
    public Command mouseReleased(MouseEvent mouseEvent);
    public ToolType getToolType();
}
