package application;

public class Util {

	public static ValueFormatter getFormatterFor(ValueType valueType){
		ValueFormatter valueFormatter=null;
		switch(valueType){
		case CURRENCY:
			break;
		case DEGREES:
			break;
		case DOUBLE_DECIMAL:
			valueFormatter=new ValueFormatter() {
				
				@Override
				public String formatWith(double value) {
					return String.format("%.2f", value);
				}

				@Override
				public String getUnit() {
					return "";
				}
			};
			break;
		case INT_ONLY:
			valueFormatter=new ValueFormatter() {
				
				@Override
				public String formatWith(double value) {
					return String.format("%d", value);
				}
				@Override
				public String getUnit() {
					return "";
				}
			};
			break;
		case PERCENTAGE:
			valueFormatter=new ValueFormatter() {
				
				@Override
				public String formatWith(double value) {
					return String.format("%.2f%%", value);
				}
				@Override
				public String getUnit() {
					return "%";
				}
			};
			break;
		case SINGLE_DECIMAL:
			valueFormatter=new ValueFormatter() {
				
				@Override
				public String formatWith(double value) {
					return String.format("%.1f", value);
				}
				@Override
				public String getUnit() {
					return "";
				}
			};
			break;
		case TIME_IN_SECONDS:
			break;
		default:
			break;
			
		}
		return valueFormatter;
	}
	
}
