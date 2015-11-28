package com.nikhil.command;

import java.util.LinkedList;
import java.util.List;

/**
 * A command that consists of a bunch of commands to execute/unexecute at once
 * Created by NikhilVerma on 27/11/15.
 */
public class CompositeCommand extends Command {

    private List<Command>commands=new LinkedList<>();

    public void addCommands(Command command){
        commands.add(command);
    }

    public boolean removeCommand(Command command){
        return commands.remove(command);
    }

    @Override
    public void execute() {
        for(Command command: commands){
            command.execute();
        }
    }

    @Override
    public void unexecute() {
        for(Command command: commands){
            command.unexecute();
        }
    }
}
