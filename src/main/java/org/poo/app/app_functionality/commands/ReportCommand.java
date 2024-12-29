package org.poo.app.app_functionality.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logic_handlers.CommandExecutor;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.utils.Command;

public class ReportCommand implements Command {
    private final CommandExecutor executor;
    private final CommandHandler handler;
    private final ArrayNode output;

    public ReportCommand(CommandExecutor executor, CommandHandler handler, ArrayNode output) {
        this.executor = executor;
        this.handler = handler;
        this.output = output;
    }

    @Override
    public void execute() {
        executor.report(handler, output);
    }
}
