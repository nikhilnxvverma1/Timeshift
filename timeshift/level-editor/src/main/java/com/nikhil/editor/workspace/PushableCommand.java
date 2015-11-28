package com.nikhil.editor.workspace;

import com.nikhil.command.Command;

/**
 * Simple POJO that stores a command and a corresponding boolean.
 * The boolean suggests if the command should be executed while pushing or not.
 * Created by NikhilVerma on 27/11/15.
 */
public class PushableCommand {
    private Command command;
    private boolean executeWhilePushing;

    public PushableCommand(Command command, boolean executeWhilePushing) {
        this.command = command;
        this.executeWhilePushing = executeWhilePushing;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public boolean isExecuteWhilePushing() {
        return executeWhilePushing;
    }

    public void setExecuteWhilePushing(boolean executeWhilePushing) {
        this.executeWhilePushing = executeWhilePushing;
    }
}
