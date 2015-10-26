package com.nikhil.view.custom;

import com.nikhil.editor.selection.SelectionArea;
import com.nikhil.editor.selection.SelectionOverlap;
import com.nikhil.timeline.KeyValue;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class KeyframePane extends AnchorPane implements SelectionOverlap{
	
	private static final double DEFAULT_HEIGHT=10;

	private double length;
	private double totalTime;
	private double currentZoom=1;
	
	private AnchorPane keyContainer;
	private boolean dragMade=false;
	private boolean readyToMove=false;
	private boolean collectedKeyAtPress=false;
	private double lastDraggedX;
	
	
	public KeyframePane(double totalTime, double length){
		this.length=length;
		this.totalTime=totalTime;
		setPrefHeight(DEFAULT_HEIGHT);
		keyContainer=new AnchorPane();
		keyContainer.setPrefSize(getPrefWidth(), getPrefHeight());
		getChildren().add(keyContainer);
		
		addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if((event.getButton()==MouseButton.PRIMARY)){
                double x = event.getX();
                double y = event.getY();
                collectedKeyAtPress=false;
                TimeValueKey keyAtPoint=findKeyAt(x, y);
                if(keyAtPoint!=null){
                    if(!keyAtPoint.isSelected()){
                        if(!event.isShiftDown()){
                            resetSelection();
                        }
//							keyAtPoint.setSelected(true);
                        selectKey(keyAtPoint, true);
                        collectedKeyAtPress=true;
                    }
                    readyToMove=true;
                }else{
                    readyToMove=false;
                    if(!event.isShiftDown()){
                        resetSelection();
                    }
                }
                lastDraggedX=x;

            }
        });
		
		addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if((event.getButton()==MouseButton.PRIMARY)&&readyToMove){
                moveSelectedKeysBy(event.getX()-lastDraggedX);
            }
            lastDraggedX=event.getX();
            dragMade=true;
        });
		addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if((event.getButton()==MouseButton.PRIMARY)&&!dragMade&&!collectedKeyAtPress){
                selectKeyAt(event.getX(), event.getY(),event.isShiftDown());
            }
            dragMade=false;
        });
	}



	private void selectAllKeys(boolean shouldSelect) {
		for(Node node: keyContainer.getChildren()){
			TimeValueKey key=(TimeValueKey)node;
//			key.setSelected(false);
			selectKey(key, shouldSelect);//fails to work here , possibly because of different thread modification
		}
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(double totalTime) {
		this.totalTime = totalTime;
	}
	
	private void shiftKeys() {
		double maxX=0;
		double minX=0;
		ObservableList<Node> markings=keyContainer.getChildren();
		for(Node node: markings){
			
			TimeValueKey key=(TimeValueKey)node;
			double keyX = getLayoutXFor(key);
			key.setLayoutX(keyX);
			
			if(keyX>maxX){
				maxX=keyX;
			}
			
			if(keyX<minX){
				minX=keyX;
			}
		}
		
	}

	private double getLayoutXFor(TimeValueKey key) {
		double timeRatio=key.getTime()/totalTime;
		double scaledLength=length*currentZoom;
		return timeRatio*scaledLength;
	}
	
	
	public void zoomBy(double step){
		zoomTo(currentZoom+step);
	}
	
	public void zoomTo(double newZoom){
		if(newZoom<1){//minimum zoom is 1
			return;
		}
		currentZoom=newZoom;
		//shift all marking according to new zoom
		shiftKeys();
		//explicitly setting width is not needed
	}

	public void addKeyAt(double time,KeyValue keyValue){
		TimeValueKey newKey=new TimeValueKey(getPrefHeight());
		newKey.setTime(time);
		newKey.setKeyValue(keyValue);
		newKey.setLayoutX(getLayoutXFor(newKey));
		
		//insert in the right spot
		int index=0;
		boolean indexFound=false;
		TimeValueKey firstKey=null,lastKey=null;
		for (Node node : keyContainer.getChildren()) {

			TimeValueKey key = (TimeValueKey) node;
			if (newKey.getTime() > key.getTime()) {//new key greater than current key
				indexFound = true;
			}
			if (!indexFound) {
				index++;
			}

			if (firstKey == null) {
				firstKey = key;
			}
			lastKey = key;
		}
		keyContainer.getChildren().add(index, newKey);
	}
	
	/**
	 * sorts all the timeValue keys on demand
	 */
	public void sortKeys(){
		Collections.sort(keyContainer.getChildren(),new Comparator<Node>() {

			@Override
			public int compare(Node o1, Node o2) {
				TimeValueKey first=(TimeValueKey)o1;
				TimeValueKey second=(TimeValueKey)o2;
				if(first.getTime()==second.getTime()){
					return 0;
				}else if(first.getTime()<second.getTime()){
					return -1;
				}else{
					return 1;
				}
			}
		});
	}
	
	public double timeAtX(double x){
		double scaledLength=currentZoom*length;
		double ratio=x/scaledLength;
		return ratio*totalTime;
	}
	
	private void selectKey(TimeValueKey key,boolean shouldSelect){
		key.setSelected(shouldSelect);
		if(shouldSelect){
//			key.toFront();//causing problems with thread concurrency
		}else{
//			key.toBack();//causing problems with thread concurrency
		}
	}
	
	private boolean selectKeyAt(double x,double y,boolean collecting){
		boolean keyWasSelected=false;
		TimeValueKey selectedKey=null;		
		for(Node node: keyContainer.getChildren()){
			TimeValueKey key=(TimeValueKey)node;
			if(key.getBoundsInParent().contains(x, y)){
				//if this key is already selected and it is in collection mode
				if(collecting&&key.isSelected()){
//					key.setSelected(false);
					selectKey(key, false);
				}else{
//					key.setSelected(true);
					selectKey(key, true);
					keyWasSelected=true;
					selectedKey=key;
				}
			}else if(!collecting){
//				key.setSelected(false);
				selectKey(key, false);
			}
		}
		return keyWasSelected;
	}
	
	private TimeValueKey findKeyAt(double x,double y){
		TimeValueKey keyContainingPoint=null;
		for(Node node: keyContainer.getChildren()){
			TimeValueKey key=(TimeValueKey)node;
			if(key.getBoundsInParent().contains(x, y)){
				keyContainingPoint=key;
			}
		}
		return keyContainingPoint;
	}
	
	public void moveSelectedKeysBy(double dl){
		
		for(Node node: keyContainer.getChildren()){
			TimeValueKey key=(TimeValueKey)node;
			if(key.isSelected()){
				double newLayoutX = key.getLayoutX()+dl;
				key.setLayoutX(newLayoutX);
				double time=timeAtX(newLayoutX);
				key.setTime(time);
			}
		}
		
		//TODO rebuild keyframes
	}

	@Override
	public void selectOverlappingItems(SelectionArea selectionArea, Bounds sceneBounds) {

	}

	@Override
	public void resetSelection() {
		selectAllKeys(false);
	}
}
