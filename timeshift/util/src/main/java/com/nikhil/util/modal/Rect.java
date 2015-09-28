package com.nikhil.util.modal;

public class Rect {

	private UtilPoint topLeft;
	private float width;
	private float height;
	
	public Rect(float x,float y,float width,float height){
		topLeft=new UtilPoint(x,y);
		this.width=width;
		this.height=height;
	}
	
	public UtilPoint getTopLeft() {
		return topLeft;
	}
	public void setTopLeft(UtilPoint topLeft) {
		this.topLeft = topLeft;
	}
	public float getWidth() {
		return width;
	}
	public void setWidth(float width) {
		this.width = width;
	}
	public float getHeight() {
		return height;
	}
	public void setHeight(float length) {
		this.height = length;
	}
	
	@Override
	public String toString(){
		return "("+topLeft.getX()+","+topLeft.getY()+","+width+","+height+")";
	}
}
