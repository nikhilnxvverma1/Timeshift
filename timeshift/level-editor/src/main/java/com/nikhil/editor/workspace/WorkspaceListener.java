package com.nikhil.editor.workspace;

import com.nikhil.command.Command;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

/**
 * Listener which handles workspace events mainly: mouse press,drag and release.
 * Created by NikhilVerma on 16/09/15.
 */
public interface WorkspaceListener {

    public void workspaceMousePressed(MouseEvent mouseEvent);
    public void workspaceMouseDragged(MouseEvent mouseEvent);
    public void workspaceMouseReleased(MouseEvent mouseEvent);

    /**
     * notification for whenever the workspace zoomed
     * @param oldZoom the old zoom it was set to (in fractional units)
     * @param newZoom the new zoom set (in fractional units)
     */
    public void workspaceDidZoom(float oldZoom,float newZoom);
}
