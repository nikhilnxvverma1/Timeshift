package com.nikhil.command;

import com.nikhil.controller.CompositionViewController;
import com.nikhil.editor.workspace.Workspace;

/**
 * Adds or removes the composition in the workspace bases a flag.
 * Created by NikhilVerma on 05/12/15.
 */
public class AddRemoveComposition extends Command{

    private CompositionViewController compositionViewController;
    private int index=0;
    private boolean add;

    /**
     * Creates a "Add or remove composition" command. The supplied composition <b>should</b> hold reference to
     * the workspace it needs to attach to.
     * @param compositionViewController the composition to add or remove
     * @param index index in the composition tab in which to add this composition
     * @param add if true, the composition will be added to the workspace, vice-versa on false
     */
    public AddRemoveComposition(CompositionViewController compositionViewController, int index, boolean add) {
        if(compositionViewController.getWorkspace()==null){
            throw new RuntimeException("No attached Workspace for this composition");
        }
        this.compositionViewController = compositionViewController;
        this.index = index;
        this.add = add;
    }

    @Override
    public void execute() {
        Workspace workspace = compositionViewController.getWorkspace();
        if(add){
            workspace.addComposition(compositionViewController, index);
        }else{
            workspace.removeComposition(compositionViewController);
        }
    }

    @Override
    public void unexecute() {
        Workspace workspace = compositionViewController.getWorkspace();
        if(add){
            workspace.removeComposition(compositionViewController);
        }else{
            workspace.addComposition(compositionViewController, index);
        }
    }
}
