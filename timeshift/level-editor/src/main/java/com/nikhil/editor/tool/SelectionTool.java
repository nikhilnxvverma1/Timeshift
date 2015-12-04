package com.nikhil.editor.tool;

import com.nikhil.command.Command;
import com.nikhil.controller.CompositionViewController;
import com.nikhil.controller.ItemViewController;
import com.nikhil.editor.selection.SelectionArea;
import com.nikhil.editor.selection.SelectionOverlap;
import com.nikhil.editor.workspace.Workspace;
import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;

import java.util.Iterator;

/**
 * Created by NikhilVerma on 23/08/15.
 */
public class SelectionTool extends BaseTool implements SelectionOverlap {

    private SelectionArea selectionArea;

    public SelectionTool(Workspace workspace) {
        this.workspace = workspace;
        initializeGraphics();
    }

    private void initializeGraphics(){
        selectionArea=new SelectionArea(this);
        workspace.getContainerPane().getChildren().add(selectionArea.getSelectRect());
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();
        selectionArea.startSelection(x, y);
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();
        selectionArea.moveSelection(x,y);
    }

    @Override
    public Command mouseReleased(MouseEvent mouseEvent) {
        selectionArea.endSelection();
        return null;
    }

    @Override
    public ToolType getToolType() {
        return ToolType.SELECTION;
    }

    @Override
    public void selectOverlappingItems(SelectionArea selectionArea, Bounds sceneBounds) {
        //check to see how many items overlap with the bounds
        for(CompositionViewController compositionViewController:workspace.getCompositionViewControllers()){

            Iterator<ItemViewController> itemViewControllerIterator = compositionViewController.getItemViewControllerIterator();
            while(itemViewControllerIterator.hasNext()){

                ItemViewController itemViewController=itemViewControllerIterator.next();
                //if it is overlapping with the bounds,add it to the selection
                if(itemViewController.overlapsWithSceneBounds(sceneBounds)&& itemViewController.isInteractive()){
                    workspace.getSelectedItems().addToSelection(itemViewController);
                }else{
                    workspace.getSelectedItems().removeFromSelection(itemViewController);
                }
            }
        }
    }

    @Override
    public void resetSelection() {
        //clear workspace selection
        workspace.getSelectedItems().clearSelection();
    }
}
