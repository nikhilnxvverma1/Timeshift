package com.nikhil.editor.tool;

import com.nikhil.command.AddCircle;
import com.nikhil.command.Command;
import com.nikhil.math.MathUtil;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.util.Stack;

/**
 * Created by NikhilVerma on 23/08/15.
 */
public class CircleTool extends BaseTool{

    private Pane pane;
    private Circle circle;
    private double initialX,initialY;
    private AddCircle addCircle;

    public CircleTool(Pane pane) {
        this.pane = pane;

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        initialX=mouseEvent.getX();
        initialY=mouseEvent.getY();
        circle =new Circle();
        circle.setCenterX(initialX);
        circle.setCenterY(initialY);
        circle.setRadius(1);//initial radius is otherwise negligible
        pane.getChildren().add(circle);
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        //find the midpoint between dragged point and inital point
        double x=mouseEvent.getX();
        double y=mouseEvent.getY();
        double radius= MathUtil.distance(initialX,initialY,x,y);
        circle.setRadius(radius);

    }

    @Override
    public Command mouseReleased(MouseEvent mouseEvent) {

        addCircle = new AddCircle(circle,pane);
//        addCircle.execute();
//        commandStack.push(addCircle);
        return addCircle;
    }

    @Override
    public ToolType getToolType() {
        return ToolType.CIRCLE;
    }
}
