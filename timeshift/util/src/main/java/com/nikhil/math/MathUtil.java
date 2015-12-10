package com.nikhil.math;

import com.nikhil.logging.Logger;
import com.nikhil.util.modal.UtilPoint;

/** Math library of common math utility methods.Every method is static */
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
	public static boolean isBetween(double a,double b,double c){
		if(c>=a&&c<=b){
			return true;
		}else if(c<=a&&c>=b){
			return true;
		}else{
			return false;
		}
	}
	public static double distance(double fromX,double fromY,double toX,double toY){
		return Math.sqrt((toX-fromX)*(toX-fromX)+(toY-fromY)*(toY-fromY));
	}

	/**
	 * Gets the angle that a vector makes with the x axis.
	 * <b>Important </b>if the vector is 0 (all arguments 0) than this will return a NaN
	 * @param originX x of origin
	 * @param originY y of origin
	 * @param toX x of endpoint
	 * @param toY x of endpoint
	 * @return angle that this vector makes with x axis
	 */
	public static double angleOfPoint(double originX,double originY,double toX,double toY){
		double angle=Math.toDegrees(Math.atan((toY-originY)/(toX-originX)));
		//get angle in 360
		double beforeAngle=angle;
		if(angle<0){
			if(toY<originY){//fourth quadrant
				angle=360+angle;//angle is negative
			}else{//second quadrant
				angle=180+angle;//angle is negative
			}
		}else{
			if(toY<originY){//third quadrant
				angle=180+angle;//angle is negative
			}else{//first quadrant
				//do nothing
			}
		}

		return angle;
	}

	public static double under360(double angle){
		double angleBetween0And360=0;
		if(angle>=0&&angle<360){
			angleBetween0And360=angle;
		}else if(angle<0){
			double angleBefore=Math.abs(angle)%360;
			angleBetween0And360=359-angleBefore;
		}else{
			angleBetween0And360=angle%360;
		}
		return angleBetween0And360;
	}
	public static UtilPoint getRotatedPoint(UtilPoint point,double degree){
		return getRotatedPoint(new UtilPoint(0,0),point,degree);
	}
	public static UtilPoint getRotatedPoint(UtilPoint around,UtilPoint point,double degree){
		UtilPoint translatedToOrigin = point.subtract(around);//translate the point to origin
		double radAng = Math.toRadians(degree);
		double rx=translatedToOrigin.getX()*Math.cos(radAng)-translatedToOrigin.getY()*Math.sin(radAng);
		double ry=translatedToOrigin.getX()*Math.sin(radAng)+translatedToOrigin.getY()*Math.cos(radAng);
//		Logger.log("px,py="+ point.getX()+","+point.getY());
//		Logger.log("x,y="+ rx+","+ry);
		return around.add(new UtilPoint(rx,ry));
	}
	public static double getNonReflexAngle(double angle1,double angle2){
		double under180=0;
		angle1=below360(angle1);
		angle2=below360(angle2);
		if(angle1>180){
			angle1-=180;
		}
		if(angle2>=180){
			angle2-=180;
		}
		under180=abs(angle1-angle2);
		if(under180>=90){
			under180=180-under180;
		}
		return under180;
	}

	/**
	 * Gets the non reflex angle difference between 2 angles.A non reflex angle is one
	 * between 0 and 180. The sign of the  angle difference is relative to
	 * the first angle
	 *
	 * @param angle1 the first angle,the other angle will always be subtracted to this angle
	 * @param angle2 the second angle, this will be subtracted from the first angle
	 * @return non reflex angle difference which could be positive or negative
	 */
	public static double getAngleDifference(double angle1,double angle2){
		double difference;
		angle1=below360(angle1);
		angle2=below360(angle2);

		if(firstQuadrant(angle1)&&fourthQuadrant(angle2)){
			//the first angle is leading ahead, so the value has to be positive
			//to find non reflex angle , we will take inverse of around 360
			difference=360-(angle2 - angle1);//notice the order of subtraction here,this makes difference positive
		}else if(fourthQuadrant(angle1)&&firstQuadrant(angle2)){
			//this value has to be negative because the first angle is trailing behind
			//its safe to get the vertically opposite angle and get its negative
			difference=(180-angle1)-(180+angle2);
		}else{
			difference=angle1-angle2;
			//if magnitude of difference is greater than 180,get the inverse on the other side
			//and FLIP the signs:  Why do we flip the sign?
			//if the difference was negative, it implies first angle was trailing behind,
			//so if we flip (after getting the non reflex angle) the first angle is now leading ahead of the second angle
			//vice versa if the difference was positive
			if(abs(difference)>=180){
				difference=difference<0?difference-180:difference+180; // take care while reducing if the number is negative
			}
		}
		return difference;
	}

	public static double below360(double angle){
		double wrappedAfter360=angle;
		if(angle<0){//negative angle
			double t=angle%360;
			wrappedAfter360=t==0?0:(360+t);//t is negative
		}
		else if(angle>=360){
			wrappedAfter360=angle%360;
		}
		return wrappedAfter360;
	}

	/**
	 * Checks if this angle is in first quadrant or not
	 * @param angle angle in degrees 
	 * @return true if angle is in first quadrant
	 */
	public static boolean firstQuadrant(double angle){
		return angle>=0&&angle<=90;
	}

	/**
	 * Checks if this angle is in second quadrant or not
	 * @param angle angle in degrees 
	 * @return true if angle is in second quadrant
	 */
	public static boolean secondQuadrant(double angle){
		return angle>=90&&angle<=180;
	}

	/**
	 * Checks if this angle is in third quadrant or not
	 * @param angle angle in degrees 
	 * @return true if angle is in third quadrant
	 */
	public static boolean thirdQuadrant(double angle){
		return angle>=180&&angle<=270;
	}

	/**
	 * Checks if this angle is in fourth quadrant or not
	 * @param angle angle in degrees 
	 * @return true if angle is in fourth quadrant
	 */
	public static boolean fourthQuadrant(double angle){
		return angle>=270&&angle<=360;
	}
}
