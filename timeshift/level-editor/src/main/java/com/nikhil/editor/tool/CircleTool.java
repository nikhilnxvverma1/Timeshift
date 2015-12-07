package com.nikhil.editor.tool;

import com.nikhil.command.AddCircle;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.math.MathUtil;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

/**
 * Created by NikhilVerma on 23/08/15.
 */
public class CircleTool extends BaseTool{

    private Circle circle;
    private double initialX,initialY;

    public CircleTool(Workspace workspace) {
        super(workspace);
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        initialX=xInWorksheet(mouseEvent);
        initialY=yInWorksheet(mouseEvent);
        circle =new Circle();
        circle.setCenterX(initialX);
        circle.setCenterY(initialY);
        circle.setRadius(1);//initial radius is otherwise negligible
        workspace.getCurrentComposition().getWorksheet().getChildren().add(circle);
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        //find the midpoint between dragged point and inital point
        double x=xInWorksheet(mouseEvent);
        double y=yInWorksheet(mouseEvent);
        double radius= MathUtil.distance(initialX,initialY,x,y);
        circle.setRadius(radius);

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

        AddCircle addCircle = new AddCircle(circle, workspace.getCurrentComposition().getWorksheet());
//        addCircle.execute();
//        commandStack.push(addCircle);
    }

    @Override
    public ToolType getToolType() {
        return ToolType.CIRCLE;
    }
}
