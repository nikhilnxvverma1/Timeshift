package com.nikhil.editor.tool;

import com.nikhil.command.Command;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * Created by NikhilVerma on 23/08/15.
 */
public interface Tool {

    /**
     * Callback for whenever the tool is selected.
     *
     * @param lastSelectedTool the tool that was selected before this tool
     */
    void toolAppointed(Tool lastSelectedTool);

    /**
     * Callback for whenever this tool is dismissed as a result of a new tool being selected
     * @param newToolSelected the new tool that just got selected
     */
    void toolDismissed(Tool newToolSelected);
    void mousePressed(MouseEvent mouseEvent);
    void mouseDragged(MouseEvent mouseEvent);
    void mouseReleased(MouseEvent mouseEvent);
    ToolType getToolType();
}
