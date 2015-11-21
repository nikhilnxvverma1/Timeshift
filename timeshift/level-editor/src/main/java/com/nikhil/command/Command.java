package com.nikhil.command;

/**
 * Command that can be used to parameterize an action.
 * Caution:Commands should NEVER create runtime objects of the state.If it does,
 * it is the one responsible for its deletion.
 * Created by NikhilVerma on 23/08/15.
 */
public interface Command {//TODO make this an abstract class and have a method for adding continuous commands
    /** executes the command.*/
    void execute();

    /** unexecutes the command */
    void unexecute();
}
