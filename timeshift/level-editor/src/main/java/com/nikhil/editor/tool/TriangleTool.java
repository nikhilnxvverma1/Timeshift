package com.nikhil.editor.tool;

import com.nikhil.command.AddTriangle;
import com.nikhil.controller.TriangleViewController;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.logging.Logger;
import com.nikhil.math.MathUtil;
import com.nikhil.view.item.TriangleView;
import javafx.scene.input.MouseEvent;

/**
 * Created by NikhilVerma on 12/09/15.
 */
public class TriangleTool extends BaseTool{

    private double initialX,initialY;
    private TriangleView triangleView;

    public TriangleTool(Workspace workspace) {
        super(workspace);
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        initialX=xInWorksheet(mouseEvent);
        initialY=yInWorksheet(mouseEvent);
        triangleView=new TriangleView(1,1);
        triangleView.setLayoutX(initialX);
        triangleView.setLayoutY(initialY);
        workspace.getCurrentComposition().getWorksheet().getChildren().add(triangleView);
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        double x=xInWorksheet(mouseEvent);
        double y=yInWorksheet(mouseEvent);
        triangleView.setBase(MathUtil.abs(x-initialX));
        triangleView.setHeight(MathUtil.abs(y-initialY));
        triangleView.setLayoutX((x+initialX)/2);
        triangleView.setLayoutY((y + initialY) / 2);
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        //check if enough distance was made or not
        double x = xInWorksheet(mouseEvent);
        double y = yInWorksheet(mouseEvent);
        double distanceWithInitial=MathUtil.distance(initialX,initialY,x,y);
        if(distanceWithInitial<NEGLIGIBLE_DRAG_DISTANCE){
            Logger.log("Negligible size of triangle,dismissing AddTriangle command");
            return;
        }

        TriangleViewController triangleViewController=new TriangleViewController(workspace.getCurrentComposition(),triangleView);
        AddTriangle addTriangle=new AddTriangle(triangleViewController);
        workspace.pushCommand(addTriangle);
    }

    @Override
    public ToolType getToolType() {
        return ToolType.TRIANGLE;
    }
}
