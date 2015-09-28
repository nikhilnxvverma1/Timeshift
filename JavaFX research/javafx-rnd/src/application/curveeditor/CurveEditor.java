package application.curveeditor;

import java.io.IOException;

import javax.xml.stream.EventFilter;
import javax.xml.stream.events.XMLEvent;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;

public class CurveEditor extends VBox{

	private static final String CUSTOM_CONTROL_FXML = "Curve_editor.fxml";
	
	@FXML private Canvas canvas;
	@FXML private Line firstAnchorHint;
	@FXML private Line lastAnchorHint;
	@FXML private CubicCurve curve;
	@FXML private Circle curveStart;
	@FXML private Circle curveEnd;
	@FXML private Circle firstControlPoint;
	@FXML private Circle lastControlPoint;
	
	@FXML private AnchorPane anchorPane;
	
	
	public CurveEditor(){
		super();

		//load the control via fxml
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(CUSTOM_CONTROL_FXML));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);               
        
        try {
            fxmlLoader.load();         
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        firstAnchorHint.getStrokeDashArray().add((double) 5);
        lastAnchorHint.getStrokeDashArray().add((double) 5);            
        
	}
	
	public void setCurveStart(double x,double y){
		firstAnchorHint.setStartX(x);
		firstAnchorHint.setStartY(y);
		curve.setStartX(x);
		curve.setStartY(y);
		curveStart.setLayoutX(x);
		curveStart.setLayoutY(y);
	}
	
	public void setCurveEnd(double x,double y){
		lastAnchorHint.setStartX(x);
		lastAnchorHint.setStartY(y);
		curve.setEndX(x);
		curve.setEndY(y);
		curveEnd.setLayoutX(x);
		curveEnd.setLayoutY(y);
	}
	
	public void setFirstControlPointAt(double x,double y){
		firstAnchorHint.setEndX(x);
		firstAnchorHint.setEndY(y);
		curve.setControlX1(x);
		curve.setControlY1(y);
		firstControlPoint.setLayoutX(x);
		firstControlPoint.setLayoutY(y);
	}
	
	public void setLastControlPointAt(double x,double y){
		lastAnchorHint.setEndX(x);
		lastAnchorHint.setEndY(y);
		curve.setControlX2(x);
		curve.setControlY2(y);
		lastControlPoint.setLayoutX(x);
		lastControlPoint.setLayoutY(y);
	}
	
	@FXML 
	private void firstControlPointDragged(MouseEvent dragEvent){
		
		Point2D anchorPaneSpace=anchorPane.sceneToLocal(new Point2D(dragEvent.getSceneX(),dragEvent.getSceneY()));
		double x=anchorPaneSpace.getX();
		double y=anchorPaneSpace.getY();
		
		setFirstControlPointAt(x,y);

	}
	
	@FXML 
	private void lastControlPointDragged(MouseEvent dragEvent){
		Point2D anchorPaneSpace=anchorPane.sceneToLocal(new Point2D(dragEvent.getSceneX(),dragEvent.getSceneY()));
		double x=anchorPaneSpace.getX();
		double y=anchorPaneSpace.getY();
		
		setLastControlPointAt(x, y);
	}
	
	@FXML 
	private void curveStartDragged(MouseEvent dragEvent){
		Point2D anchorPaneSpace=anchorPane.sceneToLocal(new Point2D(dragEvent.getSceneX(),dragEvent.getSceneY()));
		double x=anchorPaneSpace.getX();
		double y=anchorPaneSpace.getY();
		
		setCurveStart(x, y);
	}
	
	@FXML 
	private void curveEndDragged(MouseEvent dragEvent){
		Point2D anchorPaneSpace=anchorPane.sceneToLocal(new Point2D(dragEvent.getSceneX(),dragEvent.getSceneY()));
		double x=anchorPaneSpace.getX();
		double y=anchorPaneSpace.getY();
		
		setCurveEnd(x, y);
	}
		
	
}
