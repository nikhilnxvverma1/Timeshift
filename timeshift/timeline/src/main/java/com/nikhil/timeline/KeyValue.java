package com.nikhil.timeline;

/**
 * A value of a property which could be multi-dimensional.
 * Once a dimension is set it cannot be changed,
 * although the values at those dimension can.
 *
 * For example:
 * <ul>
 *     <li>1D value for rotation</li>
 *     <li>2D value for 2d coordinates</li>
 *     <li>3D value for 3d coordinates</li>
 *     <li>4D value for RGBA value</li>
 * </ul>
 * etc.
 * Internally it holds a double array which stores all these primitive values.
 * The term "KeyValue" suggests "Keyframe's Value" and not "Key-Value pairs"
 * @author Nikhil Verma 
 *
 */
public class KeyValue {
	
	private int dimension;
	private double []values;//TODO replace doubles with doubles
	public int getDimension() {
		return dimension;
	}
	/**
	 * Sets the dimension of the values array.
	 * This truncates all the old values too(if they exist)
	 * @param dimension size of the values array
	 */
	public void setDimension(int dimension) {
		this.dimension = dimension;
		this.values=new double[dimension];
	}
	public double[] getValues() {
		return values;
	}
	public void setValues(double[] values) {
		this.values = values;
	}
	
	/**
	 * gets the values at index.
	 * Mind the out of bound exception
	 * @param index should not cross dimensions of this KeyValue
	 * @return value value at index from the array
	 */
	public double get(int index){
		return values[index];
	}

	/**
	 * Sets(copies) the value of this keyframe from another KeyValue
	 * @param copyFrom copies of all the values from this KeyValue.
	 *                 If the dimension of argument KeyValue is lesser
	 *                 than "this" KeyValue, it only copies up till
	 *                 argument's dimension.If its greater, it copies
	 *                 first n values up till "this" KeyValue's dimension.
	 */
	public void set(KeyValue copyFrom){
		for (int i = 0; i < dimension && i < copyFrom.dimension; i++) {
			values[i]=copyFrom.values[i];
		}
	}

	/**
	 * sets the value at specified index.
	 * Mind the out of bound exception
	 * @param index index to set value at.it should not cross dimensions of this KeyValue
	 * @param value value to set for index
	 */
	public void set(int index, double value){
		values[index]=value;
	}
	
	/**
	 * Creates a null KeyValue(good if you want to create more than 4 dimensions ).
	 * dimension is not defined and should be defined later before setting values
	 */
	public KeyValue(){
	}

	/**
	 * Creates a 1D Key value
	 * @param value value of this key frame
	 */
	public KeyValue(double value){
		this.setDimension(1);
		this.set(0, value);
	}
	
	/**
	 * Creates a 2D Key value
	 */
	public KeyValue(double value1,double value2){
		this.setDimension(2);
		this.set(0, value1);
		this.set(1, value2);
	}
	
	/**
	 * Creates a 3D Key value
	 */
	public KeyValue(double value1,double value2,double value3){
		this.setDimension(3);
		this.set(0, value1);
		this.set(1, value2);
		this.set(2, value3);
	}
	
	/**
	 * Creates a 4D Key value
	 */
	public KeyValue(double value1,double value2,double value3,double value4){
		this.setDimension(4);
		this.set(0, value1);
		this.set(1, value2);
		this.set(2, value3);
		this.set(3, value4);
	}
	
	/**
	 * creates a new key value as a copy of another keyvalue.
	 * good for creating interpolated values
	 * @param keyValue key value to copy values from
	 */
	public KeyValue(KeyValue keyValue){
		this.setDimension(keyValue.dimension);
		for(int i=0;i<dimension;i++){
			set(i, keyValue.get(i));
		}
	}
	
	/**
	 * creates a new key value by using a value for first dimension 
	 * followed by copy of another keyvalue.
	 * @param prependedValue value added before values from second argument
	 * @param keyValue key value to copy values from
	 */
	public KeyValue(double prependedValue,KeyValue keyValue){
		int totalDimension=keyValue.dimension+1;
		this.setDimension(totalDimension);
		set(0, prependedValue);
		for(int i=1;i<totalDimension;i++){
			set(i, keyValue.get(i - 1));
		}
	}
	
	/**
	 * creates a new key value by using provided values 
	 * for first two dimensions 
	 * followed by copy of another keyvalue.
	 * @param firstPrependedValue first value of this keyframe
	 * @param secondPrependedValue second value of this keyframe
	 * @param keyValue key value to copy values from post first two arguments
	 */
	public KeyValue(double firstPrependedValue,double secondPrependedValue,KeyValue keyValue){
		int totalDimension=keyValue.dimension+2;
		this.setDimension(totalDimension);
		set(0, firstPrependedValue);
		set(1, secondPrependedValue);
		for(int i=2;i<totalDimension;i++){
			set(i, keyValue.get(i - 2));
		}
	}
	
	/**
	 * Creates a new Key value by simply using the value array 
	 * where the length acts as dimension
	 * @param values values to be set in this key value.
	 */
	public KeyValue(double[] values){
		this.values=values;
		this.dimension=values.length;
	}
	
	public String toString(){
		//return CSV of all values
		StringBuffer allValues=new StringBuffer();
		allValues.append(dimension);
		allValues.append("=");
		allValues.append("(");
		for(int i=0;i<dimension;i++){
			allValues.append(String.format("%.2f",values[i]) );
			if((i+1)<dimension){
				allValues.append(", ");
			}
		}
		allValues.append(")");
		return allValues.toString();
	}
}

