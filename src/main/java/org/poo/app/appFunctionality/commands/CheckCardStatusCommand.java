package org.poo.app.appFunctionality.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logicHandlers.CommandExecutor;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.utils.Command;

public class CheckCardStatusCommand implements Command {
    private final CommandExecutor executor;
    private final CommandHandler handler;
    private final ArrayNode output;

    public CheckCardStatusCommand(CommandExecutor executor, CommandHandler handler, ArrayNode output) {
        this.executor = executor;
        this.handler = handler;
        this.output = output;
    }

    @Override
    public void execute() {
        executor.checkCardStatus(handler, output);
    }
}
