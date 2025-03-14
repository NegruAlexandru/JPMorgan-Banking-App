package org.poo.app.appFunctionality.userOperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.baseClasses.User;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.app.logicHandlers.DB;
import org.poo.utils.Operation;
import org.poo.utils.RequestSP;

import java.util.ArrayList;

public class RejectSplitPayment extends Operation {
    public RejectSplitPayment(final CommandHandler handler,
                              final ArrayNode output) {
        super(handler, output);
    }

    /**
     * Rejects a split payment request if the conditions are met
     */
    @Override
    public void execute() {
        User user = DB.findUserByEmail(handler.getEmail());
        if (user == null) {
            addTransactionToOutput("description", "User not found");
            return;
        }

        ArrayList<RequestSP> requests = new ArrayList<>(user.getSplitPaymentRequests());
        for (RequestSP req : requests) {
            if (req.isCancelled()) {
                user.getSplitPaymentRequests().remove(req);
                continue;
            }

            if (req.getSplitPayment().getType().equals(handler.getSplitPaymentType())) {
                user.getSplitPaymentRequests().remove(req);

                req.setAccepted(false);
                req.getSplitPayment().checkForSplitPayment(req);
            }
        }
    }
}
