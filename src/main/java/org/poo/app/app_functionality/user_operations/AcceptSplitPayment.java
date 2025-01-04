package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.input.User;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.utils.Operation;
import org.poo.utils.RequestSP;

public class AcceptSplitPayment extends Operation {
    public AcceptSplitPayment(CommandHandler handler, ArrayNode output) {
        super(handler, output);
    }

    @Override
    public void execute() {
        User user = DB.findUserByEmail(handler.getEmail());
        if (user == null) {
            System.out.println("User not found in AcceptSplitPayment");
            return;
        }

        // check check check
        if (user.getSplitPaymentRequests().isEmpty()) {
            return;
        }
        RequestSP request = user.getSplitPaymentRequests().getFirst();

        while (request.isCancelled()) {
            user.getSplitPaymentRequests().removeFirst();

            if (user.getSplitPaymentRequests().isEmpty()) {
                return;
            }
            request = user.getSplitPaymentRequests().getFirst();
        }

        request.setAccepted(true);
        request.getSplitPayment().checkForSplitPayment(request);
    }
}
