package com.nikhil.model.shape;

import com.nikhil.common.Observer;
import com.nikhil.math.MathUtil;
import com.nikhil.model.ModelVisitor;
import com.nikhil.model.freeform.MovablePoint;
import com.nikhil.timeline.Timeline;

/**
 * Polygon model which can have any number of vertices.
 *
 * Holds a circular linked list of movable points which are relative to the "translation" point.
 * Changes in scale and rotation do not affect the position of the points.
 * Created by NikhilVerma on 21/08/15.
 */
public class PolygonModel extends ShapeModel {

	private static int POLYGON_MODEL_COUNT=1;

	/** polygon points in a circular linked list*/
	protected MovablePoint polygonPointStart;

	public PolygonModel() {
		this.setName("Polygon "+POLYGON_MODEL_COUNT++);
	}

	public MovablePoint getPolygonPointStart() {
		return polygonPointStart;
	}

	public void setPolygonPointStart(MovablePoint polygonPointStart) {
		this.polygonPointStart = polygonPointStart;
	}

	public double getHeight(){
		if (polygonPointStart == null) {
			return 0;
		}

		MovablePoint t= polygonPointStart;
		double smallest=999999;
		double largest=-999999;
		do{
			if(t.getPoint().getY()<smallest){
				smallest=t.getPoint().getY();
			}

			if(t.getPoint().getY()>largest){
				largest=t.getPoint().getY();
			}
			t=t.getNext();
		}while(t!= polygonPointStart);
		double height = MathUtil.abs(largest - smallest);
		return height;
	}
	
	public double getWidth() {
		if (polygonPointStart == null) {
			return 0;
		}
		MovablePoint t = polygonPointStart;
		double smallest = 999999;
		double largest = -999999;
		do {
			if (t.getPoint().getX() < smallest) {
				smallest = t.getPoint().getX();
			}

			if (t.getPoint().getX() > largest) {
				largest = t.getPoint().getX();
			}
			t = t.getNext();
		} while (t != polygonPointStart);
		double width = MathUtil.abs(largest - smallest);
		return width;
	}

	@Override
	public void acceptVisitor(ModelVisitor visitor) {
		visitor.visit(this);
		MovablePoint t= polygonPointStart;
		if (t != null) {
			do{
				t.acceptVisitor(visitor);
				t=t.getNext();
			}while(t!= polygonPointStart);
		}
	}

	//TODO might not be required
	public void setObserver(Observer observer){

		MovablePoint t= polygonPointStart;
		if (t!=null) {
			do{
                t.setObserver(observer);
            }while (t!= polygonPointStart);
		}
	}

	/**
	 * gets the point at specified index.
	 * Keep in mind the points are still stored in a
	 * circular linked list and not in arrays so this
	 * method still takes linear time.
	 * @param index starts at 0
	 * @return point at index,null if index is out of bounds
	 */
	public MovablePoint getPointAtIndex(int index){
		MovablePoint pointAtIndex=null;
		if (polygonPointStart != null) {
			MovablePoint t= polygonPointStart;
			int i=0;
			do{
				if(i==index){
					pointAtIndex=t;
					break;
				}
				i++;
				t=t.getNext();
			}while(t!= polygonPointStart);
		}
		return pointAtIndex;
	}

	/**
	 * counts and gets the index of the specified point.
	 * @param movablePoint the point whose index needs to be known
	 * @return index of specified point, if not found then -1
	 */
	public int getIndexOfPoint(MovablePoint movablePoint){
		int index=-1;
		if (polygonPointStart != null) {
			MovablePoint t= polygonPointStart;
			index=0;
			do{
				index++;
				t=t.getNext();
			}while(t!=movablePoint&&t!= polygonPointStart);
			//index is off by 1 so decrement once
			index--;
		}
		return index;
	}

	/**
	 * counts the total vertices
	 * @return total points in circular linked list
	 */
	public int countTotalPoints(){
		int totalPoints=0;
		if (polygonPointStart != null) {
			MovablePoint t= polygonPointStart;
			do{
				totalPoints++;
				t=t.getNext();
			}while(t!= polygonPointStart);
		}
		return totalPoints;
	}

	/**
	 * Adds a point at the very end
	 * @param polygonPoint the point to add in the end
	 * @return true,if addition was successful,false otherwise.
	 */
	public boolean addPoint(MovablePoint polygonPoint){
		return addPoint(polygonPoint,-1);
	}

