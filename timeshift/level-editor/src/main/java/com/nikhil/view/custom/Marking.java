package com.nikhil.view.custom;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;

enum MarkingStrikeSize{
	BIG_MARK,
	SUB_MARK,
	SMALL_MARK
};

public class Marking extends VBox{

	private static final double DEFAULT_WIDTH=30;
	private static final double DEFAULT_HEIGHT=40;
	
	private static final double MAIN_STRIKE_SIZE=20;
	
	private double value;
	private long orderInThisZoom;
	private int subUnitsInSingleUnit;
	
	private Label mark;
	private Line strike;	
	
	public Marking(double value,long orderInThisZoom,int subUnitsInSingleUnit){
		this(value,orderInThisZoom,subUnitsInSingleUnit,DEFAULT_WIDTH,DEFAULT_HEIGHT);
	}
	
	public Marking(double value,long orderInThisZoom,int subUnitsInSingleUnit,double width,double height){
		super();
		this.value=value;
		this.orderInThisZoom=orderInThisZoom;
		this.subUnitsInSingleUnit=subUnitsInSingleUnit;
		
		setPrefSize(width, height);
		setAlignment(Pos.BOTTOM_CENTER);
		translateXProperty().bind(widthProperty().multiply(-0.5));
				
		mark=new Label(String.format("%.1f",value));

		double startY=0;
//		double endY=getEndYForStrikeSize(MarkingStrikeSize.SUB_MARK);
		double endY=MAIN_STRIKE_SIZE;
		strike=new Line(0,startY,0,endY);
		getChildren().addAll(mark,strike);
	}

	public double getValue() {
		return value;
	}

	public long getOrderInThisZoom() {
		return orderInThisZoom;
	}

	public void setMarkText(String markText){
		mark.setText(markText);
	}
	
	public void setMarkVisible(boolean isMarkVisisble){
		mark.setVisible(isMarkVisisble);
	}
	
	public void setMarkStrikeSize(double fractionOfMain){
		strike.setEndY(fractionOfMain*MAIN_STRIKE_SIZE);
	}
	
	public void setMarkStrikeSize(MarkingStrikeSize strikeSize){
		strike.setEndY(getEndYForStrikeSize(strikeSize));
	}
	
	private double getEndYForStrikeSize(MarkingStrikeSize strikeSize){
		double endY=0;
		switch(strikeSize){
		case BIG_MARK:
			endY=20;
			break;
		case SUB_MARK:
			endY=15;
			break;
		case SMALL_MARK:
			endY=10;
			break;
		default:
			break;
		
		}
		return endY;
	}

	public int getSubUnitsInSingleUnit() {
		return subUnitsInSingleUnit;
	}

	public void setSubUnitsInSingleUnit(int subUnitsInSingleUnit) {
		this.subUnitsInSingleUnit = subUnitsInSingleUnit;
	}
}
