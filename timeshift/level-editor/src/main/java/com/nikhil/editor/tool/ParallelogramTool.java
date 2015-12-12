package com.nikhil.editor.tool;

import com.nikhil.command.AddParallelogram;
import com.nikhil.command.item.AddItem;
import com.nikhil.controller.ParallelogramViewController;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.logging.Logger;
import com.nikhil.math.MathUtil;
import com.nikhil.view.item.ParallelogramView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

/**
 * Created by NikhilVerma on 23/08/15.
 */
public class ParallelogramTool extends BaseTool {

    private ParallelogramView parallelogramView;

    private double initialX,initialY;

    public ParallelogramTool(Workspace workspace) {
        super(workspace);
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        initialX=xInWorksheet(mouseEvent);
        initialY=yInWorksheet(mouseEvent);
        parallelogramView=new ParallelogramView(1,1,0);
        workspace.getCurrentComposition().getWorksheet().getChildren().add(parallelogramView);
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        double dragX=xInWorksheet(mouseEvent);
        double dragY=yInWorksheet(mouseEvent);

        double width=MathUtil.abs(dragX-initialX);
        double height=MathUtil.abs(dragY-initialY);

        double x= (dragX+initialX)/2;
        double y= (dragY+initialY)/2;
        parallelogramView.setLayoutX(x);
        parallelogramView.setLayoutY(y);
        parallelogramView.setWidth(width);
        parallelogramView.setHeight(height);
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        //check if enough distance was made or not
        double x = xInWorksheet(mouseEvent);
        double y = yInWorksheet(mouseEvent);
        double distanceWithInitial=MathUtil.distance(initialX,initialY,x,y);
        if(distanceWithInitial<NEGLIGIBLE_DRAG_DISTANCE){
            Logger.log("Negligible size of parallelogram,dismissing Adding shape command");
            return;
        }

        //Add parallelogram via command
        ParallelogramViewController parallelogramViewController=new ParallelogramViewController(
                workspace.getCurrentComposition(),parallelogramView);
        AddItem addParallelogram=new AddItem(parallelogramViewController);
        workspace.pushCommand(addParallelogram);
    }

    @Override
    public ToolType getToolType() {
        return ToolType.PARALLELOGRAM;
    }
}
