package org.poo.app.appFunctionality.userOperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.input.User;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.app.logicHandlers.DB;
import org.poo.utils.Operation;
import org.poo.utils.RequestSP;

import java.util.ArrayList;

public class AcceptSplitPayment extends Operation {
    public AcceptSplitPayment(CommandHandler handler, ArrayNode output) {
        super(handler, output);
    }

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

                req.setAccepted(true);
                req.getSplitPayment().checkForSplitPayment(req);
            }
        }
    }
}
