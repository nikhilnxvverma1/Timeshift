package com.nikhil.view.custom.keyframe;

import com.nikhil.command.keyframe.MoveKeyframes;
import com.nikhil.controller.CompositionViewController;
import com.nikhil.editor.selection.SelectionArea;
import com.nikhil.editor.selection.SelectionOverlap;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.timeline.keyframe.Keyframe;
import com.nikhil.view.item.record.Metadata;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * An abstract pane that contains all the keys along with other elements.
 * Handles any keyframe events like selection(individual and combined) and movement.
 * Note for subclasses: All {@link KeyframeView} nodes go to the "keyContainer" node.
 */
public abstract class KeyframePane extends AnchorPane implements SelectionOverlap{
	
	private static final double DEFAULT_HEIGHT=10;
	protected double length;
	protected double totalTime;
	private double currentZoom=1;

	protected AnchorPane keyContainer;

	/*
	 * The following variables are used internally for handling the press-drag-release events.
	 * These variables are static because only one keyframe pane will be handling this sequence of events
	 * at any given time.So no point wasting memory keeping multiple copies of this variable
	 */
	private static double initialX;
	private static double lastDraggedX;
	private static boolean dragMade=false;
	private static boolean readyToMove=false;
	private static boolean collectedKeyAtPress=false;

