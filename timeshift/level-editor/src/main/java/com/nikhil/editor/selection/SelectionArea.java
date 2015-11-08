package com.nikhil.editor.selection;

import com.nikhil.logging.Logger;
import com.nikhil.math.MathUtil;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Widget class that is used to make a rectangular selection area ot select elements inside.
 * A {@link SelectionOverlap} delegate can be supplied to receive relevant
 * callbacks for selection and deselection.
 */
public class SelectionArea implements EventHandler<MouseEvent>{
	
	private Rectangle selectRect;
	private double startX,startY;
	private double dragX,dragY;
	
	private SelectionOverlap overlapChecker;

	public SelectionArea() {
		this(null);
	}

	public SelectionArea(SelectionOverlap overlapChecker){
		this.overlapChecker=overlapChecker;
		selectRect=new Rectangle();
		selectRect.setStroke(Color.BLACK);
		selectRect.setFill(Color.TRANSPARENT);
		selectRect.getStrokeDashArray().add(3d);
		setVisible(false);
	}

	public Rectangle getSelectRect() {
		return selectRect;
	}

	public void setVisible(boolean isVisible){
		selectRect.setVisible(isVisible);
	}

	public SelectionOverlap getOverlapChecker() {
		return overlapChecker;
	}

	public void setOverlapChecker(SelectionOverlap overlapChecker) {
		this.overlapChecker = overlapChecker;
	}

	/**
	 * set the starting point in workspace coordinates
	 * @param startX x in workspace coordinates
	 * @param startY y in workspace coordinates
	 */
	public void setStart(double startX,double startY){
		this.startX=startX;
		this.startY=startY;
	}

	/**
	 * set the point dragged to during drag event
	 * @param dragX x in workspace coordinates
	 * @param dragY y in workspace coordinates
	 */
	private void draggedTo(double dragX, double dragY){
		this.dragX=dragX;
		this.dragY=dragY;
		computeSelectRectDimensions();
	}
	
	private void computeSelectRectDimensions(){
		double x=MathUtil.getMin(startX, dragX);
		double y= MathUtil.getMin(startY, dragY);
		
		double width=MathUtil.abs(dragX-startX);
		double height=MathUtil.abs(dragY-startY);
		
		selectRect.setX(x);
		selectRect.setY(y);
		selectRect.setWidth(width);
		selectRect.setHeight(height);

	}

	@Override
	public void handle(MouseEvent mouseEvent) {

		EventType<MouseEvent> eventType=(EventType<MouseEvent>) mouseEvent.getEventType();
		
		double x = mouseEvent.getX();
		double y = mouseEvent.getY();
		if((eventType.equals(MouseEvent.MOUSE_PRESSED))&&(mouseEvent.getButton()==MouseButton.PRIMARY)){
			startSelection(x, y);
		}else if((eventType.equals(MouseEvent.MOUSE_DRAGGED))&&(mouseEvent.getButton()==MouseButton.PRIMARY)){
			moveSelection(x, y);
		}else if((eventType.equals(MouseEvent.MOUSE_RELEASED))&&(mouseEvent.getButton()==MouseButton.PRIMARY)){
			endSelection();
		} 
	}

	public void endSelection() {
		setVisible(false);
	}

	public void moveSelection(double x, double y) {
		setVisible(true);
		draggedTo(x, y);
		if(overlapChecker!=null){
            Bounds sceneBounds=selectRect.localToScene(selectRect.getBoundsInLocal());
            overlapChecker.selectOverlappingItems(this, sceneBounds);
        }
	}

	public void startSelection(double x, double y) {
		setStart(x, y);
		if(overlapChecker!=null){
            overlapChecker.resetSelection();
        }
	}

}

