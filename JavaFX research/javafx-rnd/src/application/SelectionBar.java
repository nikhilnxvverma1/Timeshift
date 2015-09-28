package application;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class SelectionBar extends Pane{

	private static final String CUSTOM_CONTROL_FXML = "selection_bar.fxml";
	private static final int DEFAULT_AREA_HEIGHT = 30;
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
	
	
	public SelectionBar(double areaWidth) {
		this(areaWidth,DEFAULT_AREA_HEIGHT);
	}
	
	public SelectionBar(double areaWidth,double areaHeight) {
		super();
		this.areaWidth=areaWidth;
		this.areaHeight=areaHeight;

		//load the control via fxml
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(CUSTOM_CONTROL_FXML));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);               
        
        try {
        
            fxmlLoader.load();         
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
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
	}
	
	@FXML
	protected void selectionDragExited(MouseEvent dragEvent){
		System.out.print("just exited drag ");
	}
	
	@FXML
	protected void rightResizeDragEntered(MouseEvent dragEvent){
		
		System.out.println("just entered drag for right ");
		lastX=dragEvent.getX();
	}
	
	@FXML
	protected void rightResizeDragged(MouseEvent dragEvent){
		
		double step = dragEvent.getX()-lastX;
		double newSelectionEndX=getXFor(selectionEnd)+step;
		setSelectionAt(selectionStart, getValueFor(newSelectionEndX));
		lastX=dragEvent.getX();
		
	}
	
	@FXML
	protected void rightResizeDragExited(MouseEvent dragEvent){
		System.out.print("just exited drag  right");
	}
	
	@FXML
	protected void leftResizeDragEntered(MouseEvent dragEvent){
		
		System.out.println("just entered drag for right ");
		lastX=dragEvent.getX();
	}
	
	@FXML
	protected void leftResizeDragged(MouseEvent dragEvent){
		
		double step = dragEvent.getX()-lastX;
		double newSelectionStartX=getXFor(selectionStart)+step;
		setSelectionAt(getValueFor(newSelectionStartX), selectionEnd);
		lastX=dragEvent.getX();

	}
	
	@FXML
	protected void leftResizeDragExited(MouseEvent dragEvent){
		
		System.out.print("just exited drag  right");
		
	}
	
	public double getValueFor(double xOnSelection){
		double ratio=xOnSelection/areaWidth;
		return start+(end-start)*ratio;
	}
	
	public void setSelectionAt(double selectionStart,double selectionEnd){
		
		if(isOutOfBounds(selectionStart)||isOutOfBounds(selectionEnd)){
			return;
		}
		
		double selectionX=getXFor(selectionStart);
		double selectionWidth=getXFor(selectionEnd)-selectionX;
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
}
