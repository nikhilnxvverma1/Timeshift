package com.nikhil.util.modal;

public class UtilPoint {

	private double x=0;
	private double y=0;
	
	public UtilPoint() {
		super();
	}

	/**
	 * Creates a copy of an existing point
	 * @param p the point to copy from
	 */
	public UtilPoint(UtilPoint p){
		super();
		this.x=p.x;
		this.y=p.y;
	}
	public UtilPoint(float x, float y) {
		super();
		this.x = x;
		this.y = y;
	}
	public UtilPoint(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void set(double x,double y){
		this.x=x;
		this.y=y;
	}

	/**
	 * Copies the x,y values from another point
	 * @param copyFrom the point to copy from
	 */
	public void set(UtilPoint copyFrom){
		this.x=copyFrom.getX();
		this.y=copyFrom.getY();
	}

	/**
	 * subtract from this point without making any changes to this object
	 * @param subtractThis point to subtract
	 * @return the difference
	 */
	public UtilPoint subtract(UtilPoint subtractThis){
		return new UtilPoint(x-subtractThis.x,y-subtractThis.y);
	}
	
	/**
	 * add to this point without making any changes to this object
	 * @param addThis point to add
	 * @return the sum 
	 */
	public UtilPoint add(UtilPoint addThis){
		return new UtilPoint(x+addThis.x,y+addThis.y);
	}
	
	/**
	 * multiply this point with a scaler without making changes to this object 
	 * @param scaler the floating number to multiply against
	 * @return a new point with multiplied scaler
	 */
	public UtilPoint multiply(double scaler){
		return new UtilPoint(x*scaler,y*scaler);
	}
	
	@Override
	public String toString(){
		return "("+x+","+y+")";
	}
}
