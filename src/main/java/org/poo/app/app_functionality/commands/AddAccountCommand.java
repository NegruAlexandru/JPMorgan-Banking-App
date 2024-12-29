package org.poo.app.app_functionality.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logic_handlers.CommandExecutor;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.utils.Command;

public class AddAccountCommand implements Command {
    private final CommandExecutor executor;

    public AddAccountCommand(final CommandExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void execute() {
        executor.addAccount();
    }
}