	public KeyframePane(double totalTime, double length){
		this.length=length;
		this.totalTime=totalTime;
		this.setPrefHeight(DEFAULT_HEIGHT);
		keyContainer=new AnchorPane();
		keyContainer.setPrefSize(getPrefWidth(), getPrefHeight());
		getChildren().add(keyContainer);

		addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if((event.getButton()==MouseButton.PRIMARY)){
                double x = event.getX();
                double y = event.getY();
                collectedKeyAtPress=false;
				KeyframeTreeView keyframeTable = getMetadata().getItemViewController().getCompositionViewController().getKeyframeTable();
                KeyframeView keyAtPoint=findKeyAt(x, y);
                if(keyAtPoint!=null){
                    if(!keyAtPoint.isSelected()){
                        if(!event.isShiftDown()){
							keyframeTable.resetSelection();
                        }
                        selectKey(keyAtPoint, true);
                        collectedKeyAtPress=true;
                    }
                    readyToMove=true;
                }else{
                    readyToMove=false;
                    if(!event.isShiftDown()){
						keyframeTable.resetSelection();
                    }
                }
                lastDraggedX=x;
				initialX=x;

            }
        });

		addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if((event.getButton()==MouseButton.PRIMARY)&&readyToMove){
				KeyframeTreeView keyframeTable = getMetadata().getItemViewController().getCompositionViewController().getKeyframeTable();
				keyframeTable.moveSelectedKeysBy(event.getX() - lastDraggedX);
            }
            lastDraggedX=event.getX();
            dragMade=true;
        });
		addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {

            if((event.getButton()==MouseButton.PRIMARY)&&!dragMade&&!collectedKeyAtPress){
                selectKeyAt(event.getX(), event.getY(), event.isShiftDown());
            }else if(readyToMove && dragMade){

				//get reference to composition and workspace
				CompositionViewController compositionViewController = getMetadata().getItemViewController().getCompositionViewController();
				Workspace workspace=compositionViewController.getWorkspace();

				//build a list of keyframes(reuse if possible)
				LinkedList<KeyframeView> selectedKeyframes = workspace.getReusableListOfKeyframesIfPossible();

				//create the move command
				double dTime=timeAtX(lastDraggedX-initialX);
				MoveKeyframes moveKeyframes=new MoveKeyframes(selectedKeyframes,dTime);
				workspace.pushCommand(moveKeyframes,false);

			}
            dragMade=false;
        });
	}

	private void selectAllKeys(boolean shouldSelect) {
		for(Node node: keyContainer.getChildren()){
			KeyframeView key=(KeyframeView)node;
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
			
			KeyframeView key=(KeyframeView)node;
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

	public double getLayoutXFor(KeyframeView key) {
		return getLayoutXFor(key.getTime());
	}

	public double getLayoutXFor(double time) {
		double timeRatio=time/totalTime;
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

	public double timeAtX(double x){
		double scaledLength=currentZoom*length;
		double ratio=x/scaledLength;
		return ratio*totalTime;
	}
	
	private void selectKey(KeyframeView key,boolean shouldSelect){
		key.setSelected(shouldSelect);
		if(shouldSelect){
//			key.toFront();//causing problems with thread concurrency
		}else{
//			key.toBack();//causing problems with thread concurrency
		}
	}
	
	private boolean selectKeyAt(double x,double y,boolean collecting){
		boolean keyWasSelected=false;
		KeyframeView selectedKey=null;
		for(Node node: keyContainer.getChildren()){
			KeyframeView key=(KeyframeView)node;
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
	
	private KeyframeView findKeyAt(double x,double y){
		KeyframeView keyContainingPoint=null;
		for(Node node: keyContainer.getChildren()){
			KeyframeView key=(KeyframeView)node;
			if(key.getBoundsInParent().contains(x, y)){
				keyContainingPoint=key;
			}
		}
		return keyContainingPoint;
	}

	/**
	 * Moves the selected keys by some distance
	 * @param dl distance to move by
	 * @return number of (selected)keys moved
	 */
	public int moveSelectedKeysBy(double dl){

		int keysMoved=0;
		for(Node node: keyContainer.getChildren()){
			KeyframeView key=(KeyframeView)node;
			if(key.isSelected()){
				double newLayoutX = key.getLayoutX()+dl;
				double time = timeAtX(newLayoutX);
				key.setTime(time);
				keysMoved++;
			}
		}
		if(keysMoved>0){
			keyframesMoved(keysMoved);
		}
		return keysMoved;
	}

	@Override
	public void selectOverlappingItems(SelectionArea selectionArea, Bounds sceneBounds) {
		for(Node node: keyContainer.getChildren()){
			KeyframeView key=(KeyframeView)node;
			Bounds keySceneBounds = key.localToScene(key.getBoundsInLocal());
			if(sceneBounds.intersects(keySceneBounds)){
				key.setSelected(true);
//				key.toFront();//tried to bring this on top but concurrent thread problems arise
			}else{
				key.setSelected(false);
			}
		}
	}

	@Override
	public void resetSelection() {
		selectAllKeys(false);
		//DON'T use the keyframe table here to reset entire selection otherwise, it will go in an infinite loop
	}

	/**
	 * Selects the next keyframe to the currently selected keyframe.
	 * If multiple keyframes are selected, it considers the one after
	 * the last selected keyframe
	 * @return null if currently selected keyframe is last selected keyframe and there is no next
	 * (or if there are no keyframes),otherwise the next keyframe every other time
	 */
	public KeyframeView selectNextKeyframe(){

		if(keyContainer.getChildren().size()==0){
			return null;
		}
		KeyframeView lastSelectedKeyframe=null;
		KeyframeView firstKeyframe=null;
		//find the last selected keyframe
		for(Node node : keyContainer.getChildren()){
			KeyframeView keyframe=(KeyframeView)node;
			if (keyframe.isSelected()&&
					((lastSelectedKeyframe==null) || (lastSelectedKeyframe.getTime() < keyframe.getTime()))
					){
				lastSelectedKeyframe=keyframe;
			}

			//side by side, also find the first keyframe(in case no keyframe is selected,
			// we will select the first keyframe)
			if(firstKeyframe==null||keyframe.getTime()<firstKeyframe.getTime()){
				firstKeyframe=keyframe;
			}
		}

		KeyframeView keyframeRightAfter=null;
		if(lastSelectedKeyframe==null){
			keyframeRightAfter=firstKeyframe;
		}else{
			//find the key frame after this
			for(Node node : keyContainer.getChildren()){
				KeyframeView keyframe=(KeyframeView)node;
				if((keyframe.getTime()>lastSelectedKeyframe.getTime())&&
						(keyframeRightAfter==null||keyframe.getTime()<keyframeRightAfter.getTime())){
					keyframeRightAfter=keyframe;
				}
			}
		}

		if(keyframeRightAfter==null){
			return null;
		}else{
			selectAllKeys(false);
			keyframeRightAfter.setSelected(true);
			return keyframeRightAfter;
		}
	}

	/**
	 * Selects the previous keyframe to the currently selected keyframe.
	 * If multiple keyframes are selected, it considers the  one before the
	 * first selected keyframe.
	 * @return null if currently selected keyframe is the first keyframe
	 * and there is no previous,(or if there are no keyframes),
	 * otherwise the previous keyframe every other time
	 */
	public KeyframeView selectPreviousKeyframe(){
		if(keyContainer.getChildren().size()==0){
			return null;
		}
		KeyframeView firstSelectedKeyframe=null;
		KeyframeView lastKeyframe=null;
		//find the last selected keyframe
		for(Node node : keyContainer.getChildren()){
			KeyframeView keyframe=(KeyframeView)node;
			if (keyframe.isSelected() &&
					((firstSelectedKeyframe == null) || (firstSelectedKeyframe.getTime() > keyframe.getTime()))
					){
				firstSelectedKeyframe=keyframe;
			}

			//side by side, also find the last keyframe(in case no keyframe is selected,
			// we will select the last keyframe)
			if(lastKeyframe==null||keyframe.getTime()>lastKeyframe.getTime()){
				lastKeyframe=keyframe;
			}
		}

		KeyframeView keyframeRightBefore=null;
		if(firstSelectedKeyframe==null){
			keyframeRightBefore=lastKeyframe;
		}else{
			//find the key frame right before this
			for(Node node : keyContainer.getChildren()){
				KeyframeView keyframe=(KeyframeView)node;
				if((keyframe.getTime()<firstSelectedKeyframe.getTime())&&
						(keyframeRightBefore==null||keyframe.getTime()>keyframeRightBefore.getTime())){
					keyframeRightBefore=keyframe;
				}
			}
		}

		if(keyframeRightBefore==null){
			return null;
		}else{
			selectAllKeys(false);
			keyframeRightBefore.setSelected(true);
			return keyframeRightBefore;
		}
	}

	/**
	 * finds the keyframe right after this time
	 * @param time the time after which keyframe needs to be found
	 * @return keyframe right after the specified time, null otherwise
	 */
	public KeyframeView findKeyframeAfter(double time){
		//find the key frame after this
		KeyframeView keyframeRightAfter=null;
		for(Node node : keyContainer.getChildren()){
			KeyframeView keyframe=(KeyframeView)node;
			if((keyframe.getTime()>time)&&
					(keyframeRightAfter==null||keyframe.getTime()<keyframeRightAfter.getTime())){
				keyframeRightAfter=keyframe;
			}
		}
		return keyframeRightAfter;
	}

	/**
	 * finds the keyframe before after this time
	 * @param time the time before which keyframe needs to be found
	 * @return keyframe right before the specified time, null otherwise
	 */

	public KeyframeView findKeyframeBefore(double time){
		//find the key frame after this
		KeyframeView keyframeRightBefore=null;
		for(Node node : keyContainer.getChildren()){
			KeyframeView keyframe=(KeyframeView)node;
			if((keyframe.getTime()<time)&&
					(keyframeRightBefore==null||keyframe.getTime()>keyframeRightBefore.getTime())){
				keyframeRightBefore=keyframe;
			}
		}
		return keyframeRightBefore;
	}

	/**
	 * Finds the last selected keyframe.If multiple keyframes are selected,
	 * it considers the the last selected keyframe
	 * @return null if no keyframes are selected or if there are no keyframes,
	 * otherwise the last selected keyframe every other time
	 */
	public KeyframeView findLastSelectedKeyframe(){

		if(keyContainer.getChildren().size()==0){
			return null;
		}
		KeyframeView lastSelectedKeyframe=null;
		//find the last selected keyframe
		for(Node node : keyContainer.getChildren()){
			KeyframeView keyframe=(KeyframeView)node;
			if (keyframe.isSelected()&&
					((lastSelectedKeyframe==null) || (lastSelectedKeyframe.getTime() < keyframe.getTime()))
					){
				lastSelectedKeyframe=keyframe;
			}
		}

		return lastSelectedKeyframe;//this can also be null if nothing is selected
	}

	/**
	 * Finds the first selected keyframe.If multiple keyframes are selected,
	 * it considers the the first selected keyframe
	 * @return null if no keyframes are selected or if there are no keyframes,
	 * otherwise the first selected keyframe every other time
	 */
	public KeyframeView findFirstSelectedKeyframe(){

		if(keyContainer.getChildren().size()==0){
			return null;
		}
		KeyframeView firstSelectedKeyframe=null;
		//find the first selected keyframe
		for(Node node : keyContainer.getChildren()){
			KeyframeView keyframe=(KeyframeView)node;
			if (keyframe.isSelected()&&
					((firstSelectedKeyframe==null) || (firstSelectedKeyframe.getTime() > keyframe.getTime()))
					){
				firstSelectedKeyframe=keyframe;
			}
		}

		return firstSelectedKeyframe;//this can also be null if nothing is selected
	}

	/**
	 * Finds the keyframe view for the given keyframe model, by linearly
	 * iterating through all the keyframe views
	 * @param keyframe keyframe model which is held in one of the keyframe views
	 * @return keyframe view holding that keyframe model, null if it was not found
	 */
	public KeyframeView findKeyframeView(Keyframe keyframe){
		for(Node node: keyContainer.getChildren()){
			KeyframeView keyframeView=(KeyframeView)node;
			if(keyframeView.getKeyframeModel()==keyframe){
				return keyframeView;
			}
		}
		return null;
	}

	/**
	 * Counts the number of selected keyframes
	 * @return total selected keyframes
	 */
	public int countSelectedKeyframes(){
		int count=0;
		for(Node node:keyContainer.getChildren()){
			KeyframeView keyframeView=(KeyframeView)node;
			if(keyframeView.isSelected()){
				count++;
			}
		}
		return count;
	}

	/**
	 * Checks if this pane contains the supplied keyframe view
	 * @param keyframeView the keyframe view that needs to be checked for containment
	 * @return true if the keyframe exists in this keyframe pane,false otherwise
	 */
	public boolean contains(KeyframeView keyframeView){
		return keyContainer.getChildren().contains(keyframeView);
	}

	/**
	 * fills the supplied list with the selected keyframes from this pane by appending to it
	 * @param keyframeViewList the list collection which must be filled with selected keyframes
	 * @return the number of additions from this pane to the list, or in other words, total selected keyframes
	 */
	public int fillWithSelectedKeyframes(List<KeyframeView> keyframeViewList){
		int totalFilled=0;
		for(Node node:keyContainer.getChildren()){
			KeyframeView keyframeView=(KeyframeView)node;
			if(keyframeView.isSelected()){
				keyframeViewList.add(keyframeView);
				totalFilled++;
			}
		}
		return totalFilled;
	}

	public abstract Metadata getMetadata();

	/**
	 * Builds a linked list of keyframes.
	 * @return Linked list containing all keyframes.
	 */
	public LinkedList<KeyframeView> getKeyframes(){
		LinkedList<KeyframeView> keyframeViews=new LinkedList<>();
		for(Node node: keyContainer.getChildren()){
			keyframeViews.add((KeyframeView)node);
		}
		return keyframeViews;
	}

	/**
	 * Callback by super class which suggests that at least one keyframe moved
	 * @param totalKeysMoved the total keys whose time was changed. This will always be >0
	 */
	public abstract void keyframesMoved(int totalKeysMoved);

	public abstract TreeSet<? extends GraphNode> getGraphNodes();

	public abstract void updateGraphNodes();

	/**
	 * Toggles the visibility of all the graph nodes in this keyframe that make up the
	 * keyframe's interpolation curve
	 * @param visible true makes the entire curve visible, false does the opposite
	 */
	public void showKeyframeCurve(boolean visible){
		for(GraphNode graphNode: getGraphNodes()){
			graphNode.setVisible(visible);
		}
	}
}
