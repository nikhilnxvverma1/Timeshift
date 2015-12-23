package com.nikhil.view.custom.keyframe;

import com.nikhil.controller.CompositionViewController;
import com.nikhil.editor.selection.SelectionArea;
import com.nikhil.editor.selection.SelectionOverlap;
import com.nikhil.logging.Logger;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

/**
 * Custom pane for editing interpolation curves in a graph. Every graph node should be added to the <b>graphNodeContainer</b>.
 * Created by NikhilVerma on 20/12/15.
 */
public class GraphEditor extends Pane implements SelectionOverlap {
    private static final String STYLE="-fx-background-color:rgb(50,50,50)";

    private CompositionViewController compositionViewController;

    /**These are actually vertical lines at specific intervals of the x axis */
    private Group xGridLines=new Group();
    /**These are actually horizontal lines at specific intervals of the y axis. It also contains the label markings */
    private Group yGridLinesAndMarkings =new Group();

    /**Every graph node will directly go in this container.This is so that every children could be safely downcasted */
    private Pane graphNodeContainer=new Pane();

    private double lowerY=0;
    private double upperY=100;
    private double lowerX=0;
    private double upperX;

    private final EventHandler<MouseEvent> mousePressedListener = e -> {
        Point2D point2D = this.localToParent(e.getX(), e.getY());
        compositionViewController.getSelectionArea().startSelection(point2D.getX(), point2D.getY());
        e.consume();
    };
    private final EventHandler<MouseEvent> mouseDraggedListener = e-> {
        Point2D point2D = this.localToParent(e.getX(), e.getY());
        compositionViewController.getSelectionArea().moveSelection(point2D.getX(), point2D.getY());
        e.consume();
    };
    private final EventHandler<MouseEvent> mouseReleasedListener = e->{
        Point2D point2D = this.localToParent(e.getX(), e.getY());
        compositionViewController.getSelectionArea().endSelection();
        e.consume();
    };

    public GraphEditor(CompositionViewController compositionViewController) {
        this.compositionViewController = compositionViewController;
        upperX=compositionViewController.getDuration();

        this.getChildren().addAll(xGridLines, yGridLinesAndMarkings,graphNodeContainer);

        this.setStyle(STYLE);
        this.addEventFilter(MouseEvent.MOUSE_PRESSED, mousePressedListener);
        this.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDraggedListener);
        this.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseReleasedListener);
    }

    @Override
    public void selectOverlappingItems(SelectionArea selectionArea, Bounds sceneBounds) {

    }

    @Override
    public void resetSelection() {

    }

    public CompositionViewController getCompositionViewController() {
        return compositionViewController;
    }

    public double getLowerY() {
        return lowerY;
    }

    public double getUpperY() {
        return upperY;
    }

    public double getLowerX() {
        return lowerX;
    }

    public double getUpperX() {
        return upperX;
    }

    public void update(){
        updateXGridLines();
        updateYGridLinesAndMarkings();
        compositionViewController.getKeyframeTable().updateAllGraphNodes();
    }
    private void updateXGridLines(){
        xGridLines.getChildren().clear();
        final double width = getWidth();
        final double height = getHeight();
        double percentageWidth= (width /this.getScene().getWidth())*100;
        int numberOfLines= (int) (percentageWidth/5);
        double gap= width /numberOfLines;
        for (int x = 0; x < width; x+=gap) {
            Line line=new Line(0,0,0, height);
            line.setLayoutX(x);
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(0.5);
            xGridLines.getChildren().add(line);
        }
    }
    private void updateYGridLinesAndMarkings(){
        yGridLinesAndMarkings.getChildren().clear();
        final double width = getWidth();
        final double height = getHeight();
        double percentageWidth= (height /this.getScene().getHeight())*100;
        int numberOfLines= (int) (percentageWidth/5);
        double gap= height /numberOfLines;

        for (int y = 0,lineNumber=0; y < height; y+=gap,lineNumber++) {

            //horizontal line
            Line line=new Line(0,0,width, 0);
            line.setLayoutY(y);
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(0.5);
            yGridLinesAndMarkings.getChildren().add(line);

            //marking label based on current y
            if (y!=0) {//to avoid it going over the keyframe table
                double value= ((height - y) / height)*(upperY-lowerY);
//            Label valueLabel=new Label(String.format("%.1f",value)); //labels cause the layoutChildren to be called again
//            valueLabel.setTextFill(Color.GRAY);
                Text valueLabel=new Text(String.format("%.0f",value));
                valueLabel.setFill(Color.GRAY);
                valueLabel.setLayoutY(y);
                yGridLinesAndMarkings.getChildren().add(valueLabel);
            }
        }
    }

    public void makeGraphNodesVisible(boolean visible){
        for(Node node: graphNodeContainer.getChildren()){
            node.setVisible(visible);//no need to downcast
        }
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        Logger.log("updating");
        update();
    }

    public void addGraphNode(GraphNode graphNode){
        for (int i = 0; i < graphNode.getGraphNodesBezierPoints().length; i++) {
            graphNode.getGraphNodesBezierPoints()[i].addAsChildrenTo(graphNodeContainer);
        }
    }

    public void removeGraphNode(GraphNode graphNode){
        for (int i = 0; i < graphNode.getGraphNodesBezierPoints().length; i++) {
            graphNode.getGraphNodesBezierPoints()[i].removeAsChildrenFrom(graphNodeContainer);
        }
    }

}
