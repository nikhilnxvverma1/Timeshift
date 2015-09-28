package com.nikhil.controller;

/**
 * Created by NikhilVerma on 26/09/15.
 */
public class CompositionViewController {

    private CompositionController compositionController;

    public CompositionViewController(CompositionController compositionController) {
        this.compositionController = compositionController;
    }

    public CompositionController getCompositionController() {
        return compositionController;
    }

    //TODO fill class as per need basis
}
