package com.nikhil.command.composite;

import com.nikhil.command.Command;

import java.util.LinkedList;
import java.util.List;

/**
 * A command that consists of general commands to execute/unexecute at once
 * Created by NikhilVerma on 27/11/15.
 */
public class GeneralCompositeCommand extends CompositeCommand {

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

    @Override
    public List<? extends Command> getCommands() {
        return commands;
    }
}
