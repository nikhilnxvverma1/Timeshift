package application;

public class MathUtil {

	public static double getMin(double a,double b){
		return a<=b?a:b;
	}
	public static double getMax(double a,double b){
		return a>=b?a:b;
	}
	public static double abs(double a){
		return a<0?a*-1:a;
	}
}
