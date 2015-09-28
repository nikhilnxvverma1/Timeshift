package application.timepane;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

enum KeyFill{
	LEFT_UNFILLED,
	BOTH_FILLED,
	RIGHT_UNFILLED
};

public class TimeValueKey extends AnchorPane implements Comparable<TimeValueKey>{

	private static final double DEFAULT_SIDE=20;
	private static final String TIME_PROPERTY_NAME="time";
	
	private static final Color HIGHLIGHT_COLOR=Color.BLUE;
	private static final Color NORMAL_COLOR=Color.DARKGRAY;	

	private static final String LEFT_PATH_ID="leftPath";
	private static final String RIGHT_PATH_ID="rightPath";
	
	private DoubleProperty time;
	private KeyValue keyValue;
	private boolean selected=false;
	
	public TimeValueKey(){
		this(DEFAULT_SIDE);
	}
	public TimeValueKey(double side){
		time=new SimpleDoubleProperty(this, TIME_PROPERTY_NAME);
		buildChildPath(side);
		DoubleBinding centerAnchorBinding = widthProperty().multiply(-0.5);
		translateXProperty().bind(centerAnchorBinding);
	}
	
	private void buildChildPath(double side){
		Path rightPath=new Path();
		MoveTo topCenterR=new MoveTo(side/2,0);
		LineTo midRightR=new LineTo(side,side/2);
		LineTo bottomCenterR=new LineTo(side/2,side);		
		rightPath.getElements().addAll(topCenterR,midRightR,bottomCenterR);
		getChildren().add(rightPath);
		rightPath.setId(RIGHT_PATH_ID);
		
		Path leftPath=new Path();
		MoveTo topCenterL=new MoveTo(side/2,0);
		LineTo midLeftL=new LineTo(0,side/2);
		LineTo bottomCenterL=new LineTo(side/2,side);
		leftPath.getElements().addAll(topCenterL,midLeftL,bottomCenterL);
		getChildren().add(leftPath);
		leftPath.setId(LEFT_PATH_ID);
		
		highlightWithColor(leftPath,rightPath,NORMAL_COLOR);
	}
	
	public void highlightWithColor(Color color){
		Path leftPath=(Path)lookup("#"+LEFT_PATH_ID);
		Path rightPath=(Path)lookup("#"+RIGHT_PATH_ID);
		highlightWithColor(leftPath,rightPath,color);
	}
	
	public void highlightWithColor(Path leftPath,Path rightPath,Color color){
		rightPath.setFill(color);
		rightPath.setStroke(color);
		leftPath.setFill(color);
		leftPath.setStroke(color);
	}
	
	public double getTime() {
		return time.doubleValue();
	}
	public void setTime(double time) {
		this.time.set(time);
	}
	public KeyValue getKeyValue() {
		return keyValue;
	}
	public void setKeyValue(KeyValue keyValue) {
		this.keyValue = keyValue;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
		if(selected){
			highlightWithColor(HIGHLIGHT_COLOR);
		}else{
			highlightWithColor(NORMAL_COLOR);
		}
	}
	@Override
	public int compareTo(TimeValueKey o) {
		
		if(o.time.doubleValue()==time.doubleValue()){
			return 0;
		}else if(time.doubleValue()<o.time.doubleValue()){
			return -1;
		}else{
			return 1;
		}
	}
	
	public DoubleProperty timeProperty(){
		return time;
	}
}

