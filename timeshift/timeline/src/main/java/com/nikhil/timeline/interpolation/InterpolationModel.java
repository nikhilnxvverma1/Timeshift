package com.nikhil.timeline.interpolation;

import com.nikhil.util.modal.UtilPoint;

/**
 * Created by NikhilVerma on 20/12/15.
 */
public class InterpolationModel implements InterpolationCurve {
    private boolean hold=false;
    private double ys=0;
    private double ye=1;
    private UtilPoint controlPoint1=new UtilPoint(0,0);
    private UtilPoint controlPoint2=new UtilPoint(0,0);

    @Override
    public double valueFor(double t) {
        if(hold){
            return t<=0.9?0:1;
        }else{
            //B(t)=(1-t)^3*P0 + 3*(1-t)^2*t*P1 + 3*(1-t)*t^2*P2 + t^3*P3
            UtilPoint firstComponenet=new UtilPoint(0,ys).multiply((1-t)*(1-t)*(1-t));
            UtilPoint secondComponenet=controlPoint1.multiply(3*(1-t)*(1-t)*t);
            UtilPoint thirdComponenet=controlPoint2.multiply(3*(1-t)*t*t);
            UtilPoint fourthComponenet=new UtilPoint(0,ye).multiply(t * t * t);
            return firstComponenet.add(secondComponenet).add(thirdComponenet).add(fourthComponenet).getY();
        }
    }

    public boolean isHold() {
        return hold;
    }

    public void setHold(boolean hold) {
        this.hold = hold;
    }

    public double getYs() {
        return ys;
    }

    public void setYs(double ys) {
        this.ys = ys;
    }

    public double getYe() {
        return ye;
    }

    public void setYe(double ye) {
        this.ye = ye;
    }

    public UtilPoint getControlPoint1() {
        return controlPoint1;
    }

    public void setControlPoint1(double x,double y) {
        controlPoint1.set(x,y);
    }

    public UtilPoint getControlPoint2() {
        return controlPoint2;
    }

    public void setControlPoint2(double x,double y) {
        controlPoint2.set(x,y);
    }
}
