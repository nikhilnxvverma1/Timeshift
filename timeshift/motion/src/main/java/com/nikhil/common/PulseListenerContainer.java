package com.nikhil.common;

/**
 * Created by NikhilVerma on 11/08/15.
 */
public class PulseListenerContainer {

    private PulseListener pulseListener;
    private PulseListenerContainer next;

    public PulseListenerContainer(PulseListener pulseListener) {
        this(pulseListener,null);
    }

    public PulseListenerContainer(PulseListener pulseListener, PulseListenerContainer next) {
        this.pulseListener = pulseListener;
        this.next = next;
    }

    public PulseListener getPulseListener() {
        return pulseListener;
    }

    public void setPulseListener(PulseListener pulseListener) {
        this.pulseListener = pulseListener;
    }

    public PulseListenerContainer getNext() {
        return next;
    }

    public void setNext(PulseListenerContainer next) {
        this.next = next;
    }

    public PulseListenerContainer getLast(){
        PulseListenerContainer last=this;
        PulseListenerContainer t=this;
        while (t != null) {
            last=t;
            t=t.next;
        }
        return last;
    }

}
