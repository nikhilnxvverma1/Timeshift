package com.nikhil.command;

import com.nikhil.editor.workspace.Workspace;

/**
 * Command that can be used to parameterize an action.
 * Caution:Commands should NEVER create runtime state objects.
 * Created by NikhilVerma on 23/08/15.
 */
public abstract class Command {
    /** executes the command.*/
    public abstract void execute();

    /** unexecutes the command */
    public abstract void unexecute();

    /**
     * Called by workspace after this command is executed
     * @param workspace workspace that executed this command
     */
    public void executedByWorkspace(Workspace workspace){

    }

    /**
     * Called by workspace after this command is un-executed
     * @param workspace workspace that un-executed this command
     */
    public void unexecutedByWorkspace(Workspace workspace){

    }
}
