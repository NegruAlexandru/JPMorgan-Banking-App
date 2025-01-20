package org.poo.utils;

import lombok.Getter;
import lombok.Setter;
import org.poo.app.appFunctionality.userOperations.SplitPayment;
import org.poo.app.baseClasses.User;

@Getter
@Setter
public class RequestSP {
    private final User user;
    private final SplitPayment splitPayment;
    private Boolean accepted;
    private Boolean cancelled = false;

    public RequestSP(final User user,
                     final SplitPayment splitPayment) {
        this.user = user;
        this.splitPayment = splitPayment;
        this.accepted = null;
    }

    /**
     * Checks if the request has been accepted
     */
    public Boolean isAccepted() {
        return accepted;
    }

    /**
     * Checks if the request has been cancelled
     */
    public Boolean isCancelled() {
        return cancelled;
    }
}
