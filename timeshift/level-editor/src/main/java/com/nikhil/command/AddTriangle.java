package com.nikhil.command;

import com.nikhil.command.item.ItemCommand;
import com.nikhil.controller.ItemViewController;
import com.nikhil.controller.TriangleViewController;
import com.nikhil.editor.workspace.Workspace;

import java.util.List;

/**
 * Command to add a triangle view controller to the composition
 * Created by NikhilVerma on 11/12/15.
 */
public class AddTriangle extends ItemCommand {

    private TriangleViewController triangleViewController;

    /**
     * Creates a new AddTriangle command for the specified triangle view controller
     * @param triangleViewController the controller to add. <b>Important:</b> this must hold reference
     *                             to the composition view controller that it needs to attach to.
     */
    public AddTriangle(TriangleViewController triangleViewController) {
        if(triangleViewController.getCompositionViewController()==null){
            throw new RuntimeException("No composition defined for this triangle");
        }
        this.triangleViewController = triangleViewController;
    }

    @Override
    public void execute() {
        triangleViewController.getCompositionViewController().addItemViewController(triangleViewController);
    }

    @Override
    public void unexecute() {
        triangleViewController.getCompositionViewController().removeItemViewController(triangleViewController);
    }


    @Override
    public List<ItemViewController> getItemList() {
        return listForSingleItem(triangleViewController);
    }

    @Override
    public void unexecutedByWorkspace(Workspace workspace) {
        workspace.getSelectedItems().clearSelection();
    }
}
