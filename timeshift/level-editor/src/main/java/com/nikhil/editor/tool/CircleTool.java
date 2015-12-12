package com.nikhil.editor.tool;

import com.nikhil.command.AddCircle;
import com.nikhil.controller.CircleViewController;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.logging.Logger;
import com.nikhil.math.MathUtil;
import com.nikhil.view.item.CircleView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

/**
 * Created by NikhilVerma on 23/08/15.
 */
public class CircleTool extends BaseTool{

    private CircleView circleView;
    private double initialX,initialY;

    public CircleTool(Workspace workspace) {
        super(workspace);
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        initialX=xInWorksheet(mouseEvent);
        initialY=yInWorksheet(mouseEvent);
        circleView =new CircleView();
        circleView.setLayoutX(initialX);
        circleView.setLayoutY(initialY);
        circleView.setOuterRadius(1);//initial radius is otherwise negligible
        workspace.getCurrentComposition().getWorksheet().getChildren().add(circleView);
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        //find the midpoint between dragged point and inital point
        double x=xInWorksheet(mouseEvent);
        double y=yInWorksheet(mouseEvent);
        double radius= MathUtil.distance(initialX,initialY,x,y);
        circleView.setOuterRadius(radius);

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        //check if enough distance was made or not
        double x = xInWorksheet(mouseEvent);
        double y = yInWorksheet(mouseEvent);
        double distanceWithInitial=MathUtil.distance(initialX,initialY,x,y);
        if(distanceWithInitial<NEGLIGIBLE_DRAG_DISTANCE){
            Logger.log("Negligible size of circle,dismissing Adding shape command");
            return;
        }
        CircleViewController circleViewController=new CircleViewController(workspace.getCurrentComposition(),circleView);
        AddCircle addCircle=new AddCircle(circleViewController);
        workspace.pushCommand(addCircle);
    }

    @Override
    public ToolType getToolType() {
        return ToolType.CIRCLE;
    }
}
