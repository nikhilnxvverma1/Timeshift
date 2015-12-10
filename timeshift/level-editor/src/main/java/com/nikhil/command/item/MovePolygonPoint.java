package com.nikhil.command.item;

import com.nikhil.command.Command;
import com.nikhil.controller.ItemViewController;
import com.nikhil.controller.PolygonViewController;
import com.nikhil.editor.selection.SelectedItems;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.item.PolygonView;

import java.util.List;

/**
 * Created by NikhilVerma on 19/09/15.
 */
public class MovePolygonPoint extends ItemCommand {

    private PolygonViewController polygonViewController;
    private int index;
    private UtilPoint initialPosition;
    private UtilPoint finalPosition;

    public MovePolygonPoint(PolygonViewController polygonViewController, int index, UtilPoint initialPosition, UtilPoint finalPosition) {
        this.polygonViewController = polygonViewController;
        this.index = index;
        this.initialPosition = initialPosition;
        this.finalPosition = finalPosition;
    }

    @Override
    public void execute() {

        //update the view to final position
        PolygonView polygonView = polygonViewController.getItemView();
        UtilPoint polygonPoint = polygonView.getPolygonPoints().get(index);
        polygonView.updatePoint(index,finalPosition);

        //update the outline too
        polygonViewController.getGizmo().updateView();

        //update the model to final position after converting to work point
        UtilPoint workPolygonPoint=polygonViewController.getCompositionViewController().getWorkspace().workPoint(finalPosition);
        polygonViewController.getPolygonModelController().getPolygonModel().getPointAtIndex(index).setPoint(workPolygonPoint);

    }

    @Override
    public void unexecute() {
        //restore the view to initial position
        PolygonView polygonView = polygonViewController.getItemView();
        UtilPoint polygonPoint = polygonView.getPolygonPoints().get(index);
        polygonView.updatePoint(index,initialPosition);

        //update the outline too
        polygonViewController.getGizmo().updateView();

        //restore the model to initial position after converting to work point
        UtilPoint workPolygonPoint=polygonViewController.getCompositionViewController().getWorkspace().workPoint(initialPosition);
        polygonViewController.getPolygonModelController().getPolygonModel().getPointAtIndex(index).setPoint(workPolygonPoint);
    }

    @Override
    public List<ItemViewController> getItemList() {
        return listForSingleItem(polygonViewController);
    }
}
