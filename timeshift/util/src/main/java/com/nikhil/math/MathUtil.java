package com.nikhil.math;

import com.nikhil.logging.Logger;
import com.nikhil.util.modal.UtilPoint;

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
		return around.add(new UtilPoint(rx,ry));
	}
}
