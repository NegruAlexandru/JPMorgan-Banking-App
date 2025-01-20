package org.poo.utils;

import lombok.Getter;
import lombok.Setter;
import org.poo.app.appFunctionality.userOperations.SplitPayment;
import org.poo.app.input.User;

@Getter
@Setter
public class RequestSP {
    private final User user;
    private final SplitPayment splitPayment;
    private Boolean accepted;
    private Boolean cancelled = false;

    public RequestSP(User user, SplitPayment splitPayment) {
        this.user = user;
        this.splitPayment = splitPayment;
        this.accepted = null;
    }

    public Boolean isAccepted() {
        return accepted;
    }

    public Boolean isCancelled() {
        return cancelled;
    }
}
