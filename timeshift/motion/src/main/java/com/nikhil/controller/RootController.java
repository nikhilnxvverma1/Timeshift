package com.nikhil.controller;

import com.nikhil.common.PulseListener;
import com.nikhil.common.PulseListenerContainer;
import com.nikhil.model.ModelElement;
import com.nikhil.model.ModelVisitor;

/**
 * Root controller has a list of composition controllers
 * Created by NikhilVerma on 10/08/15.
 */
public class RootController implements PulseListener,ModelElement{

    private CompositionController compositionControllerStart;
    private PulseListenerContainer pulseListenerContainerStart;

    public RootController() {
        this(null,null);
    }

    public RootController(CompositionController compositionControllerStart) {
        this(compositionControllerStart,null);
    }

    public RootController(CompositionController compositionControllerStart, PulseListenerContainer pulseListenerContainerStart) {
        this.compositionControllerStart = compositionControllerStart;
        this.pulseListenerContainerStart = pulseListenerContainerStart;
    }

    public CompositionController getCompositionControllerStart() {
        return compositionControllerStart;
    }

    public void setCompositionControllerStart(CompositionController compositionControllerStart) {
        this.compositionControllerStart = compositionControllerStart;
    }

    public PulseListenerContainer getPulseListenerContainerStart() {
        return pulseListenerContainerStart;
    }

    public void setPulseListenerContainerStart(PulseListenerContainer pulseListenerContainerStart) {
        this.pulseListenerContainerStart = pulseListenerContainerStart;
    }

    @Override
    public void step(double delta, double totalTimeInContext){
        //TODO step self

        //step all children composition controllers
        CompositionController t= compositionControllerStart;
        while (t != null) {

            t.step(delta, 0);
            t=t.getNext();
        }

        //additionally step all pulse listeners (if they exist)
        PulseListenerContainer plt=pulseListenerContainerStart;
        while (t != null) {

            plt.getPulseListener().step(delta,0 );
            plt=plt.getNext();
        }
    }

    public void registerPulseListener(PulseListener pulseListener) {
        if (pulseListenerContainerStart== null) {
            pulseListenerContainerStart=new PulseListenerContainer(pulseListener);
        }else{
            PulseListenerContainer alreadyExists=findContainerFor(pulseListener);
            if (alreadyExists != null) {
                PulseListenerContainer last= pulseListenerContainerStart.getLast();
                last.setNext(new PulseListenerContainer(pulseListener));
            }
        }
    }

    public void deregisterPulseListener(PulseListener pulseListener){
        PulseListenerContainer t=pulseListenerContainerStart;
        PulseListenerContainer previous=null;
        while (t != null) {
            if (t.getPulseListener() == pulseListener) {
                if (previous == null) {
                    pulseListenerContainerStart=t.getNext();
                }else{
                    previous.setNext(t.getNext());
                }
                t.setNext(null);
                break;
            }
            previous=t;
            t = t.getNext();
        }
    }

    public PulseListenerContainer findContainerFor(PulseListener pulseListener){
        PulseListenerContainer t=pulseListenerContainerStart;
        while (t != null) {
            if(t.getPulseListener()==pulseListener){
                return t;
            }
            t=t.getNext();
        }
        return null;
    }

    public void addCompositionController(CompositionController compositionController) {
        if (compositionControllerStart == null) {
            compositionControllerStart=compositionController;
        }else{
            //TODO just prepend in O(1) time
            compositionController.setNext(compositionControllerStart);
            compositionControllerStart=compositionController;
//            compositionControllerStart.getLast().setNext(compositionController);//no need to traverse the entire list
        }
    }

    public boolean removeCompositionController(CompositionController compositionController){
        //traverse through the list to look for the composition controller
        CompositionController t=compositionControllerStart;
        CompositionController previous=null;
        while(t!=null){
            if(t==compositionController){
                if(previous==null){//start
                    compositionControllerStart=t.getNext();
                }else{
                    previous.setNext(t.getNext());
                }
                return true;// to indicate that the composition controller was found and removed
            }
            previous=t;
            t=t.getNext();
        }
        return false;//to inidicate the composition controller was not found
    }

    @Override
    public void acceptVisitor(ModelVisitor visitor) {
        visitor.visit(this);
        //have all composition controllers accept visitors
        CompositionController t= compositionControllerStart;
        while (t != null) {
            t.acceptVisitor(visitor);
            t=t.getNext();
        }
    }
}
