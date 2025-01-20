package org.poo.app.appFunctionality.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logicHandlers.CommandExecutor;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.utils.Command;

public class PayOnlineCommand implements Command {
    private final CommandExecutor executor;
    private final CommandHandler handler;
    private final ArrayNode output;

    public PayOnlineCommand(final CommandExecutor executor,
                            final CommandHandler handler,
                            final ArrayNode output) {
        this.executor = executor;
        this.handler = handler;
        this.output = output;
    }

    /**
     * Execute the command
     */
    @Override
    public void execute() {
        executor.payOnline(handler, output);
    }
}
