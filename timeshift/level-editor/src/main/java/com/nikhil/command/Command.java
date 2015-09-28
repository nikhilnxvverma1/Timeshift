package com.nikhil.command;

/**
 * Command that can be used to parameterize an action.
 * Caution:Commands should NEVER create runtime objects of the state .
 * Created by NikhilVerma on 23/08/15.
 */
public interface Command {
    /** executes the command.*/
    public void execute();

    /** unexecutes the command */
    public void unexecute();
}
