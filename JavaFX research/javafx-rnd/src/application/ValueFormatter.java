package application;



public interface ValueFormatter {

	public String formatWith(double value);
	public String getUnit();
	
}
enum ValueType{
	TIME_IN_SECONDS,
	PERCENTAGE,
	DEGREES,
	SINGLE_DECIMAL,
	DOUBLE_DECIMAL,
	INT_ONLY,
	CURRENCY // units will be mentioned first
}
