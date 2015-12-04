package com.nikhil.command.composite;

import com.nikhil.command.Command;

import java.util.List;

/**
 * A command that consists of a bunch of commands to execute/unexecute at once
 * Created by NikhilVerma on 27/11/15.
 */
public abstract class CompositeCommand extends Command {
    /**
     * @return List of commands that this composite command executes
     */
    public abstract List<? extends Command> getCommands();

    public boolean contains(Command command){
        return getCommands().contains(command);
    }
}