	/**
	 * adds(or rather inserts) a point after the specified index.
	 * if index is out of bounds(except -1) this method is a no-op
	 * @param polygonPoint the point to insert
	 * @param index must lie between 0 and "total points",or -1 if insertion needs to be made at the end
	 * @return true,if insertion was successful,false otherwise(especially in cases of no-ops)
	 */
	public boolean addPoint(MovablePoint polygonPoint,int index) {

		if (polygonPoint == null) {
			return false;
		}
		boolean insertionSuccessful = false;
		if (index == -1) {
			MovablePoint last = findLastPoint();
			if (last != null) {
				last.setNext(polygonPoint);
			} else {
				polygonPointStart = polygonPoint;
			}
			polygonPoint.setNext(polygonPointStart);
		}else if(index>=0){
			if(polygonPointStart!=null){
				MovablePoint t=polygonPoint;
				int i=0;
				do{
					if(i==index) {
						if (i == 0) {
							polygonPointStart=polygonPoint;
						}
						polygonPoint.setNext(t.getNext());
						t.setNext(polygonPoint);
						insertionSuccessful=true;
					}
					i++;
					t=t.getNext();
				}while(t!=polygonPointStart);
			}
		}
		return insertionSuccessful;
	}

	/**
	 * removes the last point in the circular linked list.
	 * if no points exist,then this method is a no-op
	 * @return the point just removed
	 */
	public MovablePoint removePoint(){

		MovablePoint removedPoint=null;
		if(polygonPointStart !=null){
			MovablePoint t= polygonPointStart;
			MovablePoint previous=null;

			do{
				//last point found
				if(t.getNext()== polygonPointStart){
					if(previous==null){//means this is the only node in the list
						polygonPointStart =null;//list is empty as no previous exists
					}else{
						previous.setNext(t.getNext());
					}
					removedPoint=t;
					break;
				}
				previous=t;
				t=t.getNext();
			}while(t!= polygonPointStart);
		}
		return removedPoint;

	}

	/**
	 * removes the point at the specified index in the circular linked list.
	 * if no points exist at index,then this method is a no-op
	 * @param index must lie between 0 and "total points",or -1 if removal needs to be made at the end
	 * @return the point just removed,null if point was not found
	 */
	public MovablePoint removePoint(int index){
		MovablePoint removedPoint=null;
		if(index==-1){
			return removePoint();
		}
		if(index>=0){
			int i=0;
			MovablePoint t= polygonPointStart;
			MovablePoint previous=null;
			do{
				if(i==index) {
					//remove current node
					if (previous == null) {//means this is the first node
						previous=findLastPoint();
						if (previous == polygonPointStart) {//only one node in list
							polygonPointStart =null;
						}else{
							polygonPointStart =t.getNext();
						}
					}
					previous.setNext(t.getNext());
					removedPoint=t;
					break;
				}
				i++;
				previous=t;
				t=t.getNext();
			}while(t!= polygonPointStart);
		}
		return removedPoint;
	}

	/**
	 * removes the specified point from the circular linked list.
	 * if specified points doesnt exist ,then this method is a no-op
	 * @param movablePoint the point in list that needs to be removed
	 * @return the point just removed,null if point was not found
	 */
	public MovablePoint removePoint(MovablePoint movablePoint){

		MovablePoint removedPoint=null;
		MovablePoint previous=findPreviousOf(movablePoint);
		if(previous!=null){

			previous.setNext(movablePoint.getNext());
			//if the list contains only one node ,nullify the start
			if(movablePoint== polygonPointStart &&movablePoint.getNext()== polygonPointStart){
				polygonPointStart =null;
			}
			removedPoint=movablePoint;

		}
		return removedPoint;
	}

	/**
	 * linearly traverses the list to look for the previous point to a particular point
	 * @param movablePoint point whose previous point needs to be known
	 * @return previous point to the specified point
	 */
	public MovablePoint findPreviousOf(MovablePoint movablePoint){
		MovablePoint previous=null;
		if(polygonPointStart !=null){
			MovablePoint t= polygonPointStart;
			do{
				if(t==movablePoint){
					if(previous==null){
						previous= findLastPoint();
					}
					break;
				}
				previous=t;
				t=t.getNext();
			}while(t!= polygonPointStart);
		}
		return previous;
	}

	/**
	 * linearly traverses the circular list to find the last node
	 * @return last node in circular linked list
	 */
	public MovablePoint findLastPoint(){
		MovablePoint last=null;
		if(polygonPointStart !=null){
			MovablePoint t= polygonPointStart;
			do{
				last=t;
				t=t.getNext();
			}while(t!= polygonPointStart);
		}
		return last;
	}

}
