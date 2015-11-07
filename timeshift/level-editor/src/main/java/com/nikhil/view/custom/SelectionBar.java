package com.nikhil.view.custom;

import com.nikhil.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.TreeTableRow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.io.IOException;

public class SelectionBar extends Pane{

	private static final String BAR_STYLE="-fx-fill:#A6A6A6;";
	private static final String RESIZE_HANDLE_STYLE="-fx-fill:#0095FF;";

	private static final int DEFAULT_AREA_HEIGHT = 26;
	private static final double RESIZE_HANDLE_WIDTH=5;
	@FXML private Rectangle selectionRect;
	@FXML private Rectangle leftResize;
	@FXML private Rectangle rightResize;
	
	private double areaWidth;
	private double areaHeight;
	
	private double lastX;
	
	private double start=0;
	private double end=100;
	
	private double selectionStart;
	private double selectionEnd;
	private SelectionBarDelegate delegate;
	
	public SelectionBar(double areaWidth) {
		this(areaWidth,DEFAULT_AREA_HEIGHT,null);
	}

	public SelectionBar(double areaWidth,SelectionBarDelegate delegate){
		this(areaWidth,DEFAULT_AREA_HEIGHT,delegate);
	}


	public SelectionBar(double areaWidth,double areaHeight){
		this(areaWidth,areaHeight,null);
	}

	public SelectionBar(double areaWidth, double areaHeight,SelectionBarDelegate delegate) {
		super();
		this.areaWidth=areaWidth;
		this.areaHeight=areaHeight;
		this.delegate=delegate;

		this.setPrefSize(areaWidth,areaHeight);//TODO this should still be responsive to container size
		selectionRect=new Rectangle(areaWidth-2*RESIZE_HANDLE_WIDTH,areaHeight);
		selectionRect.addEventHandler(MouseEvent.MOUSE_PRESSED,this::selectionDragEntered);
		selectionRect.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::selectionDragged);
		selectionRect.addEventHandler(MouseEvent.MOUSE_RELEASED, this::selectionDragExited);
		selectionRect.setStyle(BAR_STYLE);

		leftResize=new Rectangle(RESIZE_HANDLE_WIDTH,areaHeight);
		leftResize.addEventHandler(MouseEvent.MOUSE_PRESSED,this::leftResizeDragEntered);
		leftResize.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::leftResizeDragged);
		leftResize.addEventHandler(MouseEvent.MOUSE_RELEASED, this::leftResizeDragExited);
		leftResize.setStyle(RESIZE_HANDLE_STYLE);
		leftResize.setCursor(Cursor.W_RESIZE);

		rightResize=new Rectangle(RESIZE_HANDLE_WIDTH,areaHeight);
		rightResize.addEventHandler(MouseEvent.MOUSE_PRESSED,this::rightResizeDragEntered);
		rightResize.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::rightResizeDragged);
		rightResize.addEventHandler(MouseEvent.MOUSE_RELEASED, this::rightResizeDragExited);
		rightResize.setStyle(RESIZE_HANDLE_STYLE);
		rightResize.setCursor(Cursor.E_RESIZE);

		this.getChildren().addAll(leftResize,selectionRect,rightResize);

        setSelectionAt(30, 70);

	}

	public double getAreaWidth() {
		return areaWidth;
	}

	public void setAreaWidth(double areaWidth) {
		this.areaWidth = areaWidth;
	}

	public double getAreaHeight() {
		return areaHeight;
	}

	public void setAreaHeight(double areaHeight) {
		this.areaHeight = areaHeight;
	}
	
	@Override
	protected void layoutChildren() {
		super.layoutChildren();
		this.areaWidth=getWidth();
		//redo selection layout
		setSelectionAt(selectionStart, selectionEnd);
	}
	
	@FXML
	protected void selectionDragEntered(MouseEvent dragEvent){
		lastX=dragEvent.getX();
	}
	
	@FXML
	protected void selectionDragged(MouseEvent dragEvent){

		double step = dragEvent.getX()-lastX;
		double newSelectionStartX=getXFor(selectionStart)+step;
		double newSelectionEndX=getXFor(selectionEnd)+step;
		setSelectionAt(getValueFor(newSelectionStartX), getValueFor(newSelectionEndX));
		lastX=dragEvent.getX();
		notifyDelegate();
	}
	
	@FXML
	protected void selectionDragExited(MouseEvent dragEvent){
//		Logger.log("just exited drag ");
		notifyDelegate();
	}
	
	@FXML
	protected void rightResizeDragEntered(MouseEvent dragEvent){

//		Logger.log("just entered drag for right ");
		lastX=dragEvent.getX();
		notifyDelegate();
	}
	
	@FXML
	protected void rightResizeDragged(MouseEvent dragEvent){
		
		double step = dragEvent.getX()-lastX;
		double newSelectionEndX=getXFor(selectionEnd)+step;
		setSelectionAt(selectionStart, getValueFor(newSelectionEndX));
		lastX=dragEvent.getX();
		notifyDelegate();
	}
	
	@FXML
	protected void rightResizeDragExited(MouseEvent dragEvent){
//		Logger.log("just exited drag  right");
		notifyDelegate();
	}
	
	@FXML
	protected void leftResizeDragEntered(MouseEvent dragEvent){

//		Logger.log("just entered drag for right ");
		lastX=dragEvent.getX();
	}
	
	@FXML
	protected void leftResizeDragged(MouseEvent dragEvent){
		
		double step = dragEvent.getX()-lastX;
		double newSelectionStartX=getXFor(selectionStart)+step;
		setSelectionAt(getValueFor(newSelectionStartX), selectionEnd);
		lastX=dragEvent.getX();
		notifyDelegate();

	}
	
	@FXML
	protected void leftResizeDragExited(MouseEvent dragEvent){
//		Logger.log("just exited drag  right");
		notifyDelegate();
	}
	
	public double getValueFor(double xOnSelection){
		double ratio=xOnSelection/areaWidth;
		return start+(end-start)*ratio;
	}
	
	public void setSelectionAt(double selectionStart,double selectionEnd){
		
		if(isOutOfBounds(selectionStart)||isOutOfBounds(selectionEnd)){
			return;
		}
		
		double selectionX=getXFor(selectionStart)+RESIZE_HANDLE_WIDTH;
		double selectionWidth=getXFor(selectionEnd)-selectionX-RESIZE_HANDLE_WIDTH;
		selectionRect.setX(selectionX);
		selectionRect.setWidth(selectionWidth);
		leftResize.setX(selectionX-leftResize.getWidth());
		rightResize.setX(selectionWidth+selectionX);
		
		this.selectionStart=selectionStart;
		this.selectionEnd=selectionEnd;
		
	}
	
	protected double getXFor(double value){
		double ratio=value/(end-start);
		return ratio*areaWidth;
	}
	
	public boolean isOutOfBounds(double value){
		if(value<start||value>end){
			return true;
		}
		return false;
	}

	private void notifyDelegate(){
		if(delegate!=null){
			delegate.selectionChanged(this,selectionStart,selectionEnd);
		}
	}
}
