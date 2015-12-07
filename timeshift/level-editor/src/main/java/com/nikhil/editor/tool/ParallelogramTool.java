package com.nikhil.editor.tool;

import com.nikhil.command.AddParallelogram;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.math.MathUtil;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

/**
 * Created by NikhilVerma on 23/08/15.
 */
public class ParallelogramTool extends BaseTool {

    private Rectangle rectangle;//TODO soon will be changed to a more sophisticated parallelgoram shape using path

    private double initialX,initialY;
    private AddParallelogram addParallelogram;

    public ParallelogramTool(Workspace workspace) {
        super(workspace);
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        initialX=mouseEvent.getX();
        initialY=mouseEvent.getY();
        rectangle=new Rectangle();
        rectangle.setX(initialX);
        rectangle.setY(initialY);
        rectangle.setWidth(1);
        rectangle.setHeight(1);
        Pane pane=workspace.getCurrentComposition().getWorksheet();
        pane.getChildren().add(rectangle);
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        double dragX=mouseEvent.getX();
        double dragY=mouseEvent.getY();
        double x= MathUtil.getMin(initialX, dragX);
        double y=MathUtil.getMin(initialY, dragY);

        double width=MathUtil.abs(dragX-initialX);
        double height=MathUtil.abs(dragY-initialY);
        rectangle.setX(x);
        rectangle.setY(y);
        rectangle.setWidth(width);
        rectangle.setHeight(height);
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        Pane pane=workspace.getCurrentComposition().getWorksheet();
        addParallelogram = new AddParallelogram(rectangle,pane);
//        addParallelogram.execute();
//        commandStack.push(addParallelogram);
    }

    @Override
    public ToolType getToolType() {
        return ToolType.PARALLELOGRAM;
    }
}
