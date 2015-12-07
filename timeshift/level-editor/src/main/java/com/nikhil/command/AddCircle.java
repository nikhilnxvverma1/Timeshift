package com.nikhil.command;

import com.nikhil.command.item.ItemCommand;
import com.nikhil.controller.CircleViewController;
import com.nikhil.controller.ItemViewController;
import com.nikhil.editor.workspace.Workspace;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.util.List;

/**
 * Command to add a circle view controller to the composition
 * Created by NikhilVerma on 23/08/15.
 */
public class AddCircle extends ItemCommand {

    private CircleViewController circleViewController;

    /**
     * Creates a new AddCircle command for the specified circle view controller
     * @param circleViewController the controller to add. <b>Important:</b> this must hold reference
     *                             to the composition view controller that it needs to attach to.
     */
    public AddCircle(CircleViewController circleViewController) {
        if(circleViewController.getCompositionViewController()==null){
            throw new RuntimeException("No composition defined for this circle");
        }
        this.circleViewController = circleViewController;
    }

    @Override
    public void execute() {
        circleViewController.getCompositionViewController().addItemViewController(circleViewController);
    }

    @Override
    public void unexecute() {
        circleViewController.getCompositionViewController().removeItemViewController(circleViewController);
    }


    @Override
    public List<ItemViewController> getItemList() {
        return listForSingleItem(circleViewController);
    }

    @Override
    public void unexecutedByWorkspace(Workspace workspace) {
        workspace.getSelectedItems().clearSelection();
    }
}
